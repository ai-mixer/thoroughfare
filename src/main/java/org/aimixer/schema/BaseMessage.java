package org.aimixer.schema;

import java.util.Map;

/**
 * Message object.
 */
public abstract class BaseMessage {

    private String content;
    private Map<String, String> additionalKwArgs;

    public BaseMessage(String content) {
        this.content = content;
    }

    public BaseMessage(String content, Map<String, String> additionalKwArgs) {
        this.content = content;
        this.additionalKwArgs = additionalKwArgs;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, String> getAdditionalKwArgs() {
        return additionalKwArgs;
    }

    public void setAdditionalKwArgs(Map<String, String> additionalKwArgs) {
        this.additionalKwArgs = additionalKwArgs;
    }

    /**
     * Type of the message, used for serialization.
     */
    public abstract String getType();
}