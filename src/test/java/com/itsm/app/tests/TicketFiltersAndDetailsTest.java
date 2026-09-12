package com.itsm.app.tests;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.itsm.app.base.BaseTest;
import com.itsm.app.pages.DashboardPage;
import com.itsm.app.pages.TicketDetailsPage;
import com.itsm.app.pages.TicketsPage;
import com.itsm.app.utils.ReportManager;

/**
 * Test suite automating:
 * 1. All 10 filter chips on the Ongoing Tickets screen:
 *    (All, New, Dispatched, Waiting For Approval, Approved, Rejected,
 *     Assigned, Pending, Escalated TTO, Escalated TTR)
 * 2. All 7 tabs on the Ticket Details screen:
 *    (Properties, SLA Details, Public Logs, CIs, Documents, Attachments, Work Order)
 */
public class TicketFiltersAndDetailsTest extends BaseTest {

	private TicketsPage ticketsPage;
	private TicketDetailsPage ticketDetailsPage;
	private DashboardPage dashboardPage;

	@Override
	protected boolean isClassLevelSession() {
		return true; // Reuses authenticated driver session across all tests
	}

	@BeforeClass
	public void setupPages() {
		ticketsPage = context.getTicketsPage();
		ticketDetailsPage = context.getTicketDetailsPage();
		dashboardPage = context.getDashboardPage();
		dashboardPage.dismissOverlaysIfPresent();
	}

	// =========================================================================
	// TEST 1: Ongoing Screen - Automate & Verify All 10 Filters
	// =========================================================================

	@Test(priority = 1, description = "Automate and verify all 10 filter chips on the Ongoing Tickets screen")
	public void testVerifyAllOngoingFilters() {
		ReportManager.info("=== TEST 1: Automate All Filters on Ongoing Screen ===");

		// 1. Open Tickets screen
		ticketsPage.openTicketsTab();
		ticketsPage.selectOngoingTab();

		int totalOngoing = ticketsPage.getOngoingTicketsCount();
		ReportManager.info("Total ongoing tickets count from tab: " + totalOngoing);
		Assert.assertTrue(totalOngoing >= 0, "Ongoing tickets count should be a valid non-negative integer");

		// All 10 filter chips on Ongoing screen
		String[] filters = {
			"All",
			"New",
			"Dispatched",
			"Waiting For Approval",
			"Approved",
			"Rejected",
			"Assigned",
			"Pending",
			"Escalated TTO",
			"Escalated TTR"
		};

		// 2. Iterate through and select each filter chip
		for (String filter : filters) {
			ReportManager.info("Testing filter chip: '" + filter + "'");
			boolean selected = ticketsPage.selectFilter(filter);
			Assert.assertTrue(selected, "Filter chip '" + filter + "' should be found and clicked");

			int visibleCount = ticketsPage.getVisibleTicketsCount();
			List<String> ticketDescs = ticketsPage.getVisibleTicketDescriptions();
			ReportManager.pass("Filter '" + filter + "' applied successfully. Visible ticket cards: " + visibleCount);

			if (visibleCount > 0) {
				ReportManager.info("First ticket under '" + filter + "': " + ticketDescs.get(0).replace("\n", " | "));
			}
		}

		// 3. Reset filter to 'All'
		ticketsPage.selectFilter("All");
		ReportManager.pass("Reset filter bar back to 'All'. All 10 Ongoing filters verified successfully!");
	}

	// =========================================================================
	// TEST 2: Ticket Details Screen - Automate & Verify All 7 Tabs
	// =========================================================================

	@Test(priority = 2, description = "Open Ticket Details and automate verification of all 7 tabs", dependsOnMethods = "testVerifyAllOngoingFilters")
	public void testVerifyTicketDetailsAllTabs() {
		ReportManager.info("=== TEST 2: Automate All 7 Tabs on Ticket Details Screen ===");

		// 1. Ensure on Tickets Ongoing screen
		ticketsPage.openTicketsTab();
		ticketsPage.selectOngoingTab();
		ticketsPage.selectFilter("All");

		// 2. Open first available ticket to enter Ticket Details
		boolean opened = ticketsPage.openFirstTicket();
		Assert.assertTrue(opened, "Should successfully click a ticket card to open Ticket Details");

		Assert.assertTrue(ticketDetailsPage.isTicketDetailsDisplayed(),
				"Ticket Details screen header should be displayed");

		String ticketId = ticketDetailsPage.getTicketId();
		String status = ticketDetailsPage.getTicketStatus();
		ReportManager.info("Opened Ticket ID: " + ticketId + " | Status: " + status);

		// 3. Verify Tab 1: Properties
		ReportManager.info("Verifying Tab 1: Properties");
		boolean propSelected = ticketDetailsPage.selectTab("Properties");
		Assert.assertTrue(propSelected, "Properties tab should be selectable");
		Assert.assertTrue(ticketDetailsPage.verifyPropertiesTab(),
				"Properties tab should display Basic details, Title, Caller, Service, and Priority");
		ReportManager.pass("Tab 1: Properties verified successfully");

		// 4. Verify Tab 2: SLA Details
		ReportManager.info("Verifying Tab 2: SLA Details");
		boolean slaSelected = ticketDetailsPage.selectTab("SLA Details");
		Assert.assertTrue(slaSelected, "SLA Details tab should be selectable");
		Assert.assertTrue(ticketDetailsPage.verifySlaDetailsTab(),
				"SLA Details tab should display SLA TTO Passed and SLA TTR Passed");
		ReportManager.pass("Tab 2: SLA Details verified successfully");

		// 5. Verify Tab 3: Public Logs
		ReportManager.info("Verifying Tab 3: Public Logs");
		boolean logsSelected = ticketDetailsPage.selectTab("Public Logs");
		Assert.assertTrue(logsSelected, "Public Logs tab should be selectable");
		Assert.assertTrue(ticketDetailsPage.verifyPublicLogsTab(),
				"Public Logs tab content should be displayed");
		ReportManager.pass("Tab 3: Public Logs verified successfully");

		// 6. Verify Tab 4: CIs & Add CI Functionality
		ReportManager.info("Verifying Tab 4: CIs");
		boolean cisSelected = ticketDetailsPage.selectTab("CIs");
		Assert.assertTrue(cisSelected, "CIs tab should be selectable");
		Assert.assertTrue(ticketDetailsPage.verifyCIsTab(),
				"CIs tab content should be displayed");

		// Detailed verification of Add CI button and modal
		ReportManager.info("Verifying 'Add CI' button and modal elements...");
		Assert.assertTrue(ticketDetailsPage.isAddCIButtonDisplayed(),
				"'Add CI' button should be visible on CIs tab");

		// Open 'Select CIs' modal
		ticketDetailsPage.clickAddCI();
		Assert.assertTrue(ticketDetailsPage.isSelectCIsModalDisplayed(),
				"'Select CIs' modal should be displayed upon clicking Add CI");
		Assert.assertTrue(ticketDetailsPage.verifySelectCIsModalElements(),
				"All Select CIs modal elements (header, search input, checkboxes, Cancel, Select) should be displayed");

		// Verify initial header and Select button state
		String headerInitial = ticketDetailsPage.getSelectCIsHeaderTitle();
		ReportManager.info("Initial modal header: " + headerInitial);
		Assert.assertTrue(headerInitial.contains("Select CIs (0)"),
				"Initial modal header should indicate 0 selected CIs");
		Assert.assertFalse(ticketDetailsPage.isSelectButtonEnabled(),
				"'Select' button should initially be disabled when 0 CIs are selected");

		// Test CI Search functionality
		int initialCount = ticketDetailsPage.getAvailableCIsCount();
		ReportManager.info("Available CIs count before search: " + initialCount);
		Assert.assertTrue(initialCount > 0, "At least one CI should be available in the list");

		ticketDetailsPage.searchCI("ESXi");
		int filteredCount = ticketDetailsPage.getAvailableCIsCount();
		ReportManager.info("Available CIs count after filtering by 'ESXi': " + filteredCount);
		Assert.assertTrue(filteredCount >= 1, "Filtering by 'ESXi' should display matching CI card");

		ticketDetailsPage.clearSearchCI();
		int restoredCount = ticketDetailsPage.getAvailableCIsCount();
		ReportManager.info("Available CIs count after clearing search: " + restoredCount);
		Assert.assertTrue(restoredCount >= initialCount, "Clearing search should restore the full CI list");

		// Test CI Selection & Header Count update
		ReportManager.info("Testing CI selection toggle...");
		ticketDetailsPage.toggleCICheckbox(0);
		String headerSelected = ticketDetailsPage.getSelectCIsHeaderTitle();
		ReportManager.info("Header after selection: " + headerSelected);
		Assert.assertTrue(headerSelected.contains("Select CIs (1)"),
				"Modal header should update to 'Select CIs (1)' after selecting a CI");
		Assert.assertTrue(ticketDetailsPage.isSelectButtonEnabled(),
				"'Select' button should be enabled when 1 or more CIs are selected");

		// Uncheck CI
		ticketDetailsPage.toggleCICheckbox(0);
		String headerUnselected = ticketDetailsPage.getSelectCIsHeaderTitle();
		ReportManager.info("Header after unselecting: " + headerUnselected);
		Assert.assertTrue(headerUnselected.contains("Select CIs (0)"),
				"Modal header should return to 'Select CIs (0)' after unselecting");
		Assert.assertFalse(ticketDetailsPage.isSelectButtonEnabled(),
				"'Select' button should become disabled again when 0 CIs are selected");

		// Cancel modal and verify return to CIs tab
		ticketDetailsPage.clickCancelCIModal();
		Assert.assertTrue(ticketDetailsPage.isAddCIButtonDisplayed(),
				"Should return cleanly to CIs tab after clicking Cancel");
		ReportManager.pass("Tab 4: CIs and 'Add CI' modal interactions verified successfully");

		// 7. Verify Tab 5: Documents
		ReportManager.info("Verifying Tab 5: Documents");
		boolean docsSelected = ticketDetailsPage.selectTab("Documents");
		Assert.assertTrue(docsSelected, "Documents tab should be selectable");
		Assert.assertTrue(ticketDetailsPage.verifyDocumentsTab(),
				"Documents tab content should be displayed");
		ReportManager.pass("Tab 5: Documents verified successfully");

		// 8. Verify Tab 6: Attachments
		ReportManager.info("Verifying Tab 6: Attachments");
		boolean attachSelected = ticketDetailsPage.selectTab("Attachments");
		Assert.assertTrue(attachSelected, "Attachments tab should be selectable");
		Assert.assertTrue(ticketDetailsPage.verifyAttachmentsTab(),
				"Attachments tab content should be displayed");
		ReportManager.pass("Tab 6: Attachments verified successfully");

		// 9. Verify Tab 7: Work Order
		ReportManager.info("Verifying Tab 7: Work Order");
		boolean woSelected = ticketDetailsPage.selectTab("Work Order");
		Assert.assertTrue(woSelected, "Work Order tab should be selectable");
		Assert.assertTrue(ticketDetailsPage.verifyWorkOrderTab(),
				"Work Order tab content should be displayed");
		ReportManager.pass("Tab 7: Work Order verified successfully");

		// 10. Navigate back to Tickets screen
		ticketDetailsPage.navigateBack();
		Assert.assertTrue(ticketsPage.isOngoingTabDisplayed(),
				"Should return safely to Tickets screen after verifying all tabs");
		ReportManager.pass("All 7 Ticket Details tabs verified and returned to Tickets screen successfully!");
	}
}
