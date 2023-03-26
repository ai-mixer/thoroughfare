package org.thoroughfare.tool;

import org.thoroughfare.callback.BaseCallbackManager;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class BaseTool {
    private String name;
    private String description;
    private boolean returnDirect;
    private boolean verbose;
    private BaseCallbackManager callbackManager;

    public BaseTool(String name, String description, boolean returnDirect, boolean verbose, BaseCallbackManager callbackManager) {
        this.name = name;
        this.description = description;
        this.returnDirect = returnDirect;
        this.verbose = verbose;
        this.callbackManager = callbackManager != null ? callbackManager : getCallbackManager();
    }

    //TODO: unimplemented
    public static BaseCallbackManager getCallbackManager() {
        // Implement this method to return the appropriate callback manager
        return null;
    }

    public String call(String toolInput) {
        return run(toolInput, null);
    }

    public String run(String toolInput, Boolean verbose) {
//        String startColor = "green";
//        String color = "green";
//        if (verbose == null) {
//            verbose = this.verbose;
//        }
        callbackManager.onToolStart(Map.of("name", name, "description", description), toolInput, null);
        String observation;
        try {
            observation = run(toolInput);
        } catch (Exception e) {
            callbackManager.onToolError(e, null);
            throw e;
        }
        callbackManager.onToolEnd(observation, null);
        return observation;
    }

    public String asyncRun(String toolInput, Boolean verbose) {
        if (verbose == null) {
            verbose = this.verbose;
        }
        if (callbackManager.isAsync()) {
            CompletableFuture.runAsync(() -> {
                callbackManager.onToolStart(Map.of("name", name, "description", description), toolInput, null);
            });
        } else {
            callbackManager.onToolStart(Map.of("name", name, "description", description), toolInput, null);
        }
        String observation;
        try {
            observation = asyncRun(toolInput);
        } catch (Exception e) {
            if (callbackManager.isAsync()) {
                CompletableFuture.runAsync(() -> {
                    callbackManager.onToolError(e, null);
                });
            } else {
                callbackManager.onToolError(e, null);
            }
            throw new RuntimeException(e);
        }
        if (callbackManager.isAsync()) {
            if (callbackManager.isAsync()) {
                callbackManager.onToolEnd(observation, null);
            }
        } else {
            callbackManager.onToolEnd(observation, null);
        }
        return observation;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public abstract String run(String toolInput);

    /**
     * Note: Java does not have async/await, so you may need to use CompletableFuture or another approach for asynchronous execution
     *
     * @param toolInput
     * @return
     */
    public abstract String asyncRun(String toolInput);
}