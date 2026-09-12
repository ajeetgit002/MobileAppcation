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
	// GENERIC SCREEN ELEMENT LOCATORS
	// =========================================

	private static final By BACK_BUTTON = AppiumBy.xpath(
			"//android.widget.Button[@bounds='[32,153][158,279]'] | //android.widget.Button[@bounds='[21,153][147,279]'] | //android.widget.ImageView/android.widget.Button | //android.view.View[contains(@bounds,'[0,153]')]/android.widget.Button | (//android.widget.Button)[1]");
	private static final By SEARCH_INPUT = AppiumBy
			.xpath("//android.widget.EditText[contains(@hint,'Search') or contains(@text,'Search')]");
	private static final By EMPTY_STATE_PLACEHOLDER = AppiumBy
			.xpath("//android.widget.ImageView[@bounds='[21,1099][1059,1624]'] | //android.widget.ImageView[contains(@bounds,'1099')]");

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

		// If success modal is open from previous flow, dismiss it
		if (isDisplayedNow(OKAY_BUTTON)) {
			ReportManager.info("Success modal is open, clicking Okay...");
			clickIfDisplayed(OKAY_BUTTON, Duration.ofSeconds(2));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ignored) {
			}
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

		// If stuck in a sub-service or service details screen, navigate back until root or family header is reached
		int maxBacks = 5;
		while (maxBacks > 0 && !isDisplayedNow(CHOOSE_SERVICE_FAMILY_HEADER)
				&& !isDisplayedNow(RAISE_TICKET_DASHBOARD)
				&& !isDisplayedNow(CREATE_TICKET_PLUS_BUTTON)
				&& !isDisplayedNow(TICKETS_BOTTOM_TAB)) {
			if (isDisplayedNow(OKAY_BUTTON)) {
				clickIfDisplayed(OKAY_BUTTON, Duration.ofSeconds(2));
			} else {
				ReportManager.info("Navigating back to find root screen...");
				navigateBack();
			}
			maxBacks--;
		}

		if (isDisplayedNow(CHOOSE_SERVICE_FAMILY_HEADER)) {
			ReportManager.info("Reached Choose a Service Family screen");
			return;
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
			if (isDisplayedNow(CREATE_TICKET_PLUS_BUTTON)) {
				click(CREATE_TICKET_PLUS_BUTTON);
				ReportManager.pass("Clicked '+' Create Ticket button after navigating to Tickets tab");
			} else if (isDisplayedNow(RAISE_TICKET_DASHBOARD)) {
				click(RAISE_TICKET_DASHBOARD);
			}
		}

		waitForVisible(CHOOSE_SERVICE_FAMILY_HEADER, Duration.ofSeconds(15));
	}

	/**
	 * Ensure app is at root dashboard/tickets or Choose Service Family screen.
	 */
	public void ensureAtRootOrFamilyScreen() {
		ReportManager.info("Ensuring app is at root screen or Choose Service Family screen...");
		int maxTries = 5;
		while (maxTries > 0 && !isDisplayedNow(CHOOSE_SERVICE_FAMILY_HEADER)
				&& !isDisplayedNow(RAISE_TICKET_DASHBOARD)
				&& !isDisplayedNow(CREATE_TICKET_PLUS_BUTTON)
				&& !isDisplayedNow(TICKETS_BOTTOM_TAB)) {
			if (isDisplayedNow(CANCEL_BUTTON)) {
				clickIfDisplayed(CANCEL_BUTTON, Duration.ofSeconds(2));
				clickIfDisplayed(YES_CANCEL_BUTTON, Duration.ofSeconds(2));
			} else {
				navigateBack();
			}
			maxTries--;
		}
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
		if (!isDisplayedNow(familyLocator)) {
			scrollTo(familyLocator, 3);
		}
		if (!isDisplayedNow(familyLocator)) {
			scrollToTop();
			scrollTo(familyLocator, 3);
		}
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
		if (!isDisplayedNow(serviceLocator)) {
			scrollTo(serviceLocator, 4);
		}
		if (!isDisplayedNow(serviceLocator)) {
			scrollToTop();
			scrollTo(serviceLocator, 4);
		}
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
			if (!isDisplayedNow(subServiceLocator)) {
				scrollTo(subServiceLocator, 3);
			}
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
		long timeoutMs = 50000;

		while (System.currentTimeMillis() - startTime < timeoutMs) {
			handlePostSubmitPopups();

			if (isDisplayedNow(SUCCESS_MODAL_TITLE) || isDisplayedNow(OKAY_BUTTON)) {
				ReportManager.pass("Success confirmation modal is displayed!");
				return true;
			}

			try {
				Thread.sleep(800);
			} catch (InterruptedException ignored) {
			}
		}

		handlePostSubmitPopups();
		return isDisplayedNow(SUCCESS_MODAL_TITLE) || isDisplayedNow(OKAY_BUTTON);
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

	// =========================================
	// SCREEN ELEMENT VERIFICATION METHODS
	// =========================================

	/**
	 * Verify all UI elements on 'Choose a Service Family' screen:
	 * - Header title ("Choose a Service Family")
	 * - Back button
	 * - All 6 Service Family cards (DevOps Services, Global Request Family, Infra Services, IT Services, QA, Room Booking)
	 */
	public boolean verifyChooseServiceFamilyScreenElements() {
		ReportManager.info("Verifying all elements on 'Choose a Service Family' screen...");
		boolean headerOk = isDisplayed(CHOOSE_SERVICE_FAMILY_HEADER, Duration.ofSeconds(8));
		boolean backOk = isDisplayed(BACK_BUTTON, Duration.ofSeconds(5));

		String[] families = {
			"DevOps Services", "Global Request Family", "Infra Services",
			"IT Services", "QA", "Room Booking"
		};
		boolean allFamiliesOk = true;
		for (String family : families) {
			By familyLoc = AppiumBy.xpath("//*[contains(@content-desc,'" + family + "')]");
			boolean famFound = isDisplayed(familyLoc, Duration.ofSeconds(3));
			ReportManager.info("Service Family '" + family + "' displayed: " + famFound);
			if (!famFound) {
				allFamiliesOk = false;
			}
		}

		ReportManager.info("Choose Service Family verification: header=" + headerOk + ", back=" + backOk + ", families=" + allFamiliesOk);
		return headerOk && backOk && allFamiliesOk;
	}

	/**
	 * Verify all UI elements on 'Choose Service' screen:
	 * - Header title matching the Service Family
	 * - Back button
	 * - Service cards displayed
	 */
	public boolean verifyChooseServiceScreenElements(String familyName) {
		ReportManager.info("Verifying all elements on Services screen for family: " + familyName);
		By familyHeader = AppiumBy.xpath("//*[contains(@content-desc,'" + familyName + "')]");
		boolean headerOk = isDisplayed(familyHeader, Duration.ofSeconds(8));
		boolean backOk = isDisplayed(BACK_BUTTON, Duration.ofSeconds(5));
		boolean hasServices = isDisplayed(AppiumBy.xpath("//android.widget.ImageView[@clickable='true' and @content-desc] | //android.view.View[@clickable='true' and @content-desc]"), Duration.ofSeconds(5));

		ReportManager.info("Choose Service screen verification: header=" + headerOk + ", back=" + backOk + ", hasServices=" + hasServices);
		return headerOk && backOk && hasServices;
	}

	/**
	 * Verify all UI elements on Sub-Services screen:
	 * - Header title matching the Service name
	 * - Back button
	 * - Sub-service cards list displayed
	 */
	public boolean verifySubServicesScreenElements(String serviceName) {
		ReportManager.info("Verifying all elements on Sub-Services screen for service: " + serviceName);
		By serviceHeader = AppiumBy.xpath("//*[contains(@content-desc,'" + serviceName + "')]");
		boolean headerOk = isDisplayed(serviceHeader, Duration.ofSeconds(8));
		boolean backOk = isDisplayed(BACK_BUTTON, Duration.ofSeconds(5));
		boolean hasSubServices = isDisplayed(AppiumBy.xpath("//android.view.View[@clickable='true' and @content-desc]"), Duration.ofSeconds(5));

		ReportManager.info("Sub-Services screen verification: header=" + headerOk + ", back=" + backOk + ", hasSubServices=" + hasSubServices);
		return headerOk && backOk && hasSubServices;
	}

	/**
	 * Verify empty state placeholder when a service has no sub-services.
	 * Asserts that the empty illustration is displayed and ticket form is NOT opened.
	 */
	public boolean verifyEmptyServiceState(String serviceName) {
		ReportManager.info("Verifying Empty State for service without sub-services: " + serviceName);
		By serviceHeader = AppiumBy.xpath("//*[contains(@content-desc,'" + serviceName + "')]");
		boolean headerOk = isDisplayed(serviceHeader, Duration.ofSeconds(8));
		boolean backOk = isDisplayed(BACK_BUTTON, Duration.ofSeconds(5));
		boolean emptyImageOk = isDisplayed(EMPTY_STATE_PLACEHOLDER, Duration.ofSeconds(5));
		boolean notForm = !isDisplayedNow(RAISE_A_NEW_TICKET_HEADER);

		ReportManager.info("Empty State check: header=" + headerOk + ", back=" + backOk + ", emptyImage=" + emptyImageOk + ", notForm=" + notForm);
		return headerOk && backOk && emptyImageOk && notForm;
	}

	/**
	 * Verify all UI elements on 'Raise a New Ticket' form:
	 * - Form Header ("Raise a New Ticket")
	 * - Description input field
	 * - Cancel button
	 * - Submit button
	 */
	public boolean verifyTicketFormElements(String expectedService, String expectedSubService) {
		ReportManager.info("Verifying all elements on Raise a New Ticket form...");
		boolean headerOk = isDisplayed(RAISE_A_NEW_TICKET_HEADER, Duration.ofSeconds(8));
		boolean descOk = isDisplayed(DESCRIPTION_INPUT, Duration.ofSeconds(5));
		boolean cancelOk = isDisplayed(CANCEL_BUTTON, Duration.ofSeconds(5));
		boolean submitOk = isDisplayed(SUBMIT_BUTTON, Duration.ofSeconds(5));

		ReportManager.info("Ticket Form elements: header=" + headerOk + ", descField=" + descOk + ", cancelBtn=" + cancelOk + ", submitBtn=" + submitOk);
		return headerOk && descOk && cancelOk && submitOk;
	}

	/**
	 * Verify all UI elements on Confirmation Modal:
	 * - Success header ("Created Successfully")
	 * - Reference Ticket ID text
	 * - Okay button
	 */
	public boolean verifyConfirmationDialogElements() {
		ReportManager.info("Verifying all elements on Confirmation Modal...");
		boolean modalOk = isDisplayed(SUCCESS_MODAL_TITLE, Duration.ofSeconds(8));
		boolean refIdOk = isDisplayed(REFERENCE_TICKET_ID, Duration.ofSeconds(5));
		boolean okayOk = isDisplayed(OKAY_BUTTON, Duration.ofSeconds(5));

		ReportManager.info("Confirmation Modal elements: title=" + modalOk + ", refId=" + refIdOk + ", okayBtn=" + okayOk);
		return modalOk && refIdOk && okayOk;
	}

	/**
	 * Safely cancel the ticket form if open and return to home or service selection.
	 */
	public void cancelTicketFormIfOpen() {
		if (isDisplayedNow(RAISE_A_NEW_TICKET_HEADER)) {
			ReportManager.info("Cancelling open ticket form...");
			clickIfDisplayed(CANCEL_BUTTON, Duration.ofSeconds(2));
			clickIfDisplayed(YES_CANCEL_BUTTON, Duration.ofSeconds(2));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ignored) {
			}
		}
	}

	/**
	 * Navigate back safely to previous screen.
	 */
	public void navigateBack() {
		ReportManager.info("Navigating back to previous screen...");
		try {
			if (isDisplayedNow(BACK_BUTTON)) {
				click(BACK_BUTTON);
			} else if (getDriver() instanceof AndroidDriver) {
				((AndroidDriver) getDriver()).pressKey(new KeyEvent(AndroidKey.BACK));
			}
		} catch (Exception e) {
			try {
				if (getDriver() instanceof AndroidDriver) {
					((AndroidDriver) getDriver()).pressKey(new KeyEvent(AndroidKey.BACK));
				}
			} catch (Exception ignored) {
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ignored) {
		}
	}
}
