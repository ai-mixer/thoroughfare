package top.aimixer.prompt.template.prompt;

import top.aimixer.parser.BaseOutputParser;
import top.aimixer.prompt.base.Base;
import top.aimixer.prompt.template.StringPromptTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Schema to represent a prompt for an LLM.
 */
public class PromptTemplate extends StringPromptTemplate {
    /**
     * The prompt template.
     */
    private String template;
    /**
     * The format of the prompt template. Options are: 'f-string', 'jinja2'.
     */
    private String templateFormat;
    /**
     * Whether to try validating the template.
     */
    private boolean validateTemplate;


    public PromptTemplate(String template, BaseOutputParser outputParser) {
        this.template = template;
        this.outputParser = outputParser;
    }

    public PromptTemplate(List<String> inputVariables, String template) {
        this.inputVariables = inputVariables;
        this.template = template;
        this.templateFormat = "f-string";
        this.validateTemplate = true;
    }

    /**
     * Format the prompt with the inputs.
     *
     * @param example – Any arguments to be passed to the prompt template.
     * @return A formatted string.
     */
    public String format(Map<String, String> example) {
        return Base.DEFAULT_FORMATTER_MAPPING.get(this.templateFormat)
                .apply(this.template, mergePartialAndUserVariables(example));
    }

    /**
     * Check that template and input variables are consistent.
     *
     * @param kwArgs
     * @return
     */
    public static Map<String, Object> templateIsValid(Map<String, Object> kwArgs) {
        if ((boolean) kwArgs.get("validate_template")) {
            List<String> allInputs = new ArrayList<>();
            allInputs.addAll((List<String>) kwArgs.get("input_variables"));
            allInputs.addAll((List<String>) kwArgs.get("partial_variables"));
            Base.checkValidTemplate((String) kwArgs.get("template"), (String) kwArgs.get("template_format"), allInputs);
        }
        return kwArgs;
    }

    /**
     * Take examples in list format with prefix and suffix to create a prompt.
     * <p>
     * Intended be used as a way to dynamically create a prompt from examples.
     * <p>
     * Args:
     * examples: List of examples to use in the prompt.
     * suffix: String to go after the list of examples. Should generally
     * set up the user's input.
     * inputVariables: A list of variable names the final prompt template
     * will expect.
     * exampleSeparator: The separator to use in between examples. Defaults
     * to two new line characters.
     * prefix: String that should go before any examples. Generally includes
     * examples. Default to an empty string.
     * <p>
     * Returns:
     * The final prompt generated.
     *
     * @param examples
     * @param suffix
     * @param inputVariables
     * @param exampleSeparator
     * @param prefix
     * @return
     */
    public static PromptTemplate fromExamples(List<String> examples, String suffix, List<String> inputVariables,
                                              String exampleSeparator, String prefix) {
        StringBuilder templateBuilder = new StringBuilder(prefix);
        for (String example : examples) {
            templateBuilder.append(exampleSeparator);
            templateBuilder.append(example);
        }
        templateBuilder.append(suffix);
        String template = templateBuilder.toString();

        return new PromptTemplate(inputVariables, template);
    }

    /**
     * Load a prompt from a file.
     * <p>
     * Args:
     * templateFile: The path to the file containing the prompt template.
     * inputVariables: A list of variable names the final prompt template
     * will expect.
     * Returns:
     * The prompt loaded from the file.
     *
     * @param templateFile
     * @param inputVariables
     * @return
     * @throws IOException
     */
    public static PromptTemplate fromFile(Path templateFile, List<String> inputVariables) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(templateFile.toFile()));
        StringBuilder templateBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            templateBuilder.append(line);
            templateBuilder.append(System.lineSeparator());
        }
        reader.close();
        String template = templateBuilder.toString();
        return new PromptTemplate(inputVariables, template);
    }

    /**
     * Load a prompt template from a template.
     *
     * @param template
     * @return
     */
    public static PromptTemplate fromTemplate(String template) {
        Set<String> inputVariables = new TreeSet<>();
        Pattern pattern = Pattern.compile("\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(template);
        while (matcher.find()) {
            String variable = matcher.group(1);
            if (variable != null) {
                inputVariables.add(variable);
            }
        }
        return new PromptTemplate(new ArrayList<>(inputVariables), template);
    }

    /**
     * Return the prompt type key.
     *
     * @return
     */
    public String getPromptType() {
        return "prompt";
    }

    public String getTemplateFormat() {
        return templateFormat;
    }

    public void setTemplateFormat(String templateFormat) {
        this.templateFormat = templateFormat;
    }

    public boolean isValidateTemplate() {
        return validateTemplate;
    }

    public void setValidateTemplate(boolean validateTemplate) {
        this.validateTemplate = validateTemplate;
    }
}

