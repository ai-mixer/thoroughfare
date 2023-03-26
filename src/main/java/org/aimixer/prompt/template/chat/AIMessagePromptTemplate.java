package org.aimixer.prompt.template.chat;

import org.aimixer.prompt.template.StringPromptTemplate;
import org.aimixer.schema.AIMessage;
import org.aimixer.schema.BaseMessage;

import java.util.Map;

public class AIMessagePromptTemplate extends BaseStringMessagePromptTemplate {

    public AIMessagePromptTemplate(StringPromptTemplate prompt, Map<String, String> additionalExample) {
        super(prompt, additionalExample);
    }

    public BaseMessage format(Map<String, String> additionalExample) {
        String text = getPrompt().format(additionalExample);
        return new AIMessage(text, getAdditionalExample());
    }
}
