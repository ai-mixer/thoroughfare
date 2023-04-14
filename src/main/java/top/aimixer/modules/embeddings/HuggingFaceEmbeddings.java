package top.aimixer.modules.embeddings;

import java.util.List;

public class HuggingFaceEmbeddings implements Embeddings {

    private String modelName = "sentence-transformers/all-mpnet-base-v2";

    @Override
    public List<List<Float>> embedDocuments(List<String> texts) {
        return null;
    }

    @Override
    public List<Float> embedQuery(String text) {
        return null;
    }
}
