package top.aimixer.utilites;

import java.util.Map;

public class OSUtils {
    public static String getFromDictOrEnv(
            Map<String, Object> data, String key, String envKey, String defaultValue
    ) throws Exception {
        if (data.containsKey(key) && data.get(key) != null) {
            return data.get(key).toString();
        } else if (System.getenv(envKey) != null) {
            return System.getenv(envKey);
        } else if (defaultValue != null) {
            return defaultValue;
        } else {
            throw new Exception(
                    String.format(
                            "Did not find %s, please add an environment variable `%s` which contains it, or pass `%s` as a named parameter.",
                            key, envKey, key
                    )
            );
        }
    }
}