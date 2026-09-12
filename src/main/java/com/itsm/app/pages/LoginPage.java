package com.itsm.app.pages;

import org.openqa.selenium.By;

import io.appium.java_client.AppiumBy;

public class LoginPage extends BasePage {

	// =========================================
	// LOCATORS
	// =========================================

	private static final By ORG_FIELD = AppiumBy.xpath("//android.widget.EditText[@hint='Eg. TCS0001,WIPRO678']");
	private static final By SEARCH_BUTTON = AppiumBy.accessibilityId("Search");
	private static final By DO_NOT_KNOW_ORG = AppiumBy.accessibilityId("~I do not know my Organization Code.");
	private static final By CHOOSE_ORG = AppiumBy
			.xpath("//android.view.View[contains(@content-desc,'https://demo-iserve.cats4u.ai')]");
	private static final By USERNAME_FIELD = AppiumBy.xpath("//android.widget.EditText[@hint='Enter Username']");
	private static final By PASSWORD_FIELD = AppiumBy.xpath("//android.widget.EditText[@hint='Enter Password']");
	private static final By LOGIN_BUTTON = AppiumBy.accessibilityId("Continue");
	private static final By FORGOT_PASSWORD_LINK = AppiumBy.accessibilityId("Forgot Password ?");
	private static final By ERROR_MESSAGE = AppiumBy.accessibilityId("Something went wrong. Please try again.");
	private static final By REMEMBER_ME_CHECKBOX = AppiumBy
			.androidUIAutomator("new UiSelector().className(\"android.widget.CheckBox\").instance(0)");

	private static final By ALLOW_PERMISSION = By.id("com.android.permissioncontroller:id/permission_allow_button");
	private static final By ALLOW_PERMISSION_TEXT = AppiumBy.xpath("//*[@text='Allow' or @content-desc='Allow']");
	private static final By DENY_PERMISSION_DONT_ASK = By
			.id("com.android.permissioncontroller:id/permission_deny_and_dont_ask_again_button");
	private static final By DENY_PERMISSION = By.id("com.android.permissioncontroller:id/permission_deny_button");
	private static final By ALLOW_WHILE_USING_APP = By
			.id("com.android.permissioncontroller:id/permission_allow_foreground_only_button");
	private static final By ALLOW_ONE_TIME = By
			.id("com.android.permissioncontroller:id/permission_allow_one_time_button");
	private static final By LATER_BUTTON = AppiumBy.accessibilityId("Later");
	private static final By LATER_BUTTON_XPATH = AppiumBy.xpath("//*[@content-desc='Later' or @text='Later']");

	private static final By ORG_SEARCH_SCREEN_MARKER = AppiumBy
			.accessibilityId("Enter your organization to get started.");
	private static final By ORG_LIST_SCREEN_MARKER = AppiumBy.accessibilityId("My Organizations");
	private static final By DASHBOARD_MARKER = AppiumBy.accessibilityId("My Activity");
	private static final By LOGIN_SCREEN_MARKER = USERNAME_FIELD;

	// =========================================
	// CONSTRUCTOR
	// =========================================

	public LoginPage() {
		super();
	}

	// =========================================
	// SCREEN VERIFICATION
	// =========================================

	public boolean isDashboardDisplayed() {
		if (isDisplayedNow(DASHBOARD_MARKER) 
				|| isDisplayedNow(AppiumBy.xpath("//*[starts-with(@content-desc,'Ongoing')]"))
				|| isDisplayedNow(AppiumBy.xpath("//android.widget.ImageView[@content-desc='Tickets']"))) {
			return true;
		}
		handleAnySystemDialogs();
		return isDisplayed(DASHBOARD_MARKER, java.time.Duration.ofSeconds(3))
				|| isDisplayed(AppiumBy.xpath("//*[starts-with(@content-desc,'Ongoing')]"), java.time.Duration.ofSeconds(2));
	}

	public boolean isOrganizationSearchDisplayed() {
		return isDisplayed(ORG_SEARCH_SCREEN_MARKER);
	}

	public boolean isOrganizationListDisplayed() {
		return isDisplayed(ORG_LIST_SCREEN_MARKER);
	}

	public boolean isLoginScreenDisplayed() {
		return isDisplayed(LOGIN_SCREEN_MARKER);
	}

	// =========================================
	// SYSTEM DIALOGS
	// =========================================

	public void handleAnySystemDialogs() {
		java.time.Duration quickWait = java.time.Duration.ofMillis(300);
		clickIfDisplayed(ALLOW_PERMISSION, quickWait);
		clickIfDisplayed(ALLOW_PERMISSION_TEXT, quickWait);
		clickIfDisplayed(DENY_PERMISSION_DONT_ASK, quickWait);
		clickIfDisplayed(DENY_PERMISSION, quickWait);
		clickIfDisplayed(ALLOW_WHILE_USING_APP, quickWait);
		clickIfDisplayed(ALLOW_ONE_TIME, quickWait);
		clickIfDisplayed(LATER_BUTTON, quickWait);
		clickIfDisplayed(LATER_BUTTON_XPATH, quickWait);
	}

	public void handlePermissionIfDisplayed() {
		clickIfDisplayed(ALLOW_PERMISSION);
		clickIfDisplayed(ALLOW_PERMISSION_TEXT);
	}

	public void clickAllowWhileUsingApp() {
		clickIfDisplayed(ALLOW_WHILE_USING_APP);
	}

	public void closeAllowWhileUsingAppDialogIfDisplayed() {
		handleAnySystemDialogs();
	}

	// =========================================
	// ORGANIZATION
	// =========================================

	public void enterOrgID(String org) {
		click(ORG_FIELD);
		type(ORG_FIELD, org);
	}

	public void clickSearchButton() {
		click(SEARCH_BUTTON);
	}

	public void clickDoNotKnowOrg() {
		click(DO_NOT_KNOW_ORG);
	}

	public void clickChooseOrg() {
		click(CHOOSE_ORG);
	}

	// =========================================
	// LOGIN
	// =========================================

	public void enterUsername(String username) {
		click(USERNAME_FIELD);
		type(USERNAME_FIELD, username);
	}

	public void enterPassword(String password) {
		click(PASSWORD_FIELD);
		type(PASSWORD_FIELD, password);
	}

	public void clickLogin() {
		click(LOGIN_BUTTON);
	}

	public void clickForgotPassword() {
		click(FORGOT_PASSWORD_LINK);
	}

	public void setRememberMe(boolean checked) {
		try {
			boolean currentState = isSelected(REMEMBER_ME_CHECKBOX);
			if (checked != currentState) {
				click(REMEMBER_ME_CHECKBOX);
			}
		} catch (Exception ignored) {
		}
	}

	// =========================================
	// BUSINESS ACTIONS
	// =========================================

	public void login(String username, String password) {
		enterUsername(username);
		enterPassword(password);
		clickLogin();
	}

	public void loginWithRememberMe(String username, String password, boolean rememberMe) {
		enterUsername(username);
		enterPassword(password);
		setRememberMe(rememberMe);
		clickLogin();
	}

	// =========================================
	// ENSURE LOGGED IN (Smart Idempotent Login)
	// =========================================

	public void ensureLoggedIn(String orgCode, String username, String password) {
		handleAnySystemDialogs();

		if (isDashboardDisplayed()) {
			return;
		}

		if (isOrganizationSearchDisplayed()) {
			enterOrgID(orgCode);
			clickSearchButton();
			handleAnySystemDialogs();
		}

		if (isOrganizationListDisplayed()) {
			clickChooseOrg();
			handleAnySystemDialogs();
		}

		if (isLoginScreenDisplayed()) {
			login(username, password);
			handleAnySystemDialogs();
		}

		handleAnySystemDialogs();
	}

	// =========================================
	// VERIFICATIONS
	// =========================================

	public boolean isErrorMessageDisplayed() {
		return isDisplayed(ERROR_MESSAGE);
	}

	public String getErrorMessage() {
		return getText(ERROR_MESSAGE);
	}

	public boolean isLoginButtonEnabled() {
		try {
			return waitForVisible(LOGIN_BUTTON).isEnabled();
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isUsernameFieldDisplayed() {
		return isDisplayed(USERNAME_FIELD);
	}

	public boolean isPasswordFieldDisplayed() {
		return isDisplayed(PASSWORD_FIELD);
	}
}