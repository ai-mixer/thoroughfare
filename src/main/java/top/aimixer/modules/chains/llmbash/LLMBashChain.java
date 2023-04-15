package top.aimixer.modules.chains.llmbash;

import top.aimixer.modules.chains.Chain;

import java.util.List;
import java.util.Map;

public class LLMBashChain extends Chain {
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
    public String chainType() {
        return "llm_bash_chain";
    }
}
