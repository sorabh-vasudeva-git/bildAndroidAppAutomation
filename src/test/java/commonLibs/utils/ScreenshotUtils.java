package commonLibs.utils;

import io.appium.java_client.android.AndroidDriver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;

public class ScreenshotUtils {
	
	private TakesScreenshot camera;

	public ScreenshotUtils(AndroidDriver driver) {

		camera = (TakesScreenshot) driver;
	}

	public void captureAndSaveScreenshot(String pathFile) throws Exception {

		pathFile = pathFile.trim();

		File imgFile, tmpFile;

		imgFile = new File(pathFile);

		if (imgFile.exists()) {
			throw new Exception("Image with this file name already exist..");
		}

		tmpFile = camera.getScreenshotAs(OutputType.FILE);

		FileUtils.moveFile(tmpFile, imgFile);
	}

}
