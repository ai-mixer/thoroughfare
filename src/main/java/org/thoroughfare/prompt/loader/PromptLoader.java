package org.thoroughfare.prompt.loader;

import org.thoroughfare.prompt.template.BasePromptTemplate;

import java.util.Map;
import java.util.Optional;

interface PromptLoader {

    Optional<BasePromptTemplate> load(Map<String, Object> config);
}
