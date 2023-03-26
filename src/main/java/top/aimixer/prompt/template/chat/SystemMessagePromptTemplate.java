package top.aimixer.prompt.template.chat;

import top.aimixer.prompt.template.StringPromptTemplate;
import top.aimixer.schema.BaseMessage;
import top.aimixer.schema.SystemMessage;

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
