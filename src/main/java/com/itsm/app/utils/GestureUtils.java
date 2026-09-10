package com.itsm.app.utils;

import io.appium.java_client.AppiumDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.Collections;

public class GestureUtils {

    private final AppiumDriver driver;
    private final WaitUtils waitUtils;
    private final ScreenshotUtils screenshotUtils;

    public GestureUtils(
            AppiumDriver driver,
            WaitUtils waitUtils,
            ScreenshotUtils screenshotUtils) {

        if (driver == null) {
            throw new IllegalArgumentException(
                    "AppiumDriver cannot be null"
            );
        }

        if (waitUtils == null) {
            throw new IllegalArgumentException(
                    "WaitUtils cannot be null"
            );
        }

        if (screenshotUtils == null) {
            throw new IllegalArgumentException(
                    "ScreenshotUtils cannot be null"
            );
        }

        this.driver = driver;
        this.waitUtils = waitUtils;
        this.screenshotUtils = screenshotUtils;
    }

    // ==========================================
    // TAP
    // ==========================================

    public void tap(By locator) {

        try {

            WebElement target =
                    waitUtils.waitForVisible(locator);

            PointerInput finger =
                    new PointerInput(
                            PointerInput.Kind.TOUCH,
                            "finger"
                    );

            Sequence tap =
                    new Sequence(finger, 0);

            tap.addAction(
                    finger.createPointerMove(
                            Duration.ZERO,
                            PointerInput.Origin.fromElement(target),
                            0,
                            0
                    )
            );

            tap.addAction(
                    finger.createPointerDown(
                            PointerInput.MouseButton.LEFT.asArg()
                    )
            );

            tap.addAction(
                    finger.createPointerUp(
                            PointerInput.MouseButton.LEFT.asArg()
                    )
            );

            driver.perform(
                    Collections.singletonList(tap)
            );

            ReportManager.pass(
                    "Tap performed successfully: "
                            + locator
            );

        } catch (Exception e) {

            handleFailure(
                    "Unable to tap element: " + locator,
                    e
            );

            throw e;
        }
    }

    // ==========================================
    // DOUBLE TAP
    // ==========================================

    public void doubleTap(By locator) {

        try {

            tap(locator);

            Thread.sleep(100);

            tap(locator);

            ReportManager.pass(
                    "Double tap performed successfully: "
                            + locator
            );

        } catch (Exception e) {

            handleFailure(
                    "Unable to double tap element: "
                            + locator,
                    e
            );

            throw new RuntimeException(e);
        }
    }

    // ==========================================
    // LONG PRESS
    // ==========================================

    public void longPress(By locator) {

        try {

            WebElement target =
                    waitUtils.waitForVisible(locator);

            PointerInput finger =
                    new PointerInput(
                            PointerInput.Kind.TOUCH,
                            "finger"
                    );

            Sequence longPress =
                    new Sequence(finger, 0);

            longPress.addAction(
                    finger.createPointerMove(
                            Duration.ZERO,
                            PointerInput.Origin.fromElement(target),
                            0,
                            0
                    )
            );

            longPress.addAction(
                    finger.createPointerDown(
                            PointerInput.MouseButton.LEFT.asArg()
                    )
            );

            longPress.addAction(
                    new Pause(
                            finger,
                            Duration.ofSeconds(2)
                    )
            );

            longPress.addAction(
                    finger.createPointerUp(
                            PointerInput.MouseButton.LEFT.asArg()
                    )
            );

            driver.perform(
                    Collections.singletonList(longPress)
            );

            ReportManager.pass(
                    "Long press performed successfully: "
                            + locator
            );

        } catch (Exception e) {

            handleFailure(
                    "Unable to long press element: "
                            + locator,
                    e
            );

            throw e;
        }
    }

    // ==========================================
    // SWIPE BY COORDINATES
    // ==========================================

    public void swipe(
            int startX,
            int startY,
            int endX,
            int endY,
            int durationMillis) {

        try {

            if (durationMillis <= 0) {

                throw new IllegalArgumentException(
                        "Swipe duration must be greater than 0"
                );
            }

            PointerInput finger =
                    new PointerInput(
                            PointerInput.Kind.TOUCH,
                            "finger"
                    );

            Sequence swipe =
                    new Sequence(finger, 0);

            swipe.addAction(
                    finger.createPointerMove(
                            Duration.ZERO,
                            PointerInput.Origin.viewport(),
                            startX,
                            startY
                    )
            );

            swipe.addAction(
                    finger.createPointerDown(
                            PointerInput.MouseButton.LEFT.asArg()
                    )
            );

            swipe.addAction(
                    finger.createPointerMove(
                            Duration.ofMillis(durationMillis),
                            PointerInput.Origin.viewport(),
                            endX,
                            endY
                    )
            );

            swipe.addAction(
                    finger.createPointerUp(
                            PointerInput.MouseButton.LEFT.asArg()
                    )
            );

            driver.perform(
                    Collections.singletonList(swipe)
            );

            ReportManager.pass(
                    "Swipe performed successfully"
            );

        } catch (Exception e) {

            handleFailure(
                    "Swipe action failed",
                    e
            );

            throw e;
        }
    }

    // ==========================================
    // SWIPE UP
    // ==========================================

    public void swipeUp() {

        int width =
                driver.manage()
                        .window()
                        .getSize()
                        .getWidth();

        int height =
                driver.manage()
                        .window()
                        .getSize()
                        .getHeight();

        int x = width / 2;

        int startY =
                (int) (height * 0.80);

        int endY =
                (int) (height * 0.20);

        swipe(
                x,
                startY,
                x,
                endY,
                700
        );

        ReportManager.info(
                "Swipe up performed"
        );
    }

    // ==========================================
    // SWIPE DOWN
    // ==========================================

    public void swipeDown() {

        int width =
                driver.manage()
                        .window()
                        .getSize()
                        .getWidth();

        int height =
                driver.manage()
                        .window()
                        .getSize()
                        .getHeight();

        int x = width / 2;

        int startY =
                (int) (height * 0.20);

        int endY =
                (int) (height * 0.80);

        swipe(
                x,
                startY,
                x,
                endY,
                700
        );

        ReportManager.info(
                "Swipe down performed"
        );
    }

    // ==========================================
    // SWIPE LEFT
    // ==========================================

    public void swipeLeft() {

        int width =
                driver.manage()
                        .window()
                        .getSize()
                        .getWidth();

        int height =
                driver.manage()
                        .window()
                        .getSize()
                        .getHeight();

        int y = height / 2;

        int startX =
                (int) (width * 0.80);

        int endX =
                (int) (width * 0.20);

        swipe(
                startX,
                y,
                endX,
                y,
                700
        );

        ReportManager.info(
                "Swipe left performed"
        );
    }

    // ==========================================
    // SWIPE RIGHT
    // ==========================================

    public void swipeRight() {

        int width =
                driver.manage()
                        .window()
                        .getSize()
                        .getWidth();

        int height =
                driver.manage()
                        .window()
                        .getSize()
                        .getHeight();

        int y = height / 2;

        int startX =
                (int) (width * 0.20);

        int endX =
                (int) (width * 0.80);

        swipe(
                startX,
                y,
                endX,
                y,
                700
        );

        ReportManager.info(
                "Swipe right performed"
        );
    }

    // ==========================================
    // DRAG AND DROP
    // ==========================================

    public void dragAndDrop(
            By source,
            By destination) {

        try {

            WebElement sourceElement =
                    waitUtils.waitForVisible(source);

            WebElement destinationElement =
                    waitUtils.waitForVisible(destination);

            PointerInput finger =
                    new PointerInput(
                            PointerInput.Kind.TOUCH,
                            "finger"
                    );

            Sequence drag =
                    new Sequence(finger, 0);

            drag.addAction(
                    finger.createPointerMove(
                            Duration.ZERO,
                            PointerInput.Origin.fromElement(
                                    sourceElement
                            ),
                            0,
                            0
                    )
            );

            drag.addAction(
                    finger.createPointerDown(
                            PointerInput.MouseButton.LEFT.asArg()
                    )
            );

            drag.addAction(
                    new Pause(
                            finger,
                            Duration.ofMillis(300)
                    )
            );

            drag.addAction(
                    finger.createPointerMove(
                            Duration.ofMillis(1000),
                            PointerInput.Origin.fromElement(
                                    destinationElement
                            ),
                            0,
                            0
                    )
            );

            drag.addAction(
                    finger.createPointerUp(
                            PointerInput.MouseButton.LEFT.asArg()
                    )
            );

            driver.perform(
                    Collections.singletonList(drag)
            );

            ReportManager.pass(
                    "Drag and drop performed successfully"
            );

        } catch (Exception e) {

            handleFailure(
                    "Drag and drop failed",
                    e
            );

            throw e;
        }
    }

    // ==========================================
    // FAILURE HANDLER
    // ==========================================

    private void handleFailure(
            String message,
            Exception exception) {

        ReportManager.fail(
                message + ": "
                        + exception.getMessage()
        );

        try {

            String screenshot =
                    screenshotUtils.capture(
                            "GestureFailure"
                    );

            ReportManager.attachScreenshot(
                    screenshot
            );

        } catch (Exception screenshotException) {

            ReportManager.warning(
                    "Unable to capture screenshot: "
                            + screenshotException.getMessage()
            );
        }
    }
}