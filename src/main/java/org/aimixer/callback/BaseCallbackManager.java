package org.aimixer.callback;

import java.util.List;

/**
 * Base callback manager that can be used to handle callbacks from LangChain.
 */
public abstract class BaseCallbackManager implements BaseCallbackHandler {

    /**
     * Whether the callback manager is async.
     *
     * @return
     */
    public boolean isAsync() {
        return false;
    }

    public abstract void addHandler(BaseCallbackHandler callback);
    // Add a handler to the callback manager.

    public abstract void removeHandler(BaseCallbackHandler handler);
    // Remove a handler from the callback manager.

    public void setHandler(BaseCallbackHandler handler) {
        // Set handler as the only handler on the callback manager.
        setHandlers(List.of(handler));
    }

    public abstract void setHandlers(List<BaseCallbackHandler> handlers);
    // Set handlers as the only handlers on the callback manager.
}

