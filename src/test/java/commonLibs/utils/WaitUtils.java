package commonLibs.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WaitUtils {

    // Wait for condition with default timeout in seconds
    public static <T> T waitFor(WebDriver driver, ExpectedCondition<T> condition, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(condition);
    }

    // Convenience with default timeout
    public static <T> T waitFor(WebDriver driver, ExpectedCondition<T> condition) {
        return waitFor(driver, condition, 30);
    }
}
