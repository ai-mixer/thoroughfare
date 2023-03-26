package org.aimixer.schema;

import java.util.Map;

/**
 * Agent's return value.
 */
public class AgentFinish {
    private final Map<String, Object> returnValues;
    private final String log;

    public AgentFinish(Map<String, Object> returnValues, String log) {
        this.returnValues = returnValues;
        this.log = log;
    }

    public Map<String, Object> getReturnValues() {
        return returnValues;
    }

    public String getLog() {
        return log;
    }
}
