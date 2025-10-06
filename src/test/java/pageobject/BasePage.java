package pageobject;

import commonLibs.implementation.ElementActions;
import commonLibs.implementation.TouchGestures;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;

public class BasePage {


    ElementActions elementActions;
    TouchGestures touchGestures;

    public BasePage(AndroidDriver driver) {
        elementActions = new ElementActions(driver);
        touchGestures= new TouchGestures(driver);
    }
}
