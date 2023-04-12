package llm.openai;

import com.knuddels.jtokkit.api.ModelType;
import top.aimixer.modules.models.llms.openai.OpenAiFactory;

import java.util.HashMap;
import java.util.Map;

public class OpenAITest {

    public static void main(String[] args) {
        Map<String, Object> instanceParams = new HashMap<>();
        instanceParams.put("openai_api_key", "sk-xxx");
        instanceParams.put("streaming", true);
        String result = OpenAiFactory.openAi(ModelType.TEXT_DAVINCI_003, instanceParams).call("Tell me a joke", null);
//        String result = OpenAiFactory.openAi(ModelType.GPT_3_5_TURBO, instanceParams).call("Tell me a joke", null);
        System.out.printf(result);
    }
}
