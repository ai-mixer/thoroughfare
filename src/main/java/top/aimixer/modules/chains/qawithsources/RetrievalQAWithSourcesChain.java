package top.aimixer.modules.chains.qawithsources;

import top.aimixer.schema.Document;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RetrievalQAWithSourcesChain extends BaseQAWithSourcesChain {

    @Override
    public List<Document> getDocs(Map<String, Object> inputs) {
        return null;
    }

    @Override
    public CompletableFuture<List<Document>> agetDocs(Map<String, Object> inputs) {
        return null;
    }
}
