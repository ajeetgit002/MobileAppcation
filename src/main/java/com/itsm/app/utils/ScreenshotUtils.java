package com.itsm.app.utils;

import io.appium.java_client.AppiumDriver;
import java.io.File;
import java.io.IOException;
import org.openqa.selenium.OutputType;
import org.apache.commons.io.FileUtils;

/**
 * ScreenshotUtils captures screenshots from the driver. Instance-based -
 * receives driver through constructor dependency injection.
 */
public class ScreenshotUtils {

	private final AppiumDriver driver;

	/**
	 * Initialize ScreenshotUtils with driver.
	 *
	 * @param driver AppiumDriver instance
	 */
	public ScreenshotUtils(AppiumDriver driver) {

		if (driver == null) {
			throw new IllegalArgumentException("AppiumDriver cannot be null");
		}

		this.driver = driver;
	}

	// ==========================================
	// CAPTURE SCREENSHOT
	// ==========================================

	/**
	 * Capture screenshot with custom name. Saves to screenshots/ directory with
	 * timestamp.
	 *
	 * @param name base name for screenshot
	 * @return path to saved screenshot
	 */
	public String capture(String name) {

		try {

			String directory = System.getProperty("user.dir") + File.separator + "screenshots";

			File folder = new File(directory);

			if (!folder.exists()) {
				folder.mkdirs();
			}

			String filePath = directory + File.separator + name + "_" + DateTimeUtils.getTimestamp() + ".png";

			File source = driver.getScreenshotAs(OutputType.FILE);

			File destination = new File(filePath);

			FileUtils.copyFile(source, destination);

			ReportManager.info("Screenshot captured: " + filePath);

			return filePath;

		} catch (IOException e) {

			ReportManager.warning("Unable to save screenshot: " + e.getMessage());

			throw new RuntimeException("Screenshot capture failed", e);
		}
	}

	/**
	 * Static convenience method to capture screenshot from current thread's driver.
	 *
	 * @param name base name for screenshot
	 * @return path to saved screenshot
	 */
	public static String captureFromCurrentDriver(String name) {
		if (!DriverManager.isDriverInitialized()) {
			return null;
		}
		return new ScreenshotUtils(DriverManager.getDriver()).capture(name);
	}
}