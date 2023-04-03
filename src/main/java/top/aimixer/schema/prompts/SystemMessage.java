package top.aimixer.schema.prompts;

import java.util.Map;

/**
 * Type of message that is a system message.
 */
public class SystemMessage extends BaseMessage {

    public SystemMessage(String content) {
        super(content);
    }

    public SystemMessage(String content, Map<String, String> additionalExample) {
        super(content, additionalExample);
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
