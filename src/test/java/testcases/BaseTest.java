package testcases;

import com.aventstack.extentreports.Status;
import commonLibs.utils.*;
import io.appium.java_client.android.AndroidDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;
import commonLibs.implementation.CommonKeys;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Idempotent BaseTest - safe to call setup/teardown repeatedly.
 *
 * Key behavior:
 *  - Reporting is initialized once per JVM (@BeforeSuite).
 *  - Driver is created only if there is no active session.
 *  - Tear down quits driver only if active; runs safely multiple times.
 *  - Report flush executes once (guarded).
 */
public class BaseTest {

    protected String currentWorkingDirectory;
    protected AndroidDriver driver;
    protected String reportFilename;
    protected ReportUtils reportUtils;

    public commonLibs.utils.ScreenshotUtils screenshotControl;
    public CommonKeys cmnKeys;

    // flag to ensure report is flushed only once
    private static final AtomicBoolean REPORT_FLUSHED = new AtomicBoolean(false);

    /**
     * Initialize reporting once per JVM run. Idempotent: safe to call multiple times.
     */
    @BeforeSuite(alwaysRun = true)
    public synchronized void initReporting() {
        if (this.reportUtils != null) {
            // already initialized in this instance
            System.out.println("ReportUtils already initialized for this BaseTest instance.");
            return;
        }

        currentWorkingDirectory = System.getProperty("user.dir");
        String reportsDir = System.getProperty("report.dir", currentWorkingDirectory + File.separator + "reports");
        File dir = new File(reportsDir);
        if (!dir.exists()) {
            boolean ok = dir.mkdirs();
            if (!ok) {
                System.err.println("Warning: could not create reports directory: " + reportsDir);
            }
        }

        String defaultName = "report-" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + ".html";
        String filename = System.getProperty("report.filename", defaultName);
        reportFilename = new File(dir, filename).getAbsolutePath();

        // create reportUtils only if it does not exist (idempotent)
        try {
            this.reportUtils = new ReportUtils(reportFilename);
            System.out.println("Initialized Extent report: " + reportFilename);
        } catch (Exception e) {
            this.reportUtils = null;
            System.err.println("Failed to initialize ReportUtils: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Set up driver and helpers. Idempotent: if an active driver session exists, reuse it.
     */
    @BeforeClass(alwaysRun = true)
    public synchronized void setUp() throws Exception {
        currentWorkingDirectory = System.getProperty("user.dir");

        // Ensure screenshots dir exists (safe to call multiple times)
        File screenshotsDir = new File(currentWorkingDirectory + File.separator + "screenshots");
        if (!screenshotsDir.exists()) screenshotsDir.mkdirs();

        // Create driver only if no active session exists
        if (!isDriverActive()) {
            try {
                driver = DriverFactory.createDriver();
                System.out.println("Driver started: " + driver);
            } catch (Exception e) {
                System.err.println("Driver initialization failed: " + e.getMessage());
                throw e;
            }
        } else {
            System.out.println("Reusing existing active driver session.");
        }

        // Initialize or reuse helpers
        if (screenshotControl == null && isDriverActive()) {
            screenshotControl = new commonLibs.utils.ScreenshotUtils(driver);
        }
        if (cmnKeys == null && isDriverActive()) {
            cmnKeys = new CommonKeys(driver);
        }

        // Try to bring app to foreground if configured - best-effort
        if (isDriverActive()) {
            String appPackage = ConfigReader.get("app.package", "com.netbiscuits.bild.android");
            try {
                driver.activateApp(appPackage);
                if (reportUtils != null) {
                    reportUtils.addLogs(Status.INFO, "App activated: " + appPackage);
                }
            } catch (Exception e) {
                System.err.println("activateApp failed (non-fatal): " + e.getMessage());
            }
        }
    }

    /**
     * Create a per-test ExtentTest entry. Safe to call repeatedly.
     */
    @BeforeMethod(alwaysRun = true)
    public void startTest(Method method) {
        String testName = method.getDeclaringClass().getSimpleName() + "." + method.getName();
        if (reportUtils != null) {
            try {
                reportUtils.createATestcase(testName);
                reportUtils.addLogs(Status.INFO, "Starting test: " + testName);
            } catch (Exception e) {
                System.err.println("Failed to create test entry in report: " + e.getMessage());
            }
        } else {
            System.err.println("reportUtils is not initialized; skipping test node creation.");
        }
    }

    /**
     * Post-test reporting and screenshots. Defensive for missing reportUtils/screenshotControl.
     */
    @AfterMethod(alwaysRun = true)
    public void postTestActions(ITestResult result) {
        String testcaseName = result != null ? result.getName() : "unknown_test";
        String currentTime = DateUtils.getCurrentDateAndTime();
        String imageFilename = String.format("%s/screenshots/%s-%s.jpeg", currentWorkingDirectory, testcaseName,
                currentTime);

        try {
            if (result != null && result.getStatus() == ITestResult.FAILURE) {
                if (reportUtils != null) {
                    try {
                        reportUtils.addLogs(Status.FAIL, "Test failed: " + testcaseName);
                    } catch (Exception e) {
                        System.err.println("reportUtils.addLogs failed: " + e.getMessage());
                    }
                } else {
                    System.err.println("reportUtils is null — cannot add failure logs.");
                }

                if (screenshotControl != null && isDriverActive()) {
                    try {
                        screenshotControl.captureAndSaveScreenshot(imageFilename);
                        try {
                            if (reportUtils != null) reportUtils.addScreenshotInReport(imageFilename);
                        } catch (Exception e) {
                            System.err.println("Attaching screenshot failed: " + e.getMessage());
                        }
                    } catch (Exception e) {
                        System.err.println("Screenshot capture failed: " + e.getMessage());
                    }
                } else {
                    System.err.println("screenshotControl is null or driver inactive — skipping screenshots.");
                }
            } else if (result != null && result.getStatus() == ITestResult.SUCCESS) {
                if (reportUtils != null) reportUtils.addLogs(Status.PASS, "Test passed: " + testcaseName);
            } else if (result != null && result.getStatus() == ITestResult.SKIP) {
                if (reportUtils != null) reportUtils.addLogs(Status.SKIP, "Test skipped: " + testcaseName);
            }
        } finally {
            // Remove thread-local test to avoid memory leaks (if ReportUtils supports it)
            if (reportUtils != null) {
                try {
                    reportUtils.removeCurrentTest();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

    /**
     * Tear down driver only if active. Idempotent: safe to call multiple times.
     */
    @AfterClass(alwaysRun = true)
    public synchronized void tearDown() {
        if (isDriverActive()) {
            try {
                DriverFactory.quitDriver();
                // DriverFactory should nullify the static driver; also null local ref
                driver = null;
                System.out.println("Driver quit successfully.");
            } catch (Exception e) {
                System.err.println("Error while quitting driver: " + e.getMessage());
            }
        } else {
            System.out.println("No active driver session to quit.");
        }
    }

    /**
     * Flush report once per suite/JVM. Idempotent.
     */
    @AfterSuite(alwaysRun = true)
    public synchronized void postCleanup() {
        if (reportUtils != null && !REPORT_FLUSHED.get()) {
            try {
                reportUtils.flushReport();
                REPORT_FLUSHED.set(true);
                System.out.println("Report flushed to: " + reportFilename);
            } catch (Exception e) {
                System.err.println("Failed flushing report: " + e.getMessage());
                e.printStackTrace();
            }
        } else if (REPORT_FLUSHED.get()) {
            System.out.println("Report already flushed - skipping.");
        } else {
            System.err.println("postCleanup: reportUtils is null — nothing to flush.");
        }
    }

    @Test
    public void appIsRunning() {
        try {
            String pkg = driver.getCurrentPackage();
            // currentActivity returns a String like ".MainActivity" or the full component name
            String activity = driver.currentActivity();
            System.out.println("Current package: " + pkg);
            System.out.println("Current activity: " + activity);
            if (!"com.netbiscuits.bild.android".equals(pkg)) {
                throw new AssertionError("Unexpected package: " + pkg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Helper to check whether driver is active (session exists).
     */
    protected boolean isDriverActive() {
        if (driver == null) return false;
        try {
            // getSessionId will throw or return null if session is gone
            return driver.getSessionId() != null;
        } catch (Exception e) {
            return false;
        }
    }
}
