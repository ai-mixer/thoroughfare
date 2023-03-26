package org.aimixer.prompt.template;

import org.aimixer.schema.PromptValue;
import org.aimixer.schema.StringPromptValue;

import java.util.Map;

public abstract class StringPromptTemplate extends BasePromptTemplate {

    /**
     * Create Chat Messages
     *
     * @param example
     * @return
     */
    public PromptValue formatPrompt(Map<String, String> example) {
        return new StringPromptValue(format(example));
    }
}

