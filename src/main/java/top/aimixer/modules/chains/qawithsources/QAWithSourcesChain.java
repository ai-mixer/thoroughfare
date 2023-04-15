package top.aimixer.modules.chains.qawithsources;

import top.aimixer.modules.chains.Chain;
import top.aimixer.schema.Document;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class QAWithSourcesChain extends BaseQAWithSourcesChain {
    @Override
    public List<String> inputKeys() {
        return null;
    }

    @Override
    public List<Document> getDocs(Map<String, Object> inputs) {
        return null;
    }

    @Override
    public CompletableFuture<List<Document>> agetDocs(Map<String, Object> inputs) {
        return null;
    }

    @Override
    public String chainType() {
        return "qa_with_sources_chain";
    }
}
