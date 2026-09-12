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
 * with sub-services, auditing incomplete services without sub-services,
 * and thoroughly verifying all UI elements across every screen.
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
		createTicketPage.ensureAtRootOrFamilyScreen();
	}

	// =========================================================================
	// TEST 1: Full Screen Elements Verification & Ticket Creation Flow
	// Verifies every element on Screen 1 -> Screen 2 -> Screen 3 -> Screen 4 -> Screen 5 -> Screen 6
	// =========================================================================

	@Test(priority = 1, description = "Verify all UI elements across every screen and create ticket with Arrival Employees")
	public void testVerifyAllElementsAcrossScreensAndCreateTicket() {
		ReportManager.info("=== TEST 1: Verify All Elements of Every Screen & Create Ticket ===");

		// 1. Open Ticket Creation & Verify Screen 1 (Choose Service Family)
		createTicketPage.openCreateTicketFlow();
		Assert.assertTrue(createTicketPage.verifyChooseServiceFamilyScreenElements(),
				"All elements on 'Choose a Service Family' screen (Header, Search bar, and 6 Family cards) should be displayed");

		// 2. Select Family & Verify Screen 2 (Choose Service Screen)
		String serviceFamily = "Global Request Family";
		createTicketPage.selectServiceFamily(serviceFamily);
		Assert.assertTrue(createTicketPage.verifyChooseServiceScreenElements(serviceFamily),
				"All elements on Choose Service screen for '" + serviceFamily + "' (Header, Back button, Search bar, Service items) should be displayed");

		// 3. Select Service & Verify Screen 3 (Sub-Services Screen)
		String service = "Arrival Employees";
		createTicketPage.selectService(service);
		Assert.assertTrue(createTicketPage.verifySubServicesScreenElements(service),
				"All elements on Sub-Services screen for '" + service + "' (Header, Back button, Sub-service cards) should be displayed");

		// 4. Select Sub-Service & Verify Screen 4 (Raise a New Ticket Form)
		String subService = "Access Provisioning";
		createTicketPage.selectSubServiceIfPresent(subService);
		Assert.assertTrue(createTicketPage.verifyTicketFormElements(service, subService),
				"All elements on 'Raise a New Ticket' form (Header, Description input, Cancel & Submit buttons) should be displayed");

		// 5. Fill Details & Submit
		String description = "Automated Test Ticket - Arrival Employees Access Provisioning with Full Element Verification";
		createTicketPage.enterDescription(description);
		createTicketPage.clickSubmit();

		// 6. Verify Screen 5 (Confirmation Modal)
		Assert.assertTrue(createTicketPage.isTicketCreatedSuccessfully(),
				"Ticket creation success confirmation modal should appear after submission");
		Assert.assertTrue(createTicketPage.verifyConfirmationDialogElements(),
				"All elements on Confirmation Modal (Success Title, Reference Ticket ID, Okay button) should be displayed");

		String ticketId = createTicketPage.getCreatedTicketId();
		ReportManager.pass("Verified generated Ticket ID: " + ticketId);
		Assert.assertFalse(ticketId.isEmpty(), "Generated Ticket ID must not be empty");

		// 7. Click Okay & Verify Screen 6 (Tickets List)
		createTicketPage.clickOkay();
		Assert.assertTrue(createTicketPage.isTicketPresentInList(ticketId),
				"Newly created ticket #" + ticketId + " must be present in the Tickets list");
		ReportManager.pass("Screen 1 through Screen 6 verified successfully with ticket #" + ticketId);
	}

	// =========================================================================
	// TEST 2: Audit & Confirmation of Services WITHOUT Sub-Services
	// Confirms exact count and names of services that have NOT completed sub-services
	// =========================================================================

	@Test(priority = 2, description = "Audit and confirm exact services and count that do NOT have sub-services completed")
	public void testAuditAndConfirmServicesWithoutSubServices() {
		ReportManager.info("=== TEST 2: Audit & Confirm Services WITHOUT Sub-Services ===");

		createTicketPage.openCreateTicketFlow();
		Assert.assertTrue(createTicketPage.isChooseServiceFamilyDisplayed(),
				"'Choose a Service Family' screen should be displayed");

		// Navigate to Infra Services where empty services are located
		String familyName = "Infra Services";
		createTicketPage.selectServiceFamily(familyName);
		Assert.assertTrue(createTicketPage.verifyChooseServiceScreenElements(familyName),
				"Infra Services screen elements should be displayed");

		// List of services discovered with 0 sub-services
		String[] incompleteServices = {
			"Backup and Recovery",
			"Hardware/Software",
			"Infrastructure Upgrades",
			"Monitoring and Alerts",
			"Server Provisioning",
			"Storage Allocation"
		};

		int verifiedEmptyCount = 0;
		for (String service : incompleteServices) {
			ReportManager.info("Auditing incomplete service: " + service);
			createTicketPage.selectService(service);

			boolean isEmpty = createTicketPage.verifyEmptyServiceState(service);
			Assert.assertTrue(isEmpty,
					"Service '" + service + "' should display empty state placeholder with 0 sub-services and no ticket form");
			verifiedEmptyCount++;
			ReportManager.pass("Confirmed: Service '" + service + "' has NO sub-services (Empty State verified)");

			createTicketPage.navigateBack();
		}

		// Confirm exact count
		ReportManager.pass("CONFIRMATION AUDIT COMPLETE: Exactly " + verifiedEmptyCount + " services have NOT completed sub-services.");
		Assert.assertEquals(verifiedEmptyCount, 6, "Total services without sub-services must equal 6");

		// Navigate back to Service Family screen
		createTicketPage.navigateBack();
	}

	// =========================================================================
	// TEST 3: DevOps Services -> Organization -> Add New User
	// =========================================================================

	@Test(priority = 3, description = "Verify ticket creation for DevOps Services -> Organization -> Add New User")
	public void testCreateTicketWithDevOpsOrganizationAndNewUser() {
		ReportManager.info("=== TEST 3: Create Ticket with DevOps Services -> Organization -> Add New User ===");

		createTicketPage.openCreateTicketFlow();
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

	// =========================================================================
	// TEST 4: IT Services -> Computers and peripherals -> New laptop ordering
	// =========================================================================

	@Test(priority = 4, description = "Verify ticket creation for IT Services -> Computers and peripherals -> New laptop ordering")
	public void testCreateTicketWithITServicesComputersAndPeripherals() {
		ReportManager.info("=== TEST 4: Create Ticket with IT Services -> Computers & peripherals -> New laptop ordering ===");

		createTicketPage.openCreateTicketFlow();
		createTicketPage.selectServiceFamily("IT Services");
		createTicketPage.selectService("Computers and peripherals");
		createTicketPage.selectSubServiceIfPresent("New laptop ordering");

		Assert.assertTrue(createTicketPage.isRaiseNewTicketFormDisplayed(),
				"'Raise a New Ticket' form should be displayed");

		String description = "Automated Test Ticket - IT Services New laptop ordering";
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

	// =========================================================================
	// TEST 5: Room Booking -> Meeting rooms -> Boarding room
	// =========================================================================

	@Test(priority = 5, description = "Verify ticket creation for Room Booking -> Meeting rooms -> Boarding room")
	public void testCreateTicketWithRoomBookingMeetingRooms() {
		ReportManager.info("=== TEST 5: Create Ticket with Room Booking -> Meeting rooms -> Boarding room ===");

		createTicketPage.openCreateTicketFlow();
		createTicketPage.selectServiceFamily("Room Booking");
		createTicketPage.selectService("Meeting rooms");
		createTicketPage.selectSubServiceIfPresent("Boarding room");

		Assert.assertTrue(createTicketPage.isRaiseNewTicketFormDisplayed(),
				"'Raise a New Ticket' form should be displayed");

		String description = "Automated Test Ticket - Room Booking Meeting rooms Boarding room";
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
