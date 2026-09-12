package com.itsm.app.base;

import com.itsm.app.pages.CreateTicketPage;
import com.itsm.app.pages.DashboardPage;
import com.itsm.app.pages.LoginPage;
import com.itsm.app.utils.AlertUtils;
import com.itsm.app.utils.AppUtils;
import com.itsm.app.utils.GestureUtils;
import com.itsm.app.utils.KeyboardUtils;
import com.itsm.app.utils.PlatformUtils;
import com.itsm.app.utils.ScreenshotUtils;
import com.itsm.app.utils.ScrollUtils;
import com.itsm.app.utils.WaitUtils;

import io.appium.java_client.AppiumDriver;

public final class TestContext {

	// =========================================
	// DRIVER
	// =========================================

	private AppiumDriver driver;

	// =========================================
	// CONFIGURATION
	// =========================================

	private final int explicitWaitTimeout;
	private final int alertTimeout;

	// =========================================
	// UI UTILITIES
	// =========================================

	private WaitUtils waitUtils;
	private ScreenshotUtils screenshotUtils;
	private GestureUtils gestureUtils;
	private ScrollUtils scrollUtils;
	private KeyboardUtils keyboardUtils;
	private AlertUtils alertUtils;
	private AppUtils appUtils;
	private PlatformUtils platformUtils;

	private LoginPage loginPage;
	private DashboardPage dashboardPage;
	private CreateTicketPage createTicketPage;

	// =========================================
	// CONSTRUCTOR
	// =========================================

	public TestContext(AppiumDriver driver, int explicitWaitTimeout, int alertTimeout) {

		if (driver == null) {
			throw new IllegalArgumentException("AppiumDriver cannot be null");
		}

		if (explicitWaitTimeout <= 0) {
			throw new IllegalArgumentException("Explicit wait timeout must be greater than 0");
		}

		if (alertTimeout <= 0) {
			throw new IllegalArgumentException("Alert timeout must be greater than 0");
		}

		this.driver = driver;
		this.explicitWaitTimeout = explicitWaitTimeout;
		this.alertTimeout = alertTimeout;

		// Core utilities
		this.screenshotUtils = new ScreenshotUtils(driver);
		this.waitUtils = new WaitUtils(driver, explicitWaitTimeout);
		this.gestureUtils = new GestureUtils(driver, waitUtils, screenshotUtils);
		this.scrollUtils = new ScrollUtils(driver, screenshotUtils);
		this.keyboardUtils = new KeyboardUtils(driver);
		this.alertUtils = new AlertUtils(driver, alertTimeout);
		this.appUtils = new AppUtils(driver);
		this.platformUtils = new PlatformUtils(driver);
	}

	// =========================================
	// DRIVER
	// =========================================

	public AppiumDriver getDriver() {
		return requireDriver();
	}

	private AppiumDriver requireDriver() {
		if (driver == null) {
			throw new IllegalStateException("AppiumDriver is NULL. Driver has not been initialized.");
		}
		return driver;
	}

	// =========================================
	// WAIT UTILS
	// =========================================

	public WaitUtils getWaitUtils() {
		if (waitUtils == null) {
			waitUtils = new WaitUtils(requireDriver(), explicitWaitTimeout);
		}
		return waitUtils;
	}

	// =========================================
	// SCREENSHOT UTILS
	// =========================================

	public ScreenshotUtils getScreenshotUtils() {
		if (screenshotUtils == null) {
			screenshotUtils = new ScreenshotUtils(requireDriver());
		}
		return screenshotUtils;
	}

	// =========================================
	// GESTURE UTILS
	// =========================================

	public GestureUtils getGestureUtils() {
		if (gestureUtils == null) {
			gestureUtils = new GestureUtils(requireDriver(), getWaitUtils(), getScreenshotUtils());
		}
		return gestureUtils;
	}

	// =========================================
	// SCROLL UTILS
	// =========================================

	public ScrollUtils getScrollUtils() {
		if (scrollUtils == null) {
			scrollUtils = new ScrollUtils(requireDriver(), getScreenshotUtils());
		}
		return scrollUtils;
	}

	// =========================================
	// KEYBOARD UTILS
	// =========================================

	public KeyboardUtils getKeyboardUtils() {
		if (keyboardUtils == null) {
			keyboardUtils = new KeyboardUtils(requireDriver());
		}
		return keyboardUtils;
	}

	// =========================================
	// ALERT UTILS
	// =========================================

	public AlertUtils getAlertUtils() {
		if (alertUtils == null) {
			alertUtils = new AlertUtils(requireDriver(), alertTimeout);
		}
		return alertUtils;
	}

	// =========================================
	// APP UTILS
	// =========================================

	public AppUtils getAppUtils() {
		if (appUtils == null) {
			appUtils = new AppUtils(requireDriver());
		}
		return appUtils;
	}

	// =========================================
	// PLATFORM UTILS
	// =========================================

	public PlatformUtils getPlatformUtils() {
		if (platformUtils == null) {
			platformUtils = new PlatformUtils(requireDriver());
		}
		return platformUtils;
	}

	// ========================================
	// PAGE OBJECTS
	// ========================================

	public LoginPage getLoginPage() {
		if (loginPage == null) {
			loginPage = new LoginPage();
		}
		return loginPage;
	}

	public DashboardPage getDashboardPage() {
		if (dashboardPage == null) {
			dashboardPage = new DashboardPage();
		}
		return dashboardPage;
	}

	public CreateTicketPage getCreateTicketPage() {
		if (createTicketPage == null) {
			createTicketPage = new CreateTicketPage();
		}
		return createTicketPage;
	}

	// =========================================
	// CLEAR DRIVER DEPENDENT OBJECTS
	// =========================================

	private void clearDriverDependentObjects() {
		waitUtils = null;
		screenshotUtils = null;
		gestureUtils = null;
		scrollUtils = null;
		keyboardUtils = null;
		alertUtils = null;
		appUtils = null;
		platformUtils = null;
		loginPage = null;
		dashboardPage = null;
		createTicketPage = null;
	}

	// =========================================
	// CLEAR CONTEXT
	// =========================================

	public void clear() {
		clearDriverDependentObjects();
		driver = null;
	}
}