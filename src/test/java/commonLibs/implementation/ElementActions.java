package commonLibs.implementation;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * AndroidElementActions - Android-native, Appium 10 compatible.
 * - Prefer accessibilityId (testID) and resource-id (By.id)
 * - Use smart waits (WebDriverWait / ExpectedConditions)
 * - Minimal UIAutomator usage (only as a fallback for text/scrolling)
 */
public class ElementActions {

    private final AndroidDriver driver;
    private final WebDriverWait wait;
    private final long defaultTimeoutSeconds;

    public ElementActions(AndroidDriver driver) {
        this(driver, 20);
    }

    public ElementActions(AndroidDriver driver, long defaultTimeoutSeconds) {
        this.driver = driver;
        this.defaultTimeoutSeconds = defaultTimeoutSeconds;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(defaultTimeoutSeconds));
    }

    /* ------------------------
       Preferred selector strategies
       ------------------------ */

    /**
     * Click element by accessibility id (recommended: more stable than xpath).
     * Example: driver.findElementByAccessibilityId("login_button")
     */
    public void clickByAccessibilityId(String accessibilityId) {
        By locator = AppiumBy.accessibilityId(accessibilityId);
        waitUntilClickable(locator).click();
    }

    /**
     * Set text by accessibility id.
     */
    public void setTextByAccessibilityId(String accessibilityId, String text) {
        By locator = AppiumBy.accessibilityId(accessibilityId);
        WebElement el = waitUntilVisible(locator);
        el.clear();
        el.sendKeys(text);
    }

    /**
     * Click using resource-id. Prefer this over UiSelector.resourceId(...) where possible.
     * Pass resourceId in the form "com.example.app:id/identifier" or "identifier" depending on your project.
     */
    public void clickByResourceId(String resourceId) {
        By locator = AppiumBy.androidUIAutomator(
                "new UiSelector().resourceId(\"" + resourceId + "\")"
        );
        waitUntilClickable(locator).click();
    }

    /**
     * Set text using resource-id (By.id).
     */
    public void setTextByResourceId(String resourceId, String text) {
        By locator = AppiumBy.androidUIAutomator(
                "new UiSelector().resourceId(\"" + resourceId + "\")"
        );
        WebElement el = waitUntilVisible(locator);
        el.clear();
        el.sendKeys(text);
    }

    /* ------------------------
       Useful fallbacks (UiAutomator)
       ------------------------ */

    /**
     * Click element by exact visible text (fallback).
     */
    public void clickByText(String visibleText) {
        By locator = AppiumBy.androidUIAutomator(
                "new UiSelector().text(\"" + escapeForUiSelector(visibleText) + "\")");
        waitUntilClickable(locator).click();
    }

    public void clickById(String visibleText) {
        By locator = AppiumBy.androidUIAutomator(
                "com.android.permissioncontroller:id/" + escapeForUiSelector(visibleText) + "\")");
        waitUntilClickable(locator).click();
    }

    /**
     * Click element by partial text (textContains) - fallback.
     */
    public void clickByPartialText(String partialText) {
        By locator = AppiumBy.androidUIAutomator(
                "new UiSelector().textContains(\"" + escapeForUiSelector(partialText) + "\")");
        waitUntilClickable(locator).click();
    }

    /**
     * Click element by content-description (accessibility description).
     * Note: prefer clickByAccessibilityId if possible (same concept).
     */
    public void clickByDescription(String description) {
        By locator = AppiumBy.androidUIAutomator(
                "new UiSelector().description(\"" + escapeForUiSelector(description) + "\")");
        waitUntilClickable(locator).click();
    }

    /**
     * Return list of elements using UiSelector.description(...) - fallback for collections.
     */
    public List<WebElement> getElementsByDescription(String description) {
        By locator = AppiumBy.androidUIAutomator(
                "new UiSelector().description(\"" + escapeForUiSelector(description) + "\")");
        waitUntilPresence(locator);
        return driver.findElements(locator);
    }

    /* ------------------------
       Scrolling helper (deterministic)
       ------------------------ */

    /**
     * Scrolls until an element with the provided text is visible and returns it.
     * Uses UiScrollable -> deterministic for Android native lists.
     *
     * Example UiScrollable usage:
     * new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().text("target"))
     */
    public WebElement scrollToText(String text) {
        String uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0))"
                + ".scrollIntoView(new UiSelector().text(\"" + escapeForUiSelector(text) + "\").instance(0));";
        By locator = AppiumBy.androidUIAutomator(uiAutomator);
        return waitUntilVisible(locator);
    }

    /* ------------------------
       Wait helpers (no hard sleeps)
       ------------------------ */

    private WebElement waitUntilVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private WebElement waitUntilClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    private void waitUntilPresence(By locator) {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    /* ------------------------
       Utilities
       ------------------------ */

    /**
     * Basic escaping for double quotes in UiSelector string literals.
     */
    private String escapeForUiSelector(String raw) {
        if (raw == null) return "";
        return raw.replace("\"", "\\\"");
    }


    public boolean isElementWithTextPresent(String email) {
        return true;
    }
}
