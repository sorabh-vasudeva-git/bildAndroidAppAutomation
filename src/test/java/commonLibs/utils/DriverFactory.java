package commonLibs.utils;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class DriverFactory {
    private static AndroidDriver driver;

    public static AndroidDriver createDriver() throws MalformedURLException {
        if (driver != null) return driver;

        // Load from config.properties
        String serverUrl = ConfigReader.get("appium.server.url", "http://127.0.0.1:4723");
        String deviceName = ConfigReader.get("device.name");
        String appPackage = ConfigReader.get("app.package", "com.netbiscuits.bild.android");
        String appActivity = ConfigReader.get("app.activity", "de.bild.android.app.MainActivity");
        String automationName = ConfigReader.get("automation.name", "UiAutomator2");
        boolean noReset = Boolean.parseBoolean(ConfigReader.get("no.reset", "true"));

        // ---- New: prefer environment variables if provided (Option B) ----
        // Set DEVICE_NAME and/or AVD_NAME in the environment to override local config.
        String deviceNameEnv = System.getenv("DEVICE_NAME");
        String avdNameEnv = System.getenv("AVD_NAME");

        if (deviceNameEnv != null && !deviceNameEnv.isBlank()) {
            deviceName = deviceNameEnv;
        }

        if (deviceName == null || deviceName.isBlank()) {
            deviceName = findFirstConnectedDevice();
            if (deviceName == null) {
                throw new RuntimeException("No connected device found and device.name not provided in config.properties");
            }
        }
        // -------------------------------------------------------------------

        UiAutomator2Options options = new UiAutomator2Options()
                .setAutomationName(automationName)
                .setNoReset(noReset)
                .setAppPackage(appPackage)
                .setAppActivity(appActivity)
                .setDeviceName(deviceName);

        // ---- New: if AVD_NAME provided via env, tell Appium to launch that AVD ----
        if (avdNameEnv != null && !avdNameEnv.isBlank()) {
            options.setAvd(avdNameEnv);
        }
        // (optional stability timeouts — uncomment if needed)
        // options.setAdbExecTimeout(Duration.ofMillis(60000));
        // options.setUiautomator2ServerLaunchTimeout(Duration.ofMillis(50000));
        // ------------------------------------------------------------------------

        System.out.println("🚀 Starting Appium driver with settings:");
        System.out.println("Server URL: " + serverUrl);
        System.out.println("Device Name: " + deviceName);
        if (avdNameEnv != null) System.out.println("AVD to launch: " + avdNameEnv);
        System.out.println("App Package: " + appPackage);
        System.out.println("App Activity: " + appActivity);

        driver = new AndroidDriver(new URL(serverUrl), options);
        return driver;
    }


    public static AndroidDriver getDriver() {
        return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    private static String findFirstConnectedDevice() {
        try {
            ProcessBuilder pb = new ProcessBuilder("adb", "devices");
            Process p = pb.start();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                boolean skippedHeader = false;
                while ((line = br.readLine()) != null) {
                    if (!skippedHeader) { skippedHeader = true; continue; }
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    String[] parts = line.split("\\s+");
                    if (parts.length >= 2 && "device".equals(parts[1])) {
                        return parts[0];
                    }
                }
            }
            p.waitFor();
        } catch (Exception e) {
            System.err.println("Warning: failed to run adb devices: " + e.getMessage());
        }
        return null;
    }
}
