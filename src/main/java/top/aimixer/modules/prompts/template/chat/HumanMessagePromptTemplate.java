package top.aimixer.modules.prompts.template.chat;

import top.aimixer.modules.prompts.template.StringPromptTemplate;
import top.aimixer.schema.prompts.BaseMessage;
import top.aimixer.schema.prompts.HumanMessage;

import java.util.Map;

public class HumanMessagePromptTemplate extends BaseStringMessagePromptTemplate {

    public HumanMessagePromptTemplate(StringPromptTemplate prompt) {
        super(prompt);
    }

    public BaseMessage format(Map<String, String> additionalExample) {
        String text = getPrompt().format(additionalExample);
        return new HumanMessage(text);
    }
}
