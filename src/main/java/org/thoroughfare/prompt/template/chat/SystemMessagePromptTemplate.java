package org.thoroughfare.prompt.template.chat;

import org.thoroughfare.prompt.template.StringPromptTemplate;
import org.thoroughfare.schema.BaseMessage;
import org.thoroughfare.schema.SystemMessage;

import java.util.Map;

public class SystemMessagePromptTemplate extends BaseStringMessagePromptTemplate {

    public SystemMessagePromptTemplate(StringPromptTemplate prompt, Map<String, String> additionalExample) {
        super(prompt, additionalExample);
    }

    public BaseMessage format(Map<String, String> example) {
        String text = getPrompt().format(example);
        return new SystemMessage(text, getAdditionalExample());
    }
}
