package top.aimixer.schema.models;

import java.util.List;
import java.util.Map;

/**
 * Class that contains all relevant information for a Chat Result.
 */
public class ChatResult {

    /**
     * List of the things generated.
     */
    private List<ChatGeneration> generations;

    /**
     * For arbitrary LLM provider specific output.
     */
    private Map<String, Long> llmOutput;

    public ChatResult(List<ChatGeneration> generations) {
        this.generations = generations;
    }

    public List<ChatGeneration> getGenerations() {
        return generations;
    }

    public void setGenerations(List<ChatGeneration> generations) {
        this.generations = generations;
    }

    public Map<String, Long> getLlmOutput() {
        return llmOutput;
    }

    public void setLlmOutput(Map<String, Long> llmOutput) {
        this.llmOutput = llmOutput;
    }
}
