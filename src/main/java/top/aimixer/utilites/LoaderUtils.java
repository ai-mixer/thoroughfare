package top.aimixer.utilites;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.regex.*;

public class LoaderUtils {
    private static final String DEFAULT_REF = System.getenv("LANGCHAIN_HUB_DEFAULT_REF") != null ? System.getenv("LANGCHAIN_HUB_DEFAULT_REF") : "master";
    private static final String URL_BASE = System.getenv("LANGCHAIN_HUB_URL_BASE") != null ? System.getenv("LANGCHAIN_HUB_URL_BASE") : "https://raw.githubusercontent.com/hwchase17/langchain-hub/{ref}/";
    private static final Pattern HUB_PATH_RE = Pattern.compile("\"lc(?:@([^:]+))?://(.*)\";");

    public static <T> Optional<T> tryLoadFromHub(
            String path,
            Function<String, T> loader,
            String validPrefix,
            Set<String> validSuffixes
    ) {
        Matcher match = HUB_PATH_RE.matcher(path);
        if (!match.matches()) {
            return Optional.empty();
        }
        String ref = match.group("ref");
        ref = ref != null ? ref.substring(1) : DEFAULT_REF;
        String remotePathStr = match.group("path");
        Path remotePath = Paths.get(remotePathStr);
        if (!remotePath.getName(0).toString().equals(validPrefix)) {
            return Optional.empty();
        }
        if (!validSuffixes.contains(remotePath.getFileName().toString().substring(1))) {
            throw new IllegalArgumentException("Unsupported file type.");
        }

        String fullUrl = URL_BASE.replace("{ref}", ref) + remotePathStr;
        try {
            URL url = new URL(fullUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() != 200) {
                throw new IllegalArgumentException("Could not find file at " + fullUrl);
            }
            Path tempDir = Files.createTempDirectory("temp");
            Path file = tempDir.resolve(remotePath.getFileName());
            try (InputStream in = connection.getInputStream();
                 OutputStream out = Files.newOutputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            T result = loader.apply(file.toString());
            Files.deleteIfExists(file);
            Files.deleteIfExists(tempDir);
            return Optional.of(result);
        } catch (IOException e) {
            throw new RuntimeException("Error loading file from hub", e);
        }
    }
}
