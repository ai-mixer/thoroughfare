package org.thoroughfare.schema;

import org.thoroughfare.memory.Utils;

import java.util.List;

public class ChatPromptValue extends PromptValue {
    private List<BaseMessage> messages;

    public ChatPromptValue(List<BaseMessage> messages) {
        this.messages = messages;
    }

    /**
     * Return prompt as string
     *
     * @return
     */
    public String toString() {
        return Utils.getBufferString(this.messages);
    }

    /**
     * Return prompt as messages
     *
     * @return
     */
    public List<BaseMessage> toMessages() {
        return this.messages;
    }
}
