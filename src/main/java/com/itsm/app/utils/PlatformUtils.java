package com.itsm.app.utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.Capabilities;

/**
 * PlatformUtils provides platform and device information queries.
 * Instance-based - receives driver through constructor dependency injection.
 */
public class PlatformUtils {

	private final AppiumDriver driver;

	/**
	 * Initialize PlatformUtils with driver.
	 *
	 * @param driver AppiumDriver instance
	 */
	public PlatformUtils(AppiumDriver driver) {

		if (driver == null) {
			throw new IllegalArgumentException("AppiumDriver cannot be null");
		}

		this.driver = driver;
	}

	/**
	 * Get platform name.
	 */
	public String getPlatformName() {

		try {

			Capabilities capabilities = driver.getCapabilities();

			String platform = String.valueOf(capabilities.getCapability("platformName"));

			ReportManager.info("Current platform: " + platform);

			return platform;

		} catch (Exception e) {

			ReportManager.fail("Unable to get platform name: " + e.getMessage());

			throw e;
		}
	}

	/**
	 * Check if platform is Android.
	 */
	public boolean isAndroid() {

		return getPlatformName().equalsIgnoreCase("Android");
	}

	/**
	 * Check if platform is iOS.
	 */
	public boolean isIOS() {

		return getPlatformName().equalsIgnoreCase("iOS");
	}

	/**
	 * Get device name.
	 */
	public String getDeviceName() {

		try {

			Capabilities capabilities = driver.getCapabilities();

			String deviceName = String.valueOf(capabilities.getCapability("deviceName"));

			ReportManager.info("Device name: " + deviceName);

			return deviceName;

		} catch (Exception e) {

			ReportManager.fail("Unable to get device name: " + e.getMessage());

			throw e;
		}
	}

	/**
	 * Get platform version.
	 */
	public String getPlatformVersion() {

		try {

			Capabilities capabilities = driver.getCapabilities();

			String version = String.valueOf(capabilities.getCapability("platformVersion"));

			ReportManager.info("Platform version: " + version);

			return version;

		} catch (Exception e) {

			ReportManager.fail("Unable to get platform version: " + e.getMessage());

			throw e;
		}
	}

	/**
	 * Get automation name.
	 */
	public String getAutomationName() {

		try {

			Capabilities capabilities = driver.getCapabilities();

			String automation = String.valueOf(capabilities.getCapability("automationName"));

			ReportManager.info("Automation engine: " + automation);

			return automation;

		} catch (Exception e) {

			ReportManager.fail("Unable to get automation name: " + e.getMessage());

			throw e;
		}
	}

	/**
	 * Execute action only for Android platform.
	 */
	public void executeForAndroid(Runnable action) {

		if (isAndroid()) {

			ReportManager.info("Executing Android-specific action");

			action.run();
		}
	}

	/**
	 * Execute action only for iOS platform.
	 */
	public void executeForIOS(Runnable action) {

		if (isIOS()) {

			ReportManager.info("Executing iOS-specific action");

			action.run();
		}
	}

	/**
	 * Log all platform information to report.
	 */
	public void logPlatformInfo() {

		ReportManager.info("Platform: " + getPlatformName());

		ReportManager.info("Device: " + getDeviceName());

		ReportManager.info("Platform Version: " + getPlatformVersion());

		ReportManager.info("Automation: " + getAutomationName());
	}
}