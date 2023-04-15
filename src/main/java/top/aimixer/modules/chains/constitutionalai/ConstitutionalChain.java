package top.aimixer.modules.chains.constitutionalai;

import top.aimixer.modules.chains.Chain;
import top.aimixer.modules.chains.LLMChain;
import top.aimixer.modules.models.BaseLanguageModel;
import top.aimixer.modules.prompts.template.BasePromptTemplate;

import java.util.List;
import java.util.Map;

public class ConstitutionalChain extends Chain {

    public static List<ConstitutionalPrinciple> getPrinciples(List<String> names) {
        return null;
    }

    public static ConstitutionalChain fromLlm(BaseLanguageModel llm,
                                              LLMChain chain,
                                              BasePromptTemplate critiquePrompt,
                                              BasePromptTemplate revisionPrompt,
                                              Object kwargs) {
        return null;
    }

    public static String parseCritique(String outputString) {
        return null;
    }

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
}
