package commonLibs.utils;

import java.util.UUID;

public class TestDataGenerator {

    public static String generateRandomEmail() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8); // short unique part
        return "testuser_" + uniqueId + "@example.com";
    }
}