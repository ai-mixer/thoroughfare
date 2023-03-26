package top.aimixer.prompt.template.chat;

import top.aimixer.schema.BaseMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Prompt template that assumes variable is already list of messages.
 */
public class MessagesPlaceholder extends BaseMessagePromptTemplate {

    private String variableName;

    public MessagesPlaceholder(String variableName) {
        this.variableName = variableName;
    }

    /**
     * To a BaseMessage.
     *
     * @param messageMap
     * @return
     */
    public List<BaseMessage> formatMessages(Map<String, String> messageMap) {
        Object value = messageMap.get(variableName);
        if (!(value instanceof List)) {
            throw new IllegalArgumentException(
                    "variable " + variableName + " should be a list of base messages, got " + value
            );
        }

        List<BaseMessage> messages = new ArrayList<>();
        for (Object v : (List) value) {
            if (!(v instanceof BaseMessage)) {
                throw new IllegalArgumentException(
                        "variable " + variableName + " should be a list of base messages, got " + value
                );
            }
            messages.add((BaseMessage) v);
        }

        return messages;
    }

    /**
     * Input variables for this prompt template.
     */
    public List<String> getInputVariables() {
        List<String> inputVariables = new ArrayList<>();
        inputVariables.add(variableName);
        return inputVariables;
    }
}