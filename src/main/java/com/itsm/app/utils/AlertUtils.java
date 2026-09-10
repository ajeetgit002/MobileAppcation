package com.itsm.app.utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * AlertUtils handles native WebDriver alerts.
 * Instance-based - receives driver through constructor dependency injection.
 * Note: Does NOT handle Android dialogs - only native WebDriver alerts.
 */
public class AlertUtils {

    private final AppiumDriver driver;
    private final int defaultTimeoutInSeconds;

    /**
     * Initialize AlertUtils with driver and default timeout.
     *
     * @param driver AppiumDriver instance
     * @param defaultTimeoutInSeconds default timeout for alert wait
     */
    public AlertUtils(
            AppiumDriver driver,
            int defaultTimeoutInSeconds) {

        if (driver == null) {
            throw new IllegalArgumentException(
                    "AppiumDriver cannot be null"
            );
        }

        this.driver = driver;
        this.defaultTimeoutInSeconds = defaultTimeoutInSeconds;
    }

    /**
     * Initialize AlertUtils with driver and default timeout of 10 seconds.
     *
     * @param driver AppiumDriver instance
     */
    public AlertUtils(AppiumDriver driver) {
        this(driver, 10);
    }

    /**
     * Wait until native alert is present.
     */
    public boolean waitForAlert() {

        try {

            new WebDriverWait(
                    driver,
                    Duration.ofSeconds(defaultTimeoutInSeconds)
            ).until(
                    ExpectedConditions.alertIsPresent()
            );

            ReportManager.info("Native alert is displayed");

            return true;

        } catch (Exception e) {

            ReportManager.fail(
                    "Native alert was not displayed within "
                            + defaultTimeoutInSeconds + " seconds"
            );

            return false;
        }
    }

    /**
     * Check whether native alert is present.
     */
    public boolean isAlertPresent() {

        try {

            driver.switchTo().alert();

            ReportManager.info("Native alert is present");

            return true;

        } catch (NoAlertPresentException e) {

            ReportManager.info("Native alert is not present");

            return false;
        }
    }

    /**
     * Get native alert text.
     */
    public String getText() {

        try {

            String text = driver
                    .switchTo()
                    .alert()
                    .getText();

            ReportManager.info(
                    "Alert text: " + text
            );

            return text;

        } catch (NoAlertPresentException e) {

            ReportManager.fail(
                    "Unable to get alert text: No alert present"
            );

            throw e;
        }
    }

    /**
     * Accept native alert.
     */
    public void accept() {

        try {

            driver.switchTo()
                    .alert()
                    .accept();

            ReportManager.pass(
                    "Native alert accepted successfully"
            );

        } catch (NoAlertPresentException e) {

            ReportManager.fail(
                    "Unable to accept alert: No alert present"
            );

            throw e;
        }
    }

    /**
     * Dismiss native alert.
     */
    public void dismiss() {

        try {

            driver.switchTo()
                    .alert()
                    .dismiss();

            ReportManager.pass(
                    "Native alert dismissed successfully"
            );

        } catch (NoAlertPresentException e) {

            ReportManager.fail(
                    "Unable to dismiss alert: No alert present"
            );

            throw e;
        }
    }

    /**
     * Accept alert after waiting for it.
     */
    public void waitAndAccept() {

        if (waitForAlert()) {
            accept();
        }
    }

    /**
     * Dismiss alert after waiting for it.
     */
    public void waitAndDismiss() {

        if (waitForAlert()) {
            dismiss();
        }
    }
}