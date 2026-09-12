package com.itsm.app.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.itsm.app.base.BaseTest;
import com.itsm.app.pages.CatalogPage;
import com.itsm.app.pages.DashboardPage;
import com.itsm.app.utils.ReportManager;

/**
 * End-to-end automation test suite for the Catalog module.
 * Verifies the Catalog screen and all 6 catalog cards:
 * 1. Announcements
 * 2. Approvals
 * 3. Documents
 * 4. Asset Management
 * 5. Close Tickets
 * 6. FAQs
 */
public class CatalogTest extends BaseTest {

	private CatalogPage catalogPage;
	private DashboardPage dashboardPage;

	@Override
	protected boolean isClassLevelSession() {
		return true; // Reuses authenticated driver session across all catalog tests
	}

	@BeforeClass
	public void setupPages() {
		catalogPage = context.getCatalogPage();
		dashboardPage = context.getDashboardPage();
		dashboardPage.dismissOverlaysIfPresent();
	}

	@BeforeMethod
	public void ensureCatalogScreen() {
		catalogPage.openCatalogTab();
	}

	// =========================================================================
	// TEST 1: Verify Catalog Screen and All 6 Cards are Displayed
	// =========================================================================

	@Test(priority = 1, description = "Verify Catalog screen displays all 6 cards properly")
	public void testCatalogScreenAndAllCardsDisplayed() {
		ReportManager.info("=== TEST 1: Verify Catalog Screen and All 6 Cards ===");

		Assert.assertTrue(catalogPage.isCatalogDisplayed(), "Catalog header should be displayed");

		Assert.assertTrue(catalogPage.isAnnouncementsCardDisplayed(),
				"Announcements card should be visible on Catalog");
		ReportManager.pass("Announcements card is displayed");

		Assert.assertTrue(catalogPage.isApprovalsCardDisplayed(),
				"Approvals card should be visible on Catalog");
		ReportManager.pass("Approvals card is displayed");

		Assert.assertTrue(catalogPage.isDocumentsCardDisplayed(),
				"Documents card should be visible on Catalog");
		ReportManager.pass("Documents card is displayed");

		Assert.assertTrue(catalogPage.isAssetManagementCardDisplayed(),
				"Asset Management card should be visible on Catalog");
		ReportManager.pass("Asset Management card is displayed");

		Assert.assertTrue(catalogPage.isCloseTicketsCardDisplayed(),
				"Close Tickets card should be visible on Catalog");
		ReportManager.pass("Close Tickets card is displayed");

		Assert.assertTrue(catalogPage.isFAQsCardDisplayed(),
				"FAQs card should be visible on Catalog");
		ReportManager.pass("FAQs card is displayed");

		ReportManager.pass("Catalog screen and all 6 cards verified successfully");
	}

	// =========================================================================
	// TEST 2: Verify Announcements Card and Sub-Screen
	// =========================================================================

	@Test(priority = 2, description = "Verify Announcements card opens list and returns to Catalog")
	public void testAnnouncementsSection() {
		ReportManager.info("=== TEST 2: Verify Announcements Card & Sub-Screen ===");

		catalogPage.clickAnnouncements();
		Assert.assertTrue(catalogPage.isAnnouncementsScreenDisplayed(),
				"Announcements sub-screen header should be displayed");

		int count = catalogPage.getAnnouncementsCount();
		ReportManager.info("Found announcements count: " + count);
		Assert.assertTrue(count > 0, "Announcements list should have at least 1 announcement");

		catalogPage.navigateBackToCatalog();
		Assert.assertTrue(catalogPage.isCatalogDisplayed(),
				"Should return to Catalog screen after clicking back");
		ReportManager.pass("Announcements section verified successfully");
	}

	// =========================================================================
	// TEST 3: Verify Approvals Card and Sub-Screen
	// =========================================================================

	@Test(priority = 3, description = "Verify Approvals card opens screen and returns to Catalog")
	public void testApprovalsSection() {
		ReportManager.info("=== TEST 3: Verify Approvals Card & Screen ===");

		catalogPage.clickApprovals();
		Assert.assertTrue(catalogPage.isApprovalsScreenDisplayed(),
				"Approvals screen header should be displayed");

		catalogPage.returnToCatalogFromApprovals();
		Assert.assertTrue(catalogPage.isCatalogDisplayed(),
				"Should return to Catalog screen after returning from Approvals");
		ReportManager.pass("Approvals section verified successfully");
	}

	// =========================================================================
	// TEST 4: Verify Documents Card, Search, and Return
	// =========================================================================

	@Test(priority = 4, description = "Verify Documents card opens screen, searches, and returns to Catalog")
	public void testDocumentsSection() {
		ReportManager.info("=== TEST 4: Verify Documents Card & Search ===");

		catalogPage.clickDocuments();
		Assert.assertTrue(catalogPage.isDocumentsScreenDisplayed(),
				"Documents screen header should be displayed");

		int initialCount = catalogPage.getDocumentsCount();
		ReportManager.info("Found documents count: " + initialCount);
		Assert.assertTrue(initialCount > 0, "Documents list should contain documents");

		catalogPage.searchDocument("Dhruv");
		int filteredCount = catalogPage.getDocumentsCount();
		ReportManager.info("Documents count after searching 'Dhruv': " + filteredCount);
		Assert.assertTrue(filteredCount >= 1, "Should show filtered document matching 'Dhruv'");

		catalogPage.clearDocumentSearch();
		int restoredCount = catalogPage.getDocumentsCount();
		ReportManager.info("Documents count after clearing search: " + restoredCount);
		Assert.assertTrue(restoredCount >= initialCount, "Clearing search should restore document list");

		catalogPage.navigateBackToCatalog();
		Assert.assertTrue(catalogPage.isCatalogDisplayed(),
				"Should return to Catalog screen after clicking back");
		ReportManager.pass("Documents section verified successfully");
	}

	// =========================================================================
	// TEST 5: Verify Asset Management Card, Search, and Return
	// =========================================================================

	@Test(priority = 5, description = "Verify Asset Management card opens screen, searches, and returns to Catalog")
	public void testAssetManagementSection() {
		ReportManager.info("=== TEST 5: Verify Asset Management Card & Search ===");

		catalogPage.clickAssetManagement();
		Assert.assertTrue(catalogPage.isAssetManagementScreenDisplayed(),
				"Asset Management screen header should be displayed");

		int initialCount = catalogPage.getAssetsCount();
		ReportManager.info("Found assets count: " + initialCount);
		Assert.assertTrue(initialCount > 0, "Asset list should contain assets");

		catalogPage.searchAsset("Dell");
		int filteredCount = catalogPage.getAssetsCount();
		ReportManager.info("Assets count after searching 'Dell': " + filteredCount);
		Assert.assertTrue(filteredCount >= 1, "Should show asset matching 'Dell'");

		catalogPage.clearAssetSearch();
		int restoredCount = catalogPage.getAssetsCount();
		ReportManager.info("Assets count after clearing search: " + restoredCount);
		Assert.assertTrue(restoredCount >= initialCount, "Clearing search should restore asset list");

		catalogPage.navigateBackToCatalog();
		Assert.assertTrue(catalogPage.isCatalogDisplayed(),
				"Should return to Catalog screen after clicking back");
		ReportManager.pass("Asset Management section verified successfully");
	}

	// =========================================================================
	// TEST 6: Verify Close Tickets Card, Search, and Return
	// =========================================================================

	@Test(priority = 6, description = "Verify Close Tickets card opens screen, searches, and returns to Catalog")
	public void testCloseTicketsSection() {
		ReportManager.info("=== TEST 6: Verify Close Tickets Card & Search ===");

		catalogPage.clickCloseTickets();
		Assert.assertTrue(catalogPage.isCloseTicketsScreenDisplayed(),
				"Closed Tickets screen header should be displayed");

		int initialCount = catalogPage.getCloseTicketsCount();
		ReportManager.info("Found closed tickets count: " + initialCount);
		Assert.assertTrue(initialCount > 0, "Closed Tickets list should contain tickets");

		catalogPage.searchCloseTicket("Docker");
		int filteredCount = catalogPage.getCloseTicketsCount();
		ReportManager.info("Closed tickets count after searching 'Docker': " + filteredCount);
		Assert.assertTrue(filteredCount >= 1, "Should show closed ticket matching 'Docker'");

		catalogPage.clearCloseTicketSearch();
		int restoredCount = catalogPage.getCloseTicketsCount();
		ReportManager.info("Closed tickets count after clearing search: " + restoredCount);
		Assert.assertTrue(restoredCount >= initialCount, "Clearing search should restore closed ticket list");

		catalogPage.navigateBackToCatalog();
		Assert.assertTrue(catalogPage.isCatalogDisplayed(),
				"Should return to Catalog screen after clicking back");
		ReportManager.pass("Close Tickets section verified successfully");
	}

	// =========================================================================
	// TEST 7: Verify FAQs Card, Search, and Return
	// =========================================================================

	@Test(priority = 7, description = "Verify FAQs card opens screen, searches, and returns to Catalog")
	public void testFAQsSection() {
		ReportManager.info("=== TEST 7: Verify FAQs Card & Search ===");

		catalogPage.clickFAQs();
		Assert.assertTrue(catalogPage.isFAQsScreenDisplayed(),
				"FAQs screen header should be displayed");

		int initialCount = catalogPage.getFAQsCount();
		ReportManager.info("Found FAQs count: " + initialCount);
		Assert.assertTrue(initialCount > 0, "FAQs list should contain questions");

		catalogPage.searchFAQ("incident");
		int filteredCount = catalogPage.getFAQsCount();
		ReportManager.info("FAQs count after searching 'incident': " + filteredCount);
		Assert.assertTrue(filteredCount >= 1, "Should show FAQ matching 'incident'");

		catalogPage.clearFAQSearch();
		int restoredCount = catalogPage.getFAQsCount();
		ReportManager.info("FAQs count after clearing search: " + restoredCount);
		Assert.assertTrue(restoredCount >= initialCount, "Clearing search should restore FAQ list");

		catalogPage.navigateBackToCatalog();
		Assert.assertTrue(catalogPage.isCatalogDisplayed(),
				"Should return to Catalog screen after clicking back");
		ReportManager.pass("FAQs section verified successfully");
	}
}
