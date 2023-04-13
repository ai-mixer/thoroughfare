package top.aimixer.modules.models.chat.openai;

import com.knuddels.jtokkit.api.ModelType;
import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.Usage;
import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.reactivex.Flowable;
import org.apache.commons.lang3.StringUtils;
import top.aimixer.modules.models.chat.BaseChatModel;
import top.aimixer.schema.models.ChatGeneration;
import top.aimixer.schema.models.ChatResult;
import top.aimixer.schema.prompts.AIMessage;
import top.aimixer.schema.prompts.BaseMessage;
import top.aimixer.schema.prompts.HumanMessage;
import top.aimixer.schema.prompts.SystemMessage;
import top.aimixer.utilites.OSUtils;
import top.aimixer.utilites.tokenizer.TokenUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ChatOpenAi extends BaseChatModel {

    private OpenAiService openAiService;
    private static String modelName = ModelType.GPT_3_5_TURBO.getName();
    private ChatCompletionRequest chatCompletionRequest;
    /**
     * What sampling temperature to use.
     */
    public double temperature = 0.7f;
    /**
     * Defaults to 16. The maximum number of tokens to generate in the completion.
     * <p>
     * The token count of your prompt plus max_tokens cannot exceed the model's context length.
     * Most models have a context length of 2048 tokens (except for the newest models, which support 4096).
     * <p>
     * The maximum number of tokens to generate in the completion.
     * -1 returns as many tokens as possible given the prompt and the models maximal context size.
     */
    public int maxTokens = 256;
    // Total probability mass of tokens to consider at each step.
    public double topP = 1;
    // Penalizes repeated tokens according to frequency.
    public double frequencyPenalty = 0;
    // Penalizes repeated tokens.
    public double presencePenalty = 0;
    // How many completions to generate for each prompt.
    public int n = 1;
    // Generates best_of completions server-side and returns the "best".
    public int bestOf = 1;
    // Whether to stream the results or not.
    public boolean streaming = false;

    public List<String> stops;
    // Adjust the probability of specific tokens being generated.
    public Map<String, Integer> logitBias = new HashMap<>();
    public String user;

    //Series of messages for Chat input.
    private List<ChatMessage> prefixMessages = new ArrayList<>();

    private String openaiApiKey;
    private int maxRetries = 6;

    public ChatOpenAi(Map<String, Object> values) {
        validateEnvironment(values);
    }

    public void validateEnvironment(Map<String, Object> values) {
        try {
            if (n < 1) {
                throw new IllegalArgumentException("n must be at least 1.");
            }
            if (n > 1 && streaming) {
                throw new IllegalArgumentException("n must be 1 when streaming.");
            }
            String openaiApiKey =
                    OSUtils.getFromDictOrEnv(values, "openai_api_key", "OPENAI_API_KEY", null);
            this.openAiService = new OpenAiService(openaiApiKey);
            this.chatCompletionRequest = new ChatCompletionRequest(
                    this.modelName, this.prefixMessages, this.temperature, this.topP, this.n,
                    this.streaming, this.stops, this.maxTokens, this.presencePenalty,
                    this.frequencyPenalty, this.logitBias, this.user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Retry decorateRetry(String retryName, int maxRetries) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(maxRetries)
//                .waitDuration(Duration.of(10, ChronoUnit.SECONDS))
                .intervalFunction(IntervalFunction.ofExponentialBackoff(Duration.ofSeconds(1), 2))
                .retryExceptions(OpenAiHttpException.class)
                .build();

        RetryRegistry registry = RetryRegistry.of(config);
        Retry retry = registry.retry(retryName);
        return retry;
    }

    /**
     * Wait 2^x * 1 second between each retry starting with 4 seconds, then up to 10
     * seconds, then 10 seconds afterwards
     *
     * @return
     */
    public ChatCompletionResult createRetryDecorator() {
        Retry retry = decorateRetry("openai-retry", maxRetries);
        return retry.executeSupplier(() -> openAiService.createChatCompletion(chatCompletionRequest));
    }

    private Flowable<ChatCompletionChunk> createRetryStreamDecorator() {
        Retry retry = decorateRetry("openai-retry-stream", maxRetries);
        return retry.executeSupplier(() -> openAiService.streamChatCompletion(chatCompletionRequest));
    }

    private ChatResult createChatResult(ChatCompletionResult response) {
        List<ChatGeneration> generations = new ArrayList<>();
        Map<String, Long> tokenUsageMap = new HashMap<>();
        for (ChatCompletionChoice choice : response.getChoices()) {
            if (choice.getMessage() != null) {
                Map<String, String> map = new HashMap<>();
                map.put("role", choice.getMessage().getRole());
                map.put("content", choice.getMessage().getContent());
                BaseMessage baseMessage = convertMapToMessage(map);
                generations.add(new ChatGeneration(baseMessage));
                updateTokenUsage(response.getUsage(), tokenUsageMap);
            }
        }
        ChatResult chatResult = new ChatResult(generations);
        chatResult.setLlmOutput(tokenUsageMap);
        return chatResult;
    }

    private List<ChatMessage> createMessageMaps(List<BaseMessage> messages, List<String> stops) {
        List<ChatMessage> messageMaps = new ArrayList<>();
        for (BaseMessage message : messages) {
            Map<String, String> messageToMap = convertMessageToMap(message);
            messageMaps.add(new ChatMessage(messageToMap.get("role"), messageToMap.get("content")));
        }
        if (stops != null) {
            chatCompletionRequest.setStop(stops);
        }
        return messageMaps;
    }

    @Override
    protected ChatResult internalGenerate(List<BaseMessage> messages, List<String> stops) {
        List<ChatMessage> chatMessages = createMessageMaps(messages, stops);
        chatCompletionRequest.setMessages(chatMessages);
        if (this.streaming) {
            final StringBuilder innerCompletion = new StringBuilder();
            final StringBuilder role = new StringBuilder("assistant");
            chatCompletionRequest.setStream(true);
            Flowable<ChatCompletionChunk> chunkFlowable = createRetryStreamDecorator();
            chunkFlowable.doOnError(Throwable::printStackTrace).blockingForEach((chatCompletionChunk) -> {
                role.append(chatCompletionChunk.getChoices().get(0).getMessage().getRole());
                String token = chatCompletionChunk.getChoices().get(0).getMessage().getContent();
                innerCompletion.append(token);
            });
            BaseMessage message = convertMapToMessage(new HashMap<>() {{
                put("role", role.toString());
                put("content", innerCompletion.toString());
            }});
            return new ChatResult(new ArrayList<>() {{
                add(new ChatGeneration(message));
            }});
        } else {
            ChatCompletionResult response = createRetryDecorator();
            return createChatResult(response);
        }
    }

    @Override
    public CompletableFuture<ChatResult> asyncInternalGenerate(List<BaseMessage> messages, List<String> stop) {
        return null;
    }

    public int getNumTokens(String text) {
        return TokenUtils.tokenByModelType(ModelType.GPT_3_5_TURBO, text);
    }

    private BaseMessage convertMapToMessage(Map<String, String> messageMap) {
        String role = String.valueOf(messageMap.get("role"));
        String content = String.valueOf(messageMap.get("content"));
        if ("user".equals(role)) {
            return new HumanMessage(content);
        } else if ("assistant".equals(role)) {
            return new AIMessage(content);
        } else if ("system".equals(role)) {
            return new SystemMessage(content);
        } else {
            return new top.aimixer.schema.prompts.ChatMessage(content);
        }
    }

    private Map<String, String> convertMessageToMap(BaseMessage message) {
        Map<String, String> messageMap = new HashMap<>();
        if (message instanceof top.aimixer.schema.prompts.ChatMessage) {
            ChatMessage chatMessage = ChatMessage.class.cast(message);
            messageMap.put("role", chatMessage.getRole());
        } else if (message instanceof HumanMessage) {
            messageMap.put("role", "user");
        } else if (message instanceof AIMessage) {
            messageMap.put("role", "assistant");
        } else if (message instanceof SystemMessage) {
            messageMap.put("role", "system");
        } else {
            throw new RuntimeException(String.format("Got unknown type %s", message));
        }
        messageMap.put("content", message.getContent());
        return messageMap;
    }

    protected static void updateTokenUsage(Usage usage, Map<String, Long> tokenUsageMap) {
        if (tokenUsageMap.containsKey("completion_tokens")) {
            tokenUsageMap.computeIfPresent("completion_tokens", (key, val) -> val + usage.getCompletionTokens());
        } else {
            tokenUsageMap.put("completion_tokens", usage.getCompletionTokens());
        }
        if (tokenUsageMap.containsKey("prompt_tokens")) {
            tokenUsageMap.computeIfPresent("prompt_tokens", (key, val) -> val + usage.getPromptTokens());
        } else {
            tokenUsageMap.put("prompt_tokens", usage.getPromptTokens());
        }
        if (tokenUsageMap.containsKey("total_tokens")) {
            tokenUsageMap.computeIfPresent("total_tokens", (key, val) -> val + usage.getTotalTokens());
        } else {
            tokenUsageMap.put("total_tokens", usage.getTotalTokens());
        }
    }

//    private BaseMessage convertDictToMessage(HashMap<String, Object> messageDict) {
//        BaseMessage message = new BaseMessage(messageDict.get("text").toString(),
//                messageDict.get("role").toString());
//        return message;
//    }
//
//    private static HashMap<String, Object> createDefaultParams() {
//        HashMap<String, Object> defaultParams = new HashMap<String, Object>();
//        defaultParams.put("model_name", "gpt-3.5-turbo");
//        defaultParams.put("model_kwargs", new HashMap<String, Object>());
//        defaultParams.put("openai_api_key", null);
//        defaultParams.put("max_retries", 6);
//        defaultParams.put("streaming", false);
//        defaultParams.put("n", 1);
//        defaultParams.put("max_tokens", 256);
//        return defaultParams;
//    }
//
}


