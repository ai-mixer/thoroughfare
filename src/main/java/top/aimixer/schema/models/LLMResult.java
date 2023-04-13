package top.aimixer.schema.models;

import top.aimixer.schema.Generation;

import java.util.List;
import java.util.Map;

/**
 * Class that contains all relevant information for an LLM Result.
 */
public class LLMResult {

    /**
     * List of the things generated. This is List<List[]> because
     * each input could have multiple generations.
     */
    private List<List<? extends Generation>> generations;

    /**
     * For arbitrary LLM provider specific output.
     */
    private Map<String, Long> llmOutput;


    public LLMResult(List<List<? extends Generation>> generations) {
        this.generations = generations;
    }

    public LLMResult(List<List<? extends Generation>> generations, Map<String, Long> llmOutput) {
        this.generations = generations;
        this.llmOutput = llmOutput;
    }

    public List<List<? extends Generation>> getGenerations() {
        return generations;
    }

    public void setGenerations(List<List<? extends Generation>> generations) {
        this.generations = generations;
    }

    public Map<String, Long> getLlmOutput() {
        return llmOutput;
    }

    public void setLlmOutput(Map<String, Long> llmOutput) {
        this.llmOutput = llmOutput;
    }

    @Override
    public String toString() {
        return "LLMResult{" +
                "generations=" + generations +
                ", llmOutput=" + llmOutput +
                '}';
    }
}

