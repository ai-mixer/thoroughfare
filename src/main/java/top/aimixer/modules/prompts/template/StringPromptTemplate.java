package top.aimixer.modules.prompts.template;

import top.aimixer.schema.prompts.PromptValue;
import top.aimixer.schema.prompts.StringPromptValue;

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

