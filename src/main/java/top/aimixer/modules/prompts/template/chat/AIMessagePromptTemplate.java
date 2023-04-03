package top.aimixer.modules.prompts.template.chat;

import top.aimixer.modules.prompts.template.StringPromptTemplate;
import top.aimixer.schema.prompts.AIMessage;
import top.aimixer.schema.prompts.BaseMessage;

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
