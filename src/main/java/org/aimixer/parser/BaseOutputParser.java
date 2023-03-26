package org.aimixer.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to parse the output of an LLM call.
 */
public abstract class BaseOutputParser {

    /**
     * Parse the output of an LLM call.
     */
    public abstract Object parse(String text);

    /**
     *  Get format instructions.
     */
    public String getFormatInstructions() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Return the type key.
     */
    protected abstract String getType();

    /**
     *  Return dictionary representation of output parser.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> outputParserMap = new HashMap<>();
        outputParserMap.put("_type", getType());
        return outputParserMap;
    }
}

