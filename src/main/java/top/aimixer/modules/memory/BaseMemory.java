package top.aimixer.modules.memory;

import java.util.List;
import java.util.Map;

public interface BaseMemory {
    List<String> memoryVariables();

    Map<String, Object> loadMemoryVariables(Map<String, Object> inputs);

    void saveContext(Map<String, Object> inputs, Map<String, String> outputs);

    void clear();
}
