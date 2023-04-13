package top.aimixer.schema.prompts;

/**
 * Type of message that is spoken by the AI.
 */
public class ChatMessage extends BaseMessage {
    private String role;

    public ChatMessage(String content) {
        super(content);
    }

    public ChatMessage(String content, String role) {
        super(content);
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

    @Override
    public String toString() {
        return "ChatMessage{" +
                "role='" + role + '\'' +
                ", content='" + this.getContent() + '\'' +
                '}';
    }
}
