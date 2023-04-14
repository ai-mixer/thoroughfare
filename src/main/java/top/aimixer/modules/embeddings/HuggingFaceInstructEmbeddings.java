package top.aimixer.modules.embeddings;

import java.util.List;

public class HuggingFaceInstructEmbeddings implements Embeddings {

    private String embedInstruction = "Represent the document for retrieval: ";

    private String modelName = "hkunlp/instructor-large";

    private String queryInstruction = "Represent the question for retrieving supporting";

    @Override
    public List<List<Float>> embedDocuments(List<String> texts) {
        return null;
    }

    @Override
    public List<Float> embedQuery(String text) {
        return null;
    }
}
