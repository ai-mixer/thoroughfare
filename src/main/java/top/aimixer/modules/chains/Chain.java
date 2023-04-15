package top.aimixer.modules.chains;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class Chain {

    public String chainType() {
        throw new UnsupportedOperationException("Saving not supported for this chain type.");
    }

    public abstract List<String> inputKeys();

    public abstract List<String> outputKeys();

    public abstract Map<String, String> call(Map<String, String> inputs);

    public CompletableFuture<Map<String, String>> acall(Map<String, String> inputs) {
        throw new UnsupportedOperationException("Async call not supported for this chain type.");
    }

    public List<Map<String, String>> apply(List<Map<String, Object>> inputList) {
        return null;
    }

    public void save(File file, String filePath){
        return;
    }

}
