package top.aimixer.schema;

import java.util.Map;

/**
 * Type of message that is spoken by the human.
 */
public class HumanMessage extends BaseMessage {

    public HumanMessage(String content) {
        super(content);
    }

    public HumanMessage(String content, Map<String, String> additionalExample) {
        super(content, additionalExample);
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

