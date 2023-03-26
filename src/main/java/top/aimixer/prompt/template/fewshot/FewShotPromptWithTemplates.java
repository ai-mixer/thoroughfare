package top.aimixer.prompt.template.fewshot;

import top.aimixer.prompt.base.Base;
import top.aimixer.prompt.template.fewshot.selector.BaseExampleSelector;
import top.aimixer.prompt.template.StringPromptTemplate;
import top.aimixer.prompt.template.prompt.PromptTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Prompt template that contains few shot examples.
 */
public class FewShotPromptWithTemplates extends StringPromptTemplate {
    /**
     * Examples to format into the prompt.
     * Either this or example_selector should be provided.
     */
    private List<Map<String, String>> examples;
    /**
     * ExampleSelector to choose the examples to format into the prompt.
     * Either this or examples should be provided.
     */
    private BaseExampleSelector exampleSelector;
    /**
     * PromptTemplate used to format an individual example.
     */
    private PromptTemplate examplePrompt;
    /**
     * A PromptTemplate to put after the examples.
     */
    private StringPromptTemplate suffix;
    /**
     * String separator used to join the prefix, the examples, and suffix.
     */
    private String exampleSeparator = "\n\n";
    /**
     * A PromptTemplate to put before the examples.
     */
    private StringPromptTemplate prefix = null;
    /**
     * The format of the prompt template. Options are: 'f-string', 'jinja2'.
     */
    private String templateFormat = "f-string";
    /**
     * Whether to try validating the template.
     */
    private boolean validateTemplate = true;

    public FewShotPromptWithTemplates(List<Map<String, String>> examples, BaseExampleSelector exampleSelector,
                                      PromptTemplate examplePrompt, List<String> inputVariables,
                                      StringPromptTemplate suffix, StringPromptTemplate prefix,
                                      boolean validateTemplate) {
        this.examples = examples;
        this.exampleSelector = exampleSelector;
        this.examplePrompt = examplePrompt;
        this.inputVariables = inputVariables;
        this.suffix = suffix;
        this.prefix = prefix;
        this.validateTemplate = validateTemplate;
    }

    /**
     * Check that one and only one of examples/example_selector are provided.
     *
     * @param values
     * @return
     */
    public static Map<String, Object> checkExamplesAndSelector(Map<String, Object> values) {
        Object examples = values.get("examples");
        Object exampleSelector = values.get("example_selector");

        if (examples != null && exampleSelector != null) {
            throw new IllegalArgumentException("Only one of 'examples' and 'example_selector' should be provided");
        }

        if (examples == null && exampleSelector == null) {
            throw new IllegalArgumentException("One of 'examples' and 'example_selector' should be provided");
        }

        return values;
    }

    /**
     * Check that prefix, suffix and input variables are consistent
     */
    public Map<String, Object> templateIsValid(Map<String, Object> values) {
        if ((boolean) values.get("validate_template")) {
            Set<String> inputVariables = new HashSet<>((List<String>) values.get("input_variables"));
            Set<String> expectedInputVariables =
                    new HashSet<>(StringPromptTemplate.class.cast(values.get("suffix")).getInputVariables());
            expectedInputVariables.addAll((List<String>) values.get("partial_variables"));
            if (values.get("prefix") != null) {
                expectedInputVariables.addAll(
                        StringPromptTemplate.class.cast(values.get("prefix")).getInputVariables());
            }
            Set<String> missingVars = new HashSet<>(expectedInputVariables);
            missingVars.removeAll(inputVariables);
            if (!missingVars.isEmpty()) {
                throw new IllegalArgumentException(
                        String.format("Got input_variables=%s, but based on prefix/suffix expected %s",
                                inputVariables, expectedInputVariables));
            }
        }
        return values;
    }

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
     * @return
     */
    public String format(Map<String, String> exampleMap) {
        // Get the examples to use.
        List<Map<String, String>> examples = getExamples(exampleMap);
        // Format the examples.
        List<String> exampleStrings =
                examples.stream()
                        .map(example -> examplePrompt.format(example))
                        .collect(Collectors.toList());

        // Create the overall prefix.
        String prefixString = "";
        if (prefix != null) {
            Map<String, String> prefixExample = new HashMap<>();
            for (String key : prefix.getInputVariables()) {
                if (exampleMap.containsKey(key)) {
                    prefixExample.put(key, exampleMap.get(key));
                    exampleMap.remove(key);
                }
            }
            prefixString = prefix.format(prefixExample);
        }

        // Create the overall suffix
        Map<String, String> suffixExample = new HashMap<>();
        for (String key : suffix.getInputVariables()) {
            if (exampleMap.containsKey(key)) {
                suffixExample.put(key, exampleMap.get(key));
                exampleMap.remove(key);
            }
        }
        String suffixString = suffix.format(suffixExample);

        List<String> pieces = new ArrayList<>();
        pieces.add(prefixString);
        pieces.addAll(exampleStrings);
        pieces.add(suffixString);

        String template = pieces.stream()
                .filter(piece -> !piece.isEmpty())
                .collect(Collectors.joining(exampleSeparator));

        // Format the template with the input variables.
        return Base.DEFAULT_FORMATTER_MAPPING.get(templateFormat).apply(template, exampleMap);
    }

    /**
     * Return the prompt type key."
     *
     * @return
     */
    public String getPromptType() {
        return "few_shot_with_templates";
    }

    /**
     * Return a dictionary of the prompt.
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