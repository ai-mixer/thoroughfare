package top.aimixer.utilites.tokenizer;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;
import com.knuddels.jtokkit.api.ModelType;

public class TokenUtils {

    public static int tokenByEncodingType(EncodingType encodingType, String text) {
        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        Encoding enc = registry.getEncoding(encodingType);
        return enc.countTokens(text);
    }

    public static int tokenByModelType(ModelType modelType, String text) {
        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        Encoding enc = registry.getEncodingForModel(modelType);
        return enc.countTokens(text);
    }
}
