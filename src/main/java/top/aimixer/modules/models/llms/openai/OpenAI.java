package top.aimixer.modules.models.llms.openai;

import com.knuddels.jtokkit.api.ModelType;
import com.theokanning.openai.completion.CompletionRequest;
import top.aimixer.modules.models.llms.BaseLLM;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic OpenAI class that uses model name.
 */
public class OpenAI extends BaseOpenAI {

    public OpenAI(Map<String, Object> instanceParams) {
        super(instanceParams);
    }

    @Override
    protected Map<String, Object> getInvocationParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("model", modelName);
        params.putAll(super.getInvocationParams());
        return params;
    }
}
