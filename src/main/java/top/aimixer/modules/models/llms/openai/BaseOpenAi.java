package top.aimixer.modules.models.llms.openai;

import com.knuddels.jtokkit.api.EncodingType;
import com.knuddels.jtokkit.api.ModelType;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionChunk;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import io.github.resilience4j.retry.Retry;
import io.reactivex.Flowable;
import top.aimixer.schema.Generation;
import top.aimixer.schema.models.LLMResult;
import top.aimixer.utilites.tokenizer.TokenUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Wrapper around OpenAI large language models.
 * To use, you should have the "openai" python package installed, and the
 * environment variable "OPENAI_API_KEY" set with your API key.
 * Any parameters that are valid to be passed to the openai.create call can be passed
 * in, even if not explicitly saved on this class.
 */
public class BaseOpenAi extends OpenAi {
    /**
     * Model name to use.
     * Must use static to avoid init after constructor.
     */
    public static String modelName = ModelType.TEXT_DAVINCI_003.getName();
    /**
     * Do not use new to initialize
     */
    private CompletionRequest completionRequest;
    private String suffix;
    private int logProbs;
    private boolean echo;
    // Batch size to use when passing multiple documents to generate.
    private int batchSize = 20;
    // Timeout for requests to OpenAI completion API. Default is 600 seconds.
    private Float requestTimeout = null;

    public BaseOpenAi(Map<String, Object> requestParams) {
        super(requestParams);
    }

//    public Map<String, Object> buildExtra(Map<String, Object> instanceParams) {
//        Set<String> allRequiredFieldNames = Arrays.stream(
//                this.getClass().getDeclaredFields()).map(field -> field.getName()).collect(Collectors.toSet());
//        Map<String, Object> extra =
//                (Map<String, Object>) instanceParams.getOrDefault("modelParams", new HashMap<>());
//        for (String valueName : instanceParams.keySet()) {
//            if (!allRequiredFieldNames.contains(valueName)) {
//                if (extra.containsKey(valueName)) {
//                    throw new IllegalArgumentException("Found " + valueName + " supplied twice.");
//                }
//                System.out.println("WARNING! " + valueName + " is not default parameter. "
//                        + valueName + " was transferred to model_kwargs. Please confirm that "
//                        + valueName + " is what you intended.");
//                extra.put(valueName, instanceParams.remove(valueName));
//            }
//        }
//        instanceParams.put("modelParams", extra);
//        return instanceParams;
//    }

    @Override
    protected void buildOpenAIRequest() {
        this.completionRequest = new CompletionRequest(
                this.modelName, null, this.suffix, this.maxTokens, this.temperature,
                this.topP, this.n, this.streaming, this.logProbs, this.echo, this.stops,
                this.presencePenalty, this.frequencyPenalty, this.bestOf, this.logitBias,
                this.user);
    }

    /**
     * Update response from the stream response.
     *
     * @param response
     * @param streamResponse
     */
    private static void updateResponse(CompletionResult response, List<CompletionChoice> streamResponse) {
        List<CompletionChoice> choices = response.getChoices();
        choices.get(0).setText(choices.get(0).getText() + streamResponse.get(0).getText());
        choices.get(0).setFinish_reason(streamResponse.get(0).getFinish_reason());
        choices.get(0).setLogprobs(streamResponse.get(0).getLogprobs());
    }

    private static CompletionResult streamingResponseTemplate() {
        CompletionResult result = new CompletionResult();
        List<CompletionChoice> choices = new ArrayList<>();
        CompletionChoice choice = new CompletionChoice();
        choice.setText("");
        choice.setFinish_reason(null);
        choice.setLogprobs(null);
        choices.add(choice);
        result.setChoices(choices);
        return result;
    }

    /**
     * Use tenacity to retry the completion call.
     * Wait 2^x * 1 second between each retry starting with
     * 4 seconds, then up to 10 seconds, then 10 seconds afterwards
     *
     * @return
     */
    private CompletionResult completionWithRetry() {
        Retry retry = decorateRetry("openai-retry", maxRetries);
        return retry.executeSupplier(() -> openAiService.createCompletion(completionRequest));
    }

    private Flowable<CompletionChunk> completionStreamWithRetry() {
        Retry retry = decorateRetry("openai-retry-stream", maxRetries);
        return retry.executeSupplier(() -> openAiService.streamCompletion(completionRequest));
    }

    /**
     * Call out to OpenAI's endpoint with k unique prompts.
     *
     * @param prompts
     * @param stops
     * @return
     */
    @Override
    public LLMResult generate(List<String> prompts, List<String> stops) {
//        Map<String, Object> params = getInvocationParams();
        List<List<String>> subPrompts = getSubPrompts(prompts, stops);
        List<CompletionChoice> choices = new ArrayList<>();
        Map<String, Long> tokenUsage = new HashMap<>();
        // Get the token usage from the response.
        // Includes prompt, completion, and total tokens used.
        // Set<String> filterKeys = new HashSet<>(Arrays.asList("completion_tokens", "prompt_tokens", "total_tokens"));
        for (List<String> subPrompt : subPrompts) {
            completionRequest.setPrompt(subPrompt.get(0));
            if (this.streaming) {
                if (subPrompt.size() > 1) {
                    throw new IllegalArgumentException("Cannot stream results with multiple prompts.");
                }
                completionRequest.setStream(true);
                CompletionResult response = streamingResponseTemplate();
                Flowable<CompletionChunk> chunkFlowable = completionStreamWithRetry();
                chunkFlowable.doOnError(Throwable::printStackTrace).blockingForEach((completionChunk) -> {
                    List<CompletionChoice> choiceList = completionChunk.getChoices();
                    updateResponse(response, choiceList);
                    choices.addAll(response.getChoices());
                });
//                this.getCallbackManager().onLLMNewToken(choiceList.get(0).getText(),
//                        choiceList.get(0).getLogprobs().getTokenLogprobs()
//                );
            } else {
                CompletionResult response = completionWithRetry();
                choices.addAll(response.getChoices());
                // Can't update token usage if streaming
                tokenUsage = updateTokenUsage(response.getUsage());
            }
        }
        return this.createLLMResult(choices, prompts, tokenUsage);
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

    /**
     * Return type of llm.
     *
     * @return
     */
    @Override
    public String llmType() {
        return "openai";
    }

//    /**
//     * Get the default parameters for calling OpenAI API.
//     */
//    private Map<String, Object> getDefaultParams() {
//        Map<String, Object> normalParams = new HashMap<>();
//        normalParams.put("temperature", this.getTemperature());
//        normalParams.put("max_tokens", this.getMaxTokens());
//        normalParams.put("top_p", this.getTopP());
//        normalParams.put("frequency_penalty", this.getFrequencyPenalty());
//        normalParams.put("presence_penalty", this.getPresencePenalty());
//        normalParams.put("n", this.getN());
//        normalParams.put("best_of", this.getBestOf());
//        normalParams.put("request_timeout", this.requestTimeout);
//        normalParams.put("logit_bias", this.getLogitBias());
//        Map<String, Object> defaultParams = new HashMap<>(normalParams);
////        defaultParams.putAll(this.);
//        return defaultParams;
//    }

    public List<List<String>> getSubPrompts(List<String> prompts, List<String> stops) {
        if (stops != null) {
            completionRequest.setStop(stops);
        }
        int maxTokens = this.maxTokensForPrompt(prompts.get(0));
        if (maxTokens < 0) {
            if (prompts.size() != 1) {
                throw new IllegalArgumentException("max_tokens set to -1 or prompt is empty is " +
                        "not supported for multiple inputs.");
            }
        }
        completionRequest.setMaxTokens(maxTokens);
        List<List<String>> subPrompts = new ArrayList<>();
        for (int i = 0; i < prompts.size(); i += this.batchSize) {
            subPrompts.add(prompts.subList(i, Math.min(i + this.batchSize, prompts.size())));
        }
        return subPrompts;
    }

    public LLMResult createLLMResult(List<CompletionChoice> choices, List<String> prompts,
                                     Map<String, Long> tokenUsage) {
        List<List<Generation>> generations = new ArrayList<>();
        for (int i = 0; i < prompts.size(); i++) {
            List<CompletionChoice> subChoices = choices.subList(i * this.n, (i + 1) * this.n);
            List<Generation> subGenerations = new ArrayList<>();
            for (CompletionChoice choice : subChoices) {
                subGenerations.add(new Generation(
                        String.valueOf(choice.getText()),
                        new HashMap() {{
                            put("finish_reason", choice.getFinish_reason());
                            put("logprobs", choice.getLogprobs());
                        }}
                ));
            }
            generations.add(subGenerations);
        }
        return new LLMResult(generations, new HashMap() {{
            put("token_usage", tokenUsage);
        }});
    }

//    /**
//     * Call OpenAI with streaming flag and return the resulting generator.
//     * <p>
//     * BETA: this is a beta feature while we figure out the right abstraction.
//     * Once that happens, this interface could change.
//     * <p>
//     * Args:
//     * prompt: The prompts to pass into the model.
//     * stop: Optional list of stop words to use when generating.
//     * <p>
//     * Returns:
//     * A generator representing the stream of tokens from OpenAI.
//     *
//     * @param prompt
//     * @param stops
//     * @return
//     * @throws Exception
//     */
//    public Stream<CompletionResult> stream(String prompt, List<String> stops) throws Exception {
//        Map<String, Object> params = this.prepStreamingParams(stops);
//        Stream<CompletionResult> stream = Stream.of(this.openAiService.createCompletion(completionRequest));
//        return stream;
//    }

//    /**
//     * Prepare the params for streaming.
//     */
//    public Map<String, Object> prepStreamingParams(List<String> stops) throws Exception {
//        Map<String, Object> params = getInvocationParams();
//        if ((int) params.get("best_of") != 1) {
//            throw new Exception("OpenAI only supports best_of == 1 for streaming");
//        }
//        if (stops != null) {
//            if (params.containsKey("stop")) {
//                throw new Exception("`stop` found in both the input and default params.");
//            }
//            params.put("stop", stops);
//        }
//        params.put("stream", true);
//        return params;
//    }

//    /**
//     * Get the parameters used to invoke the model.
//     *
//     * @return
//     */
//    protected Map<String, Object> getInvocationParams() {
//        return this.getDefaultParams();
//    }

//    /**
//     * Get the identifying parameters.
//     *
//     * @return
//     */
//    public Map<String, Object> getIdentifyingParams() {
//        Map<String, Object> identifying_params = new HashMap<>();
//        identifying_params.put("model_name", this.modelName);
//        identifying_params.putAll(getDefaultParams());
//        return identifying_params;
//    }

    /**
     * Calculate num tokens with token util.
     *
     * @param text
     * @return
     */
    @Override
    public int getNumTokens(String text) {
        if (ModelType.TEXT_DAVINCI_003.getName().equals(modelName) ||
                ModelType.TEXT_DAVINCI_002.getName().equals(modelName) ||
                modelName.startsWith("code")) {
            return TokenUtils.tokenByEncodingType(EncodingType.R50K_BASE, text);
        }
        // create a default GPT-3.5 encoder instance
        return super.getNumTokens(text);
    }

    /**
     * Calculate the maximum number of tokens possible to generate for a model.
     * <p>
     * text-davinci-003: 4,097 tokens
     * text-curie-001: 2,048 tokens
     * text-babbage-001: 2,048 tokens
     * text-ada-001: 2,048 tokens
     * code-davinci-002: 8,000 tokens
     * code-cushman-001: 2,048 tokens
     */
    public int modelNameToContextSize(String modelName) {

        if (ModelType.TEXT_DAVINCI_003.getName().equals(modelName)) {
            return 4097;
        } else if (ModelType.TEXT_CURIE_001.getName().equals(modelName)) {
            return 2048;
        } else if (ModelType.TEXT_BABBAGE_001.getName().equals(modelName)) {
            return 2048;
        } else if (ModelType.TEXT_ADA_001.getName().equals(modelName)) {
            return 2048;
        } else if (ModelType.TEXT_DAVINCI_002.getName().equals(modelName)) {
            return 8000;
        } else if (ModelType.CODE_CUSHMAN_001.getName().equals(modelName)) {
            return 2048;
        } else {
            return 4097;
        }
    }

    /**
     * Calculate the maximum number of tokens possible to generate for a prompt.
     */
    private int maxTokensForPrompt(String prompt) {
        int num_tokens = getNumTokens(prompt);
        // get max context size for model by name
        int max_size = modelNameToContextSize(this.modelName);
        return max_size - num_tokens;
    }
}
