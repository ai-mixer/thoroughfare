package org.thoroughfare.schema;

import java.util.Map;

/**
 * Type of message that is spoken by the AI.
 */
public class AIMessage extends BaseMessage {

    public AIMessage(String content) {
        super(content);
    }

    public AIMessage(String content, Map<String, String> additionalExample) {
        super(content, additionalExample);
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