package com.itsm.app.pages;

import java.time.Duration;
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
 * Page Object representing the Ticket Details screen and its horizontal tabs:
 * Properties, SLA Details, Public Logs, CIs, Documents, Attachments, Work Order.
 */
@Getter
public class TicketDetailsPage extends BasePage {

	// =========================================
	// HEADER LOCATORS
	// =========================================

	private static final By TICKET_DETAILS_HEADER = AppiumBy
			.xpath("//*[contains(@content-desc,'Ticket Details')]");
	private static final By BACK_BUTTON = AppiumBy.xpath(
			"//android.widget.Button[@bounds='[32,153][158,279]'] | //android.widget.Button[@bounds='[21,153][147,279]'] | //android.view.View[contains(@bounds,'[0,153]')]/android.widget.Button | (//android.widget.Button)[1]");

	// =========================================
	// TAB CONTENT LOCATORS
	// =========================================

	// Properties Tab
	private static final By BASIC_DETAILS_HEADER = AppiumBy
			.xpath("//*[contains(@content-desc,'Basic details')]");
	private static final By TICKET_TITLE_FIELD = AppiumBy
			.xpath("//*[contains(@content-desc,'Ticket Title')]");
	private static final By CALLER_FIELD = AppiumBy
			.xpath("//*[contains(@content-desc,'Caller')]");
	private static final By SERVICE_FIELD = AppiumBy
			.xpath("//*[contains(@content-desc,'Service')]");
	private static final By SUB_SERVICE_FIELD = AppiumBy
			.xpath("//*[contains(@content-desc,'Sub Service')]");
	private static final By PRIORITY_FIELD = AppiumBy
			.xpath("//*[contains(@content-desc,'Priority')]");
	private static final By DESCRIPTION_FIELD = AppiumBy
			.xpath("//*[contains(@content-desc,'Description')]");

	// SLA Details Tab
	private static final By SLA_TTO_PASSED = AppiumBy
			.xpath("//*[contains(@content-desc,'SLA TTO Passed')]");
	private static final By SLA_TTR_PASSED = AppiumBy
			.xpath("//*[contains(@content-desc,'SLA TTR Passed')]");

	// Public Logs Tab
	private static final By PUBLIC_LOGS_INDICATOR = AppiumBy
			.xpath("//*[contains(@content-desc,'Public Logs')]");

	// CIs Tab & Add CI Modal
	private static final By CIS_INDICATOR = AppiumBy
			.xpath("//*[contains(@content-desc,'CIs')]");
	private static final By ADD_CI_BUTTON = AppiumBy
			.xpath("//android.widget.Button[@content-desc='Add CI']");
	private static final By SELECT_CIS_HEADER = AppiumBy
			.xpath("//*[contains(@content-desc,'Select CIs')]");
	private static final By SEARCH_CI_INPUT = AppiumBy
			.xpath("//android.widget.EditText");
	private static final By CI_CHECKBOXES = AppiumBy
			.xpath("//android.widget.CheckBox");
	private static final By CANCEL_CI_BUTTON = AppiumBy
			.xpath("//android.widget.Button[@content-desc='Cancel']");
	private static final By SELECT_CI_BUTTON = AppiumBy
			.xpath("//android.widget.Button[@content-desc='Select']");

	// Documents Tab
	private static final By DOCUMENTS_INDICATOR = AppiumBy
			.xpath("//*[contains(@content-desc,'Documents')]");

	// Attachments Tab
	private static final By ATTACHMENTS_INDICATOR = AppiumBy
			.xpath("//*[contains(@content-desc,'Attachments')]");

	// Work Order Tab
	private static final By WORK_ORDER_INDICATOR = AppiumBy
			.xpath("//*[contains(@content-desc,'Work Order')]");

	public TicketDetailsPage() {
		super();
	}

	// =========================================
	// HEADER METHODS
	// =========================================

	/**
	 * Check if the Ticket Details screen is displayed.
	 */
	public boolean isTicketDetailsDisplayed() {
		return isDisplayed(TICKET_DETAILS_HEADER, Duration.ofSeconds(10));
	}

	/**
	 * Extract the Ticket ID (e.g. "R-011701") from the top bar.
	 */
	public String getTicketId() {
		try {
			WebElement element = waitForVisible(AppiumBy.xpath("//*[contains(@content-desc,'R-')]"), Duration.ofSeconds(5));
			String desc = element.getAttribute("content-desc");
			if (desc != null) {
				Matcher matcher = Pattern.compile("(R-\\d+)").matcher(desc);
				if (matcher.find()) {
					return matcher.group(1);
				}
			}
		} catch (Exception e) {
			ReportManager.warning("Could not extract Ticket ID: " + e.getMessage());
		}
		return "";
	}

	/**
	 * Extract the current ticket status (e.g. "New", "Waiting For Approval").
	 */
	public String getTicketStatus() {
		try {
			By statusLoc = AppiumBy.xpath(
					"//android.view.View[contains(@content-desc,'New') or contains(@content-desc,'Waiting') or contains(@content-desc,'Approved') or contains(@content-desc,'Dispatched')]");
			WebElement element = waitForVisible(statusLoc, Duration.ofSeconds(5));
			return element.getAttribute("content-desc");
		} catch (Exception e) {
			return "";
		}
	}

	// =========================================
	// TAB NAVIGATION METHODS
	// =========================================

	/**
	 * Swipe left on the tab bar to reveal rightmost tabs.
	 * Tab bar is around Y ratio 0.15 (Y ≈ 374 on 2424h screen).
	 */
	public void scrollTabsRight() {
		swipe(0.85, 0.15, 0.18, 0.15);
		try {
			Thread.sleep(500);
		} catch (InterruptedException ignored) {
		}
	}

	/**
	 * Swipe right on the tab bar to return to initial tabs.
	 */
	public void scrollTabsLeft() {
		swipe(0.18, 0.15, 0.85, 0.15);
		try {
			Thread.sleep(500);
		} catch (InterruptedException ignored) {
		}
	}

	/**
	 * Reset tab bar to the beginning (leftmost position).
	 */
	public void resetTabsToStart() {
		for (int i = 0; i < 3; i++) {
			scrollTabsLeft();
		}
	}

	/**
	 * Select any tab by name (e.g. "Properties", "SLA Details", "Public Logs",
	 * "CIs", "Documents", "Attachments", "Work Order").
	 * Handles horizontal scrolling automatically.
	 */
	public boolean selectTab(String tabName) {
		ReportManager.info("Selecting Ticket Details tab: " + tabName);
		By tabLoc = AppiumBy.xpath("//*[contains(@content-desc,'" + tabName + "') and @clickable='true']");

		// If visible now, click directly
		if (isDisplayedNow(tabLoc)) {
			click(tabLoc);
			ReportManager.pass("Clicked tab: " + tabName);
			pauseForContent();
			return true;
		}

		// Scroll right up to 3 times to find it
		for (int i = 0; i < 3; i++) {
			scrollTabsRight();
			if (isDisplayedNow(tabLoc)) {
				click(tabLoc);
				ReportManager.pass("Clicked tab after scrolling: " + tabName);
				pauseForContent();
				return true;
			}
		}

		// Reset to start and try
		resetTabsToStart();
		for (int i = 0; i < 3; i++) {
			if (isDisplayedNow(tabLoc)) {
				click(tabLoc);
				ReportManager.pass("Clicked tab after resetting: " + tabName);
				pauseForContent();
				return true;
			}
			scrollTabsRight();
		}

		ReportManager.warning("Tab not found: " + tabName);
		return false;
	}

	private void pauseForContent() {
		try {
			Thread.sleep(800);
		} catch (InterruptedException ignored) {
		}
	}

	// =========================================
	// TAB CONTENT VERIFICATION METHODS
	// =========================================

	/**
	 * Verify content of Properties tab:
	 * - Basic details header
	 * - Ticket Title
	 * - Caller
	 * - Service & Sub Service
	 * - Priority
	 * - Description
	 */
	public boolean verifyPropertiesTab() {
		ReportManager.info("Verifying Properties tab content...");
		boolean basicOk = isDisplayed(BASIC_DETAILS_HEADER, Duration.ofSeconds(5));
		boolean titleOk = isDisplayed(TICKET_TITLE_FIELD, Duration.ofSeconds(3));
		boolean callerOk = isDisplayed(CALLER_FIELD, Duration.ofSeconds(3));
		boolean serviceOk = isDisplayed(SERVICE_FIELD, Duration.ofSeconds(3));
		boolean priorityOk = isDisplayed(PRIORITY_FIELD, Duration.ofSeconds(3));
		boolean descOk = isDisplayed(DESCRIPTION_FIELD, Duration.ofSeconds(3));

		ReportManager.info("Properties tab verification: basic=" + basicOk + ", title=" + titleOk + ", caller=" + callerOk + ", service=" + serviceOk + ", priority=" + priorityOk + ", desc=" + descOk);
		return basicOk && titleOk && callerOk && serviceOk && priorityOk;
	}

	/**
	 * Verify content of SLA Details tab:
	 * - SLA TTO Passed
	 * - SLA TTR Passed
	 */
	public boolean verifySlaDetailsTab() {
		ReportManager.info("Verifying SLA Details tab content...");
		boolean ttoOk = isDisplayed(SLA_TTO_PASSED, Duration.ofSeconds(5));
		boolean ttrOk = isDisplayed(SLA_TTR_PASSED, Duration.ofSeconds(3));

		ReportManager.info("SLA Details tab verification: TTO=" + ttoOk + ", TTR=" + ttrOk);
		return ttoOk && ttrOk;
	}

	/**
	 * Verify content of Public Logs tab.
	 */
	public boolean verifyPublicLogsTab() {
		ReportManager.info("Verifying Public Logs tab content...");
		boolean tabOk = isDisplayed(PUBLIC_LOGS_INDICATOR, Duration.ofSeconds(5));
		ReportManager.info("Public Logs tab displayed: " + tabOk);
		return tabOk;
	}

	/**
	 * Verify content of CIs tab.
	 */
	public boolean verifyCIsTab() {
		ReportManager.info("Verifying CIs tab content...");
		boolean tabOk = isDisplayed(CIS_INDICATOR, Duration.ofSeconds(5));
		boolean addCiOk = isDisplayed(ADD_CI_BUTTON, Duration.ofSeconds(5));
		ReportManager.info("CIs tab displayed: " + tabOk + ", Add CI button displayed: " + addCiOk);
		return tabOk;
	}

	/**
	 * Check if 'Add CI' button is displayed on the CIs tab.
	 */
	public boolean isAddCIButtonDisplayed() {
		return isDisplayed(ADD_CI_BUTTON, Duration.ofSeconds(5));
	}

	/**
	 * Click the 'Add CI' button to open the Select CIs modal.
	 */
	public void clickAddCI() {
		ReportManager.info("Clicking 'Add CI' button...");
		click(ADD_CI_BUTTON);
		waitForVisible(SELECT_CIS_HEADER, Duration.ofSeconds(8));
		ReportManager.pass("Clicked 'Add CI' button, Select CIs modal is displayed");
	}

	/**
	 * Check if 'Select CIs' modal is displayed.
	 */
	public boolean isSelectCIsModalDisplayed() {
		return isDisplayed(SELECT_CIS_HEADER, Duration.ofSeconds(5));
	}

	/**
	 * Verify all elements on the 'Select CIs' modal:
	 * - Header ("Select CIs")
	 * - Search CI field
	 * - Available CI items
	 * - Cancel button
	 * - Select button
	 */
	public boolean verifySelectCIsModalElements() {
		ReportManager.info("Verifying all elements on 'Select CIs' modal...");
		boolean headerOk = isDisplayed(SELECT_CIS_HEADER, Duration.ofSeconds(5));
		boolean searchOk = isDisplayed(SEARCH_CI_INPUT, Duration.ofSeconds(5));
		boolean itemsOk = isDisplayed(CI_CHECKBOXES, Duration.ofSeconds(5));
		boolean cancelOk = isDisplayed(CANCEL_CI_BUTTON, Duration.ofSeconds(5));
		boolean selectOk = isDisplayed(SELECT_CI_BUTTON, Duration.ofSeconds(5));

		ReportManager.info("Select CIs modal elements: header=" + headerOk + ", search=" + searchOk + ", items=" + itemsOk + ", cancelBtn=" + cancelOk + ", selectBtn=" + selectOk);
		return headerOk && searchOk && itemsOk && cancelOk && selectOk;
	}

	/**
	 * Click 'Cancel' on the Select CIs modal to return to the CIs tab.
	 */
	public void clickCancelCIModal() {
		ReportManager.info("Clicking Cancel on Select CIs modal...");
		click(CANCEL_CI_BUTTON);
		waitForVisible(ADD_CI_BUTTON, Duration.ofSeconds(5));
		ReportManager.pass("Cancelled Select CIs modal and returned to CIs tab");
	}

	/**
	 * Click 'Select' on the Select CIs modal to add selected CIs.
	 */
	public void clickSelectCIModal() {
		ReportManager.info("Clicking Select on Select CIs modal...");
		click(SELECT_CI_BUTTON);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ignored) {
		}
	}

	/**
	 * Get the current header text of Select CIs modal (e.g. "Select CIs (0)").
	 */
	public String getSelectCIsHeaderTitle() {
		try {
			WebElement header = waitForVisible(SELECT_CIS_HEADER, Duration.ofSeconds(5));
			return header.getAttribute("content-desc");
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Search CIs using the search input.
	 */
	public void searchCI(String query) {
		ReportManager.info("Searching CI with query: " + query);
		try {
			WebElement input = waitForVisible(SEARCH_CI_INPUT, Duration.ofSeconds(5));
			input.click();
			input.clear();
			input.sendKeys(query);
			Thread.sleep(800);
		} catch (Exception e) {
			ReportManager.warning("Search CI failed: " + e.getMessage());
		}
	}

	/**
	 * Clear the CI search input using DEL keyevents to ensure Flutter triggers onChange.
	 */
	public void clearSearchCI() {
		ReportManager.info("Clearing CI search input...");
		try {
			WebElement input = waitForVisible(SEARCH_CI_INPUT, Duration.ofSeconds(5));
			input.click();
			if (getDriver() instanceof AndroidDriver) {
				AndroidDriver androidDriver = (AndroidDriver) getDriver();
				androidDriver.pressKey(new KeyEvent(AndroidKey.MOVE_END));
				for (int i = 0; i < 20; i++) {
					androidDriver.pressKey(new KeyEvent(AndroidKey.DEL));
				}
			} else {
				input.clear();
			}
			Thread.sleep(1000);
		} catch (Exception e) {
			ReportManager.warning("Clear CI search failed: " + e.getMessage());
		}
	}

	/**
	 * Get count of currently displayed CI checkboxes.
	 */
	public int getAvailableCIsCount() {
		try {
			return getDriver().findElements(CI_CHECKBOXES).size();
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Toggle CI checkbox at the specified index by tapping the checkbox square.
	 */
	public void toggleCICheckbox(int index) {
		ReportManager.info("Toggling CI checkbox at index: " + index);
		try {
			java.util.List<WebElement> boxes = getDriver().findElements(CI_CHECKBOXES);
			if (index < boxes.size()) {
				WebElement box = boxes.get(index);
				org.openqa.selenium.Rectangle rect = box.getRect();
				int tapX = rect.getX() + 45;
				int tapY = rect.getY() + 45;
				tapAt(tapX, tapY);
				Thread.sleep(800);
			}
		} catch (Exception e) {
			ReportManager.warning("Toggle CI checkbox failed: " + e.getMessage());
		}
	}

	/**
	 * Check if the CI at the specified index is checked.
	 */
	public boolean isCIChecked(int index) {
		try {
			java.util.List<WebElement> boxes = getDriver().findElements(CI_CHECKBOXES);
			if (index < boxes.size()) {
				String checked = boxes.get(index).getAttribute("checked");
				return "true".equalsIgnoreCase(checked);
			}
		} catch (Exception ignored) {
		}
		return false;
	}

	/**
	 * Check if the 'Select' button is enabled.
	 */
	public boolean isSelectButtonEnabled() {
		try {
			WebElement btn = waitForVisible(SELECT_CI_BUTTON, Duration.ofSeconds(3));
			String enabled = btn.getAttribute("enabled");
			String clickable = btn.getAttribute("clickable");
			return "true".equalsIgnoreCase(enabled) || "true".equalsIgnoreCase(clickable);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Verify content of Documents tab.
	 */
	public boolean verifyDocumentsTab() {
		ReportManager.info("Verifying Documents tab content...");
		boolean tabOk = isDisplayed(DOCUMENTS_INDICATOR, Duration.ofSeconds(5));
		ReportManager.info("Documents tab displayed: " + tabOk);
		return tabOk;
	}

	/**
	 * Verify content of Attachments tab.
	 */
	public boolean verifyAttachmentsTab() {
		ReportManager.info("Verifying Attachments tab content...");
		boolean tabOk = isDisplayed(ATTACHMENTS_INDICATOR, Duration.ofSeconds(5));
		ReportManager.info("Attachments tab displayed: " + tabOk);
		return tabOk;
	}

	/**
	 * Verify content of Work Order tab.
	 */
	public boolean verifyWorkOrderTab() {
		ReportManager.info("Verifying Work Order tab content...");
		boolean tabOk = isDisplayed(WORK_ORDER_INDICATOR, Duration.ofSeconds(5));
		ReportManager.info("Work Order tab displayed: " + tabOk);
		return tabOk;
	}

	/**
	 * Navigate back to Tickets screen from Ticket Details.
	 */
	public void navigateBack() {
		ReportManager.info("Navigating back from Ticket Details...");
		try {
			if (isDisplayedNow(BACK_BUTTON)) {
				click(BACK_BUTTON);
			} else if (getDriver() instanceof AndroidDriver) {
				((AndroidDriver) getDriver()).pressKey(new KeyEvent(AndroidKey.BACK));
			}
		} catch (Exception e) {
			if (getDriver() instanceof AndroidDriver) {
				((AndroidDriver) getDriver()).pressKey(new KeyEvent(AndroidKey.BACK));
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ignored) {
		}
	}
}
