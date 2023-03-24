package org.thoroughfare.prompt.template.fewshot.selector;

import org.thoroughfare.dantic.BaseModel;
import org.thoroughfare.prompt.template.prompt.PromptTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class LengthBasedExampleSelector extends BaseExampleSelector implements BaseModel {
    private List<Map<String, String>> examples;
    private PromptTemplate examplePrompt;
    private Function<String, Integer> getTextLength = LengthBasedExampleSelector::getLengthBased;
    private int maxLength = 2048;
    private List<Integer> exampleTextLengths;

    public void addExample(Map<String, String> example) {
        this.examples.add(example);
        String stringExample = this.examplePrompt.format(example);
        this.exampleTextLengths.add(getTextLength.apply(stringExample));
    }

    public List<Integer> calculateExampleTextLengths(List<Integer> v, Map<String, Object> values) {
        if (v != null) {
            return v;
        }
        PromptTemplate examplePrompt = (PromptTemplate) values.get("examplePrompt");
        Function<String, Integer> getTextLength = (Function<String, Integer>) values.get("getTextLength");
        List<Map<String, String>> stringExamples = (List<Map<String, String>>) values.get("examples");
        List<Integer> result = new ArrayList<>();
        for (Map<String, String> eg : stringExamples) {
            result.add(getTextLength.apply(examplePrompt.format(eg)));
        }
        return result;
    }

    public List<Map<String, String>> selectExamples(Map<String, String> inputVariables) {
        String inputs = String.join(" ", inputVariables.values());
        int remainingLength = this.maxLength - this.getTextLength.apply(inputs);
        int i = 0;
        List<Map<String, String>> examples = new ArrayList<>();
        while (remainingLength > 0 && i < this.examples.size()) {
            int newLength = remainingLength - this.exampleTextLengths.get(i);
            if (newLength < 0) {
                break;
            } else {
                examples.add(this.examples.get(i));
                remainingLength = newLength;
            }
            i++;
        }
        return examples;
    }

    /**
     * Get the length of the text based on the number of lines and words.
     *
     * @param text the input text
     * @return the length of the text
     */
    private static int getLengthBased(String text) {
        return text.split("\\n| ").length;
    }
}
