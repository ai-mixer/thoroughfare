package top.aimixer.modules.chains.llmmath;

import top.aimixer.modules.chains.Chain;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class LLMMathChain extends Chain {
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
        return "llm_math_chain";
    }
}
