package top.aimixer.cache;

import top.aimixer.schema.Generation;

import java.util.List;
import java.util.Optional;

public abstract class BaseCache {
    /**
     * Base interface for cache.
     */

    public abstract Optional<List<? extends Generation>> lookup(String prompt, String llm_string);

    /**
     * Look up based on prompt and llm_string.
     */

    public abstract void update(String prompt, String llm_string, List<? extends Generation> return_val);
    /**
     * Update cache based on prompt and llm_string.
     */
}

