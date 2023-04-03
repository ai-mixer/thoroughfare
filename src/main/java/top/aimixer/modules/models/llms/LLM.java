package top.aimixer.modules.models.llms;

import top.aimixer.callback.BaseCallbackManager;
import top.aimixer.schema.Generation;
import top.aimixer.schema.models.LLMResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * LLM class that expects subclasses to implement a simpler call method.
 * The purpose of this class is to expose a simpler interface for working
 * with LLMs, rather than expect the user to implement the full _generate method.
 */
public abstract class LLM extends BaseLLM {

    public LLM(Boolean cache, BaseCallbackManager callbackManager) {
        super(cache, callbackManager);
    }

    public abstract String call(String prompt, List<String> stop);

    public CompletableFuture<String> asyncCall(String prompt, List<String> stop) {
        throw new UnsupportedOperationException("Async generation not implemented for this LLM.");
    }

    /**
     * Run the LLM on the given prompt and input.
     */
    @Override
    public LLMResult generate(List<String> prompts, List<String> stop) {
        // TODO: add caching here.
        List<List<Generation>> generations = new ArrayList<>();
        for (String prompt : prompts) {
            String text = call(prompt, stop);
            generations.add(Collections.singletonList(new Generation(text, Optional.empty())));
        }
        return new LLMResult(generations);
    }

    /**
     * Run the LLM on the given prompt and input.
     */
    @Override
    public CompletableFuture<LLMResult> asyncGenerate(List<String> prompts, List<String> stop) {
        List<List<Generation>> generations = new ArrayList<>();
        for (String prompt : prompts) {
            try {
                String text = asyncCall(prompt, stop).get();
                generations.add(Collections.singletonList(new Generation(text, Optional.empty())));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return CompletableFuture.completedFuture(new LLMResult(generations));
    }
}

