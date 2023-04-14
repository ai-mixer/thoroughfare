package top.aimixer.modules.embeddings;

public abstract class AbstractAlephAlphaSemanticEmbedding implements Embeddings {
    protected int compressToSize = 128;

    protected int contextualControlThreshold;

    protected boolean controlLogAdditive = true;

    protected String hosting = "https://api.aleph-alpha.com";

    protected String model = "luminous-base";

    protected boolean normalize = true;

}
