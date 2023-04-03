package top.aimixer.modules.prompts.template.chat;

import top.aimixer.modules.prompts.template.StringPromptTemplate;
import top.aimixer.schema.prompts.BaseMessage;
import top.aimixer.schema.prompts.ChatMessage;

import java.util.Map;

public class ChatMessagePromptTemplate extends BaseStringMessagePromptTemplate {
    public String role;

    public ChatMessagePromptTemplate(String role, StringPromptTemplate prompt, Map<String, String> additionalExample) {
        super(prompt, additionalExample);
        this.role = role;
    }

    @Override
    public BaseMessage format(Map<String, String> additionalExample) {
        String text = getPrompt().format(additionalExample);
        return new ChatMessage(text, this.role, getAdditionalExample());
    }
}