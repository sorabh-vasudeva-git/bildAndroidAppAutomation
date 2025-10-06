package commonLibs.utils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Minimal AppInstaller
 * - installSplitApksIfMissing(avdName, appPackage, apkDir)
 * - uninstall(appPackage, avdName)
 *
 * avdName: AVD name like "Pixel_9_API_36". If null or blank, reads from ConfigReader.get("device.name").
 * apkDir: directory containing base + split apks (relative to project root or absolute)
 *
 * Requirements: adb must be on PATH.
 */
public class AppInstaller {

    private static final int WAIT_SECONDS = 60; // short wait for emulator to appear

    /** Public: install all apks in apkDir if appPackage not already installed. */
    public static void installSplitApksIfMissing(String avdName, String appPackage, String apkDir)
            throws Exception {
        String effectiveAvd = chooseAvd(avdName);
        String deviceId = findDeviceIdForAvd(effectiveAvd, WAIT_SECONDS);
        if (deviceId == null) {
            throw new IllegalStateException("No running emulator found for AVD: " + effectiveAvd);
        }

        if (isPackageInstalled(deviceId, appPackage)) {
            System.out.println("App already installed: " + appPackage + " on " + deviceId);
            return;
        }

        File dir = new File(resolvePath(apkDir));
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("APK directory not found: " + dir.getAbsolutePath());
        }

        File[] apks = dir.listFiles((d, n) -> n.toLowerCase().endsWith(".apk"));
        if (apks == null || apks.length == 0) {
            throw new IllegalArgumentException("No APK files found in: " + dir.getAbsolutePath());
        }

        List<String> apkPaths = Arrays.stream(apks)
                .sorted(Comparator.comparing(File::getName))
                .map(File::getAbsolutePath)
                .collect(Collectors.toList());

        List<String> cmd = new ArrayList<>();
        cmd.add("adb"); cmd.add("-s"); cmd.add(deviceId);
        cmd.add("install-multiple"); cmd.add("-r");
        cmd.addAll(apkPaths);

        System.out.println("Installing APKs on " + deviceId + " ...");
        runCommandAndPrint(cmd);
        System.out.println("Install finished.");
    }

    /** Public: uninstall the package from the emulator identified by avdName (or config). */
    public static void uninstall(String appPackage, String avdName) throws Exception {
        String effectiveAvd = chooseAvd(avdName);
        String deviceId = findDeviceIdForAvd(effectiveAvd, WAIT_SECONDS);
        if (deviceId == null) {
            System.out.println("No running emulator found for AVD: " + effectiveAvd + " — skipping uninstall.");
            return;
        }

        if (!isPackageInstalled(deviceId, appPackage)) {
            System.out.println("Package not installed on " + deviceId + ": " + appPackage);
            return;
        }

        List<String> cmd = Arrays.asList("adb", "-s", deviceId, "uninstall", appPackage);
        System.out.println("Uninstalling " + appPackage + " from " + deviceId + " ...");
        runCommandAndPrint(cmd);
        System.out.println("Uninstall finished.");
    }

    /* ---------------------- helper methods ---------------------- */

    private static String chooseAvd(String avdName) {
        if (avdName != null && !avdName.isBlank()) return avdName;
        String cfg = ConfigReader.get("device.name");
        if (cfg == null || cfg.isBlank()) {
            throw new IllegalArgumentException("device name not provided and device.name missing in config");
        }
        return cfg;
    }

    /** Find runtime device id (emulator-5554) for the given avdName. Waits up to waitSeconds. */
    private static String findDeviceIdForAvd(String avdName, int waitSeconds) {
        int waited = 0;
        while (waited < waitSeconds) {
            Map<String, String> map = listDevicesWithAvdNames();
            for (Map.Entry<String, String> e : map.entrySet()) {
                if (avdName.equalsIgnoreCase(e.getValue())) {
                    return e.getKey();
                }
            }
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            waited += 2;
        }
        return null;
    }

    /** Return map of deviceId -> avdName (may return "unknown" if avd name not extractable). */
    private static Map<String, String> listDevicesWithAvdNames() {
        Map<String, String> result = new HashMap<>();
        try {
            ProcessBuilder pb = new ProcessBuilder("adb", "devices", "-l");
            Process p = pb.start();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                boolean headerSkipped = false;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (!headerSkipped) { headerSkipped = true; continue; }
                    if (line.isEmpty()) continue;
                    if (line.contains("device")) {
                        String[] parts = line.split("\\s+");
                        String id = parts[0];
                        String avd = extractAvdName(id);
                        result.put(id, avd);
                    }
                }
            }
            p.waitFor();
        } catch (Exception e) {
            System.err.println("listDevicesWithAvdNames error: " + e.getMessage());
        }
        return result;
    }

    /** Run "adb -s <device> emu avd name" to get avd name; returns "unknown" on failure. */
    private static String extractAvdName(String deviceId) {
        try {
            ProcessBuilder pb = new ProcessBuilder("adb", "-s", deviceId, "emu", "avd", "name");
            Process p = pb.start();
            String out;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                out = br.lines().collect(Collectors.joining()).trim();
            }
            p.waitFor();
            return out.isEmpty() ? "unknown" : out;
        } catch (Exception e) {
            return "unknown";
        }
    }

    private static boolean isPackageInstalled(String deviceId, String packageName) {
        try {
            ProcessBuilder pb = new ProcessBuilder("adb", "-s", deviceId, "shell", "pm", "list", "packages", packageName);
            Process p = pb.start();
            String out;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                out = br.lines().collect(Collectors.joining("\n"));
            }
            p.waitFor();
            return out.contains("package:" + packageName);
        } catch (Exception e) {
            System.err.println("isPackageInstalled error: " + e.getMessage());
            return false;
        }
    }

    private static void runCommandAndPrint(List<String> cmd) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("[adb] " + line);
            }
        }
        int exit = p.waitFor();
        if (exit != 0) throw new RuntimeException("Command failed with exit code " + exit + " : " + cmd);
    }

    private static String resolvePath(String path) {
        if (path == null) return null;
        File f = new File(path);
        if (f.isAbsolute()) return path;
        return System.getProperty("user.dir") + File.separator + path;
    }
}
