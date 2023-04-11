package llm.openai;

import top.aimixer.modules.models.llms.openai.OpenAI;

import java.util.HashMap;
import java.util.Map;

public class OpenAITest {

    public static void main(String[] args) {
        Map<String, Object> instanceParams = new HashMap<>();
        instanceParams.put("openai_api_key", "sk-xxxxx");
        String result = new OpenAI(instanceParams).call("Tell me a joke", null);
        System.out.printf(result);
    }
}
