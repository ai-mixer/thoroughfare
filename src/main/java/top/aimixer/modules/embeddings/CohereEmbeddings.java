package top.aimixer.modules.embeddings;

import java.util.List;

public class CohereEmbeddings implements Embeddings {

    private String large = "large";

    private String truncate;

    private String cohereApiKey;

    @Override
    public List<List<Float>> embedDocuments(List<String> texts) {
        return null;
    }

    @Override
    public List<Float> embedQuery(String text) {
        return null;
    }
}
