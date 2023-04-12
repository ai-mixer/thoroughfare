package top.aimixer.modules.models.llms.openai;

import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.Usage;
import com.theokanning.openai.service.OpenAiService;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import top.aimixer.modules.models.llms.BaseLLM;
import top.aimixer.utilites.OSUtils;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic OpenAI class that uses model name.
 */
public abstract class OpenAi extends BaseLLM {

    // :meta private:
    public OpenAiService openAiService;
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
    // Maximum number of retries to make when generating.
    public int maxRetries = 6;

    public OpenAi(Map<String, Object> requestParams) {
        buildApiEnvironment(fillRequestParams(requestParams));
    }

    protected void buildOpenAIService(Map<String, Object> values) throws Exception {
        String openaiApiKey = OSUtils.getFromDictOrEnv(
                values, "openai_api_key", "OPENAI_API_KEY", null);
        this.openAiService = new OpenAiService(openaiApiKey);
    }

    protected Map<String, Object> buildApiEnvironment(Map<String, Object> values) {
        try {
            buildOpenAIService(values);
            buildOpenAIRequest();
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(
                    "Could not import openai python package. Please install it with `pip install openai`.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if ((values.get("streaming") != null && (boolean) values.get("streaming"))
                && (values.get("n") != null && (int) values.get("n") > 1)) {
            throw new IllegalArgumentException("Cannot stream results when n > 1.");
        }
        if ((values.get("streaming") != null && (boolean) values.get("streaming"))
                && (values.get("best_of") != null && (int) values.get("best_of") > 1)) {
            throw new IllegalArgumentException("Cannot stream results when best_of > 1.");
        }
        return values;
    }

    protected Map<String, Object> fillRequestParams(Map<String, Object> requestParams) {
        Field[] fields = this.getClass().getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            Object paramValue = requestParams.get(fieldName);
            if (paramValue != null) {
                field.setAccessible(true);
                try {
                    field.set(this, paramValue);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

            }
        }
        return requestParams;
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
     * Update token usage.
     *
     * @param usage
     * @return
     */
    protected static Map<String, Long> updateTokenUsage(Usage usage) {
        Map<String, Long> tokenUsageMap = new HashMap<>();
        tokenUsageMap.computeIfPresent("completion_tokens", (key, val) -> val + usage.getCompletionTokens());
        tokenUsageMap.computeIfPresent("prompt_tokens", (key, val) -> val + usage.getPromptTokens());
        tokenUsageMap.computeIfPresent("total_tokens", (key, val) -> val + usage.getTotalTokens());
        return tokenUsageMap;
    }

    protected abstract void buildOpenAIRequest();

}
