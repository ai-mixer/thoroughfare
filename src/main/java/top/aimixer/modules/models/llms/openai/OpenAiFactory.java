package top.aimixer.modules.models.llms.openai;

import com.knuddels.jtokkit.api.ModelType;

import java.util.Map;

public class OpenAiFactory {

    public static OpenAi openAi(ModelType modelType, Map<String, Object> requestParams) {
        if (ModelType.GPT_3_5_TURBO.getName().equals(modelType.getName())) {
            return new OpenAiChat(requestParams);
        }
        return new BaseOpenAi(requestParams);
    }
}
