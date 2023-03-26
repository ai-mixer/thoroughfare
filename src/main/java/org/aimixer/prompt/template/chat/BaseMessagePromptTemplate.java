package org.aimixer.prompt.template.chat;

import org.aimixer.schema.BaseMessage;

import java.util.List;
import java.util.Map;

public abstract class BaseMessagePromptTemplate {

    /**
     * To messages.
     *
     * @param messageMap
     * @return
     */
    public abstract List<BaseMessage> formatMessages(Map<String, String> messageMap);

    /**
     * Input variables for this prompt template.
     *
     * @return
     */
    public abstract List<String> getInputVariables();
}
