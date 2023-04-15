package top.aimixer.modules.chains.hyde;

import top.aimixer.modules.chains.Chain;
import top.aimixer.modules.embeddings.Embeddings;

import java.util.List;
import java.util.Map;

public class HypotheticalDocumentEmbedder extends Chain implements Embeddings {
    @Override
    public List<String> inputKeys() {
        return null;
    }

    @Override
    public List<String> outputKeys() {
        return null;
    }

    @Override
    public Map<String, String> call(Map<String, String> inputs) {
        return null;
    }

    @Override
    public List<List<Float>> embedDocuments(List<String> texts) {
        return null;
    }

    @Override
    public List<Float> embedQuery(String text) {
        return null;
    }

    @Override
    public String chainType() {
        return "hyde_chain";
    }
}
