package top.aimixer.schema.prompts;

import java.util.List;

public abstract class PromptValue {

    /**
     * Return prompt as string.
     */
    public abstract String toString();

    /**
     * Return prompt as messages.
     */
    public abstract List<BaseMessage> toMessages();

}

