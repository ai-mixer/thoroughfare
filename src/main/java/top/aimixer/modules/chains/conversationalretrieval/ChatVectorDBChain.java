package top.aimixer.modules.chains.conversationalretrieval;

import top.aimixer.schema.Document;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ChatVectorDBChain extends BaseConversationalRetrievalChain {
    @Override
    public List<Document> getDocs(String question, Map<String, Object> inputs) {
        return null;
    }

    @Override
    public CompletableFuture<List<Document>> agetDocs(String question, Map<String, Object> inputs) {
        return null;
    }

    @Override
    public String chainType() {
        return "chat-vector-db";
    }
}
