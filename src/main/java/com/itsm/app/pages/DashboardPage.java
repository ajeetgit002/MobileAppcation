package com.itsm.app.pages;

import org.openqa.selenium.By;


import io.appium.java_client.AppiumBy;
import lombok.Getter;

@Getter
public class DashboardPage extends BasePage {

	// =========================================
	// HEADER / DASHBOARD MARKER
	// =========================================

	private static final By DASHBOARD_MARKER = AppiumBy.accessibilityId("My Activity");
	private static final By USER_NAME = AppiumBy.xpath("//*[contains(@content-desc, 'Hello')]");
	private static final By USER_ROLE = AppiumBy.xpath("//*[contains(@content-desc,'Service Requester')]");
	private static final By NOTIFICATION_BUTTON = AppiumBy
			.xpath("//*[contains(@content-desc,'notification') or contains(@text,'notification')]");

	// =========================================
	// TOP ANNOUNCEMENT / BANNER
	// =========================================

	private static final By ANNOUNCEMENT_BANNER = AppiumBy.xpath("//*[contains(@content-desc,'Work as a Team')]");
	private static final By ANNOUNCEMENT_CLOSE_BUTTON = AppiumBy
			.androidUIAutomator("new UiSelector().className(\"android.view.View\").instance(8)");

	// =========================================
	// RAISE NEW TICKET
	// =========================================

	private static final By RAISE_NEW_TICKET = AppiumBy.xpath("//*[contains(@content-desc,'Raise a New Ticket')]");

	// =========================================
	// MY ACTIVITY
	// =========================================

	private static final By MY_ACTIVITY = AppiumBy.accessibilityId("My Activity");
	private static final By MY_ACTIVITY_VIEW_ALL = AppiumBy
			.xpath("//*[contains(@content-desc,'My Activity')]/following::*[contains(@content-desc,'View All')][1]");
	private static final By OPEN_INCIDENTS = AppiumBy.xpath("//*[contains(@content-desc,'Open Incidents')]");
	private static final By PENDING_APPROVALS = AppiumBy.xpath("//*[contains(@content-desc,'Pending Approvals')]");
	private static final By OPEN_SERVICE_REQUESTS = AppiumBy
			.xpath("//*[contains(@content-desc,'Open Service Requests')]");
	private static final By ASSIGNED_ASSETS = AppiumBy.xpath("//*[contains(@content-desc,'Assigned Assets')]");

	// =========================================
	// ONGOING TICKETS
	// =========================================

	private static final By ONGOING_TICKETS = AppiumBy.accessibilityId("Ongoing Tickets");
	private static final By ONGOING_TICKETS_VIEW_ALL = AppiumBy.xpath(
			"//*[contains(@content-desc,'Ongoing Tickets')]/following::*[contains(@content-desc,'View All')][1]");

	// =========================================
	// SLA HEALTH
	// =========================================

	private static final By SLA_HEALTH = AppiumBy.xpath("//*[contains(@content-desc,'SLA Health')]");
	private static final By TICKETS_WITHIN_SLA = AppiumBy.xpath("//*[contains(@content-desc,'Tickets within SLA')]");
	private static final By TICKET_APPROACHING_SLA = AppiumBy
			.xpath("//*[contains(@content-desc,'Ticket approaching SLA')]");

	// =========================================
	// OPERATIONS
	// =========================================

	private static final By OPERATIONS = AppiumBy.accessibilityId("Operations");
	private static final By OPERATIONS_VIEW_ALL = AppiumBy
			.xpath("//*[contains(@text,'Operations')]/following::*[contains(@content-desc,'View All')][1]");

	// =========================================
	// OPERATION MENU ITEMS
	// =========================================

	private static final By ANNOUNCEMENTS = AppiumBy.xpath("//*[contains(@content-desc,'Announcements')]");
	private static final By APPROVALS = AppiumBy.xpath("//*[contains(@content-desc,'Approvals')]");
	private static final By DOCUMENTS = AppiumBy.xpath("//*[contains(@content-desc,'Documents')]");
	private static final By ASSET_MANAGEMENT = AppiumBy.xpath("//*[contains(@content-desc,'Asset Management')]");
	private static final By CLOSE_TICKETS = AppiumBy.xpath("//*[contains(@content-desc,'Close Tickets')]");
	private static final By FAQS = AppiumBy
			.xpath("//*[contains(@content-desc,'FAQs') or contains(@content-desc,'Find answers to common questions')]");

	// =========================================
	// BOTTOM NAVIGATION
	// =========================================

	private static final By HOME_TAB = AppiumBy.accessibilityId("Home");
	private static final By TICKETS_TAB = AppiumBy
			.xpath("(//*[contains(@content-desc,'Ticket') or contains(@text,'Ticket')])[last()]");
	private static final By APPROVAL_TAB = AppiumBy
			.xpath("(//*[contains(@content-desc,'Approval') or contains(@text,'Approval')])[last()]");
	private static final By MORE_TAB = AppiumBy.xpath("//*[contains(@content-desc,'More') or contains(@text,'More')]");

	// =========================================
	// CONSTRUCTORS
	// =========================================

	public DashboardPage() {
		super();
	}

	// =========================================
	// OVERLAYS / DIALOGS
	// =========================================

	public void dismissOverlaysIfPresent() {
		clickIfDisplayed(AppiumBy.accessibilityId("Later"));
		clickIfDisplayed(AppiumBy.xpath("//*[@content-desc='Later' or @text='Later']"));
		clickIfDisplayed(By.id("com.android.permissioncontroller:id/permission_allow_button"));
		clickIfDisplayed(AppiumBy.xpath("//*[@text='Allow' or @content-desc='Allow']"));
	}

	// =========================================
	// DASHBOARD VERIFICATION
	// =========================================

	public boolean isDashboardDisplayed() {
		dismissOverlaysIfPresent();
		return scrollToElement(DASHBOARD_MARKER);
	}

	public boolean isUserNameDisplayed() {
		return scrollToElement(USER_NAME);
	}

	public boolean isUserRoleDisplayed() {
		return scrollToElement(USER_ROLE);
	}

	public boolean isMyActivityDisplayed() {
		return scrollToElement(MY_ACTIVITY);
	}

	public boolean isSlaHealthDisplayed() {
		return scrollToElement(SLA_HEALTH);
	}

	public boolean isOperationsDisplayed() {
		return scrollToElement(OPERATIONS);
	}

	// =========================================
	// HEADER ACTIONS
	// =========================================

	public void clickNotification() {
		click(NOTIFICATION_BUTTON);
	}

	// =========================================
	// ANNOUNCEMENT BANNER
	// =========================================

	public boolean isAnnouncementBannerDisplayed() {
		return scrollToElement(ANNOUNCEMENT_BANNER);
	}

	public void closeAnnouncementBanner() {
		clickIfDisplayed(ANNOUNCEMENT_CLOSE_BUTTON);
	}

	// =========================================
	// RAISE NEW TICKET
	// =========================================

	public boolean isRaiseNewTicketDisplayed() {
		return scrollToElement(RAISE_NEW_TICKET);
	}

	public void clickRaiseNewTicket() {
		click(RAISE_NEW_TICKET);
	}

	// =========================================
	// MY ACTIVITY
	// =========================================

	public boolean isOpenIncidentsDisplayed() {
		return scrollToElement(OPEN_INCIDENTS);
	}

	public boolean isPendingApprovalsDisplayed() {
		return scrollToElement(PENDING_APPROVALS);
	}

	public boolean isOpenServiceRequestsDisplayed() {
		return scrollToElement(OPEN_SERVICE_REQUESTS);
	}

	public boolean isAssignedAssetsDisplayed() {
		return scrollToElement(ASSIGNED_ASSETS);
	}

	public void clickMyActivityViewAll() {
		click(MY_ACTIVITY_VIEW_ALL);
	}

	// =========================================
	// ONGOING TICKETS
	// =========================================

	public boolean isOngoingTicketsDisplayed() {
		return scrollToElement(ONGOING_TICKETS);
	}

	public void clickOngoingTicketsViewAll() {
		click(ONGOING_TICKETS_VIEW_ALL);
	}

	// =========================================
	// SLA HEALTH
	// =========================================

	public boolean isTicketsWithinSlaDisplayed() {
		return scrollToElement(TICKETS_WITHIN_SLA);
	}

	public boolean isTicketApproachingSlaDisplayed() {
		return scrollToElement(TICKET_APPROACHING_SLA);
	}

	public String getTicketsWithinSlaText() {
		return getText(TICKETS_WITHIN_SLA);
	}

	public String getTicketApproachingSlaText() {
		return getText(TICKET_APPROACHING_SLA);
	}

	// =========================================
	// OPERATIONS
	// =========================================

	public void clickOperationsViewAll() {
		click(OPERATIONS_VIEW_ALL);
	}

	// =========================================
	// OPERATION MENU ITEMS
	// =========================================

	public boolean isAnnouncementsDisplayed() {
		return scrollToElement(ANNOUNCEMENTS);
	}

	public void clickAnnouncements() {
		click(ANNOUNCEMENTS);
	}

	public boolean isApprovalsDisplayed() {
		return scrollToElement(APPROVALS);
	}

	public void clickApprovals() {
		click(APPROVALS);
	}

	public boolean isDocumentsDisplayed() {
		return scrollToElement(DOCUMENTS);
	}

	public void clickDocuments() {
		click(DOCUMENTS);
	}

	public boolean isAssetManagementDisplayed() {
		return scrollToElement(ASSET_MANAGEMENT);
	}

	public void clickAssetManagement() {
		click(ASSET_MANAGEMENT);
	}

	public boolean isCloseTicketsDisplayed() {
		return scrollToElement(CLOSE_TICKETS);
	}

	public void clickCloseTickets() {
		click(CLOSE_TICKETS);
	}

	public boolean isFaqsDisplayed() {
		return scrollToElement(FAQS);
	}

	public void clickFaqs() {
		click(FAQS);
	}

	// =========================================
	// BOTTOM NAVIGATION
	// =========================================

	public void clickHome() {
		click(HOME_TAB);
	}

	public void clickTickets() {
		click(TICKETS_TAB);
	}

	public void clickApprovalsTab() {
		click(APPROVAL_TAB);
	}

	public void clickMore() {
		click(MORE_TAB);
	}

	// =========================================
	// COMPLETE DASHBOARD VERIFICATION
	// =========================================

	public boolean isAllDashboardContentDisplayed() {
		return isDashboardDisplayed() && isUserNameDisplayed() && isUserRoleDisplayed() && isSlaHealthDisplayed()
				&& isTicketsWithinSlaDisplayed() && isOperationsDisplayed()
				&& isAnnouncementsDisplayed() && isApprovalsDisplayed() && isDocumentsDisplayed()
				&& isAssetManagementDisplayed() && isCloseTicketsDisplayed() && isFaqsDisplayed();
	}
}