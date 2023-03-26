package top.aimixer.schema;

import java.util.ArrayList;
import java.util.List;

public class StringPromptValue extends PromptValue {
    private String text;

    public StringPromptValue(String text) {
        this.text = text;
    }

    /**
     * Return prompt as string
     *
     * @return
     */
    @Override
    public String toString() {
        return this.text;
    }

    /**
     * Return prompt as messages
     *
     * @return
     */
    @Override
    public List<BaseMessage> toMessages() {
        List<BaseMessage> messages = new ArrayList<>();
        messages.add(new HumanMessage(this.text));
        return messages;
    }
}

