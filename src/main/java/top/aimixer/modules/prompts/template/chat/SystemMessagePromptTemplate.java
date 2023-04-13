package top.aimixer.modules.prompts.template.chat;

import top.aimixer.modules.prompts.template.StringPromptTemplate;
import top.aimixer.schema.prompts.BaseMessage;
import top.aimixer.schema.prompts.SystemMessage;

import java.util.Map;

public class SystemMessagePromptTemplate extends BaseStringMessagePromptTemplate {

    public SystemMessagePromptTemplate(StringPromptTemplate prompt) {
        super(prompt);
    }

    public BaseMessage format(Map<String, String> example) {
        String text = getPrompt().format(example);
        return new SystemMessage(text);
    }
}
