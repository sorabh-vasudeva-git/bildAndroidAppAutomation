package pageobject;

import commonLibs.implementation.ElementActions;
import io.appium.java_client.android.AndroidDriver;
import org.testng.Assert;

/**
 * AccountScreen - encapsulates account-related verifications
 */
public class AccountScreen {
    private final AndroidDriver driver;
    private final ElementActions androidActions;

    public AccountScreen(AndroidDriver driver, ElementActions androidActions) {
        this.driver = driver;
        this.androidActions = androidActions;
    }

    public void verifyUserLoggedIn(String expectedEmail) {
        androidActions.assertViewByText(expectedEmail);
    }

}
