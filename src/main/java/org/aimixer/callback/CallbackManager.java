package org.aimixer.callback;

import org.aimixer.schema.AgentAction;
import org.aimixer.schema.AgentFinish;
import org.aimixer.schema.LLMResult;

import java.util.List;
import java.util.Map;

public class CallbackManager extends BaseCallbackManager {
    @Override
    public void onLLMStart(Map<String, Object> serialized, List<String> prompts, Map<String, Object> kwargs) {

    }

    @Override
    public void onLLMNewToken(String token, Map<String, Object> kwargs) {

    }

    @Override
    public void onLLMEnd(LLMResult response, Map<String, Object> kwargs) {

    }

    @Override
    public void onLLMError(Exception error, Map<String, Object> kwargs) {

    }

    @Override
    public void onChainStart(Map<String, Object> serialized, Map<String, Object> inputs, Map<String, Object> kwargs) {

    }

    @Override
    public void onChainEnd(Map<String, Object> outputs, Map<String, Object> kwargs) {

    }

    @Override
    public void onChainError(Exception error, Map<String, Object> kwargs) {

    }

    @Override
    public void onToolStart(Map<String, Object> serialized, String inputStr, Map<String, Object> kwargs) {

    }

    @Override
    public void onToolEnd(String output, Map<String, Object> kwargs) {

    }

    @Override
    public void onToolError(Exception error, Map<String, Object> kwargs) {

    }

    @Override
    public void onText(String text, Map<String, Object> kwargs) {

    }

    @Override
    public void onAgentAction(AgentAction action, Map<String, Object> kwargs) {

    }

    @Override
    public void onAgentFinish(AgentFinish finish, Map<String, Object> kwargs) {

    }

    @Override
    public void addHandler(BaseCallbackHandler callback) {

    }

    @Override
    public void removeHandler(BaseCallbackHandler handler) {

    }

    @Override
    public void setHandlers(List<BaseCallbackHandler> handlers) {

    }
}
