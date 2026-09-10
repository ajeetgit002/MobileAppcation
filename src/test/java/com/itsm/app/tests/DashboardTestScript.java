package com.itsm.app.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.itsm.app.base.BaseTest;
import com.itsm.app.pages.DashboardPage;
import com.itsm.app.utils.ReportManager;

public class DashboardTestScript extends BaseTest {

	private DashboardPage dashboardPage;

	@Override
	protected boolean isClassLevelSession() {
		return true; // Reuses single session across all dashboard tests
	}

	@BeforeClass
	public void setupDashboard() {
		dashboardPage = context.getDashboardPage();
	}

	// =========================================
	// HEADER / DASHBOARD MARKER
	// =========================================

	@Test(priority = 1, description = "Verify Dashboard Header and User Details")
	public void testDashboardHeaderElements() {
		Assert.assertTrue(dashboardPage.isDashboardDisplayed(), "Dashboard marker should be displayed");
		Assert.assertTrue(dashboardPage.isUserNameDisplayed(), "User name should be displayed");
		Assert.assertTrue(dashboardPage.isUserRoleDisplayed(), "User role should be displayed");
	}

	// =========================================
	// OPERATION MENU ITEMS
	// =========================================

	@Test(priority = 2, description = "Verify Operation Menu Items")
	public void testOperationMenuItems() {
		Assert.assertTrue(dashboardPage.isAnnouncementsDisplayed(), "Announcements should be displayed");
		Assert.assertTrue(dashboardPage.isApprovalsDisplayed(), "Approvals should be displayed");
		Assert.assertTrue(dashboardPage.isDocumentsDisplayed(), "Documents should be displayed");
		Assert.assertTrue(dashboardPage.isAssetManagementDisplayed(), "Asset Management should be displayed");
		Assert.assertTrue(dashboardPage.isCloseTicketsDisplayed(), "Close Tickets should be displayed");
		Assert.assertTrue(dashboardPage.isFaqsDisplayed(), "FAQs should be displayed");
	}

	// =========================================
	// RAISE NEW TICKET
	// =========================================

	@Test(priority = 3, description = "Verify Raise New Ticket is Visible")
	public void testRaiseNewTicketVisible() {
		Assert.assertTrue(dashboardPage.isRaiseNewTicketDisplayed(), "Raise New Ticket option should be displayed");
	}

	// =========================================
	// MY ACTIVITY
	// =========================================

	@Test(priority = 4, description = "Verify My Activity Section Items")
	public void testMyActivitySection() {
		Assert.assertTrue(dashboardPage.isMyActivityDisplayed(), "My Activity section should be displayed");
		Assert.assertTrue(dashboardPage.isOpenIncidentsDisplayed(), "Open Incidents should be displayed");
		Assert.assertTrue(dashboardPage.isPendingApprovalsDisplayed(), "Pending Approvals should be displayed");
		Assert.assertTrue(dashboardPage.isOpenServiceRequestsDisplayed(), "Open Service Requests should be displayed");
		Assert.assertTrue(dashboardPage.isAssignedAssetsDisplayed(), "Assigned Assets should be displayed");
	}

	// =========================================
	// ONGOING TICKETS
	// =========================================

	@Test(priority = 5, description = "Verify Ongoing Tickets Section")
	public void testOngoingTicketsSection() {
		Assert.assertTrue(dashboardPage.isOngoingTicketsDisplayed(), "Ongoing Tickets section should be displayed");
	}

	// =========================================
	// SLA HEALTH
	// =========================================

	@Test(priority = 6, description = "Verify SLA Health Section")
	public void testSlaHealthSection() {
		Assert.assertTrue(dashboardPage.isSlaHealthDisplayed(), "SLA Health section should be displayed");
		Assert.assertTrue(dashboardPage.isTicketsWithinSlaDisplayed(), "Tickets within SLA should be displayed");

		if (dashboardPage.isTicketApproachingSlaDisplayed()) {
			ReportManager.info("Ticket approaching SLA is displayed");
		} else {
			ReportManager.info("Ticket approaching SLA not shown — likely 0 tickets approaching SLA");
		}
	}

	// =========================================
	// OPERATIONS
	// =========================================

	@Test(priority = 7, description = "Verify Operations Section")
	public void testOperationsSection() {
		Assert.assertTrue(dashboardPage.isOperationsDisplayed(), "Operations section should be displayed");
	}

	// =========================================
	// ANNOUNCEMENT BANNER
	// =========================================

	@Test(priority = 8, description = "Verify Announcement Banner")
	public void testAnnouncementBanner() {
		Assert.assertTrue(dashboardPage.isAnnouncementBannerDisplayed(), "Announcement banner should be displayed");
	}
}