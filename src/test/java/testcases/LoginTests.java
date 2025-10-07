package testcases;

import commonLibs.implementation.ElementActions;
import commonLibs.utils.ConfigReader;
import commonLibs.utils.TestDataGenerator;
import io.appium.java_client.android.AndroidDriver;
import org.testng.annotations.Test;
import pageobject.AccountScreen;
import pageobject.LoginScreen;

public class LoginTests extends BaseTest{

    @Test(priority = 1, description = "To login with a registered user")
    public void loginWithRegisteredUser() throws Exception{

        AndroidDriver driver = this.driver;
        ElementActions androidActions = new ElementActions(driver);
        LoginScreen login = new LoginScreen(driver, androidActions);
        AccountScreen accountScreen = new AccountScreen(driver,androidActions);

        String testEmail = ConfigReader.get("registered.email", "");
        String testPassword = ConfigReader.get("registered.valid.password", "");


        login.clickMoreMehrButton();
        login.clickMyaccountMeinKonto();
        login.clickLoginButton();
        login.enterRegisteredUserEmail(testEmail);
        login.enterPassword(testPassword);
        login.submitLoginJETZTANMELDEN();
        accountScreen.verifyUserLoggedIn(testEmail);
    }
    @Test (priority =2,description = "To create a new user account")
    public void createANewUserAccount() throws Exception{

        AndroidDriver driver = this.driver;
        ElementActions androidActions = new ElementActions(driver);
        LoginScreen login = new LoginScreen(driver, androidActions);
        AccountScreen accountScreen = new AccountScreen(driver,androidActions);
        String testPassword = ConfigReader.get("registered.valid.password", "");
        String randomEmail = TestDataGenerator.generateRandomEmail();

        login.clickLogoutABMELDEN();
        login.clickCreateAccountKONTOANLEGEN();
        login.enterNewUserEmail(randomEmail);
        login.enterPassword(testPassword);
        login.clickCheckboxAccountCreation();
        login.clickCreateAccountKONTOANLEGEN();
        accountScreen.verifyUserLoggedIn(randomEmail);

    }

    @Test(priority =3,description = "To login with a not registered user")
    public void loginWithNotRegisteredUser() throws Exception {

        AndroidDriver driver = this.driver;
        ElementActions androidActions = new ElementActions(driver);
        LoginScreen login = new LoginScreen(driver, androidActions);
        String testPassword = ConfigReader.get("registered.valid.password", "");
        String randomEmail = TestDataGenerator.generateRandomEmail();


        login.clickLogoutABMELDEN();
        login.clickLoginButton();
        login.enterRegisteredUserEmail(randomEmail);
        login.enterPassword(testPassword);
        login.submitLoginJETZTANMELDEN();
    }

}
