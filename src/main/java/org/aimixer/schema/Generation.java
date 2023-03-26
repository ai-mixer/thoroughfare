package org.aimixer.schema;

import java.util.Map;
import java.util.Optional;

/**
 * Output of a single generation.
 */
public class Generation {
    /**
     * Generated text output.
     */
    private final String text;
    private final Optional<Map<String, Object>> generationInfo;

    public Generation(String text, Optional<Map<String, Object>> generationInfo) {
        this.text = text;
        this.generationInfo = generationInfo;
    }

    public String getText() {
        return text;
    }

    /**
     * Raw generation info response from the provider
     * May include things like reason for finishing (e.g. in OpenAI)
     * TODO: add log probs
     *
     * @return
     */
    public Optional<Map<String, Object>> getGenerationInfo() {
        return generationInfo;
    }
}
