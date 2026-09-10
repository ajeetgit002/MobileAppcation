package com.itsm.app.utils;

import io.appium.java_client.AppiumDriver;

public final class DriverManager {

	private static final ThreadLocal<AppiumDriver> DRIVER = new ThreadLocal<>();

	private DriverManager() {
	}

	// ==========================================
	// SET DRIVER
	// ==========================================

	public static void setDriver(AppiumDriver driver) {

		if (driver == null) {
			throw new IllegalArgumentException("AppiumDriver cannot be null");
		}

		DRIVER.set(driver);
	}

	// ==========================================
	// GET DRIVER
	// ==========================================

	public static AppiumDriver getDriver() {

		AppiumDriver driver = DRIVER.get();

		if (driver == null) {
			throw new IllegalStateException("AppiumDriver is not initialized " + "for the current thread.");
		}

		return driver;
	}

	// ==========================================
	// CHECK DRIVER
	// ==========================================

	public static boolean isDriverInitialized() {

		return DRIVER.get() != null;
	}

	// ==========================================
	// QUIT DRIVER
	// ==========================================

	public static void quitDriver() {

		AppiumDriver driver = DRIVER.get();

		try {

			if (driver != null) {
				driver.quit();
			}

		} finally {

			DRIVER.remove();
		}
	}
}