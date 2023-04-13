package top.aimixer.schema.prompts;

/**
 * Type of message that is spoken by the human.
 */
public class HumanMessage extends BaseMessage {

    public HumanMessage(String content) {
        super(content);
    }

    /**
     * Type of the message, used for serialization.
     *
     * @return
     */
    @Override
    public String getType() {
        return "human";
    }
}

