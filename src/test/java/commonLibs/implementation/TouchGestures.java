package commonLibs.implementation;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.interactions.Pause;

import java.time.Duration;
import java.util.Collections;

/**
 * TouchGestures - Minimal Android gesture helper (Appium 10 compatible)
 * Includes only the most common actions:
 *  - tap
 *  - longPress
 *  - swipeUp / swipeDown
 */
public class TouchGestures {

    private final AndroidDriver driver;

    public TouchGestures(AndroidDriver driver) {
        this.driver = driver;
    }

    /* ------------------------
       1. Tap
       ------------------------ */
    public void tap(WebElement element) {
        Point center = getCenter(element);
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);
        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), center.x, center.y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(tap));
    }

    /* ------------------------
       2. Long Press
       ------------------------ */
    public void longPress(WebElement element, int durationMs) {
        Point center = getCenter(element);
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence longPress = new Sequence(finger, 1);
        longPress.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), center.x, center.y));
        longPress.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        longPress.addAction(new Pause(finger, Duration.ofMillis(durationMs)));
        longPress.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(longPress));
    }

    /* ------------------------
       3. Swipe Up / Down
       ------------------------ */
    public void swipeUp() {
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.8);
        int endY = (int) (size.height * 0.2);
        performSwipe(startX, startY, startX, endY, 800);
    }

    public void swipeDown() {
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.2);
        int endY = (int) (size.height * 0.8);
        performSwipe(startX, startY, startX, endY, 800);
    }

    /* ------------------------
       Helper methods
       ------------------------ */
    private void performSwipe(int startX, int startY, int endX, int endY, int durationMs) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(new Pause(finger, Duration.ofMillis(100)));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(durationMs),
                PointerInput.Origin.viewport(), endX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(swipe));
    }

    private Point getCenter(WebElement element) {
        Point p = element.getLocation();
        Dimension s = element.getSize();
        return new Point(p.x + s.width / 2, p.y + s.height / 2);
    }
}
