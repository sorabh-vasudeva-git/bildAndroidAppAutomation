package testcases;

import commonLibs.implementation.ElementActions;
import commonLibs.implementation.TouchGestures;
import io.appium.java_client.android.AndroidDriver;
import org.testng.annotations.Test;
import pageobject.BildHomePage;
import pageobject.HomeScreen;


/**
 * App Launch (TestNG)
 */
public class AppLaunchTests extends BaseTest{

    @Test (priority = 1,description = "Verify First Time App launch Scenarios")
    public void verifyFirstTimeAppLaunch() throws Exception {
        reportUtils.createATestcase("Verify First Time App launch Scenarios");
        AndroidDriver driver = this.driver;
        ElementActions androidActions = new ElementActions(driver);
        BildHomePage bildHomePage = new BildHomePage(driver, androidActions);
        // Flow

        bildHomePage.verifyBildLogoAtLaunch();
    }
    @Test (priority =2,description = "Verify Onboarding screens")
    public void verifyOnboardingScreens() throws Exception {

        reportUtils.createATestcase("Verify Onboarding screens");
        AndroidDriver driver = this.driver;
        ElementActions androidActions = new ElementActions(driver);
        TouchGestures gestures = new TouchGestures(driver);
        HomeScreen home = new HomeScreen(driver, androidActions, gestures);

        home.acceptConsentIfShown();
        home.continueOnboarding();
        home.submitJsIfShown();
        home.activatePushIfShown();
        home.onboardingNeinDanke();
    }

    @Test (priority = 3, description = "Verify footer icons")
    public void verifyFooterIcons() throws Exception {
        reportUtils.createATestcase("Verify footer icons");
        AndroidDriver driver = this.driver;
        ElementActions androidActions = new ElementActions(driver);
        BildHomePage bildHomePage = new BildHomePage(driver, androidActions);

        bildHomePage.verifyFooter();
    }
}
