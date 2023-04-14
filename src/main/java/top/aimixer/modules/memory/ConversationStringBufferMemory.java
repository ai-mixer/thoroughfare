package top.aimixer.modules.memory;

import java.util.List;
import java.util.Map;

public class ConversationStringBufferMemory implements BaseMemory {
    @Override
    public List<String> memoryVariables() {
        return null;
    }

    @Override
    public Map<String, Object> loadMemoryVariables(Map<String, Object> inputs) {
        return null;
    }

    @Override
    public void saveContext(Map<String, Object> inputs, Map<String, String> outputs) {

    }

    @Override
    public void clear() {

    }
}
