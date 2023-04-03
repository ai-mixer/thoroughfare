package top.aimixer.callback;

import top.aimixer.schema.AgentAction;
import top.aimixer.schema.AgentFinish;
import top.aimixer.schema.models.LLMResult;

import java.util.List;
import java.util.Map;

/**
 * Base callback handler that can be used to handle callbacks from langchain.
 */
public interface BaseCallbackHandler {

    default boolean alwaysVerbose() {
        // Whether to call verbose callbacks even if verbose is False.
        return false;
    }

    default boolean ignoreLLM() {
        // Whether to ignore LLM callbacks.
        return false;
    }

    default boolean ignoreChain() {
        // Whether to ignore chain callbacks.
        return false;
    }

    default boolean ignoreAgent() {
        // Whether to ignore agent callbacks.
        return false;
    }

    /**
     * Run when LLM starts running.
     *
     * @param serialized
     * @param prompts
     */
    void onLLMStart(Map<String, Object> serialized, List<String> prompts);

    //

    void onLLMNewToken(String token, Map<String, Object> kwargs);

    // Run on new LLM token. Only available when streaming is enabled.

    void onLLMEnd(LLMResult response);

    // Run when LLM ends running.

    void onLLMError(Exception error);

    // Run when LLM errors.

    void onChainStart(Map<String, Object> serialized, Map<String, Object> inputs, Map<String, Object> kwargs);

    // Run when chain starts running.

    void onChainEnd(Map<String, Object> outputs, Map<String, Object> kwargs);

    // Run when chain ends running.

    void onChainError(Exception error, Map<String, Object> kwargs);

    // Run when chain errors.

    void onToolStart(Map<String, Object> serialized, String inputStr, Map<String, Object> kwargs);

    // Run when tool starts running.

    void onToolEnd(String output, Map<String, Object> kwargs);

    // Run when tool ends running.

    void onToolError(Exception error, Map<String, Object> kwargs);

    // Run when tool errors.

    void onText(String text, Map<String, Object> kwargs);

    // Run on arbitrary text.

    void onAgentAction(AgentAction action, Map<String, Object> kwargs);

    // Run on agent action.

    void onAgentFinish(AgentFinish finish, Map<String, Object> kwargs);
}

