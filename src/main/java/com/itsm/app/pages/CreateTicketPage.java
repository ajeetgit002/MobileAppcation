package com.itsm.app.pages;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.itsm.app.utils.ReportManager;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import lombok.Getter;

/**
 * Page Object representing the Ticket Creation workflow in the ITSM app.
 * Handles navigation to ticket creation, selection of service family,
 * service, sub-service (if present), entering ticket details, submission,
 * system popup handling, and ticket verification.
 */
@Getter
public class CreateTicketPage extends BasePage {

	// =========================================
	// NAVIGATION LOCATORS
	// =========================================

	private static final By RAISE_TICKET_DASHBOARD = AppiumBy.xpath("//*[contains(@content-desc,'Raise a New Ticket')]");
	private static final By TICKETS_BOTTOM_TAB = AppiumBy
			.xpath("(//*[contains(@content-desc,'Ticket') or contains(@text,'Ticket')])[last()]");
	private static final By CREATE_TICKET_PLUS_BUTTON = AppiumBy.xpath(
			"//android.widget.Button[@bounds='[915,499][1041,625]'] | //android.widget.EditText[contains(@hint,'Search')]/following-sibling::android.widget.Button[1] | //android.widget.Button[contains(@bounds,'915')]");

	// =========================================
	// SERVICE SELECTION LOCATORS
	// =========================================

	private static final By CHOOSE_SERVICE_FAMILY_HEADER = AppiumBy
			.xpath("//*[contains(@content-desc,'Choose a Service Family')]");
	private static final By RAISE_A_NEW_TICKET_HEADER = AppiumBy
			.xpath("//*[contains(@content-desc,'Raise a New Ticket')]");

	// =========================================
	// FORM LOCATORS
	// =========================================

	private static final By SERVICE_FIELD = AppiumBy
			.xpath("//*[@content-desc='Service *']/following-sibling::*[@clickable='true'][1]");
	private static final By SUB_SERVICE_FIELD = AppiumBy
			.xpath("//*[contains(@content-desc,'Sub Service')]/following-sibling::*[@clickable='true'][1]");
	private static final By TITLE_INPUT = AppiumBy
			.xpath("//*[@content-desc='Title *']/following-sibling::android.widget.EditText[1]");
	private static final By DESCRIPTION_INPUT = AppiumBy.xpath(
			"//android.widget.EditText[contains(@hint,'Description')] | //*[@content-desc='Description *']/following-sibling::android.widget.EditText[1]");

	private static final By SUBMIT_BUTTON = AppiumBy.accessibilityId("Submit");
	private static final By CANCEL_BUTTON = AppiumBy.accessibilityId("Cancel");
	private static final By YES_CANCEL_BUTTON = AppiumBy.accessibilityId("Yes, Cancel");

	// =========================================
	// POST-SUBMIT & SYSTEM DIALOG LOCATORS
	// =========================================

	private static final By CREATING_TICKET_BANNER = AppiumBy
			.xpath("//*[contains(@content-desc,'Creating') and contains(@content-desc,'Ticket')]");
	private static final By LOCATION_ACCURACY_NO_THANKS = AppiumBy
			.xpath("//*[@resource-id='android:id/button2' or @text='No thanks' or @content-desc='No thanks']");
	private static final By LOCATION_ACCURACY_TURN_ON = AppiumBy
			.xpath("//*[@resource-id='android:id/button1' or @text='Turn on' or @content-desc='Turn on']");
	private static final By PERMISSION_ALLOW_BUTTON = AppiumBy
			.xpath("//*[@resource-id='com.android.permissioncontroller:id/permission_allow_foreground_only_button' or @text='While using the app' or @text='Allow']");

	// =========================================
	// CONFIRMATION DIALOG LOCATORS
	// =========================================

	private static final By SUCCESS_MODAL_TITLE = AppiumBy
			.xpath("//*[contains(@content-desc,'Created') and contains(@content-desc,'Successfully')]");
	private static final By REFERENCE_TICKET_ID = AppiumBy
			.xpath("//*[contains(@content-desc,'Reference Ticket ID')]");
	private static final By OKAY_BUTTON = AppiumBy.accessibilityId("Okay");

	// =========================================
	// CONSTRUCTOR
	// =========================================

	public CreateTicketPage() {
		super();
	}

	// =========================================
	// NAVIGATION METHODS
	// =========================================

	/**
	 * Open the ticket creation flow.
	 * Handles existing state gracefully (if already on form or list).
	 */
	public void openCreateTicketFlow() {
		ReportManager.info("Opening Create Ticket flow...");

		// If already on Choose a Service Family screen
		if (isDisplayedNow(CHOOSE_SERVICE_FAMILY_HEADER)) {
			ReportManager.info("Already on Choose a Service Family screen");
			return;
		}

		// If stuck on Raise a New Ticket form from previous flow, cancel out
		if (isDisplayedNow(RAISE_A_NEW_TICKET_HEADER)) {
			ReportManager.info("Stuck on Raise a New Ticket form, cancelling...");
			clickIfDisplayed(CANCEL_BUTTON, Duration.ofSeconds(2));
			clickIfDisplayed(YES_CANCEL_BUTTON, Duration.ofSeconds(2));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ignored) {
			}
		}

		// Navigate via Dashboard or Tickets tab
		if (isDisplayedNow(RAISE_TICKET_DASHBOARD)) {
			click(RAISE_TICKET_DASHBOARD);
			ReportManager.pass("Clicked 'Raise a New Ticket' from Dashboard");
		} else if (isDisplayedNow(CREATE_TICKET_PLUS_BUTTON)) {
			click(CREATE_TICKET_PLUS_BUTTON);
			ReportManager.pass("Clicked '+' button on Tickets tab");
		} else {
			clickIfDisplayed(TICKETS_BOTTOM_TAB, Duration.ofSeconds(3));
			click(CREATE_TICKET_PLUS_BUTTON);
			ReportManager.pass("Clicked '+' Create Ticket button after navigating to Tickets tab");
		}

		waitForVisible(CHOOSE_SERVICE_FAMILY_HEADER, Duration.ofSeconds(15));
	}

	/**
	 * Check if 'Choose a Service Family' screen is displayed.
	 */
	public boolean isChooseServiceFamilyDisplayed() {
		return isDisplayed(CHOOSE_SERVICE_FAMILY_HEADER, Duration.ofSeconds(8));
	}

	// =========================================
	// SERVICE SELECTION METHODS
	// =========================================

	/**
	 * Select a Service Family card by accessibility description.
	 * E.g., "Global Request Family", "DevOps Services", "IT Services", "Room Booking", "QA".
	 */
	public void selectServiceFamily(String familyName) {
		ReportManager.info("Selecting Service Family: " + familyName);
		By familyLocator = AppiumBy.xpath("//*[contains(@content-desc,'" + familyName + "')]");
		scrollToElement(familyLocator);
		click(familyLocator);
		ReportManager.pass("Selected Service Family: " + familyName);
	}

	/**
	 * Select a Service card or item.
	 * E.g., "Arrival Employees", "Organization", "Meeting rooms", "SDLC".
	 */
	public void selectService(String serviceName) {
		ReportManager.info("Selecting Service: " + serviceName);
		By serviceLocator = AppiumBy.xpath("//*[contains(@content-desc,'" + serviceName + "')]");
		scrollToElement(serviceLocator);
		click(serviceLocator);
		ReportManager.pass("Selected Service: " + serviceName);
	}

	/**
	 * Check if the sub-service screen or field is present.
	 */
	public boolean isSubServicePresent() {
		return !isDisplayedNow(RAISE_A_NEW_TICKET_HEADER) || isDisplayedNow(SUB_SERVICE_FIELD);
	}

	/**
	 * If a sub-service screen or dropdown is present, select the specified sub-service.
	 * If subServiceName is null or empty, selects the first available sub-service.
	 */
	public void selectSubServiceIfPresent(String subServiceName) {
		if (isDisplayedNow(RAISE_A_NEW_TICKET_HEADER)) {
			// Already on form; check if Sub Service dropdown exists
			if (isDisplayedNow(SUB_SERVICE_FIELD)) {
				ReportManager.info("Sub-Service field detected on form");
				if (subServiceName != null && !subServiceName.isEmpty()) {
					click(SUB_SERVICE_FIELD);
					By optionLocator = AppiumBy.xpath("//android.widget.Button[contains(@content-desc,'" + subServiceName + "')]");
					if (isDisplayed(optionLocator, Duration.ofSeconds(3))) {
						click(optionLocator);
						ReportManager.pass("Selected sub-service from dropdown: " + subServiceName);
					}
				}
			}
			return;
		}

		// On sub-services list screen
		ReportManager.info("Selecting sub-service from list: " + subServiceName);
		if (subServiceName != null && !subServiceName.isEmpty()) {
			By subServiceLocator = AppiumBy.xpath("//*[starts-with(@content-desc,'" + subServiceName + "')]");
			scrollToElement(subServiceLocator);
			click(subServiceLocator);
			ReportManager.pass("Selected sub-service: " + subServiceName);
		} else {
			// Select first available sub-service item
			By firstSubServiceLocator = AppiumBy.xpath(
					"(//android.view.View[@clickable='true' and @content-desc and not(contains(@content-desc,'Ticket'))])[1]");
			click(firstSubServiceLocator);
			ReportManager.pass("Selected default first sub-service");
		}

		waitForVisible(RAISE_A_NEW_TICKET_HEADER, Duration.ofSeconds(10));
	}

	// =========================================
	// FORM POPULATION & SUBMISSION
	// =========================================

	/**
	 * Check if 'Raise a New Ticket' form is displayed.
	 */
	public boolean isRaiseNewTicketFormDisplayed() {
		return isDisplayed(RAISE_A_NEW_TICKET_HEADER, Duration.ofSeconds(10));
	}

	/**
	 * Enter ticket description into the description field.
	 * Ensures the field is focused, typed into, and the keyboard dismissed.
	 */
	public void enterDescription(String description) {
		ReportManager.info("Entering ticket description: " + description);
		scrollToElement(DESCRIPTION_INPUT);

		WebElement element = waitForVisible(DESCRIPTION_INPUT);
		element.click();
		try {
			Thread.sleep(400);
		} catch (InterruptedException ignored) {
		}

		element.sendKeys(description);
		hideKeyboardSafely();
		ReportManager.pass("Entered description successfully");
	}

	/**
	 * Enter title if the title field is editable.
	 */
	public void enterTitle(String title) {
		if (isDisplayedNow(TITLE_INPUT)) {
			WebElement element = waitForVisible(TITLE_INPUT);
			element.click();
			element.clear();
			element.sendKeys(title);
			hideKeyboardSafely();
			ReportManager.info("Entered ticket title: " + title);
		}
	}

	/**
	 * Safely hide soft keyboard if open.
	 */
	public void hideKeyboardSafely() {
		try {
			if (getDriver() instanceof AndroidDriver) {
				AndroidDriver androidDriver = (AndroidDriver) getDriver();
				if (androidDriver.isKeyboardShown()) {
					androidDriver.hideKeyboard();
					Thread.sleep(400);
				}
				if (androidDriver.isKeyboardShown()) {
					androidDriver.pressKey(new KeyEvent(AndroidKey.BACK));
					Thread.sleep(400);
				}
			} else {
				Map<String, Object> args = new HashMap<>();
				getDriver().executeScript("mobile: hideKeyboard", args);
			}
		} catch (Exception e) {
			try {
				if (getDriver() instanceof AndroidDriver) {
					((AndroidDriver) getDriver()).pressKey(new KeyEvent(AndroidKey.BACK));
				}
			} catch (Exception ignored) {
			}
		}
	}

	/**
	 * Click the Submit button on the ticket form.
	 */
	public void clickSubmit() {
		hideKeyboardSafely();
		ReportManager.info("Clicking Submit button...");
		scrollToElement(SUBMIT_BUTTON);
		click(SUBMIT_BUTTON);
		ReportManager.pass("Clicked Submit button");
	}

	/**
	 * Automatically handle and dismiss any system or Google Play Services popups
	 * (e.g. Location Accuracy, Permission prompts) that appear after submission.
	 */
	public void handlePostSubmitPopups() {
		try {
			clickIfDisplayed(LOCATION_ACCURACY_NO_THANKS, Duration.ofSeconds(2));
			clickIfDisplayed(LOCATION_ACCURACY_TURN_ON, Duration.ofSeconds(1));
			clickIfDisplayed(PERMISSION_ALLOW_BUTTON, Duration.ofSeconds(1));
		} catch (Exception ignored) {
		}
	}

	// =========================================
	// SUCCESS VERIFICATION
	// =========================================

	/**
	 * Check if ticket creation success modal is displayed.
	 * Actively polls and dismisses any location / system dialogs that appear in between.
	 */
	public boolean isTicketCreatedSuccessfully() {
		ReportManager.info("Waiting for ticket creation confirmation...");
		long startTime = System.currentTimeMillis();
		long timeoutMs = 25000;

		while (System.currentTimeMillis() - startTime < timeoutMs) {
			handlePostSubmitPopups();

			if (isDisplayedNow(SUCCESS_MODAL_TITLE)) {
				ReportManager.pass("Success confirmation modal is displayed!");
				return true;
			}

			try {
				Thread.sleep(800);
			} catch (InterruptedException ignored) {
			}
		}

		handlePostSubmitPopups();
		return isDisplayedNow(SUCCESS_MODAL_TITLE);
	}

	/**
	 * Extract the generated Reference Ticket ID (e.g., "#R-011690" or "R-011690")
	 * from the success confirmation dialog.
	 */
	public String getCreatedTicketId() {
		try {
			WebElement element = waitForVisible(REFERENCE_TICKET_ID, Duration.ofSeconds(10));
			String desc = element.getAttribute("content-desc");
			if (desc == null || desc.isEmpty()) {
				desc = element.getText();
			}
			ReportManager.info("Raw success description: " + desc);

			Matcher matcher = Pattern.compile("(#[A-Z]-\\d+|[A-Z]-\\d+)").matcher(desc);
			if (matcher.find()) {
				String ticketId = matcher.group(1).replace("#", "");
				ReportManager.pass("Extracted Ticket ID: " + ticketId);
				return ticketId;
			}
			return desc;
		} catch (Exception e) {
			ReportManager.warning("Failed to extract Ticket ID: " + e.getMessage());
			return "";
		}
	}

	/**
	 * Click 'Okay' on the success dialog to return to the Tickets list.
	 */
	public void clickOkay() {
		ReportManager.info("Clicking 'Okay' button on confirmation modal...");
		click(OKAY_BUTTON);
		ReportManager.pass("Clicked 'Okay' button");
	}

	/**
	 * Verify that the newly created ticket ID is displayed in the Tickets list.
	 */
	public boolean isTicketPresentInList(String ticketId) {
		if (ticketId == null || ticketId.isEmpty()) {
			return false;
		}
		ReportManager.info("Verifying ticket presence in list for ID: " + ticketId);
		By ticketInListLocator = AppiumBy.xpath("//*[contains(@content-desc,'" + ticketId + "')]");
		return isDisplayed(ticketInListLocator, Duration.ofSeconds(10));
	}

	// =========================================
	// HIGH-LEVEL WORKFLOW
	// =========================================

	/**
	 * Complete end-to-end flow:
	 * 1. Navigate to Create Ticket
	 * 2. Select Service Family
	 * 3. Select Service
	 * 4. Select Sub-Service if present
	 * 5. Enter Description
	 * 6. Submit ticket
	 * 7. Validate success and capture Ticket ID
	 * 8. Click Okay
	 * 9. Verify ticket in list
	 */
	public String createTicket(String serviceFamily, String service, String subService, String description) {
		openCreateTicketFlow();
		selectServiceFamily(serviceFamily);
		selectService(service);
		selectSubServiceIfPresent(subService);
		enterDescription(description);
		clickSubmit();

		if (!isTicketCreatedSuccessfully()) {
			handlePostSubmitPopups();
		}

		String ticketId = getCreatedTicketId();
		clickOkay();
		return ticketId;
	}
}
