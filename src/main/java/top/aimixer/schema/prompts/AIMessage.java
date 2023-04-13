package top.aimixer.schema.prompts;

/**
 * Type of message that is spoken by the AI.
 */
public class AIMessage extends BaseMessage {

    public AIMessage(String content) {
        super(content);
    }

    /**
     * Type of the message, used for serialization.
     *
     * @return
     */
    @Override
    public String getType() {
        return "ai";
    }

}