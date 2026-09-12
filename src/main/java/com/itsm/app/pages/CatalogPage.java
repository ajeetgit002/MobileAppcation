package com.itsm.app.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.itsm.app.utils.ReportManager;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import lombok.Getter;

/**
 * Page Object representing the Catalog screen and all its catalog cards:
 * Announcements, Approvals, Documents, Asset Management, Close Tickets, and FAQs.
 */
@Getter
public class CatalogPage extends BasePage {

	// =========================================
	// CATALOG MAIN SCREEN LOCATORS
	// =========================================

	private static final By CATALOG_BOTTOM_TAB = AppiumBy
			.xpath("//android.widget.ImageView[@content-desc='Catalog']");
	private static final By CATALOG_HEADER = AppiumBy
			.xpath("//android.view.View[@content-desc='Catalog']");

	// Catalog Cards
	private static final By ANNOUNCEMENTS_CARD = AppiumBy
			.xpath("//android.widget.ImageView[contains(@content-desc,'Announcements')]");
	private static final By APPROVALS_CARD = AppiumBy
			.xpath("//android.widget.ImageView[@content-desc='Approvals']");
	private static final By DOCUMENTS_CARD = AppiumBy
			.xpath("//android.widget.ImageView[@content-desc='Documents']");
	private static final By ASSET_MANAGEMENT_CARD = AppiumBy
			.xpath("//android.widget.ImageView[contains(@content-desc,'Asset Management')]");
	private static final By CLOSE_TICKETS_CARD = AppiumBy
			.xpath("//android.widget.ImageView[@content-desc='Close Tickets']");
	private static final By FAQS_CARD = AppiumBy
			.xpath("//android.widget.ImageView[@content-desc='FAQs']");

	// Sub-screen Common Locators
	private static final By SUB_SCREEN_BACK_BUTTON = AppiumBy
			.xpath("//android.widget.Button[contains(@bounds,'[21,153][147,279]') or not(@content-desc)]");

	// Announcements Sub-screen
	private static final By ANNOUNCEMENT_HEADER = AppiumBy
			.xpath("//android.view.View[@content-desc='Announcement']");
	private static final By ANNOUNCEMENT_ITEMS = AppiumBy
			.xpath("//android.widget.ImageView[contains(@content-desc,'See More')]");

	// Approvals Sub-screen
	private static final By APPROVALS_HEADER = AppiumBy
			.xpath("//android.view.View[@content-desc='Approvals']");
	private static final By APPROVALS_SEARCH_INPUT = AppiumBy
			.xpath("//android.widget.EditText");

	// Documents Sub-screen
	private static final By DOCUMENTS_HEADER = AppiumBy
			.xpath("//android.view.View[@content-desc='Documents']");
	private static final By DOCUMENTS_SEARCH_INPUT = AppiumBy
			.xpath("//android.widget.EditText");
	private static final By DOCUMENT_ITEMS = AppiumBy
			.xpath("//android.widget.ImageView[contains(@content-desc,'Document') or contains(@content-desc,'document') or contains(@content-desc,'Note') or contains(@content-desc,'File')]");

	// Asset Management Sub-screen
	private static final By ASSET_MANAGEMENT_HEADER = AppiumBy
			.xpath("//android.view.View[@content-desc='Asset Management']");
	private static final By ASSET_SEARCH_INPUT = AppiumBy
			.xpath("//android.widget.EditText");
	private static final By ASSET_ITEMS = AppiumBy
			.xpath("//android.view.View[contains(@bounds,'[39,460][1041,2361]')]//android.widget.ImageView");

	// Close Tickets Sub-screen
	private static final By CLOSED_TICKETS_HEADER = AppiumBy
			.xpath("//android.view.View[@content-desc='Closed Tickets']");
	private static final By CLOSED_TICKETS_SEARCH_INPUT = AppiumBy
			.xpath("//android.widget.EditText");
	private static final By CLOSED_TICKET_ITEMS = AppiumBy
			.xpath("//android.view.View[contains(@content-desc,'Closed')]");

	// FAQs Sub-screen
	private static final By FAQS_HEADER = AppiumBy
			.xpath("//android.view.View[@content-desc='FAQs']");
	private static final By FAQS_SEARCH_INPUT = AppiumBy
			.xpath("//android.widget.EditText");
	private static final By FAQ_ITEMS = AppiumBy
			.xpath("//android.view.View[starts-with(@content-desc,'How') or starts-with(@content-desc,'Why')]");

	public CatalogPage() {
		super();
	}

	// =========================================
	// NAVIGATION METHODS
	// =========================================

	/**
	 * Open Catalog screen from bottom navigation.
	 */
	public void openCatalogTab() {
		ReportManager.info("Navigating to Catalog tab...");
		if (isDisplayedNow(CATALOG_HEADER)) {
			ReportManager.pass("Already on Catalog screen");
			return;
		}
		// If on any detail screen or sub-screen with a back button, tap back
		for (int i = 0; i < 3; i++) {
			if (isDisplayedNow(CATALOG_HEADER)) {
				return;
			}
			if (isDisplayedNow(SUB_SCREEN_BACK_BUTTON) || isDisplayedNow(AppiumBy.xpath("//*[contains(@content-desc,'Details')]"))) {
				tapAt(84, 216);
				try { Thread.sleep(600); } catch (Exception ignored) {}
			}
		}
		try {
			if (isDisplayedNow(CATALOG_BOTTOM_TAB)) {
				click(CATALOG_BOTTOM_TAB);
			} else {
				// Tap Catalog bottom tab (4th tab from left around x=984, y=2272)
				tapAt(984, 2272);
			}
		} catch (Exception e) {
			tapAt(984, 2272);
		}
		waitForVisible(CATALOG_HEADER, Duration.ofSeconds(10));
		ReportManager.pass("Catalog screen is displayed");
	}

	/**
	 * Check if Catalog screen header is displayed.
	 */
	public boolean isCatalogDisplayed() {
		return isDisplayed(CATALOG_HEADER, Duration.ofSeconds(8));
	}

	/**
	 * Navigate back to Catalog from a sub-screen using top-left back button or tap.
	 */
	public void navigateBackToCatalog() {
		ReportManager.info("Navigating back to Catalog screen...");
		if (isDisplayedNow(CATALOG_HEADER)) {
			return;
		}
		try {
			if (isDisplayedNow(SUB_SCREEN_BACK_BUTTON)) {
				click(SUB_SCREEN_BACK_BUTTON);
			} else {
				tapAt(84, 216); // coordinate of top-left back arrow
			}
			Thread.sleep(800);
		} catch (Exception e) {
			tapAt(84, 216);
		}
		if (!isDisplayedNow(CATALOG_HEADER)) {
			tapAt(984, 2272); // bottom nav Catalog tab fallback
		}
		waitForVisible(CATALOG_HEADER, Duration.ofSeconds(10));
		ReportManager.pass("Returned to Catalog screen successfully");
	}

	// =========================================
	// CARD 1: ANNOUNCEMENTS
	// =========================================

	public boolean isAnnouncementsCardDisplayed() {
		return isDisplayed(ANNOUNCEMENTS_CARD, Duration.ofSeconds(5));
	}

	public void clickAnnouncements() {
		ReportManager.info("Opening Announcements card...");
		try {
			click(ANNOUNCEMENTS_CARD);
		} catch (Exception e) {
			tapAt(200, 634); // center of Announcements card
		}
		waitForVisible(ANNOUNCEMENT_HEADER, Duration.ofSeconds(10));
		ReportManager.pass("Announcements screen opened");
	}

	public boolean isAnnouncementsScreenDisplayed() {
		return isDisplayed(ANNOUNCEMENT_HEADER, Duration.ofSeconds(5));
	}

	public int getAnnouncementsCount() {
		try {
			waitForVisible(ANNOUNCEMENT_ITEMS, Duration.ofSeconds(8));
			return getDriver().findElements(ANNOUNCEMENT_ITEMS).size();
		} catch (Exception e) {
			return getDriver().findElements(ANNOUNCEMENT_ITEMS).size();
		}
	}

	// =========================================
	// CARD 2: APPROVALS
	// =========================================

	public boolean isApprovalsCardDisplayed() {
		return isDisplayed(APPROVALS_CARD, Duration.ofSeconds(5));
	}

	public void clickApprovals() {
		ReportManager.info("Opening Approvals card...");
		try {
			click(APPROVALS_CARD);
		} catch (Exception e) {
			tapAt(540, 634); // center of Approvals card
		}
		waitForVisible(APPROVALS_HEADER, Duration.ofSeconds(10));
		ReportManager.pass("Approvals screen opened");
	}

	public boolean isApprovalsScreenDisplayed() {
		return isDisplayed(APPROVALS_HEADER, Duration.ofSeconds(5));
	}

	public void returnToCatalogFromApprovals() {
		ReportManager.info("Returning to Catalog from Approvals via bottom navigation...");
		tapAt(984, 2272);
		waitForVisible(CATALOG_HEADER, Duration.ofSeconds(10));
		ReportManager.pass("Returned to Catalog from Approvals");
	}

	// =========================================
	// CARD 3: DOCUMENTS
	// =========================================

	public boolean isDocumentsCardDisplayed() {
		return isDisplayed(DOCUMENTS_CARD, Duration.ofSeconds(5));
	}

	public void clickDocuments() {
		ReportManager.info("Opening Documents card...");
		try {
			click(DOCUMENTS_CARD);
		} catch (Exception e) {
			tapAt(881, 634); // center of Documents card
		}
		waitForVisible(DOCUMENTS_HEADER, Duration.ofSeconds(10));
		ReportManager.pass("Documents screen opened");
	}

	public boolean isDocumentsScreenDisplayed() {
		return isDisplayed(DOCUMENTS_HEADER, Duration.ofSeconds(5));
	}

	public int getDocumentsCount() {
		try {
			waitForVisible(DOCUMENT_ITEMS, Duration.ofSeconds(8));
			return getDriver().findElements(DOCUMENT_ITEMS).size();
		} catch (Exception e) {
			return getDriver().findElements(DOCUMENT_ITEMS).size();
		}
	}

	public void searchDocument(String query) {
		ReportManager.info("Searching Document: " + query);
		try {
			WebElement search = waitForVisible(DOCUMENTS_SEARCH_INPUT, Duration.ofSeconds(5));
			search.click();
			search.clear();
			search.sendKeys(query);
			Thread.sleep(800);
		} catch (Exception e) {
			ReportManager.warning("Search Document failed: " + e.getMessage());
		}
	}

	public void clearDocumentSearch() {
		ReportManager.info("Clearing Document search...");
		try {
			WebElement search = waitForVisible(DOCUMENTS_SEARCH_INPUT, Duration.ofSeconds(5));
			search.click();
			if (getDriver() instanceof AndroidDriver) {
				AndroidDriver androidDriver = (AndroidDriver) getDriver();
				androidDriver.pressKey(new KeyEvent(AndroidKey.MOVE_END));
				for (int i = 0; i < 25; i++) {
					androidDriver.pressKey(new KeyEvent(AndroidKey.DEL));
				}
			} else {
				search.clear();
			}
			Thread.sleep(800);
		} catch (Exception e) {
			ReportManager.warning("Clear Document search failed: " + e.getMessage());
		}
	}

	// =========================================
	// CARD 4: ASSET MANAGEMENT
	// =========================================

	public boolean isAssetManagementCardDisplayed() {
		return isDisplayed(ASSET_MANAGEMENT_CARD, Duration.ofSeconds(5));
	}

	public void clickAssetManagement() {
		ReportManager.info("Opening Asset Management card...");
		try {
			click(ASSET_MANAGEMENT_CARD);
		} catch (Exception e) {
			tapAt(200, 1115); // center of Asset Management card
		}
		waitForVisible(ASSET_MANAGEMENT_HEADER, Duration.ofSeconds(10));
		ReportManager.pass("Asset Management screen opened");
	}

	public boolean isAssetManagementScreenDisplayed() {
		return isDisplayed(ASSET_MANAGEMENT_HEADER, Duration.ofSeconds(5));
	}

	public int getAssetsCount() {
		try {
			waitForVisible(ASSET_ITEMS, Duration.ofSeconds(8));
			return getDriver().findElements(ASSET_ITEMS).size();
		} catch (Exception e) {
			return getDriver().findElements(ASSET_ITEMS).size();
		}
	}

	public void searchAsset(String query) {
		ReportManager.info("Searching Asset: " + query);
		try {
			WebElement search = waitForVisible(ASSET_SEARCH_INPUT, Duration.ofSeconds(5));
			search.click();
			search.clear();
			search.sendKeys(query);
			Thread.sleep(800);
		} catch (Exception e) {
			ReportManager.warning("Search Asset failed: " + e.getMessage());
		}
	}

	public void clearAssetSearch() {
		ReportManager.info("Clearing Asset search...");
		try {
			WebElement search = waitForVisible(ASSET_SEARCH_INPUT, Duration.ofSeconds(5));
			search.click();
			if (getDriver() instanceof AndroidDriver) {
				AndroidDriver androidDriver = (AndroidDriver) getDriver();
				androidDriver.pressKey(new KeyEvent(AndroidKey.MOVE_END));
				for (int i = 0; i < 25; i++) {
					androidDriver.pressKey(new KeyEvent(AndroidKey.DEL));
				}
			} else {
				search.clear();
			}
			Thread.sleep(800);
		} catch (Exception e) {
			ReportManager.warning("Clear Asset search failed: " + e.getMessage());
		}
	}

	// =========================================
	// CARD 5: CLOSE TICKETS
	// =========================================

	public boolean isCloseTicketsCardDisplayed() {
		return isDisplayed(CLOSE_TICKETS_CARD, Duration.ofSeconds(5));
	}

	public void clickCloseTickets() {
		ReportManager.info("Opening Close Tickets card...");
		try {
			click(CLOSE_TICKETS_CARD);
		} catch (Exception e) {
			tapAt(540, 1115); // center of Close Tickets card
		}
		waitForVisible(CLOSED_TICKETS_HEADER, Duration.ofSeconds(10));
		ReportManager.pass("Closed Tickets screen opened");
	}

	public boolean isCloseTicketsScreenDisplayed() {
		return isDisplayed(CLOSED_TICKETS_HEADER, Duration.ofSeconds(5));
	}

	public int getCloseTicketsCount() {
		try {
			waitForVisible(CLOSED_TICKET_ITEMS, Duration.ofSeconds(8));
			return getDriver().findElements(CLOSED_TICKET_ITEMS).size();
		} catch (Exception e) {
			return getDriver().findElements(CLOSED_TICKET_ITEMS).size();
		}
	}

	public void searchCloseTicket(String query) {
		ReportManager.info("Searching Closed Ticket: " + query);
		try {
			WebElement search = waitForVisible(CLOSED_TICKETS_SEARCH_INPUT, Duration.ofSeconds(5));
			search.click();
			search.clear();
			search.sendKeys(query);
			Thread.sleep(800);
		} catch (Exception e) {
			ReportManager.warning("Search Closed Ticket failed: " + e.getMessage());
		}
	}

	public void clearCloseTicketSearch() {
		ReportManager.info("Clearing Closed Ticket search...");
		try {
			WebElement search = waitForVisible(CLOSED_TICKETS_SEARCH_INPUT, Duration.ofSeconds(5));
			search.click();
			if (getDriver() instanceof AndroidDriver) {
				AndroidDriver androidDriver = (AndroidDriver) getDriver();
				androidDriver.pressKey(new KeyEvent(AndroidKey.MOVE_END));
				for (int i = 0; i < 25; i++) {
					androidDriver.pressKey(new KeyEvent(AndroidKey.DEL));
				}
			} else {
				search.clear();
			}
			Thread.sleep(800);
		} catch (Exception e) {
			ReportManager.warning("Clear Closed Ticket search failed: " + e.getMessage());
		}
	}

	// =========================================
	// CARD 6: FAQS
	// =========================================

	public boolean isFAQsCardDisplayed() {
		return isDisplayed(FAQS_CARD, Duration.ofSeconds(5));
	}

	public void clickFAQs() {
		ReportManager.info("Opening FAQs card...");
		try {
			click(FAQS_CARD);
		} catch (Exception e) {
			tapAt(881, 1115); // center of FAQs card
		}
		waitForVisible(FAQS_HEADER, Duration.ofSeconds(10));
		ReportManager.pass("FAQs screen opened");
	}

	public boolean isFAQsScreenDisplayed() {
		return isDisplayed(FAQS_HEADER, Duration.ofSeconds(5));
	}

	public int getFAQsCount() {
		try {
			waitForVisible(FAQ_ITEMS, Duration.ofSeconds(8));
			return getDriver().findElements(FAQ_ITEMS).size();
		} catch (Exception e) {
			return getDriver().findElements(FAQ_ITEMS).size();
		}
	}

	public void searchFAQ(String query) {
		ReportManager.info("Searching FAQ: " + query);
		try {
			WebElement search = waitForVisible(FAQS_SEARCH_INPUT, Duration.ofSeconds(5));
			search.click();
			search.clear();
			search.sendKeys(query);
			Thread.sleep(800);
		} catch (Exception e) {
			ReportManager.warning("Search FAQ failed: " + e.getMessage());
		}
	}

	public void clearFAQSearch() {
		ReportManager.info("Clearing FAQ search...");
		try {
			WebElement search = waitForVisible(FAQS_SEARCH_INPUT, Duration.ofSeconds(5));
			search.click();
			if (getDriver() instanceof AndroidDriver) {
				AndroidDriver androidDriver = (AndroidDriver) getDriver();
				androidDriver.pressKey(new KeyEvent(AndroidKey.MOVE_END));
				for (int i = 0; i < 25; i++) {
					androidDriver.pressKey(new KeyEvent(AndroidKey.DEL));
				}
			} else {
				search.clear();
			}
			Thread.sleep(800);
		} catch (Exception e) {
			ReportManager.warning("Clear FAQ search failed: " + e.getMessage());
		}
	}
}
