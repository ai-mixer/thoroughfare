package org.aimixer.prompt.loader;

import org.aimixer.prompt.template.BasePromptTemplate;

import java.util.Map;
import java.util.Optional;

interface PromptLoader {

    Optional<BasePromptTemplate> load(Map<String, Object> config);
}
