package top.aimixer.prompt.template.chat;

import top.aimixer.prompt.template.StringPromptTemplate;
import top.aimixer.schema.BaseMessage;
import top.aimixer.schema.HumanMessage;

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
