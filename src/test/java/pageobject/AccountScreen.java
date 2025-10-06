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

    public void openMoreMenu() {
        androidActions.clickByPartialText("Mehr");
    }

    public void openMyAccount() {
        androidActions.clickByText("Mein Konto");
    }

//    public void verifyUserLoggedIn(String expectedEmail) {
//        Assert.assertTrue(androidActions.clickByPartialText("Eingeloggt als"),
//                "'Eingeloggt als' label not visible — login may have failed.");
//        Assert.assertTrue(androidActions.clickByPartialText(expectedEmail),
//                "Expected email not visible after login: " + expectedEmail);
//    }

    public boolean isEmailVisible(String email) {
        return androidActions.isElementWithTextPresent(email);
    }
}
