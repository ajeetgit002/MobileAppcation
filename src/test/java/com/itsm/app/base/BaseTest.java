package com.itsm.app.base;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import com.itsm.app.listeners.TestListener;
import com.itsm.app.pages.LoginPage;
import com.itsm.app.utils.ConfigReader;
import com.itsm.app.utils.DriverFactory;
import com.itsm.app.utils.DriverManager;
import com.itsm.app.utils.ReportManager;

import io.appium.java_client.AppiumDriver;

/**
 * BaseTest has a single responsibility: managing test execution lifecycle
 * and Appium driver session setup/teardown.
 */
@Listeners(TestListener.class)
public abstract class BaseTest {

	private static final int EXPLICIT_WAIT_TIMEOUT = 15;
	private static final int ALERT_TIMEOUT = 10;

	protected ConfigReader configReader;
	protected String orgId;
	protected String username;
	protected String password;

	protected TestContext context;

	protected BaseTest() {
		this.configReader = new ConfigReader();
		this.orgId = configReader.getProperty("orgId", "IN2IT-304CATS");
		this.username = configReader.getProperty("username", "abhi.portal");
		this.password = configReader.getProperty("password", "Admin@123");
	}

	// =========================================
	// TESTNG SUITE LIFECYCLE
	// =========================================

	@BeforeSuite(alwaysRun = true)
	public void beforeSuite() {
		ReportManager.initReport();
	}

	@AfterSuite(alwaysRun = true)
	public void afterSuite() {
		ReportManager.flush();
	}

	// =========================================
	// DRIVER SESSION CONFIGURATION HOOKS
	// =========================================

	/**
	 * Override to return true for test classes that share a driver session across tests.
	 */
	protected boolean isClassLevelSession() {
		return false;
	}

	/**
	 * Override to return false for tests that validate login themselves.
	 */
	protected boolean requiresLogin() {
		return true;
	}

	// =========================================
	// CLASS LIFECYCLE
	// =========================================

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		if (isClassLevelSession()) {
			startDriverSession();
		}
	}

	@AfterClass(alwaysRun = true)
	public void tearDownClass() {
		if (isClassLevelSession()) {
			stopDriverSession();
		}
	}

	// =========================================
	// METHOD LIFECYCLE
	// =========================================

	@BeforeMethod(alwaysRun = true)
	public void setUpMethod(ITestResult result) {
		if (!isClassLevelSession()) {
			startDriverSession();
		}
	}

	@AfterMethod(alwaysRun = true)
	public void tearDownMethod(ITestResult result) {
		if (!isClassLevelSession()) {
			stopDriverSession();
		}
	}

	// =========================================
	// DRIVER SETUP & TEARDOWN
	// =========================================

	private void startDriverSession() {
		try {
			AppiumDriver driver = DriverFactory.createAndroidDriver(configReader);
			DriverManager.setDriver(driver);

			context = new TestContext(driver, EXPLICIT_WAIT_TIMEOUT, ALERT_TIMEOUT);
			context.getPlatformUtils().logPlatformInfo();

			if (requiresLogin()) {
				LoginPage loginPage = context.getLoginPage();
				loginPage.ensureLoggedIn(orgId, username, password);
				loginPage.closeAllowWhileUsingAppDialogIfDisplayed();
			}

		} catch (Exception e) {
			ReportManager.fail("Session initialization failed: " + e.getMessage());
			throw e;
		}
	}

	private void stopDriverSession() {
		try {
			if (context != null) {
				context.clear();
			}
		} finally {
			DriverManager.quitDriver();
		}
	}

	protected TestContext getContext() {
		return context;
	}

	protected AppiumDriver getDriver() {
		return DriverManager.getDriver();
	}
}