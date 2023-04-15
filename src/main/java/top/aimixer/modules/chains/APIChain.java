package top.aimixer.modules.chains;

import top.aimixer.modules.models.BaseLanguageModel;
import top.aimixer.modules.prompts.template.BasePromptTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class APIChain extends Chain {

    public static APIChain fromLlmAndApiDocs(BaseLanguageModel llm,
                                             String apiDocs,
                                             Map<String, String> headers,
                                             BasePromptTemplate apiUrlPrompt,
                                             BasePromptTemplate apiResponsePrompt,
                                             Object kwargs) {
        return null;
    }

    @Override
    public List<String> inputKeys() {
        return null;
    }

    @Override
    public List<String> outputKeys() {
        return null;
    }

    @Override
    public Map<String, String> call(Map<String, String> inputs) {
        return null;
    }

    @Override
    public CompletableFuture<Map<String, String>> acall(Map<String, String> inputs) {
        return super.acall(inputs);
    }

    @Override
    public String chainType() {
        return "api_chain";
    }
}
