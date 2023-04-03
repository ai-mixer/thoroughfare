package template.chat;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import top.aimixer.callback.CallbackManager;
import top.aimixer.modules.prompts.template.chat.HumanMessagePromptTemplate;
import top.aimixer.modules.prompts.template.chat.SystemMessagePromptTemplate;
import top.aimixer.tools.BaseTool;
import top.aimixer.tools.Tool;

import java.util.List;

public class HumanMessagePromptTemplateTest {

    public static void main(String[] args) {
        String prefix = "Answer the following questions as best you can. You have access to the following tools:";
        String formatInstructions = "The way you use the tools is by specifying a json blob.\n" +
                "Specifically, this json should have a `action` key (with the name of the tool to use) and a `action_input` key (with the input to the tool going here).\n" +
                "\n" +
                "The only values that should be in the \"action\" field are: {tool_names}\n" +
                "\n" +
                "The $JSON_BLOB should only contain a SINGLE action, do NOT return a list of multiple actions. Here is an example of a valid $JSON_BLOB:\n" +
                "\n" +
                "```\n" +
                "{{{{\n" +
                "  \"action\": $TOOL_NAME,\n" +
                "  \"action_input\": $INPUT\n" +
                "}}}}\n" +
                "```\n" +
                "\n" +
                "ALWAYS use the following format:\n" +
                "\n" +
                "Question: the input question you must answer\n" +
                "Thought: you should always think about what to do\n" +
                "Action:\n" +
                "```\n" +
                "$JSON_BLOB\n" +
                "```\n" +
                "Observation: the result of the action\n" +
                "... (this Thought/Action/Observation can repeat N times)\n" +
                "Thought: I now know the final answer\n" +
                "Final Answer: the final answer to the original input question";
        String suffix = "Begin! Reminder to always use the exact characters `Final Answer` when responding.";

        List<BaseTool> tools = List.of(new Tool("chatgpt", "chatgpt", new CallbackManager()));
        String toolStrings = "";
        for (BaseTool tool : tools) {
            toolStrings = StringUtils.join(
                    new String[]{"{" + tool.getName() + ": {" + tool.getDescription() + "}"}, "\n\n");
        }
        String template = StringUtils.join(
                new String[]{prefix, toolStrings, formatInstructions, suffix}, "\n\n");
        System.out.println(new Gson().toJson(
                SystemMessagePromptTemplate.fromTemplate(SystemMessagePromptTemplate.class,
                        template, null)));
        System.out.println(new Gson().toJson(
                HumanMessagePromptTemplate.fromTemplate(HumanMessagePromptTemplate.class,
                        "{input}\n\n{agent_scratchpad}", null)));
    }
}
