package org.thoroughfare.prompt.template;

import org.thoroughfare.schema.PromptValue;
import org.thoroughfare.schema.StringPromptValue;

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

