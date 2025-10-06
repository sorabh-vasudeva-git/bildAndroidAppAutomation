package commonLibs.utils;

import com.aventstack.extentreports.ExtentReporter;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

public class ReportUtils {

    private final ExtentReporter htmlReport;
    private final ExtentReports extentReport;

    // ThreadLocal to hold current test for each running thread (test method)
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    public ReportUtils(String filename) {
        this.htmlReport = new ExtentHtmlReporter(filename);
        this.extentReport = new ExtentReports();
        this.extentReport.attachReporter(this.htmlReport);
    }

    /**
     * Create a new ExtentTest for the current thread/test.
     */
    public void createATestcase(String testcaseName) {
        ExtentTest t = extentReport.createTest(testcaseName);
        extentTest.set(t);
    }

    /**
     * Add a log to the current thread's ExtentTest.
     * If there is no current test, create a fallback test node named "UNASSIGNED_TEST".
     */
    public void addLogs(Status status, String comment) {
        ExtentTest t = extentTest.get();
        if (t == null) {
            // create a small fallback test node so logs aren't lost
            ExtentTest fallback = extentReport.createTest("UNASSIGNED_TEST");
            fallback.log(status, comment);
            extentTest.set(fallback);
        } else {
            t.log(status, comment);
        }
    }

    /**
     * Attach screenshot for the current test.
     */
    public void addScreenshotInReport(String imageFilename) throws Exception {
        ExtentTest t = extentTest.get();
        if (t == null) {
            throw new IllegalStateException("No active test to attach screenshot to");
        }
        t.addScreenCaptureFromPath(imageFilename);
    }

    /**
     * Flush the report to disk (call once after the whole suite).
     */
    public synchronized void flushReport() {
        extentReport.flush();
    }

    /**
     * Remove the current thread's ExtentTest reference (call after test finished).
     */
    public void removeCurrentTest() {
        extentTest.remove();
    }
}
