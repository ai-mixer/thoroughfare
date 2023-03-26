package org.thoroughfare.prompt.template.chat;

import org.thoroughfare.prompt.template.StringPromptTemplate;
import org.thoroughfare.schema.BaseMessage;
import org.thoroughfare.schema.HumanMessage;

import java.util.Map;

public class HumanMessagePromptTemplate extends BaseStringMessagePromptTemplate {

    public HumanMessagePromptTemplate(StringPromptTemplate prompt, Map<String, String> additionalExample) {
        super(prompt, additionalExample);
    }

    public BaseMessage format(Map<String, String> additionalExample) {
        String text = getPrompt().format(additionalExample);
        return new HumanMessage(text, getAdditionalExample());
    }
}
