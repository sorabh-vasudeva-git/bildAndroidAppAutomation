package commonLibs.implementation;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;

/**
 * Utility class for common Android hardware key actions.
 * Compatible with Appium Java Client 10.0.0.
 */
public class CommonKeys {

    private final AndroidDriver driver;

    public CommonKeys(AndroidDriver driver) {
        this.driver = driver;
    }

    /** Press the Back key. */
    public void pressBackKey() {
        driver.pressKey(new KeyEvent(AndroidKey.BACK));
    }

    /** Press the App-Switch (Recents) key. */
    public void pressAppSwitchKey() {
        driver.pressKey(new KeyEvent(AndroidKey.APP_SWITCH));
    }

    /** Press the Enter key. */
    public void pressEnterKey() {
        driver.pressKey(new KeyEvent(AndroidKey.ENTER));
    }

    /** Press the Search key. */
    public void pressSearchKey() {
        driver.pressKey(new KeyEvent(AndroidKey.SEARCH));
    }

    /** Press Volume Up. */
    public void pressVolumeUpKey() {
        driver.pressKey(new KeyEvent(AndroidKey.VOLUME_UP));
    }

    /** Press Volume Down. */
    public void pressVolumeDownKey() {
        driver.pressKey(new KeyEvent(AndroidKey.VOLUME_DOWN));
    }

    /** Press the Power key (toggle screen). */
    public void pressPowerKey() {
        driver.pressKey(new KeyEvent(AndroidKey.POWER));
    }

    /** Press the Camera key. */
    public void pressCameraKey() {
        driver.pressKey(new KeyEvent(AndroidKey.CAMERA));
    }

    /** Press the Tab key (useful for form navigation). */
    public void pressTabKey() {
        driver.pressKey(new KeyEvent(AndroidKey.TAB));
    }
}
