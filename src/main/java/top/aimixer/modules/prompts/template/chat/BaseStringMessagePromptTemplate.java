package top.aimixer.modules.prompts.template.chat;

import top.aimixer.modules.prompts.template.StringPromptTemplate;
import top.aimixer.modules.prompts.template.prompt.PromptTemplate;
import top.aimixer.schema.prompts.BaseMessage;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public abstract class BaseStringMessagePromptTemplate extends BaseMessagePromptTemplate {
    private StringPromptTemplate prompt;
    public BaseStringMessagePromptTemplate(StringPromptTemplate prompt) {
        this.prompt = prompt;
    }

    public static BaseMessagePromptTemplate fromTemplate(Class<? extends BaseStringMessagePromptTemplate> clazz,
                                                         String template, Map<String, Object> additionalKwArgs) {
        StringPromptTemplate prompt = PromptTemplate.fromTemplate(template);
        try {
            return clazz.getDeclaredConstructor(StringPromptTemplate.class, Map.class)
                    .newInstance(prompt, additionalKwArgs);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * To a BaseMessage
     */
    public abstract BaseMessage format(Map<String, String> additionalExample);

    public List<BaseMessage> formatMessages(Map<String, String> example) {
        return List.of(this.format(example));
    }

    public List<String> getInputVariables() {
        return this.prompt.getInputVariables();
    }

    public StringPromptTemplate getPrompt() {
        return prompt;
    }

}
