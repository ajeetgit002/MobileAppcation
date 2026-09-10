package com.itsm.app.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;

/**
 * ScrollUtils provides reusable scrolling methods.
 * All element-based operations use By locators to avoid stale element references.
 */
public class ScrollUtils {

	private final AppiumDriver driver;
	private final ScreenshotUtils screenshotUtils;

	public ScrollUtils(AppiumDriver driver, ScreenshotUtils screenshotUtils) {
		if (driver == null) {
			throw new IllegalArgumentException("AppiumDriver cannot be null");
		}
		if (screenshotUtils == null) {
			throw new IllegalArgumentException("ScreenshotUtils cannot be null");
		}
		this.driver = driver;
		this.screenshotUtils = screenshotUtils;
	}

	public ScrollUtils(AppiumDriver driver) {
		this(driver, new ScreenshotUtils(driver));
	}

	// ==========================================
	// SCROLL DOWN
	// ==========================================

	public void scrollDown() {
		scroll("down");
	}

	// ==========================================
	// SCROLL UP
	// ==========================================

	public void scrollUp() {
		scroll("up");
	}

	// ==========================================
	// SCROLL LEFT
	// ==========================================

	public void scrollLeft() {
		scroll("left");
	}

	// ==========================================
	// SCROLL RIGHT
	// ==========================================

	public void scrollRight() {
		scroll("right");
	}

	// ==========================================
	// COMMON SCROLL
	// ==========================================

	private void scroll(String direction) {
		try {
			Dimension size = driver.manage().window().getSize();
			Map<String, Object> params = new HashMap<>();
			params.put("left", 0);
			params.put("top", 0);
			params.put("width", size.getWidth());
			params.put("height", size.getHeight());
			params.put("direction", direction);
			params.put("percent", 0.75);

			driver.executeScript("mobile: scrollGesture", params);
			ReportManager.pass("Scrolled " + direction + " successfully");

		} catch (Exception e) {
			handleFailure("Unable to scroll " + direction, e);
			throw e;
		}
	}

	// ==========================================
	// SCROLL INSIDE ELEMENT
	// ==========================================

	public void scrollInsideElement(By locator, String direction, double percent) {
		try {
			WebElement container = driver.findElement(locator);

			Map<String, Object> params = new HashMap<>();
			params.put("left", container.getLocation().getX());
			params.put("top", container.getLocation().getY());
			params.put("width", container.getSize().getWidth());
			params.put("height", container.getSize().getHeight());
			params.put("direction", direction);
			params.put("percent", percent);

			driver.executeScript("mobile: scrollGesture", params);
			ReportManager.pass("Scrolled inside element successfully");

		} catch (Exception e) {
			handleFailure("Unable to scroll inside element: " + locator, e);
			throw e;
		}
	}

	// ==========================================
	// SCROLL UNTIL VISIBLE
	// ==========================================

	public void scrollUntilVisible(By locator) {
		scrollUntilVisible(locator, 10);
	}

	public void scrollUntilVisible(By locator, int maxScrolls) {
		if (locator == null) {
			throw new IllegalArgumentException("Locator cannot be null");
		}
		if (maxScrolls <= 0) {
			throw new IllegalArgumentException("Max scrolls must be greater than 0");
		}

		try {
			for (int i = 0; i < maxScrolls; i++) {
				if (isElementDisplayedNow(locator)) {
					ReportManager.pass("Element found after " + i + " scroll(s): " + locator);
					return;
				}
				scrollDown();
			}

			throw new RuntimeException("Element not found after " + maxScrolls + " scrolls: " + locator);

		} catch (Exception e) {
			handleFailure("Unable to find element after scrolling: " + locator, e);
			throw e;
		}
	}

	public boolean scrollUntilVisible(By locator, int maxScrolls, boolean returnResult) {
		for (int i = 0; i < maxScrolls; i++) {
			if (isElementDisplayedNow(locator)) {
				ReportManager.info("Element found after " + i + " scroll(s): " + locator);
				return true;
			}
			scrollDown();
		}

		ReportManager.info("Element not found after " + maxScrolls + " scroll(s): " + locator);
		return false;
	}

	private boolean isElementDisplayedNow(By locator) {
		try {
			List<WebElement> elements = driver.findElements(locator);
			return !elements.isEmpty() && elements.get(0).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	// ==========================================
	// FAILURE HANDLER
	// ==========================================

	private void handleFailure(String message, Exception exception) {
		ReportManager.fail(message + ": " + exception.getMessage());

		try {
			String screenshot = screenshotUtils.capture("ScrollFailure");
			ReportManager.attachScreenshot(screenshot);
		} catch (Exception screenshotException) {
			ReportManager.warning("Unable to capture screenshot: " + screenshotException.getMessage());
		}
	}
}