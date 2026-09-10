package com.itsm.app.listeners;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.itsm.app.utils.ReportManager;
import com.itsm.app.utils.ScreenshotUtils;

/**
 * TestListener connects TestNG test lifecycle events to ExtentReports and
 * handles automatic failure screenshot capture.
 */
public class TestListener implements ITestListener, ISuiteListener {

	@Override
	public void onStart(ISuite suite) {
		ReportManager.initReport();
	}

	@Override
	public void onFinish(ISuite suite) {
		ReportManager.flush();
	}

	@Override
	public void onTestStart(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		String description = result.getMethod().getDescription();

		ReportManager.createTest(testName);
		if (description != null && !description.isBlank()) {
			ReportManager.info("Description: " + description);
		}
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		ReportManager.pass("Test Passed: " + result.getMethod().getMethodName());
		ReportManager.removeTest();
	}

	@Override
	public void onTestFailure(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		Throwable throwable = result.getThrowable();

		ReportManager.fail("Test Failed: " + testName);
		if (throwable != null) {
			ReportManager.fail(throwable.getMessage());
		}

		try {
			String screenshotPath = ScreenshotUtils.captureFromCurrentDriver("FAIL_" + testName);
			if (screenshotPath != null) {
				ReportManager.attachScreenshot(screenshotPath);
			}
		} catch (Exception e) {
			ReportManager.warning("Failed to attach failure screenshot: " + e.getMessage());
		} finally {
			ReportManager.removeTest();
		}
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		try {
			ReportManager.createTest(testName);
		} catch (Exception ignored) {
			// Test node might already exist
		}
		ReportManager.skip("Test Skipped: " + (result.getThrowable() != null ? result.getThrowable().getMessage() : ""));
		ReportManager.removeTest();
	}
}
