package top.aimixer.schema.prompts;

/**
 * Type of message that is a system message.
 */
public class SystemMessage extends BaseMessage {

    public SystemMessage(String content) {
        super(content);
    }

    /**
     * Type of the message, used for serialization.
     *
     * @return
     */
    @Override
    public String getType() {
        return "system";
    }
}
