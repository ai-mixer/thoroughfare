package top.aimixer.prompt.template.chat;

import top.aimixer.prompt.template.StringPromptTemplate;
import top.aimixer.schema.BaseMessage;
import top.aimixer.schema.ChatMessage;

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