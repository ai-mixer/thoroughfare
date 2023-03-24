package org.thoroughfare.prompt.template.chat;

import org.thoroughfare.prompt.template.StringPromptTemplate;
import org.thoroughfare.schema.AIMessage;
import org.thoroughfare.schema.BaseMessage;

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
