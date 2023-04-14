package top.aimixer.modules.embeddings;

import java.util.List;
import java.util.Map;

public class HuggingFaceHubEmbeddings implements Embeddings {

    private Map<String, String> modelKwargs;

    private String repoId = "sentence-transformers/all-mpnet-base-v2";

    private String task = "feature-extraction";

    @Override
    public List<List<Float>> embedDocuments(List<String> texts) {
        return null;
    }

    @Override
    public List<Float> embedQuery(String text) {
        return null;
    }
}
