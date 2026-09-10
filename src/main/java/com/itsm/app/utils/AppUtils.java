package com.itsm.app.utils;

import io.appium.java_client.AppiumDriver;
import java.util.HashMap;
import java.util.Map;

/**
 * AppUtils provides app lifecycle and state management functions.
 * Instance-based - receives driver through constructor dependency injection.
 * Uses Appium 3.x compatible mobile commands.
 */
public class AppUtils {

	private final AppiumDriver driver;

	/**
	 * Initialize AppUtils with driver.
	 *
	 * @param driver AppiumDriver instance
	 */
	public AppUtils(AppiumDriver driver) {

		if (driver == null) {
			throw new IllegalArgumentException("AppiumDriver cannot be null");
		}

		this.driver = driver;
	}

	// ==========================================
	// LAUNCH / ACTIVATE APP
	// ==========================================

	public void launchApp(String appPackage) {

		try {

			Map<String, Object> args = new HashMap<>();
			args.put("appId", appPackage);

			driver.executeScript("mobile: activateApp", args);

			ReportManager.pass("Application launched successfully: " + appPackage);

		} catch (Exception e) {

			ReportManager.fail("Unable to launch application: " + appPackage + " | " + e.getMessage());

			throw e;
		}
	}

	// ==========================================
	// TERMINATE APP
	// ==========================================

	public void terminateApp(String appPackage) {

		try {

			Map<String, Object> args = new HashMap<>();
			args.put("appId", appPackage);

			driver.executeScript("mobile: terminateApp", args);

			ReportManager.pass("Application terminated successfully: " + appPackage);

		} catch (Exception e) {

			ReportManager.fail("Unable to terminate application: " + appPackage + " | " + e.getMessage());

			throw e;
		}
	}

	// ==========================================
	// CHECK APP INSTALLED
	// ==========================================

	public boolean isAppInstalled(String appPackage) {

		try {

			Map<String, Object> args = new HashMap<>();
			args.put("appId", appPackage);

			Object result = driver.executeScript("mobile: isAppInstalled", args);

			boolean installed = Boolean.parseBoolean(String.valueOf(result));

			ReportManager.info("Application installed status: " + appPackage + " = " + installed);

			return installed;

		} catch (Exception e) {

			ReportManager.fail("Unable to check application installation: " + appPackage);

			throw e;
		}
	}

	// ==========================================
	// RESET APP
	// ==========================================

	public void resetApp(String appPackage) {

		try {

			terminateApp(appPackage);

			ReportManager.pass("Application reset successfully: " + appPackage);

		} catch (Exception e) {

			ReportManager.fail("Unable to reset application: " + appPackage);

			throw e;
		}
	}

	// ==========================================
	// RUN APP IN BACKGROUND
	// ==========================================

	public void runInBackground(int seconds) {

		try {

			Map<String, Object> args = new HashMap<>();
			args.put("seconds", seconds);

			driver.executeScript("mobile: backgroundApp", args);

			ReportManager.info("Application moved to background for " + seconds + " seconds");

		} catch (Exception e) {

			ReportManager.fail("Unable to move application to background: " + e.getMessage());

			throw e;
		}
	}

	// ==========================================
	// BRING APP TO FOREGROUND
	// ==========================================

	public void bringToForeground(String appPackage) {

		launchApp(appPackage);

		ReportManager.pass("Application brought to foreground: " + appPackage);
	}

	// ==========================================
	// CHECK APP STATE
	// ==========================================

	public int getAppState(String appPackage) {

		try {

			Map<String, Object> args = new HashMap<>();
			args.put("appId", appPackage);

			Object result = driver.executeScript("mobile: queryAppState", args);

			int state = Integer.parseInt(String.valueOf(result));

			ReportManager.info("Application state for " + appPackage + " = " + getStateName(state));

			return state;

		} catch (Exception e) {

			ReportManager.fail("Unable to query application state: " + appPackage);

			throw e;
		}
	}

	// ==========================================
	// CHECK APP RUNNING
	// ==========================================

	public boolean isAppRunning(String appPackage) {

		int state = getAppState(appPackage);

		/*
		 * Appium states:
		 *
		 * 0 = NOT_INSTALLED 1 = NOT_RUNNING 2 = RUNNING_IN_BACKGROUND_SUSPENDED 3 =
		 * RUNNING_IN_BACKGROUND 4 = RUNNING_IN_FOREGROUND
		 */

		return state == 2 || state == 3 || state == 4;
	}

	// ==========================================
	// STATE NAME
	// ==========================================

	private String getStateName(int state) {

		switch (state) {

		case 0:
			return "NOT_INSTALLED";

		case 1:
			return "NOT_RUNNING";

		case 2:
			return "RUNNING_IN_BACKGROUND_SUSPENDED";

		case 3:
			return "RUNNING_IN_BACKGROUND";

		case 4:
			return "RUNNING_IN_FOREGROUND";

		default:
			return "UNKNOWN";
		}
	}
}