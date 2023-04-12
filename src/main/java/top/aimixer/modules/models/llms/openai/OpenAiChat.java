package top.aimixer.modules.models.llms.openai;

import com.knuddels.jtokkit.api.ModelType;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import io.github.resilience4j.retry.Retry;
import io.reactivex.Flowable;
import org.apache.commons.lang3.StringUtils;
import top.aimixer.schema.Generation;
import top.aimixer.schema.models.LLMResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class OpenAiChat extends OpenAi {

    /**
     * Model name to use.
     * Must use static to avoid init after constructor.
     */
    private static String modelName = ModelType.GPT_3_5_TURBO.getName();

    /**
     * Do not use new to initialize
     */
    private ChatCompletionRequest chatCompletionRequest;
    //Series of messages for Chat input.
    private List<ChatMessage> prefixMessages = new ArrayList<>();


    public OpenAiChat(Map<String, Object> requestParams) {
        super(requestParams);
    }

    @Override
    protected void buildOpenAIRequest() {
        this.chatCompletionRequest = new ChatCompletionRequest(
                this.modelName, this.prefixMessages, this.temperature, this.topP, this.n,
                this.streaming, this.stops, this.maxTokens, this.presencePenalty,
                this.frequencyPenalty, this.logitBias, this.user);
    }

    /**
     * Use tenacity to retry the completion call.
     * Wait 2^x * 1 second between each retry starting with
     * 4 seconds, then up to 10 seconds, then 10 seconds afterwards
     *
     * @return
     */
    private ChatCompletionResult completionWithRetry() {
        Retry retry = decorateRetry("openai-retry", maxRetries);
        return retry.executeSupplier(() -> openAiService.createChatCompletion(chatCompletionRequest));
    }

    private Flowable<ChatCompletionChunk> completionStreamWithRetry() {
        Retry retry = decorateRetry("openai-retry-stream", maxRetries);
        return retry.executeSupplier(() -> openAiService.streamChatCompletion(chatCompletionRequest));
    }

    private List<ChatMessage> getChatPrompts(List<String> prompts, List<String> stops) throws Exception {
        if (prompts.size() > 1) {
            throw new Exception("OpenAIChat currently only supports single prompt, got " + prompts);
        }
        List<ChatMessage> messages = new ArrayList<>(this.prefixMessages);
        messages.add(new ChatMessage("user", prompts.get(0)));
        if (stops != null) {
            chatCompletionRequest.setStop(stops);
        }
        //for ChatGPT api, omitting max_tokens is equivalent to having no limit

        return messages;
    }

    public LLMResult createLLMResult(String message, Map<String, Long> tokenUsage) {
        List<List<Generation>> generations = new ArrayList<>();
        List<Generation> subGenerations = new ArrayList<>();
        subGenerations.add(new Generation(message));
        generations.add(subGenerations);
        return new LLMResult(generations, new HashMap() {{
            put("token_usage", tokenUsage);
        }});
    }

    @Override
    public LLMResult generate(List<String> prompts, List<String> stop) throws Exception {
        List<ChatMessage> chatMessages = this.getChatPrompts(prompts, stop);
        chatCompletionRequest.setMessages(chatMessages);
        StringBuilder response = new StringBuilder();
        Map<String, Long> tokenUsage = new HashMap<>();
        if (this.streaming) {
            chatCompletionRequest.setStream(true);
            Flowable<ChatCompletionChunk> chunkFlowable = completionStreamWithRetry();
            chunkFlowable.doOnError(Throwable::printStackTrace).blockingForEach((chatCompletionChunk) -> {
                String token = chatCompletionChunk.getChoices().get(0).getMessage().getContent();
                if (StringUtils.isNotEmpty(token)) {
                    response.append(token);
                }
            });
        } else {
            ChatCompletionResult chatCompletionResult = completionWithRetry();
            response.append(chatCompletionResult.getChoices().get(0).getMessage().getContent());
            tokenUsage = updateTokenUsage(chatCompletionResult.getUsage());
        }
        return this.createLLMResult(response.toString(), tokenUsage);
    }

    /**
     * TODO: unimplemented
     *
     * @param prompts
     * @param stop
     * @return
     */
    @Override
    public CompletableFuture<LLMResult> asyncGenerate(List<String> prompts, List<String> stop) {
        return null;
    }

    @Override
    public String llmType() {
        return "openai-chat";
    }

    @Override
    public int getNumTokens(String text) {
        // create a default GPT-3.5 encoder instance
        return super.getNumTokens(text);
    }
}
