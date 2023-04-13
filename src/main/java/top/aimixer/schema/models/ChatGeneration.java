package top.aimixer.schema.models;

import top.aimixer.schema.Generation;
import top.aimixer.schema.prompts.BaseMessage;

import java.util.Map;

public class ChatGeneration extends Generation {

    private BaseMessage message;

    public ChatGeneration(BaseMessage message) {
        super("");
        this.message = message;
    }

    public ChatGeneration(String text, BaseMessage message) {
        super(text);
        this.message = message;
    }

    public Map<String, Object> setText(Map<String, Object> values) {
        values.put("text", ((BaseMessage) values.get("message")).getContent());
        return values;
    }

    public BaseMessage getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ChatGeneration{" +
                "message=" + message +
                '}';
    }
}
