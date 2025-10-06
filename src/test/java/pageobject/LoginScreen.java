package pageobject;

import commonLibs.implementation.ElementActions;
import io.appium.java_client.android.AndroidDriver;

/**
 * LoginScreen - encapsulates login interactions
 */
public class LoginScreen {
    private final AndroidDriver driver;
    private final ElementActions androidActions;

    public LoginScreen(AndroidDriver driver, ElementActions androidActions) {
        this.driver = driver;
        this.androidActions = androidActions;
    }

    public void enterEmail(String email) {
        // resource-id "identifier"
        androidActions.setTextByResourceId("identifier", email);
    }

    public void enterPassword(String password) {
        androidActions.setTextByResourceId("password", password);
    }

    public void submitLogin() {
        // button text "JETZT ANMELDEN"
        androidActions.clickByText("JETZT ANMELDEN");
    }
}
