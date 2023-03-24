package org.thoroughfare.prompt.template.fewshot.selector;

import java.util.Map;
import java.util.List;

/**
 * Interface for selecting examples to include in prompts.
 */
public abstract class BaseExampleSelector {

    /**
     * Add new example to store for a key.
     */
    public abstract void addExample(Map<String, String> example);

    /**
     * Select which examples to use based on the inputs.
     */
    public abstract List<Map<String, String>> selectExamples(Map<String, String> exampleMap);
}

