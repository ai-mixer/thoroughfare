package top.aimixer.modules.models.chat;

import top.aimixer.modules.models.BaseLanguageModel;
import top.aimixer.schema.Generation;
import top.aimixer.schema.models.ChatGeneration;
import top.aimixer.schema.models.ChatResult;
import top.aimixer.schema.models.LLMResult;
import top.aimixer.schema.prompts.BaseMessage;
import top.aimixer.schema.prompts.HumanMessage;
import top.aimixer.schema.prompts.PromptValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public abstract class BaseChatModel extends BaseLanguageModel {

//    public BaseCallbackManager callbackManager;
//
//    public BaseCallbackManager setCallbackManager(BaseCallbackManager callbackManager) {
//        return callbackManager != null ? callbackManager : getCallbackManager();
//    }

    private LLMResult generate0(List<List<BaseMessage>> messages, List<String> stop) {
        List<ChatResult> results = new ArrayList<>();
        Map<String, Long> globalLlmOutput = new HashMap<>();
        for (List<BaseMessage> m : messages) {
            ChatResult chatResult = internalGenerate(m, stop);
            Map<String, Long> llmMap = chatResult.getLlmOutput();
            if (globalLlmOutput.isEmpty()) {
                globalLlmOutput.putAll(llmMap);
            } else {
                llmMap.forEach((key, val) -> {
                    globalLlmOutput.computeIfPresent(key, (k, v) -> v + val);
                });
            }
            results.add(chatResult);
        }
        LLMResult llmResult = new LLMResult(results.stream().map(r -> r.getGenerations()).collect(Collectors.toList()));
        llmResult.setLlmOutput(globalLlmOutput);
        return llmResult;
    }

    private CompletableFuture<LLMResult> asyncGenerate0(List<List<BaseMessage>> messages, List<String> stop) {
        List<CompletableFuture<ChatResult>> results = new ArrayList<>();
        for (List<BaseMessage> m : messages) {
            results.add(asyncInternalGenerate(m, stop));
        }
        return CompletableFuture.allOf(results.toArray(new CompletableFuture[0]))
                .thenApply(v -> new LLMResult(results.stream().map(CompletableFuture::join)
                        .map(r -> r.getGenerations()).collect(Collectors.toList())));
    }

    public LLMResult generatePrompt(List<PromptValue> prompts, List<String> stop) {
        List<List<BaseMessage>> promptMessages = prompts.stream().map(PromptValue::toMessages)
                .collect(Collectors.toList());
//        List<String> prompt_strings = prompts.stream().map(PromptValue::toString).collect(Collectors.toList());
        LLMResult output = generate0(promptMessages, stop);
        return output;
    }

    public LLMResult asyncGeneratePrompt(List<PromptValue> prompts, List<String> stop) {
        List<List<BaseMessage>> promptMessages = prompts.stream().map(PromptValue::toMessages)
                .collect(Collectors.toList());
        try {
            return asyncGenerate0(promptMessages, stop).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public LLMResult generate(List<List<BaseMessage>> messages, List<String> stops) {
        return generate0(messages, stops);
    }

    public CompletableFuture<LLMResult> asyncGenerate(List<List<BaseMessage>> messages, List<String> stops) {
        return asyncGenerate0(messages, stops);
    }

    protected abstract ChatResult internalGenerate(List<BaseMessage> messages, List<String> stops);

    protected abstract CompletableFuture<ChatResult> asyncInternalGenerate(List<BaseMessage> messages, List<String> stop);

    public BaseMessage call(List<BaseMessage> messages, List<String> stop) {
        return internalGenerate(messages, stop).getGenerations().get(0).getMessage();
    }

    public String callAsLlm(String message, List<String> stops) {
        BaseMessage result = call(List.of(new HumanMessage(message)), stops);
        return result.getContent();
    }
}
