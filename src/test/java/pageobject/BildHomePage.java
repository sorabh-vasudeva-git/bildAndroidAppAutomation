package pageobject;

import commonLibs.implementation.ElementActions;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import java.time.Duration;

public class BildHomePage extends BasePage {

    private final AndroidDriver driver;
    private final ElementActions androidActions;

    // === Constructor ===
    public BildHomePage(AndroidDriver driver, ElementActions androidActions) {
        super(driver);
        this.driver = driver;
        this.androidActions = androidActions;

        // Initialize all @AndroidFindBy elements with timeout for element binding
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    public void verifyBildLogoAtLaunch() {
            String resId = "com.netbiscuits.bild.android:id/decor_content_parent";

            // this will wait up to the default timeout (20s) for visibility
            WebElement el = androidActions.viewByResourceId(resId,30);

            // assertions
            Assert.assertNotNull(el, "Expected element with resource-id " + resId + " to be present at launch");
            Assert.assertTrue(el.isDisplayed(), "Expected element with resource-id " + resId + " to be visible at launch");
    }

    public void verifyFooter() {
        androidActions.assertViewByText("Startseite");
        androidActions.assertViewByText("Sport");
        androidActions.assertViewByText("BILDplay");
        androidActions.assertViewByText("BILD-KI");
        androidActions.assertViewByText("Mehr");

    }

}
