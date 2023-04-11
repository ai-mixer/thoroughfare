package top.aimixer.modules.models.llms.openai;

import top.aimixer.modules.models.llms.BaseLLM;
import top.aimixer.schema.models.LLMResult;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class OpenAIChat extends BaseLLM {

    public OpenAIChat(Map<String, Object> requestParams) {
        super();
    }

    @Override
    public LLMResult generate(List<String> prompts, List<String> stop) {
        return null;
    }

    @Override
    public CompletableFuture<LLMResult> asyncGenerate(List<String> prompts, List<String> stop) {
        return null;
    }

    @Override
    public String llmType() {
        return null;
    }
}
