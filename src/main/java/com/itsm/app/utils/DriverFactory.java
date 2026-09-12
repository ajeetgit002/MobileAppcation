package com.itsm.app.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

public class DriverFactory {

	private static final String APPIUM_SERVER_URL = "http://127.0.0.1:4723";

	public static AppiumDriver createAndroidDriver() {
		return createAndroidDriver(new ConfigReader());
	}

	public static AppiumDriver createAndroidDriver(ConfigReader configReader) {

		try {

			UiAutomator2Options options = new UiAutomator2Options();

			// ==========================================
			// PLATFORM
			// ==========================================

			options.setPlatformName(configReader.getPlatformName());
			options.setAutomationName(configReader.getAutomationName());

			// ==========================================
			// DEVICE
			// ==========================================

			options.setDeviceName(configReader.getDeviceName());
			options.setPlatformVersion(configReader.getPlatformVersion());
			options.setUdid(configReader.getUdid());

			// ==========================================
			// APP CONFIGURATION
			// ==========================================

			String appPackage = configReader.getAppPackage();
			String appActivity = configReader.getAppActivity();
			String appPath = configReader.getAppPath();

			options.setAppPackage(appPackage);
			options.setAppActivity(appActivity);

			// Check whether app is already installed
			if (!isAppInstalled(configReader.getUdid(), appPackage)) {

				ReportManager.info("App is not installed. Installing APK: " + appPath);

				options.setApp(appPath);

			} else {

				ReportManager.info("App is already installed. Using existing application.");
			}

			// ==========================================
			// SESSION SETTINGS
			// ==========================================

			options.setNewCommandTimeout(Duration.ofSeconds(configReader.getNewCommandTimeout()));

			options.setNoReset(configReader.isNoReset());
			options.setFullReset(configReader.isFullReset());
			options.setAutoGrantPermissions(true);
			options.setSkipDeviceInitialization(true);
			options.setUiautomator2ServerLaunchTimeout(Duration.ofSeconds(60));
			options.setUiautomator2ServerInstallTimeout(Duration.ofSeconds(60));
			options.setAdbExecTimeout(Duration.ofSeconds(60));

			// ==========================================
			// APPIUM SERVER
			// ==========================================

			String serverUrl = configReader.getAppiumServerUrl();

			if (serverUrl == null || serverUrl.isBlank()) {
				serverUrl = APPIUM_SERVER_URL;
			}

			// ==========================================
			// CREATE DRIVER
			// ==========================================

			AppiumDriver driver = new AndroidDriver(new URL(serverUrl), options);

			ReportManager.info("Android driver created successfully" + " | Device: " + configReader.getDeviceName()
					+ " | App: " + appPackage);

			return driver;

		} catch (MalformedURLException e) {

			throw new RuntimeException("Invalid Appium server URL", e);

		} catch (Exception e) {

			throw new RuntimeException("Unable to create Android driver", e);
		}
	}

	// ==========================================
	// CHECK APP INSTALLED
	// ==========================================

	private static boolean isAppInstalled(String udid, String appPackage) {

		try {

			ProcessBuilder processBuilder = new ProcessBuilder("adb", "-s", udid, "shell", "pm", "list", "packages",
					appPackage);

			processBuilder.redirectErrorStream(true);

			Process process = processBuilder.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;

			while ((line = reader.readLine()) != null) {

				if (line.contains(appPackage)) {
					return true;
				}
			}

			process.waitFor();

			return false;

		} catch (Exception e) {

			throw new RuntimeException("Unable to check whether app is installed: " + appPackage, e);
		}
	}

	// ==========================================
	// QUIT DRIVER
	// ==========================================

	public static void quitDriver(AppiumDriver driver) {

		if (driver != null) {

			try {

				driver.quit();

				ReportManager.info("Driver quit successfully");

			} catch (Exception e) {

				ReportManager.warning("Error while quitting driver: " + e.getMessage());
			}
		}
	}
}