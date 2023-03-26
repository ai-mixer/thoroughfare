package top.aimixer.parser;

import java.util.*;
import java.util.regex.*;

public class RegexParser extends BaseOutputParser {
    private String regex;
    private List<String> outputKeys;
    private Optional<String> defaultOutputKey;

    public RegexParser(String regex, List<String> outputKeys, Optional<String> defaultOutputKey) {
        this.regex = regex;
        this.outputKeys = outputKeys;
        this.defaultOutputKey = defaultOutputKey;
    }

    public String getType() {
        return "regex_parser";
    }

    public Map<String, String> parse(String text) {
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(text);
        Map<String, String> resultMap = new HashMap<>();

        if (match.find()) {
            for (int i = 0; i < outputKeys.size(); i++) {
                resultMap.put(outputKeys.get(i), match.group(i + 1));
            }
        } else {
            if (!defaultOutputKey.isPresent()) {
                throw new IllegalArgumentException("Could not parse output: " + text);
            } else {
                for (String key : outputKeys) {
                    resultMap.put(key, key.equals(defaultOutputKey.get()) ? text : "");
                }
            }
        }

        return resultMap;
    }
}