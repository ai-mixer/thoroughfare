package models.chat.openai;

import top.aimixer.modules.models.chat.openai.ChatOpenAi;
import top.aimixer.schema.models.LLMResult;
import top.aimixer.schema.prompts.BaseMessage;
import top.aimixer.schema.prompts.HumanMessage;
import top.aimixer.schema.prompts.SystemMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatOpenAiTest {

    public static void main(String[] args) {
        Map<String, Object> values = new HashMap<>();
        values.put("openai_api_key", "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");
//        List<BaseMessage> messageList = new ArrayList<>() {{
//            add(new HumanMessage("Translate this sentence from English to French. I love programming."));
//        }};
//        List<BaseMessage> messageList = new ArrayList<>() {{
//            add(new SystemMessage("You are a helpful assistant that translates English to French."));
//            add(new HumanMessage("Translate this sentence from English to French. I love programming."));
//        }};
//        BaseMessage baseMessage = new ChatOpenAi(values).call(messageList, null);
        List<List<BaseMessage>> batchMessages = new ArrayList<>();
        batchMessages.add(new ArrayList<BaseMessage>() {{
            add(new SystemMessage("You are a helpful assistant that translates English to French."));
            add(new HumanMessage("Translate this sentence from English to French. I love programming."));
        }});
        batchMessages.add(new ArrayList<BaseMessage>() {{
            add(new SystemMessage("You are a helpful assistant that translates English to French."));
            add(new HumanMessage("Translate this sentence from English to French. I love artificial intelligence."));
        }});
        LLMResult llmResult = new ChatOpenAi(values).generate(batchMessages, null);
        System.out.println(llmResult);
    }
}
