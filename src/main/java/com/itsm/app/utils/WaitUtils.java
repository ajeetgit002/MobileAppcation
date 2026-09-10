package com.itsm.app.utils;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumDriver;

public class WaitUtils {

	private final AppiumDriver driver;
	private final int defaultTimeoutInSeconds;

	// ==========================================
	// CONSTRUCTOR
	// ==========================================

	public WaitUtils(AppiumDriver driver, int defaultTimeoutInSeconds) {

		if (driver == null) {
			throw new IllegalArgumentException("AppiumDriver cannot be null");
		}

		if (defaultTimeoutInSeconds <= 0) {
			throw new IllegalArgumentException("Timeout must be greater than 0");
		}

		this.driver = driver;
		this.defaultTimeoutInSeconds = defaultTimeoutInSeconds;
	}

	public WaitUtils(AppiumDriver driver) {

		this(driver, 15);
	}

	// ==========================================
	// WAIT OBJECT
	// ==========================================

	private WebDriverWait getWait() {

		return new WebDriverWait(driver, Duration.ofSeconds(defaultTimeoutInSeconds));
	}

	private WebDriverWait getWait(int timeoutInSeconds) {

		if (timeoutInSeconds <= 0) {

			throw new IllegalArgumentException("Timeout must be greater than 0");
		}

		return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
	}

	// ==========================================
	// WAIT FOR VISIBLE
	// ==========================================

	public WebElement waitForVisible(By locator) {

		return getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	public WebElement waitForVisible(By locator, int timeoutInSeconds) {

		return getWait(timeoutInSeconds).until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	// ==========================================
	// WAIT FOR CLICKABLE
	// ==========================================

	public WebElement waitForClickable(By locator) {

		return getWait().until(ExpectedConditions.elementToBeClickable(locator));
	}

	public WebElement waitForClickable(By locator, int timeoutInSeconds) {

		return getWait(timeoutInSeconds).until(ExpectedConditions.elementToBeClickable(locator));
	}

	// ==========================================
	// WAIT FOR INVISIBLE
	// ==========================================

	public boolean waitForInvisible(By locator) {

		return getWait().until(ExpectedConditions.invisibilityOfElementLocated(locator));
	}

	public boolean waitForInvisible(By locator, int timeoutInSeconds) {

		return getWait(timeoutInSeconds).until(ExpectedConditions.invisibilityOfElementLocated(locator));
	}

	// ==========================================
	// WAIT FOR SELECTED
	// ==========================================

	public boolean waitForSelected(By locator) {

		return getWait().until(ExpectedConditions.elementToBeSelected(locator));
	}

	public boolean waitForSelected(By locator, int timeoutInSeconds) {

		return getWait(timeoutInSeconds).until(ExpectedConditions.elementToBeSelected(locator));
	}

	// ==========================================
	// WAIT FOR ENABLED
	// ==========================================

	public WebElement waitForEnabled(By locator) {

		return getWait().until(driver -> {

			WebElement element = driver.findElement(locator);

			if (element.isDisplayed() && element.isEnabled()) {

				return element;
			}

			return null;
		});
	}

	public WebElement waitForEnabled(By locator, int timeoutInSeconds) {

		return getWait(timeoutInSeconds).until(driver -> {

			WebElement element = driver.findElement(locator);

			if (element.isDisplayed() && element.isEnabled()) {

				return element;
			}

			return null;
		});
	}

	// ==========================================
	// WAIT FOR TEXT
	// ==========================================

	public boolean waitForText(By locator, String expectedText) {

		return getWait().until(ExpectedConditions.textToBePresentInElementLocated(locator, expectedText));
	}

	public boolean waitForText(By locator, String expectedText, int timeoutInSeconds) {

		return getWait(timeoutInSeconds)
				.until(ExpectedConditions.textToBePresentInElementLocated(locator, expectedText));
	}

	// ==========================================
	// WAIT FOR ATTRIBUTE
	// ==========================================

	public boolean waitForAttribute(By locator, String attribute, String expectedValue) {

		return getWait().until(ExpectedConditions.attributeContains(locator, attribute, expectedValue));
	}

	public boolean waitForAttribute(By locator, String attribute, String expectedValue, int timeoutInSeconds) {

		return getWait(timeoutInSeconds).until(ExpectedConditions.attributeContains(locator, attribute, expectedValue));
	}

	// ==========================================
	// WAIT FOR ALL VISIBLE
	// ==========================================

	public List<WebElement> waitForAllVisible(By locator) {

		return getWait().until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
	}

	public List<WebElement> waitForAllVisible(By locator, int timeoutInSeconds) {

		return getWait(timeoutInSeconds).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
	}

	// ==========================================
	// FAST CHECK - NO EXPLICIT WAIT
	// ==========================================

	public boolean isElementDisplayed(By locator) {

		try {

			return driver.findElement(locator).isDisplayed();

		} catch (Exception e) {

			return false;
		}
	}
}