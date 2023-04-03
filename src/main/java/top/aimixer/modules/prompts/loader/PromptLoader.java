package top.aimixer.modules.prompts.loader;

import top.aimixer.modules.prompts.template.BasePromptTemplate;

import java.util.Map;
import java.util.Optional;

interface PromptLoader {

    Optional<BasePromptTemplate> load(Map<String, Object> config);
}
