package top.aimixer.modules.chains;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class LLMChain extends Chain {
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
    public List<Map<String, String>> apply(List<Map<String, Object>> inputList) {
        return super.apply(inputList);
    }

    @Override
    public String chainType() {
        return "llm_chain";
    }
}
