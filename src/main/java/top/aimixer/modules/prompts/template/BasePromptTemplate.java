package top.aimixer.modules.prompts.template;

import java.util.*;
import java.nio.file.*;
import java.io.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import top.aimixer.parser.BaseOutputParser;
import top.aimixer.schema.prompts.PromptValue;
import org.yaml.snakeyaml.Yaml;

/**
 * Prompt template classes.
 * Base prompt should expose the format method, returning a prompt.
 */
public abstract class BasePromptTemplate {

    /**
     * A list of the names of the variables the prompt template expects.
     */
    protected List<String> inputVariables;

    /**
     * How to parse the output of calling an LLM on this formatted prompt.
     */
    protected BaseOutputParser outputParser;

    /**
     * Return map representation of prompt.
     */
    protected Map<String, String> partialVariables;

    /**
     * Validate variable names do not include restricted names.
     *
     * @param instance
     */
    //TODO: [check]  https://langchain.readthedocs.io/en/latest/_modules/langchain/prompts/base.html#BasePromptTemplate.dict
    public static void validateVariableNames(BasePromptTemplate instance) {
        if (instance.inputVariables.contains("stop")) {
            throw new IllegalArgumentException(
                    "Cannot have an input variable named 'stop', as it is used internally, please rename."
            );
        }
        if (instance.partialVariables.containsKey("stop")) {
            throw new IllegalArgumentException(
                    "Cannot have a partial variable named 'stop', as it is used internally, please rename."
            );
        }

        Set<String> overall = new HashSet<>(instance.inputVariables);
        overall.retainAll(instance.partialVariables.keySet());
        if (!overall.isEmpty()) {
            throw new IllegalArgumentException(
                    "Found overlapping input and partial variables: " + overall
            );
        }
    }

    /**
     * Return a partial of the prompt template.
     *
     * @param example
     * @return
     * @throws CloneNotSupportedException
     */
    //TODO: [check]  https://langchain.readthedocs.io/en/latest/_modules/langchain/prompts/base.html#BasePromptTemplate.dict
    public BasePromptTemplate partial(Map<String, String> example) throws CloneNotSupportedException {
        BasePromptTemplate newPrompt = (BasePromptTemplate) this.clone();
        newPrompt.inputVariables.removeAll(example.keySet());
        newPrompt.partialVariables.putAll(example);
        return newPrompt;
    }

    protected Map<String, String> mergePartialAndUserVariables(Map<String, String> example) {
        Map<String, String> partialExample = new HashMap<>();
        if (example != null) {
            for (Map.Entry<String, String> entry : this.partialVariables.entrySet()) {
                partialExample.put(entry.getKey(), entry.getValue() instanceof String ?
                        entry.getValue() : entry.getValue());
            }
            partialExample.putAll(example);
        }
        return partialExample;
    }

    /**
     * Return map representation of prompt.
     *
     * @return
     */
    public Map<String, Object> toMap() {
        Map<String, Object> promptMap = new HashMap<>();
        promptMap.put("inputVariables", this.inputVariables);
        promptMap.put("outputParser", this.outputParser);
        promptMap.put("partialVariables", this.partialVariables);
        promptMap.put("_type", this.getPromptType());
        return promptMap;
    }

    /**
     * Save the prompt.
     * prompt.save(file_path="path/prompt.yaml")
     *
     * @param filePath Path to directory to save prompt to.
     * @throws IOException
     */
    public void save(String filePath) throws IOException {
        if (!partialVariables.isEmpty()) {
            throw new IllegalArgumentException("Cannot save prompt with partial variables.");
        }

        Path savePath = Paths.get(filePath);
        Path directoryPath = savePath.getParent();
        Files.createDirectories(directoryPath);

        Map<String, Object> promptDict = this.toMap();

        if (savePath.toString().endsWith(".json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            try (FileWriter fileWriter = new FileWriter(filePath)) {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(fileWriter, promptDict);
            }
        } else if (savePath.toString().endsWith(".yaml")) {
            Yaml yaml = new Yaml();
            try (FileWriter fileWriter = new FileWriter(filePath)) {
                yaml.dump(promptDict, fileWriter);
            }
        } else {
            throw new IllegalArgumentException(savePath + " must be json or yaml");
        }
    }

    public List<String> getInputVariables() {
        return inputVariables;
    }

    /**
     * Format the prompt with the inputs.
     *
     * @param example – Any arguments to be passed to the prompt template.
     * @return A formatted string.
     */
    public abstract String format(Map<String, String> example);

    protected abstract String getPromptType();

    public abstract PromptValue formatPrompt(Map<String, String> example);

//    protected abstract BasePromptTemplate clone();

}