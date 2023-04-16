package top.aimixer.modules.prompts.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import top.aimixer.modules.prompts.template.BasePromptTemplate;
import top.aimixer.modules.prompts.template.fewshot.FewShotPromptTemplate;
import top.aimixer.modules.prompts.template.prompt.PromptTemplate;
import top.aimixer.parser.BaseOutputParser;
import top.aimixer.parser.RegexParser;
import top.aimixer.utilites.LoaderUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Load prompts from disk.
 */
public class BaseLoader {

    private static final String URL_BASE = "https://raw.xxxx.com/xxxx/xxx-hub/";
    private static final Logger logger = Logger.getLogger(BaseLoader.class.getName());

    private static Map<String, Function<Map<String, Object>, BasePromptTemplate>> TYPE_TO_LOADER_MAP = new HashMap<>() {{
        put("prompt", config -> loadPrompt(config));
        put("few_shot", config -> loadFewShotPrompt(config));
    }};

    /**
     * Load prompt from Config Dict.
     *
     * @param config
     * @return
     */
    public static BasePromptTemplate loadPromptFromConfig(Map<String, Object> config) {
        // Load prompt from Config Dict
        if (!config.containsKey("_type")) {
            logger.warning("No `_type` key found, defaulting to `prompt`.");
        }
        String configType = (String) config.getOrDefault("_type", "prompt");

        if (!TYPE_TO_LOADER_MAP.containsKey(configType)) {
            throw new IllegalArgumentException("Loading " + configType + " prompt not supported");
        }

        Function<Map<String, Object>, BasePromptTemplate> promptTemplateConsumer = TYPE_TO_LOADER_MAP.get(configType);
        return promptTemplateConsumer.apply(config);
    }

    /**
     * Unified method for loading a prompt from LangChainHub or local fs.
     *
     * @param path
     * @return
     */
    public static BasePromptTemplate loadPrompt(String path) {
        Optional<BasePromptTemplate> hubResult = LoaderUtils.tryLoadFromHub(path,
                BaseLoader::loadPromptFromFile, "prompts", Set.of("py", "json", "yaml"));
        if (hubResult.isEmpty()) {
            return hubResult.get();
        } else {
            return loadPromptFromFile(path);
        }
    }

    /**
     * Load template from disk if applicable.
     *
     * @param varName
     * @param config
     * @return
     */
    private static void loadTemplate(String varName, Map<String, Object> config) {
        String templatePathKey = varName + "_path";
        if (config.containsKey(templatePathKey)) {
            if (config.containsKey(varName)) {
                throw new IllegalArgumentException("Both `" + templatePathKey + "` and `" + varName + "` cannot be provided.");
            }
            Path templatePath = Paths.get((String) config.remove(templatePathKey));
            String template;
            if (templatePath.toString().endsWith(".txt")) {
                try (BufferedReader reader = Files.newBufferedReader(templatePath)) {
                    template = reader.lines().collect(Collectors.joining("\n"));
                } catch (IOException e) {
                    throw new RuntimeException("Error reading template file", e);
                }
            } else {
                throw new IllegalArgumentException("Invalid file format for template");
            }
            config.put(varName, template);
        }
    }

    /**
     * Load examples if necessary.
     *
     * @param config
     * @return
     * @throws IOException
     */
    private static void loadExamples(Map<String, Object> config) {
        Object examples = config.get("examples");
        if (examples instanceof List) {
            // Do nothing
        } else if (examples instanceof String) {
            String examplesPath = (String) examples;
            File file = new File(examplesPath);
            if (examplesPath.endsWith(".json")) {
                ObjectMapper objectMapper = new ObjectMapper();
                try (FileReader reader = new FileReader(file)) {
                    examples = objectMapper.readValue(reader, Map.class);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (examplesPath.endsWith(".yaml") || examplesPath.endsWith(".yml")) {
                Yaml yaml = new Yaml();
                try (FileReader reader = new FileReader(file)) {
                    examples = yaml.load(reader);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new IllegalArgumentException(
                        "Invalid file format. Only json or yaml formats are supported.");
            }
            config.put("examples", examples);
        } else {
            throw new IllegalArgumentException(
                    "Invalid examples format. Only list or string are supported.");
        }
    }

    private static void loadOutputParser(Map<String, Object> config) {
        if (config.containsKey("output_parsers") && config.get("output_parsers") != null) {
            Map<String, Object> _config = (Map<String, Object>) config.get("output_parsers");
            String outputParserType = (String) _config.get("_type");
            RegexParser outputParser;

            if (outputParserType.equals("regex_parser")) {
                outputParser = new RegexParser(
                        (String) _config.get("regex"),
                        (List<String>) _config.get("output_keys"),
                        Optional.ofNullable((String) _config.get("default_output_key"))
                );
            } else {
                throw new IllegalArgumentException("Unsupported output parser " + outputParserType);
            }

            config.put("output_parsers", outputParser);
        }
    }

    /**
     * Load the few shot prompt from the config.
     *
     * @param config
     * @return
     */
    private static FewShotPromptTemplate loadFewShotPrompt(Map<String, Object> config) {
        loadTemplate("suffix", config);
        loadTemplate("prefix", config);
        // Load the example prompt.
        if (config.containsKey("example_prompt_path")) {
            if (config.containsKey("example_prompt")) {
                throw new IllegalArgumentException("Only one of example_prompt and example_prompt_path should be specified.");
            }
            config.put("example_prompt", loadPrompt(String.valueOf(config.remove("example_prompt_path"))));
        } else {
            config.put("example_prompt", loadPromptFromConfig((Map<String, Object>) config.get("example_prompt")));
        }
        // Load the examples.
        loadExamples(config);
        loadOutputParser(config);
        return new FewShotPromptTemplate(String.valueOf(config.get("suffix")), String.valueOf(config.get("prefix")),
                PromptTemplate.class.cast(config.get("example_prompt")), List.class.cast(config.get("examples")),
                BaseOutputParser.class.cast(config.get("output_parsers")));
    }

    /**
     * Load the prompt template from config.
     *
     * @param config
     * @return
     */
    private static PromptTemplate loadPrompt(Map<String, Object> config) {
        loadTemplate("template", config);
        loadOutputParser(config);
        return new PromptTemplate(String.valueOf(config.get("template")),
                BaseOutputParser.class.cast(config.get("output_parsers")));
    }

    /**
     * Load prompt from file.
     *
     * @param file
     * @return
     */
    private static BasePromptTemplate loadPromptFromFile(String file) {
        Path filePath = Paths.get(file);
        Map<String, Object> config;
        if (filePath.toString().endsWith(".json")) {
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                config = new Gson().fromJson(reader, Map.class);
            } catch (IOException e) {
                throw new RuntimeException("Error reading JSON file", e);
            }
        } else if (filePath.toString().endsWith(".yaml") || filePath.toString().endsWith(".yml")) {
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                Yaml yaml = new Yaml();
                config = yaml.load(reader);
            } catch (IOException e) {
                throw new RuntimeException("Error reading YAML file", e);
            }
        } else {
            throw new IllegalArgumentException("Got unsupported file type " + filePath.toString());
        }
        // Load the prompt from the config now.
        return loadPromptFromConfig(config);
    }

}