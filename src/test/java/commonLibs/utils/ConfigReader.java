package commonLibs.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        properties = new Properties();
        String configPath = System.getProperty("user.dir") + "/mobile-test-challenge/config/config.properties";
        try (InputStream input = new FileInputStream(configPath)) {
            properties.load(input);
            System.out.println("Loaded config from: " + configPath);
        } catch (IOException e) {
            System.err.println("⚠️ Could not read config.properties. Using defaults. " + e.getMessage());
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
