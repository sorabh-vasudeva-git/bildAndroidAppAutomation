package testcases;

import commonLibs.implementation.ElementActions;
import commonLibs.implementation.TouchGestures;
import io.appium.java_client.android.AndroidDriver;
import org.testng.Assert;
import org.testng.annotations.Test;
import pageobject.AccountScreen;
import pageobject.HomeScreen;
import pageobject.LoginScreen;
import commonLibs.utils.ConfigReader;

/**
 * PositiveTests (TestNG)
 */
public class AppLaunch extends BaseTest {

    @Test (description = "Verify First Time App launch Scenarios")
    public void verifyFirstTimeAppLaunch() throws Exception {
        AndroidDriver driver = this.driver; // from BaseTest
        ElementActions androidActions = new ElementActions(driver);
        TouchGestures gestures = new TouchGestures(driver);

        HomeScreen home = new HomeScreen(driver, androidActions, gestures);
        LoginScreen login = new LoginScreen(driver, androidActions);
        AccountScreen account = new AccountScreen(driver, androidActions);

        String testEmail = ConfigReader.get("test.email", "");
        String testPassword = ConfigReader.get("test.password", "");

        // Flow
        home.acceptConsentIfShown();
        home.continueOnboarding();
        home.submitJsIfShown();
        home.activatePushIfShown();

        home.openLogin();

        login.enterEmail(testEmail);
        login.enterPassword(testPassword);
        login.submitLogin();

        // recovery: if article displayed, go back and swipe up a bit
        try {
            if (!androidActions.getElementsByDescription("article").isEmpty()) {
                driver.navigate().back();
                gestures.swipeUp();
            }
        } catch (Exception ignored) {}

        account.openMoreMenu();
        account.openMyAccount();

        // verify
        Assert.assertTrue(account.isEmailVisible(testEmail), "Username should be displayed");
    }

}
