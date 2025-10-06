package pageobject;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class BildHomePage extends BasePage {

    private final AndroidDriver driver;

    // === Constructor ===
    public BildHomePage(AndroidDriver driver) {
        super(driver);
        this.driver = driver;

        // Initialize all @AndroidFindBy elements with timeout for element binding
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

}
