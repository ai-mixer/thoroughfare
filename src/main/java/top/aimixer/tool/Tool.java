package top.aimixer.tool;

import top.aimixer.callback.BaseCallbackManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class Tool extends BaseTool {
    /**
     * Tool that takes in function or coroutine directly.
     */
    public String description = "";
    public Function<String, String> func;
    public Function<String, CompletableFuture<String>> coroutine = null;

    /**
     * Initialize tool.
     */
    public Tool(String name, String description, BaseCallbackManager callbackManager) {
        super(name, description, true, true, callbackManager);
    }

    /**
     * Use the tool.
     */
    public String run(String tool_input) {
        return func.apply(tool_input);
    }

    /**
     * Use the tool asynchronously.
     */
    public String asyncRun(String tool_input) {
        if (coroutine != null) {
            try {
                return coroutine.apply(tool_input).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        throw new UnsupportedOperationException("Tool does not support async");
    }

}
