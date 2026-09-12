package com.itsm.app.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.itsm.app.base.BaseTest;
import com.itsm.app.pages.CreateTicketPage;
import com.itsm.app.pages.DashboardPage;
import com.itsm.app.utils.ReportManager;

/**
 * Test class automating end-to-end ticket creation across services
 * and sub-services in the ITSM mobile application.
 */
public class TicketCreationTest extends BaseTest {

	private CreateTicketPage createTicketPage;
	private DashboardPage dashboardPage;

	@Override
	protected boolean isClassLevelSession() {
		return true; // Reuses authenticated driver session across all ticket creation tests
	}

	@BeforeClass
	public void setupPages() {
		createTicketPage = context.getCreateTicketPage();
		dashboardPage = context.getDashboardPage();
		dashboardPage.dismissOverlaysIfPresent();
	}

	// =========================================
	// TEST 1: Global Request Family -> Arrival Employees -> Access Provisioning
	// =========================================

	@Test(priority = 1, description = "Verify ticket creation for Arrival Employees service with Access Provisioning sub-service")
	public void testCreateTicketWithArrivalEmployeesAndAccessProvisioning() {
		ReportManager.info("Starting test: Create Ticket with Arrival Employees & Access Provisioning sub-service");

		createTicketPage.openCreateTicketFlow();
		Assert.assertTrue(createTicketPage.isChooseServiceFamilyDisplayed(),
				"'Choose a Service Family' screen should be displayed");

		createTicketPage.selectServiceFamily("Global Request Family");
		createTicketPage.selectService("Arrival Employees");
		createTicketPage.selectSubServiceIfPresent("Access Provisioning");

		Assert.assertTrue(createTicketPage.isRaiseNewTicketFormDisplayed(),
				"'Raise a New Ticket' form should be displayed");

		String description = "Automated Test Ticket - Arrival Employees Access Provisioning";
		createTicketPage.enterDescription(description);
		createTicketPage.clickSubmit();

		Assert.assertTrue(createTicketPage.isTicketCreatedSuccessfully(),
				"Ticket creation success confirmation modal should be displayed");

		String ticketId = createTicketPage.getCreatedTicketId();
		ReportManager.pass("Created Ticket ID: " + ticketId);
		Assert.assertFalse(ticketId.isEmpty(), "Generated Ticket ID should not be empty");

		createTicketPage.clickOkay();

		Assert.assertTrue(createTicketPage.isTicketPresentInList(ticketId),
				"Newly created ticket #" + ticketId + " should be visible in the Tickets list");
	}

	// =========================================
	// TEST 2: DevOps Services -> Organization -> Add New User
	// =========================================

	@Test(priority = 2, description = "Verify ticket creation for DevOps Services with Organization service and Add New User sub-service")
	public void testCreateTicketWithDevOpsOrganizationAndNewUser() {
		ReportManager.info("Starting test: Create Ticket with DevOps Services -> Organization -> Add New User");

		createTicketPage.openCreateTicketFlow();
		Assert.assertTrue(createTicketPage.isChooseServiceFamilyDisplayed(),
				"'Choose a Service Family' screen should be displayed");

		createTicketPage.selectServiceFamily("DevOps Services");
		createTicketPage.selectService("Organization");
		createTicketPage.selectSubServiceIfPresent("Add New User");

		Assert.assertTrue(createTicketPage.isRaiseNewTicketFormDisplayed(),
				"'Raise a New Ticket' form should be displayed");

		String description = "Automated Test Ticket - DevOps Organization Add New User";
		createTicketPage.enterDescription(description);
		createTicketPage.clickSubmit();

		Assert.assertTrue(createTicketPage.isTicketCreatedSuccessfully(),
				"Ticket creation success confirmation modal should be displayed");

		String ticketId = createTicketPage.getCreatedTicketId();
		ReportManager.pass("Created Ticket ID: " + ticketId);
		Assert.assertFalse(ticketId.isEmpty(), "Generated Ticket ID should not be empty");

		createTicketPage.clickOkay();

		Assert.assertTrue(createTicketPage.isTicketPresentInList(ticketId),
				"Newly created ticket #" + ticketId + " should be visible in the Tickets list");
	}
}
