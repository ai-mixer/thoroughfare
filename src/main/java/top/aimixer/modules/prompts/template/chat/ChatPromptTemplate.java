package top.aimixer.modules.prompts.template.chat;

import top.aimixer.modules.prompts.template.prompt.PromptTemplate;
import top.aimixer.modules.prompts.template.BasePromptTemplate;
import top.aimixer.schema.Tuple;
import top.aimixer.schema.prompts.BaseMessage;
import top.aimixer.schema.prompts.ChatPromptValue;
import top.aimixer.schema.prompts.PromptValue;

import java.util.*;

public class ChatPromptTemplate extends BasePromptTemplate {
    /**
     * Union of BaseMessagePromptTemplate and BaseMessage
     */
    private List<Object> messages;

    public ChatPromptTemplate(List<String> inputVariables, List<Object> messages) {
        this.inputVariables = inputVariables;
        this.messages = messages;
    }

    public static ChatPromptTemplate fromRoleStrings(List<Tuple<String, String>> stringMessages) {
        List<Object> messages = new ArrayList<>();
        for (Tuple<String, String> tuple : stringMessages) {
            String role = tuple.getFirst();
            String template = tuple.getSecond();
            messages.add(new ChatMessagePromptTemplate(role, PromptTemplate.fromTemplate(template),
                    null));
        }
        return fromMessages(messages);
    }

    public static ChatPromptTemplate fromStrings(List<Tuple<Class<? extends BaseMessagePromptTemplate>, String>> stringMessages) {
        List<Object> messages = new ArrayList<>();
        for (Tuple<Class<? extends BaseMessagePromptTemplate>, String> tuple : stringMessages) {
            Class<? extends BaseMessagePromptTemplate> role = tuple.getFirst();
            String template = tuple.getSecond();
            try {
                messages.add(role.getDeclaredConstructor(String.class).newInstance(PromptTemplate.fromTemplate(template)));
            } catch (Exception e) {
                throw new RuntimeException("Error creating instance of BaseMessagePromptTemplate", e);
            }
        }
        return fromMessages(messages);
    }

    public static ChatPromptTemplate fromMessages(List<Object> messages) {
        Set<String> inputVars = new HashSet<>();
        for (Object message : messages) {
            if (message instanceof BaseMessagePromptTemplate) {
                inputVars.addAll(((BaseMessagePromptTemplate) message).getInputVariables());
            }
        }
        return new ChatPromptTemplate(new ArrayList<>(inputVars), messages);
    }

    @Override
    public String format(Map<String, String> example) {
        return formatPrompt(example).toString();
    }

    @Override
    public PromptValue formatPrompt(Map<String, String> example) {
        example = mergePartialAndUserVariables(example);
        List<BaseMessage> result = new ArrayList<>();
        for (Object messageTemplate : messages) {
            if (messageTemplate instanceof BaseMessage) {
                result.add(BaseMessage.class.cast(messageTemplate));
            } else if (messageTemplate instanceof BaseMessagePromptTemplate) {
                Map<String, String> messageMap = new HashMap<>();
                for (String key : ((BaseMessagePromptTemplate) messageTemplate).getInputVariables()) {
                    messageMap.put(key, example.get(key));
                }
                List<BaseMessage> message = ((BaseMessagePromptTemplate) messageTemplate).formatMessages(messageMap);
                result.addAll(message);
            } else {
                throw new IllegalArgumentException("Unexpected input: " + messageTemplate);
            }
        }
        return new ChatPromptValue(result);
    }

    @Override
    public BasePromptTemplate partial(Map<String, String> kwArgs) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getPromptType() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void save(String filePath) {
        throw new UnsupportedOperationException("Not implemented");
    }

}

