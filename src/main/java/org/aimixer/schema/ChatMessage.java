package org.aimixer.schema;

import java.util.Map;

/**
 * Type of message that is spoken by the AI.
 */
public class ChatMessage extends BaseMessage {
    private String role;

    public ChatMessage(String role) {
        super(role);
        this.role = role;
    }

    public ChatMessage(String content, String role, Map<String, String> additionalExample) {
        super(content, additionalExample);
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getType() {
        // Type of the message, used for serialization.
        return "chat";
    }
}
