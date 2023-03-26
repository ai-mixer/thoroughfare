package org.aimixer.prompt.base;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import com.hubspot.jinjava.Jinjava;

/**
 * BasePrompt schema definition.
 */
public class Base {

    public static final Map<String, BiFunction<String, Map<String, String>, String>> DEFAULT_FORMATTER_MAPPING;

    static {
        DEFAULT_FORMATTER_MAPPING = new HashMap<>();
        DEFAULT_FORMATTER_MAPPING.put("f-string", (template, kwArgs) -> String.format(template, kwArgs));
        DEFAULT_FORMATTER_MAPPING.put("jinja2", Base::jinja2Formatter);
    }

    /**
     * Format a template using jinja2.
     *
     * @param template
     * @param context
     * @return
     */
    public static String jinja2Formatter(String template, Map<String, String> context) {
        try {
            Jinjava jinjaTemplate = new Jinjava();
            return jinjaTemplate.render(Files.readString(Paths.get(template)), context);
        } catch (Exception e) {
            throw new RuntimeException("Jinjava render error.", e);
        }
    }

    /**
     * Check that template string is valid.
     *
     * @param template
     * @param templateFormat
     * @param inputVariables
     */
    public static void checkValidTemplate(String template, String templateFormat, List<String> inputVariables) {
        if (!DEFAULT_FORMATTER_MAPPING.containsKey(templateFormat)) {
            String validFormats = String.join(", ", DEFAULT_FORMATTER_MAPPING.keySet());
            throw new IllegalArgumentException(
                    "Invalid template format. Got `" + templateFormat + "`;"
                            + " should be one of " + validFormats);
        }
        Map<String, String> inputVariableMap = new HashMap<>();
        for (String inputVariable : inputVariables) {
            inputVariableMap.put(inputVariable, "foo");
        }
        try {
            BiFunction<String, Map<String, String>, String> formatterFunc = DEFAULT_FORMATTER_MAPPING
                    .get(templateFormat);
            formatterFunc.apply(template, inputVariableMap);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Invalid prompt schema; check for mismatched or missing input parameters. "
                            + e.getMessage(),
                    e);
        }
    }
}

