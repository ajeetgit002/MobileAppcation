package com.itsm.app.utils;

import io.appium.java_client.AppiumDriver;
import java.util.HashMap;
import java.util.Map;

/**
 * KeyboardUtils provides keyboard-related actions. Instance-based - receives
 * driver through constructor dependency injection.
 */
public class KeyboardUtils {

	private final AppiumDriver driver;

	/**
	 * Initialize KeyboardUtils with driver.
	 *
	 * @param driver AppiumDriver instance
	 */
	public KeyboardUtils(AppiumDriver driver) {

		if (driver == null) {
			throw new IllegalArgumentException("AppiumDriver cannot be null");
		}

		this.driver = driver;
	}

	/**
	 * Hide keyboard using mobile command.
	 */
	public void hideKeyboard() {

		try {

			Map<String, Object> args = new HashMap<>();

			driver.executeScript("mobile: hideKeyboard", args);

			ReportManager.pass("Keyboard hidden successfully");

		} catch (Exception e) {

			ReportManager.fail("Unable to hide keyboard: " + e.getMessage());

			throw e;
		}
	}

	/**
	 * Hide keyboard if it's shown (doesn't throw if not shown).
	 */
	public void hideKeyboardIfShown() {

		try {

			Map<String, Object> args = new HashMap<>();

			driver.executeScript("mobile: hideKeyboard", args);

			ReportManager.pass("Keyboard hidden successfully");

		} catch (Exception e) {

			ReportManager.info("Keyboard was not visible or could not be hidden");
		}
	}
}