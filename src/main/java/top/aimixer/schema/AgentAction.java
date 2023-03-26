package top.aimixer.schema;

/**
 * Agent's action to take.
 */
public class AgentAction {
    private final String tool;
    private final String toolInput;
    private final String log;

    public AgentAction(String tool, String toolInput, String log) {
        this.tool = tool;
        this.toolInput = toolInput;
        this.log = log;
    }

    public String getTool() {
        return tool;
    }

    public String getToolInput() {
        return toolInput;
    }

    public String getLog() {
        return log;
    }
}
