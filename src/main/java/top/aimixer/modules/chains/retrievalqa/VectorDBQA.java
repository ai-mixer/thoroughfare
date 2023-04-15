package top.aimixer.modules.chains.retrievalqa;

import top.aimixer.schema.Document;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VectorDBQA extends BaseRetrievalQA {
    @Override
    public List<Document> getDocs(String question) {
        return null;
    }

    @Override
    public CompletableFuture<List<Document>> agetDocs(String question) {
        return null;
    }

    @Override
    public String chainType() {
        return "vector_db_qa";
    }
}
