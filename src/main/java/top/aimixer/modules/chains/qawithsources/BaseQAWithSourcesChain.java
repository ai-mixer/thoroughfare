package top.aimixer.modules.chains.qawithsources;

import top.aimixer.modules.chains.Chain;
import top.aimixer.schema.Document;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class BaseQAWithSourcesChain extends Chain {

    @Override
    public List<String> inputKeys() {
        return null;
    }

    @Override
    public List<String> outputKeys() {
        return null;
    }

    @Override
    public Map<String, String> call(Map<String, String> inputs) {
        return null;
    }

    @Override
    public CompletableFuture<Map<String, String>> acall(Map<String, String> inputs) {
        return super.acall(inputs);
    }

    public abstract List<Document> getDocs(Map<String, Object> inputs);
    public abstract CompletableFuture<List<Document>> agetDocs(Map<String, Object> inputs);
}
