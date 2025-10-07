package commonLibs.implementation;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

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

    public List<WebElement> getElementsByDescription(String description) {
        By locator = AppiumBy.androidUIAutomator(
                "new UiSelector().description(\"" + escapeForUiSelector(description) + "\")");
        waitUntilPresence(locator);
        return driver.findElements(locator);
    }



    public void assertViewByText(String text) {
        By locator = AppiumBy.androidUIAutomator(
                "new UiSelector().text(\"" + text + "\")"
        );
        WebElement element = driver.findElement(locator);
        Assert.assertTrue(element.isDisplayed(), "Element with text '" + text + "' not displayed!");
    }

    public WebElement viewByResourceId(String resourceId, int timeoutSeconds) {
        By locator = AppiumBy.androidUIAutomator(
                "new UiSelector().resourceId(\"" + resourceId + "\")"
        );

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
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

}
