package org.aimixer.schema;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Class that contains all relevant information for an LLM Result.
 */
public class LLMResult {

    /**
     * List of the things generated. This is List<List[]> because
     * each input could have multiple generations.
     */
    private List<List<Generation>> generations;

    private Optional<Map<String, Object>> llmOutput;
    // For arbitrary LLM provider specific output.

    public LLMResult(List<List<Generation>> generations, Optional<Map<String, Object>> llmOutput) {
        this.generations = generations;
        this.llmOutput = llmOutput;
    }

    public List<List<Generation>> getGenerations() {
        return generations;
    }

    public void setGenerations(List<List<Generation>> generations) {
        this.generations = generations;
    }

    public Optional<Map<String, Object>> getLlmOutput() {
        return llmOutput;
    }

    public void setLlmOutput(Optional<Map<String, Object>> llmOutput) {
        this.llmOutput = llmOutput;
    }
}

