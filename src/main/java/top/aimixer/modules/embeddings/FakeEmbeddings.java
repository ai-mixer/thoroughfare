package top.aimixer.modules.embeddings;

import java.util.List;

public class FakeEmbeddings implements Embeddings {
    @Override
    public List<List<Float>> embedDocuments(List<String> texts) {
        return null;
    }

    @Override
    public List<Float> embedQuery(String text) {
        return null;
    }
}
