package com.itsm.app.pages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.itsm.app.utils.ReportManager;

import io.appium.java_client.AppiumBy;
import lombok.Getter;

/**
 * Page Object representing the Tickets screen (Ongoing and Resolved lists)
 * and the horizontal filter bar in the ITSM mobile application.
 */
@Getter
public class TicketsPage extends BasePage {

	// =========================================
	// NAVIGATION & TAB LOCATORS
	// =========================================

	private static final By TICKETS_BOTTOM_TAB = AppiumBy
			.xpath("//android.widget.ImageView[@content-desc='Tickets']");
	private static final By ONGOING_TAB = AppiumBy.xpath("//*[starts-with(@content-desc,'Ongoing')]");
	private static final By RESOLVED_TAB = AppiumBy.xpath("//*[starts-with(@content-desc,'Resolved')]");
	private static final By TICKETS_HEADER = AppiumBy.xpath("//*[contains(@content-desc,'Tickets')]");

	// =========================================
	// FILTER & LIST LOCATORS
	// =========================================

	private static final By TICKET_CARDS = AppiumBy.xpath(
			"//android.view.View[starts-with(@content-desc,'P') and contains(@content-desc,'R-')]");
	private static final By NO_TICKETS_PLACEHOLDER = AppiumBy.xpath(
			"//*[contains(@content-desc,'No Data') or contains(@content-desc,'No Ticket') or contains(@content-desc,'No Record')]");

	public TicketsPage() {
		super();
	}

	// =========================================
	// NAVIGATION METHODS
	// =========================================

	/**
	 * Navigate to the Tickets tab from bottom navigation.
	 */
	public void openTicketsTab() {
		ReportManager.info("Opening Tickets tab from bottom navigation...");
		if (isDisplayedNow(ONGOING_TAB)) {
			ReportManager.pass("Already on Tickets screen");
			return;
		}
		try {
			if (isDisplayedNow(AppiumBy.xpath("//*[contains(@content-desc,'Ticket Details')]"))) {
				tapAt(60, 220);
				Thread.sleep(1000);
				if (isDisplayedNow(ONGOING_TAB)) {
					ReportManager.pass("Returned to Tickets screen");
					return;
				}
			}
			if (isDisplayedNow(TICKETS_BOTTOM_TAB)) {
				click(TICKETS_BOTTOM_TAB);
			} else {
				// Tap bottom nav tab 2 (Tickets is the 2nd tab from left around x=330, y=2272)
				tapAt(330, 2272);
			}
		} catch (Exception e) {
			tapAt(330, 2272);
		}
		waitForVisible(ONGOING_TAB, Duration.ofSeconds(10));
		ReportManager.pass("Tickets screen is displayed");
	}

	/**
	 * Check if Ongoing tab is displayed.
	 */
	public boolean isOngoingTabDisplayed() {
		return isDisplayed(ONGOING_TAB, Duration.ofSeconds(8));
	}

	/**
	 * Click the Ongoing tab at the top.
	 */
	public void selectOngoingTab() {
		ReportManager.info("Selecting Ongoing tab...");
		click(ONGOING_TAB);
		try {
			Thread.sleep(800);
		} catch (InterruptedException ignored) {
		}
		ReportManager.pass("Ongoing tab selected");
	}

	/**
	 * Click the Resolved tab at the top.
	 */
	public void selectResolvedTab() {
		ReportManager.info("Selecting Resolved tab...");
		click(RESOLVED_TAB);
		try {
			Thread.sleep(800);
		} catch (InterruptedException ignored) {
		}
		ReportManager.pass("Resolved tab selected");
	}

	/**
	 * Get the total ticket count displayed in the Ongoing tab header (e.g. "66").
	 */
	public int getOngoingTicketsCount() {
		try {
			WebElement element = waitForVisible(ONGOING_TAB, Duration.ofSeconds(5));
			String desc = element.getAttribute("content-desc");
			if (desc != null) {
				Matcher matcher = Pattern.compile("Ongoing\\s*(\\d+)", Pattern.CASE_INSENSITIVE).matcher(desc);
				if (matcher.find()) {
					return Integer.parseInt(matcher.group(1));
				}
			}
		} catch (Exception e) {
			ReportManager.warning("Could not parse Ongoing count: " + e.getMessage());
		}
		return 0;
	}

	// =========================================
	// HORIZONTAL FILTER BAR METHODS
	// =========================================

	/**
	 * Swipe left on the filter bar to reveal rightmost filters.
	 * Filter bar is located around Y ratio 0.29 (Y ≈ 712 on 2424h screen).
	 */
	public void scrollFilterBarRight() {
		swipe(0.85, 0.29, 0.18, 0.29);
		try {
			Thread.sleep(500);
		} catch (InterruptedException ignored) {
		}
	}

	/**
	 * Swipe right on the filter bar to return to initial filters.
	 */
	public void scrollFilterBarLeft() {
		swipe(0.18, 0.29, 0.85, 0.29);
		try {
			Thread.sleep(500);
		} catch (InterruptedException ignored) {
		}
	}

	/**
	 * Reset filter bar to the beginning (leftmost position).
	 */
	public void resetFilterBarToStart() {
		for (int i = 0; i < 3; i++) {
			scrollFilterBarLeft();
		}
	}

	/**
	 * Select a filter chip by name. Handles horizontal scrolling if not immediately visible.
	 * Available chips: "All", "New", "Dispatched", "Waiting For Approval", "Approved",
	 * "Rejected", "Assigned", "Pending", "Escalated TTO", "Escalated TTR".
	 */
	public boolean selectFilter(String filterName) {
		ReportManager.info("Selecting filter: " + filterName);
		By filterLoc = AppiumBy.xpath("//android.view.View[@content-desc='" + filterName + "' and @clickable='true']");

		// If visible now, click directly
		if (isDisplayedNow(filterLoc)) {
			click(filterLoc);
			ReportManager.pass("Clicked filter: " + filterName);
			pauseForListUpdate();
			return true;
		}

		// Scroll right up to 4 times to find it
		for (int i = 0; i < 4; i++) {
			scrollFilterBarRight();
			if (isDisplayedNow(filterLoc)) {
				click(filterLoc);
				ReportManager.pass("Clicked filter after scrolling: " + filterName);
				pauseForListUpdate();
				return true;
			}
		}

		// Reset to start and try scrolling again
		resetFilterBarToStart();
		for (int i = 0; i < 4; i++) {
			if (isDisplayedNow(filterLoc)) {
				click(filterLoc);
				ReportManager.pass("Clicked filter after resetting: " + filterName);
				pauseForListUpdate();
				return true;
			}
			scrollFilterBarRight();
		}

		ReportManager.warning("Filter chip not found: " + filterName);
		return false;
	}

	private void pauseForListUpdate() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ignored) {
		}
	}

	// =========================================
	// TICKET LIST METHODS
	// =========================================

	/**
	 * Get the number of ticket cards currently visible in the list.
	 */
	public int getVisibleTicketsCount() {
		try {
			List<WebElement> tickets = getDriver().findElements(TICKET_CARDS);
			return tickets.size();
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Get all visible ticket summaries / reference IDs from the list.
	 */
	public List<String> getVisibleTicketDescriptions() {
		List<String> descriptions = new ArrayList<>();
		try {
			List<WebElement> tickets = getDriver().findElements(TICKET_CARDS);
			for (WebElement ticket : tickets) {
				String desc = ticket.getAttribute("content-desc");
				if (desc != null && !desc.isEmpty()) {
					descriptions.add(desc);
				}
			}
		} catch (Exception ignored) {
		}
		return descriptions;
	}

	/**
	 * Open the first available ticket in the list to navigate to Ticket Details.
	 */
	public boolean openFirstTicket() {
		ReportManager.info("Opening the first visible ticket...");
		for (int attempt = 1; attempt <= 3; attempt++) {
			try {
				WebElement firstTicket = waitForVisible(TICKET_CARDS, Duration.ofSeconds(10));
				firstTicket.click();
				ReportManager.pass("Clicked first ticket card (attempt " + attempt + ")");
				Thread.sleep(1500);
				if (isDisplayedNow(AppiumBy.xpath("//*[contains(@content-desc,'Ticket Details')]"))) {
					return true;
				}
				// Retry with coordinate tap if standard element click was intercepted
				org.openqa.selenium.Rectangle rect = firstTicket.getRect();
				tapAt(rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2);
				Thread.sleep(1500);
				if (isDisplayedNow(AppiumBy.xpath("//*[contains(@content-desc,'Ticket Details')]"))) {
					return true;
				}
			} catch (Exception e) {
				ReportManager.warning("Attempt " + attempt + " to open first ticket failed: " + e.getMessage());
			}
		}
		return isDisplayedNow(AppiumBy.xpath("//*[contains(@content-desc,'Ticket Details')]"));
	}

	/**
	 * Open a specific ticket by its Reference Ticket ID (e.g. "R-011701").
	 */
	public boolean openTicketByReferenceId(String ticketId) {
		ReportManager.info("Opening ticket by ID: " + ticketId);
		By ticketLoc = AppiumBy.xpath("//android.view.View[contains(@content-desc,'" + ticketId + "')]");
		if (isDisplayed(ticketLoc, Duration.ofSeconds(5))) {
			click(ticketLoc);
			ReportManager.pass("Clicked ticket #" + ticketId);
			return true;
		}
		return false;
	}
}
