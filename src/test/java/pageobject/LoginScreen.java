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
    public void clickButtonByText(String buttonText) {
        androidActions.clickByText(buttonText);
    }

    public void enterRegisteredUserEmail(String email) {
        androidActions.setTextByResourceId("identifier", email);
    }

    public void enterNewUserEmail(String email){
        androidActions.setTextByResourceId("traits.email", email);
    }

    public void enterPassword(String password) {
        androidActions.setTextByResourceId("password", password);
    }

    public void submitLoginJETZTANMELDEN() {
        clickButtonByText("JETZT ANMELDEN");
    }
    public void clickLogoutABMELDEN(){
        clickButtonByText("ABMELDEN");
    }

    public void clickMoreMehrButton() {
        clickButtonByText("Mehr");
    }
    public void clickMyaccountMeinKonto() {
        clickButtonByText("Mein Konto");
    }
    public void clickLoginButton(){
        clickButtonByText("LOGIN");
    }
    public void clickCreateAccountKONTOANLEGEN(){
        clickButtonByText("KONTO ANLEGEN");
    }

    public void clickCheckboxAccountCreation(){
        androidActions.clickByResourceId("traits.optIns.Medienangebote_AS_2020");
    }

}
