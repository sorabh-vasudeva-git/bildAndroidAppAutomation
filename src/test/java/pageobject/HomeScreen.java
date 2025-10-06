package pageobject;

import commonLibs.implementation.ElementActions;
import commonLibs.implementation.TouchGestures;
import io.appium.java_client.android.AndroidDriver;

/**
 * HomeScreen - encapsulates actions on the home/onboarding screens
 * (uses AndroidElementActions for all element interactions)
 */
public class HomeScreen {
    private final AndroidDriver driver;
    private final ElementActions androidActions;
    private final TouchGestures gestures;

    public HomeScreen(AndroidDriver driver, ElementActions androidActions, TouchGestures gestures) {
        this.driver = driver;
        this.androidActions = androidActions;
        this.gestures = gestures;
    }

    public void acceptConsentIfShown() {
        // Accept CMP if present
        try {
            androidActions.clickByText("Alle akzeptieren");
        } catch (Exception ignored) {}
    }

    public void continueOnboarding() {
        try {
            androidActions.clickByText("WEITER");
        } catch (Exception ignored) {}
    }

    public void submitJsIfShown() {
        try {
            androidActions.clickByResourceId("js-submit-button");
        } catch (Exception ignored) {}
    }

    public void activatePushIfShown() {
        try {
            androidActions.clickByText("ÜBERSPRINGEN");
        } catch (Exception ignored) {
        }
    }

    public void openLogin() {
        try {
            androidActions.clickByText("ANMELDEN");
        } catch (Exception ignored) {
        }
    }
}
