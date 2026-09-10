package com.itsm.app.utils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public final class ReportManager {

    private static ExtentReports extentReports;

    private static final ThreadLocal<ExtentTest> extentTest =
            new ThreadLocal<>();

    private ReportManager() {
    }


    // ==========================================
    // INITIALIZE REPORT
    // ==========================================

    public static void initReport() {

        if (extentReports != null) {
            return;
        }

        try {

            String timestamp =
                    LocalDateTime.now()
                            .format(
                                    DateTimeFormatter.ofPattern(
                                            "yyyyMMdd_HHmmss"
                                    )
                            );

            String reportDirectory =
                    System.getProperty("user.dir")
                            + File.separator
                            + "reports";

            File directory =
                    new File(reportDirectory);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            String reportPath =
                    reportDirectory
                            + File.separator
                            + "AppiumReport_"
                            + timestamp
                            + ".html";

            ExtentSparkReporter sparkReporter =
                    new ExtentSparkReporter(
                            reportPath
                    );

            sparkReporter.config()
                    .setDocumentTitle(
                            "Appium Automation Report"
                    );

            sparkReporter.config()
                    .setReportName(
                            "Mobile Automation Test Report"
                    );

            extentReports =
                    new ExtentReports();

            extentReports.attachReporter(
                    sparkReporter
            );

            // System information

            extentReports.setSystemInfo(
                    "OS",
                    System.getProperty("os.name")
            );

            extentReports.setSystemInfo(
                    "OS Version",
                    System.getProperty("os.version")
            );

            extentReports.setSystemInfo(
                    "Java Version",
                    System.getProperty("java.version")
            );

            extentReports.setSystemInfo(
                    "Environment",
                    "QA"
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to initialize Extent Report",
                    e
            );
        }
    }


    // ==========================================
    // CREATE TEST
    // ==========================================

    public static void createTest(
            String testName) {

        if (extentReports == null) {
            throw new IllegalStateException(
                    "Report is not initialized. "
                            + "Call initReport() first."
            );
        }

        ExtentTest test =
                extentReports.createTest(
                        testName
                );

        extentTest.set(test);
    }


    // ==========================================
    // GET CURRENT TEST
    // ==========================================

    public static boolean isTestInitialized() {
        return extentTest.get() != null;
    }

    public static ExtentTest getTest() {
        return extentTest.get();
    }


    // ==========================================
    // INFO
    // ==========================================

    public static void info(String message) {
        if (isTestInitialized()) {
            getTest().log(Status.INFO, message);
        } else {
            System.out.println("[INFO] " + message);
        }
    }


    // ==========================================
    // PASS
    // ==========================================

    public static void pass(String message) {
        if (isTestInitialized()) {
            getTest().log(Status.PASS, MarkupHelper.createLabel(message, ExtentColor.GREEN));
        } else {
            System.out.println("[PASS] " + message);
        }
    }


    // ==========================================
    // FAIL
    // ==========================================

    public static void fail(String message) {
        if (isTestInitialized()) {
            getTest().log(Status.FAIL, MarkupHelper.createLabel(message, ExtentColor.RED));
        } else {
            System.err.println("[FAIL] " + message);
        }
    }


    // ==========================================
    // WARNING
    // ==========================================

    public static void warning(String message) {
        if (isTestInitialized()) {
            getTest().log(Status.WARNING, message);
        } else {
            System.out.println("[WARN] " + message);
        }
    }


    // ==========================================
    // SKIP
    // ==========================================

    public static void skip(String message) {
        if (isTestInitialized()) {
            getTest().log(Status.SKIP, message);
        } else {
            System.out.println("[SKIP] " + message);
        }
    }


    // ==========================================
    // DEBUG
    // ==========================================

    public static void debug(String message) {
        if (isTestInitialized()) {
            getTest().log(Status.INFO, "[DEBUG] " + message);
        } else {
            System.out.println("[DEBUG] " + message);
        }
    }


    // ==========================================
    // ASSIGN CATEGORY
    // ==========================================

    public static void addCategory(
            String category) {

        getTest().assignCategory(
                category
        );
    }


    // ==========================================
    // ASSIGN AUTHOR
    // ==========================================

    public static void assignAuthor(
            String author) {

        getTest().assignAuthor(
                author
        );
    }


    // ==========================================
    // ATTACH SCREENSHOT
    // ==========================================

    public static void attachScreenshot(String screenshotPath) {
        if (!isTestInitialized() || screenshotPath == null) {
            return;
        }
        try {
            getTest().addScreenCaptureFromPath(screenshotPath);
        } catch (Exception e) {
            warning("Unable to attach screenshot: " + e.getMessage());
        }
    }


    // ==========================================
    // LOG SCREENSHOT WITH MESSAGE
    // ==========================================

    public static void failWithScreenshot(String message, String screenshotPath) {
        fail(message);
        attachScreenshot(screenshotPath);
    }


    // ==========================================
    // END CURRENT TEST
    // ==========================================

    public static void removeTest() {
        extentTest.remove();
    }


    // ==========================================
    // FLUSH REPORT
    // ==========================================

    public static void flush() {

        if (extentReports != null) {

            extentReports.flush();
        }
    }
}