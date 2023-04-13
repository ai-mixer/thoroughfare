package top.aimixer.modules.models;

import com.knuddels.jtokkit.api.ModelType;
import top.aimixer.schema.models.LLMResult;
import top.aimixer.schema.prompts.PromptValue;
import top.aimixer.utilites.tokenizer.TokenUtils;

import java.util.List;

public abstract class BaseLanguageModel {

    /**
     * Take in a list of prompt values and return an LLMResult.
     *
     * @param prompts
     * @param stop
     * @return
     */
    public abstract LLMResult generatePrompt(List<PromptValue> prompts, List<String> stop);

    public abstract LLMResult asyncGeneratePrompt(List<PromptValue> prompts, List<String> stop);

    /**
     * Get the number of tokens present in the text.
     *
     * @param text
     * @return
     */
    public int getNumTokens(String text) {
        return TokenUtils.tokenByModelType(ModelType.GPT_3_5_TURBO, text);
    }
}
