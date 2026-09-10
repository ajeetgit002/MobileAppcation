package com.itsm.app.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.itsm.app.base.BaseTest;
import com.itsm.app.pages.LoginPage;

public class LoginTest extends BaseTest {

	private LoginPage loginPage;

	@Override
	protected boolean requiresLogin() {
		// Do not auto-login in BaseTest because this test validates the login flow itself
		return false;
	}

	@BeforeMethod
	public void setUpLoginPage() {
		loginPage = context.getLoginPage();
	}

	@Test(description = "Verify successful login with valid credentials")
	public void testValidLogin() {
		loginPage.ensureLoggedIn(orgId, username, password);
		loginPage.closeAllowWhileUsingAppDialogIfDisplayed();

		Assert.assertTrue(loginPage.isDashboardDisplayed(), "Dashboard should be displayed after successful login");
	}
}