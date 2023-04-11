package top.aimixer.modules.models.llms;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.theokanning.openai.service.OpenAiService;
import top.aimixer.callback.BaseCallbackManager;
import top.aimixer.init.TF;
import top.aimixer.schema.Generation;
import top.aimixer.schema.Quadruple;
import top.aimixer.schema.models.LLMResult;
import top.aimixer.schema.prompts.PromptValue;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * LLM wrapper should take in a prompt and return a string.
 */
public abstract class BaseLLM extends BaseLanguageModel {
    // :meta private:
    public OpenAiService openAiService;
    /**
     * Whether to print out response text."
     */
    private Boolean cache;
//    private BaseCallbackManager callbackManager;

    public BaseLLM() {
    }

    public BaseLLM(Boolean cache, BaseCallbackManager callbackManager) {
        this.cache = cache;
//        if (callbackManager != null) {
//            this.callbackManager = callbackManager;
//        }
    }

    public static Quadruple<Map<Integer, List<Generation>>, String, List<Integer>, List<String>> getPrompts(
            Map<String, Object> params, List<String> prompts) {
        String llmString = String.valueOf(new TreeMap<>(params));
        List<String> missingPrompts = new ArrayList<>();
        List<Integer> missingPromptIdxs = new ArrayList<>();
        Map<Integer, List<Generation>> existingPrompts = new HashMap<>();
        for (int i = 0; i < prompts.size(); i++) {
            if (TF.LLM_CACHE != null) {
                String prompt = prompts.get(i);
                Object cacheVal = TF.LLM_CACHE.lookup(prompt, llmString);
                if (cacheVal instanceof List) {
                    existingPrompts.put(i, (List<Generation>) cacheVal);
                } else {
                    missingPrompts.add(prompt);
                    missingPromptIdxs.add(i);
                }
            }
        }
        return new Quadruple<>(existingPrompts, llmString, missingPromptIdxs, missingPrompts);
    }

    public static Map<String, Object> updateCache(
            Map<Integer, List<Generation>> existingPrompts,
            String llmString,
            List<Integer> missingPromptIdxs,
            LLMResult newResults,
            List<String> prompts) {
        for (int i = 0; i < newResults.getGenerations().size(); i++) {
            existingPrompts.put(missingPromptIdxs.get(i), newResults.getGenerations().get(i));
            String prompt = prompts.get(missingPromptIdxs.get(i));
            if (TF.LLM_CACHE != null) {
                TF.LLM_CACHE.update(prompt, llmString, newResults.getGenerations().get(i));
            }
        }
        Map<String, Object> llmOutput = newResults.getLlmOutput();
        return llmOutput;
    }

    @Override
    public LLMResult generatePrompt(List<PromptValue> prompts, List<String> stop) {
        List<String> prompt_strings = prompts.stream().map(PromptValue::toString).collect(Collectors.toList());
        return generate0(prompt_strings, stop);
    }

    @Override
    public LLMResult asyncGeneratePrompt(List<PromptValue> prompts, List<String> stop) {
        List<String> prompt_strings = prompts.stream().map(PromptValue::toString).collect(Collectors.toList());
        return asyncGenerate0(prompt_strings, stop);
    }

    /**
     * Run the LLM on the given prompt and input.
     *
     * @param prompts
     * @param stop
     * @return
     */
    private LLMResult generate0(List<String> prompts, List<String> stop) {
        if (!(prompts instanceof List)) {
            throw new IllegalArgumentException("Argument 'prompts' is expected to be of type List<String>, received argument of type " + prompts.getClass().getSimpleName() + ".");
        }
        boolean disregardCache = this.cache != null && !this.cache;
        if (TF.LLM_CACHE == null || disregardCache) {
            if (this.cache != null && this.cache) {
                throw new IllegalArgumentException("Asked to cache, but no cache found.");
            }
            Map map = new HashMap<String, String>() {{
                put("name", this.getClass().getSimpleName());
            }};
//            this.callbackManager.onLLMStart(map, prompts);
            try {
                LLMResult output = this.generate(prompts, stop);
//                this.callbackManager.onLLMEnd(output);
                return output;
            } catch (Exception e) {
//                this.callbackManager.onLLMError(e);
                throw new RuntimeException(e);
            }
        }
        Map<String, Object> params = new HashMap<>() {{
            put("stop", stop);
        }};
        Quadruple<Map<Integer, List<Generation>>, String, List<Integer>, List<String>> promptsQuadruple =
                getPrompts(params, prompts);
        Map<Integer, List<Generation>> existingPrompts = promptsQuadruple.getFirst();
        String llmString = promptsQuadruple.getSecond();
        List<Integer> missingPromptIndexes = promptsQuadruple.getThird();
        List<String> missingPrompts = promptsQuadruple.getFourth();

        Map<String, Object> llmOutput = new HashMap<>();
        if (missingPrompts.size() > 0) {
            Map map = new HashMap<String, String>() {{
                put("name", this.getClass().getSimpleName());
            }};
//            this.callbackManager.onLLMStart(map, missingPrompts);
            try {
                LLMResult newResults = this.generate(missingPrompts, stop);
//                this.callbackManager.onLLMEnd(newResults);
                llmOutput = updateCache(existingPrompts, llmString, missingPromptIndexes, newResults, prompts);
            } catch (Exception e) {
//                this.callbackManager.onLLMError(e);
                throw new RuntimeException(e);
            }
        }
        List<List<Generation>> generations = new ArrayList<>();
        for (int i = 0; i < prompts.size(); i++) {
            generations.add(existingPrompts.get(i));
        }
        return new LLMResult(generations, llmOutput);
    }

    /**
     * Run the LLM on the given prompt and input.
     *
     * @param prompts
     * @param stop
     * @return
     */
    private LLMResult asyncGenerate0(List<String> prompts, List<String> stop) {
        boolean disregard_cache = this.cache != null && !this.cache;
        if (TF.LLM_CACHE == null || disregard_cache) {
            if (this.cache != null && this.cache) {
                throw new IllegalArgumentException("Asked to cache, but no cache found at `cache`.");
            }
//            this.callbackManager.onLLMStart(
//                    Map.of("name", this.getClass().getSimpleName()), prompts);
            try {
                CompletableFuture<LLMResult> asyncOutput = this.asyncGenerate(prompts, stop);
                LLMResult output = null;
//                if (this.callbackManager.isAsync()) {
//                output = asyncOutput.get();
//                } else {
                output = asyncOutput.getNow(null);
//                }
//                this.callbackManager.onLLMEnd(output);
                return output;
            } catch (Exception e) {
//                this.callbackManager.onLLMError(e);
                throw new RuntimeException(e);
            }

        }
        Map<String, Object> params = new HashMap<>() {{
            put("stop", stop);
        }};
        Quadruple<Map<Integer, List<Generation>>, String, List<Integer>, List<String>> promptsQuadruple =
                getPrompts(params, prompts);
        Map<Integer, List<Generation>> existingPrompts = promptsQuadruple.getFirst();
        String llmString = promptsQuadruple.getSecond();
        List<Integer> missingPromptIndexes = promptsQuadruple.getThird();
        List<String> missingPrompts = promptsQuadruple.getFourth();

        Map<String, Object> llmOutput = new HashMap<>();
        if (missingPrompts.size() > 0) {
            Map map = new HashMap<String, String>() {{
                put("name", this.getClass().getSimpleName());
            }};
//            this.callbackManager.onLLMStart(map, missingPrompts);
            try {
                CompletableFuture<LLMResult> asyncNewResults = asyncGenerate(missingPrompts, stop);
                LLMResult newResults = null;
//                if (callbackManager.isAsync()) {
//                    newResults = asyncNewResults.get();
//                } else {
                newResults = asyncNewResults.getNow(null);
//                }
//                callbackManager.onLLMEnd(newResults);
                llmOutput = updateCache(existingPrompts, llmString, missingPromptIndexes, newResults, prompts);
            } catch (Exception e) {
//                this.callbackManager.onLLMError(e);
                throw new RuntimeException(e);
            }
        }
        List<List<Generation>> generations = new ArrayList<>();
        for (int i = 0; i < prompts.size(); i++) {
            generations.add(existingPrompts.get(i));
        }
        return new LLMResult(generations, llmOutput);
    }

    /**
     * Check Cache and run the LLM on the given prompt and input.
     *
     * @param prompt
     * @param stop
     * @return
     */
    public String call(String prompt, List<String> stop) {
        return generate0(Arrays.asList(prompt), stop).getGenerations().get(0).get(0).getText();
    }

    /**
     * Return a dictionary of the LLM.
     *
     * @param kwargs
     * @return
     */
    public Map<String, Object> dict(Object... kwargs) {
        Map<String, Object> starterDict = new HashMap<>();
        for (int i = 0; i < kwargs.length; i += 2) {
            starterDict.put((String) kwargs[i], kwargs[i + 1]);
        }
        starterDict.put("_type", llmType());
        return starterDict;
    }

    /**
     * Save the LLM.
     * <p>
     * Args:
     * file_path: Path to file to save the LLM to.
     * <p>
     * llm.save(file_path="path/llm.yaml")
     *
     * @param file_path
     * @throws IOException
     */
    public void save(String file_path) throws IOException {
        Path save_path = Paths.get(file_path);
        Path directory_path = save_path.getParent();
        Files.createDirectories(directory_path);

        Map<String, Object> prompt_dict = dict();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (save_path.toString().endsWith(".json")) {
            try (Writer writer = new FileWriter(file_path)) {
                gson.toJson(prompt_dict, writer);
            }
        } else if (save_path.toString().endsWith(".yaml")) {
            throw new UnsupportedOperationException("YAML not supported with Gson");
        } else {
            throw new IllegalArgumentException(save_path + " must be json or yaml");
        }
    }

    private Map<String, Object> identifyingParams() {
        return Maps.newHashMap();
    }

    public String toString() {
        String cls_name = "\033[1m" + this.getClass().getSimpleName() + "\033[0m";
        return cls_name + "\nParams: " + identifyingParams();
    }

    public Boolean getCache() {
        return cache;
    }

    public void setCache(Boolean cache) {
        this.cache = cache;
    }

//    public BaseCallbackManager getCallbackManager() {
//        return callbackManager;
//    }

//    public void setCallbackManager(BaseCallbackManager callbackManager) {
//        this.callbackManager = callbackManager;
//    }

//    public abstract Map<String, Object> buildExtra(Map<String, Object> values);
//
//    public abstract Map<String, Object> validateEnvironment(Map<String, Object> values);

    /**
     * Run the LLM on the given prompts.
     *
     * @param prompts
     * @param stop
     * @return
     */
    public abstract LLMResult generate(List<String> prompts, List<String> stop);

    /**
     * Run the LLM on the given prompts.
     *
     * @param prompts
     * @param stop
     * @return
     */
    public abstract CompletableFuture<LLMResult> asyncGenerate(List<String> prompts, List<String> stop);

    public abstract String llmType();

}
