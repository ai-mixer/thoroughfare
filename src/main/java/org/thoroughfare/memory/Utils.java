package org.thoroughfare.memory;

import org.thoroughfare.schema.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Utils {

    /**
     * Get buffer string of messages.
     *
     * @param messages
     * @return
     */
    public static String getBufferString(List<BaseMessage> messages) {
        String humanPrefix = "Human";
        String aiPrefix = "AI";
        List<String> stringMessages = new ArrayList<>();
        for (BaseMessage m : messages) {
            String role;
            if (m instanceof HumanMessage) {
                role = humanPrefix;
            } else if (m instanceof AIMessage) {
                role = aiPrefix;
            } else if (m instanceof SystemMessage) {
                role = "System";
            } else if (m instanceof ChatMessage) {
                role = ((ChatMessage) m).getRole();
            } else {
                throw new IllegalArgumentException("Got unsupported message type: " + m);
            }
            stringMessages.add(role + ": " + m.getContent());
        }
        return String.join("\n", stringMessages);
    }

    /**
     * "stop" is a special key that can be passed as input but is not used to
     * format the prompt.
     *
     * @param inputs
     * @param memoryVariables
     * @return
     */
    public static String getPromptInputKey(Map<String, Object> inputs, List<String> memoryVariables) {
        Set<String> promptInputKeys = inputs.keySet().stream()
                .filter(key -> !memoryVariables.contains(key) && !"stop".equals(key))
                .collect(Collectors.toSet());

        if (promptInputKeys.size() != 1) {
            throw new IllegalArgumentException("One input key expected got " + promptInputKeys);
        }

        return promptInputKeys.iterator().next();
    }
}
