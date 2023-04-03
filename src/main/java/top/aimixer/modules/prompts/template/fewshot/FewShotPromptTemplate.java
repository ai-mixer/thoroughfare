package top.aimixer.modules.prompts.template.fewshot;

import top.aimixer.modules.prompts.template.fewshot.selector.BaseExampleSelector;
import top.aimixer.parser.BaseOutputParser;
import top.aimixer.modules.prompts.base.Base;
import top.aimixer.modules.prompts.template.StringPromptTemplate;
import top.aimixer.modules.prompts.template.prompt.PromptTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Prompt template that contains few shot examples.
 */
public class FewShotPromptTemplate extends StringPromptTemplate {

    /**
     * Examples to format into the prompt. Either this or exampleSelector should be provided.
     */
    private List<Map<String, String>> examples;
    /**
     * ExampleSelector to choose the examples to format into the prompt. Either this or examples should be provided.
     */
    private BaseExampleSelector exampleSelector;
    /**
     * PromptTemplate used to format an individual example.
     */
    private PromptTemplate examplePrompt;
    /**
     * A prompt template string to put after the examples.
     */
    private String suffix;
    /**
     * A list of the names of the variables the prompt template expects.
     */
    private List<String> inputVariables;
    /**
     * String separator used to join the prefix, the examples, and suffix.
     */
    private String exampleSeparator = "\n\n";
    /**
     * A prompt template string to put before the examples.
     */
    private String prefix = "";
    /**
     * The format of the prompt template. Options are: 'f-string', 'jinja2'.
     */
    private String templateFormat = "f-string";
    /**
     * Whether to try validating the template.
     */
    private boolean validateTemplate = true;

    public FewShotPromptTemplate(String suffix, String prefix, PromptTemplate examplePrompt,
                                 List<Map<String, String>> examples, BaseOutputParser outputParser) {
        this.suffix = suffix;
        this.prefix = prefix;
        this.examplePrompt = examplePrompt;
        this.examples = examples;
        this.outputParser = outputParser;
    }

    /**
     * Check that one and only one of examples/exampleSelector are provided.
     */
    public void checkExamplesAndSelector() {
        if (examples != null && exampleSelector != null) {
            throw new IllegalArgumentException("Only one of 'examples' and 'exampleSelector' should be provided");
        }

        if (examples == null && exampleSelector == null) {
            throw new IllegalArgumentException("One of 'examples' and 'exampleSelector' should be provided");
        }
    }

    /**
     * Check that prefix, suffix and input variables are consistent.
     */
    public void templateIsValid() {
        if (validateTemplate) {
            Base.checkValidTemplate(prefix + suffix, templateFormat, inputVariables);
        }
    }

    /**
     * Get the examples to use.
     *
     * @param exampleMap
     * @return
     */
    private List<Map<String, String>> getExamples(Map<String, String> exampleMap) {
        if (examples != null) {
            return examples;
        } else if (exampleSelector != null) {
            return exampleSelector.selectExamples(exampleMap);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Format the prompt with the inputs.
     *
     * @param exampleMap – Any arguments to be passed to the prompt template.
     * @return A formatted string.
     */
    public String format(Map<String, String> exampleMap) {
        exampleMap = mergePartialAndUserVariables(exampleMap);
        // Get the examples to use.
        List<Map<String, String>> examples = getExamples(exampleMap);
        // Format the examples.
        List<String> exampleStrings = new ArrayList<>();
        for (Map<String, String> example : examples) {
            exampleStrings.add(examplePrompt.format(example));
        }
        // Create the overall template.
        List<String> pieces = new ArrayList<>();
        pieces.add(prefix);
        pieces.addAll(exampleStrings);
        pieces.add(suffix);
        String template = String.join(exampleSeparator, pieces);

        // Format the template with the input variables.
        return Base.DEFAULT_FORMATTER_MAPPING.get(templateFormat).apply(template, exampleMap);
    }

    /**
     * Return the prompt type key.
     */
    public String getPromptType() {
        return "few_shot";
    }

    /**
     * Saving an example selector is not currently supported
     *
     * @return
     */
    public Map<String, Object> toMap() {
        if (exampleSelector != null) {
            throw new IllegalArgumentException("Saving an example selector is not currently supported");
        }
        return super.toMap();
    }

}
