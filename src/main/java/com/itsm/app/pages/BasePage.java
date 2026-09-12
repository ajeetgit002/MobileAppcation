package com.itsm.app.pages;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.itsm.app.utils.DriverManager;
import com.itsm.app.utils.ReportManager;

import io.appium.java_client.AppiumDriver;

/**
 * BasePage provides core synchronization, interaction, and gesture capabilities
 * for all Page Objects in the framework.
 */
public abstract class BasePage {

	private static final Duration DEFAULT_EXPLICIT_TIMEOUT = Duration.ofSeconds(15);
	private static final Duration DEFAULT_POLL_TIMEOUT = Duration.ofSeconds(3);

	protected BasePage() {
	}

	/**
	 * Get the current thread-safe AppiumDriver instance.
	 */
	protected AppiumDriver getDriver() {
		return DriverManager.getDriver();
	}

	/**
	 * Create a WebDriverWait instance with custom timeout.
	 */
	protected WebDriverWait getWait(Duration timeout) {
		return new WebDriverWait(getDriver(), timeout);
	}

	/**
	 * Wait until an element is visible using default timeout.
	 */
	protected WebElement waitForVisible(By locator) {
		return waitForVisible(locator, DEFAULT_EXPLICIT_TIMEOUT);
	}

	/**
	 * Wait until an element is visible using custom timeout.
	 */
	protected WebElement waitForVisible(By locator, Duration timeout) {
		return getWait(timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	/**
	 * Wait until an element is clickable using default timeout.
	 */
	protected WebElement waitForClickable(By locator) {
		return waitForClickable(locator, DEFAULT_EXPLICIT_TIMEOUT);
	}

	/**
	 * Wait until an element is clickable using custom timeout.
	 */
	protected WebElement waitForClickable(By locator, Duration timeout) {
		return getWait(timeout).until(ExpectedConditions.elementToBeClickable(locator));
	}

	/**
	 * Click an element after waiting for it to be clickable.
	 */
	protected void click(By locator) {
		try {
			waitForClickable(locator).click();
		} catch (Exception e) {
			ReportManager.warning("Click failed for locator: " + locator + " | " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Type text into an element after clearing its contents.
	 */
	protected void type(By locator, String text) {
		try {
			WebElement element = waitForVisible(locator);
			element.clear();
			element.sendKeys(text);
		} catch (Exception e) {
			ReportManager.warning("Type failed for locator: " + locator + " | " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Clear an element's text.
	 */
	protected void clear(By locator) {
		waitForVisible(locator).clear();
	}

	/**
	 * Get visible text from an element.
	 */
	protected String getText(By locator) {
		return waitForVisible(locator).getText();
	}

	/**
	 * Get attribute value from an element.
	 */
	protected String getAttribute(By locator, String attribute) {
		return waitForVisible(locator).getAttribute(attribute);
	}

	/**
	 * Smart check: checks if element is displayed with a short default polling window (3s)
	 * to prevent false negatives from rendering delays.
	 */
	public boolean isDisplayed(By locator) {
		return isDisplayed(locator, DEFAULT_POLL_TIMEOUT);
	}

	/**
	 * Checks if element is displayed within the given timeout.
	 */
	public boolean isDisplayed(By locator, Duration timeout) {
		try {
			return getWait(timeout).until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Fast check: instant check with zero explicit wait.
	 */
	public boolean isDisplayedNow(By locator) {
		try {
			List<WebElement> elements = getDriver().findElements(locator);
			return !elements.isEmpty() && elements.get(0).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Click an element only if displayed within timeout; otherwise silently returns false.
	 */
	public boolean clickIfDisplayed(By locator, Duration timeout) {
		if (isDisplayed(locator, timeout)) {
			try {
				click(locator);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	/**
	 * Click an element only if displayed within 2 seconds.
	 */
	public boolean clickIfDisplayed(By locator) {
		return clickIfDisplayed(locator, Duration.ofSeconds(2));
	}

	/**
	 * Perform a scroll down gesture using W3C pointer actions.
	 */
	public void scrollDown() {
		swipe(0.5, 0.70, 0.5, 0.30);
	}

	/**
	 * Perform a scroll up gesture using W3C pointer actions.
	 */
	public void scrollUp() {
		swipe(0.5, 0.30, 0.5, 0.70);
	}

	/**
	 * Perform a swipe gesture using W3C PointerInput.
	 */
	public void swipe(double startXRatio, double startYRatio, double endXRatio, double endYRatio) {
		try {
			Dimension size = getDriver().manage().window().getSize();
			int startX = (int) (size.getWidth() * startXRatio);
			int startY = (int) (size.getHeight() * startYRatio);
			int endX = (int) (size.getWidth() * endXRatio);
			int endY = (int) (size.getHeight() * endYRatio);

			PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
			Sequence sequence = new Sequence(finger, 1);

			sequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
			sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
			sequence.addAction(new Pause(finger, Duration.ofMillis(200)));
			sequence.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), endX, endY));
			sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

			getDriver().perform(Collections.singletonList(sequence));
			Thread.sleep(300);
		} catch (Exception e) {
			ReportManager.warning("Swipe gesture failed: " + e.getMessage());
		}
	}

	/**
	 * Perform a single tap at exact screen coordinates using W3C pointer actions.
	 */
	public void tapAt(int x, int y) {
		try {
			PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
			Sequence tap = new Sequence(finger, 1);
			tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
			tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
			tap.addAction(new Pause(finger, Duration.ofMillis(100)));
			tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
			getDriver().perform(Collections.singletonList(tap));
			Thread.sleep(200);
		} catch (Exception e) {
			ReportManager.warning("tapAt failed at (" + x + "," + y + "): " + e.getMessage());
		}
	}

	/**
	 * Reset view to the top by scrolling up.
	 */
	public void scrollToTop() {
		for (int i = 0; i < 2; i++) {
			scrollUp();
		}
	}

	/**
	 * Check if element is enabled.
	 */
	public boolean isEnabled(By locator) {
		try {
			return waitForVisible(locator).isEnabled();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Check if element is selected.
	 */
	public boolean isSelected(By locator) {
		try {
			return waitForVisible(locator).isSelected();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Direct access to find a WebElement.
	 */
	public WebElement getElement(By locator) {
		return waitForVisible(locator);
	}

	/**
	 * Send keys without clearing.
	 */
	public void sendKeys(By locator, CharSequence... keys) {
		waitForVisible(locator).sendKeys(keys);
	}

	/**
	 * Wait until an element becomes invisible.
	 */
	public boolean waitForInvisible(By locator, Duration timeout) {
		try {
			return getWait(timeout).until(ExpectedConditions.invisibilityOfElementLocated(locator));
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Scroll down repeatedly until the specified element is visible or maxScrolls reached.
	 */
	public boolean scrollTo(By locator, int maxScrolls) {
		for (int i = 0; i < maxScrolls; i++) {
			if (isDisplayedNow(locator)) {
				return true;
			}
			scrollDown();
		}
		return isDisplayedNow(locator);
	}

	/**
	 * Bidirectional smart scroll: checks if element is displayed now,
	 * then scrolls down up to 2 times, then up up to 2 times if needed.
	 */
	public boolean scrollToElement(By locator) {
		if (isDisplayedNow(locator)) {
			return true;
		}
		for (int i = 0; i < 2; i++) {
			scrollDown();
			if (isDisplayedNow(locator)) {
				return true;
			}
		}
		for (int i = 0; i < 3; i++) {
			scrollUp();
			if (isDisplayedNow(locator)) {
				return true;
			}
		}
		return isDisplayed(locator, Duration.ofSeconds(2));
	}
}
