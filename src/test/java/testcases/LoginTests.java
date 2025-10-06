package testcases;

import com.aventstack.extentreports.Status;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest{

    @Test (description = "To create a new user account")
    public void createANewUserAccount() throws Exception{
        reportUtils.createATestcase(testName);
        reportUtils.addLogs(Status.INFO, "To create a new user account: " + testName);

    }

    @Test(description = "To login with a not registered user")
    public void loginWithNotRegisteredUser() throws Exception{

    }

    @Test(description = "To login with a registered user")
    public void loginWithRegisteredUser() throws Exception{

    }
}
