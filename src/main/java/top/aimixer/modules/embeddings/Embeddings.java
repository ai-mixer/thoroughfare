package top.aimixer.modules.embeddings;

import java.util.List;

public interface Embeddings {
    List<List<Float>> embedDocuments(List<String> texts);

    List<Float> embedQuery(String text);
}
