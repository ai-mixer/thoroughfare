package top.aimixer.modules.models.llms.openai.completion;

import com.theokanning.openai.OpenAiResponse;

import java.util.Map;

public class APIRequester {

    public APIRequester(String apiKey, String apiBase, String apiType, String apiVersion, String organization) {
    }

    public OpenAiResponse request(String post, String url, Map<String, Object> requestParams,
                                  Map<String, String> headers, Boolean stream, String requestId, Integer requestTimeout) {
        return null;
    }
}
