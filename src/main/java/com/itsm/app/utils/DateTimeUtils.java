package com.itsm.app.utils;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateTimeUtils {

    private DateTimeUtils() {
    }

    // Common formats
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String DATE_TIME_FORMAT =
            "dd-MM-yyyy HH:mm:ss";

    /**
     * Get current date.
     */
    public static String getCurrentDate() {

        return LocalDate.now()
                .format(
                        DateTimeFormatter.ofPattern(DATE_FORMAT)
                );
    }

    /**
     * Get current time.
     */
    public static String getCurrentTime() {

        return LocalTime.now()
                .format(
                        DateTimeFormatter.ofPattern(TIME_FORMAT)
                );
    }

    /**
     * Get current date and time.
     */
    public static String getCurrentDateTime() {

        return LocalDateTime.now()
                .format(
                        DateTimeFormatter.ofPattern(
                                DATE_TIME_FORMAT
                        )
                );
    }

    /**
     * Get current date/time using custom format.
     */
    public static String getCurrentDateTime(String format) {

        return LocalDateTime.now()
                .format(
                        DateTimeFormatter.ofPattern(format)
                );
    }

    /**
     * Get current date using custom format.
     */
    public static String getCurrentDate(String format) {

        return LocalDate.now()
                .format(
                        DateTimeFormatter.ofPattern(format)
                );
    }

    /**
     * Get current time using custom format.
     */
    public static String getCurrentTime(String format) {

        return LocalTime.now()
                .format(
                        DateTimeFormatter.ofPattern(format)
                );
    }

    /**
     * Get timestamp useful for screenshots/reports.
     */
    public static String getTimestamp() {

        return LocalDateTime.now()
                .format(
                        DateTimeFormatter.ofPattern(
                                "yyyyMMdd_HHmmss"
                        )
                );
    }

    /**
     * Add days to current date.
     */
    public static String addDays(
            int days,
            String format) {

        return LocalDate.now()
                .plusDays(days)
                .format(
                        DateTimeFormatter.ofPattern(format)
                );
    }

    /**
     * Subtract days from current date.
     */
    public static String subtractDays(
            int days,
            String format) {

        return LocalDate.now()
                .minusDays(days)
                .format(
                        DateTimeFormatter.ofPattern(format)
                );
    }

    /**
     * Add minutes to current date/time.
     */
    public static String addMinutes(
            int minutes,
            String format) {

        return LocalDateTime.now()
                .plusMinutes(minutes)
                .format(
                        DateTimeFormatter.ofPattern(format)
                );
    }

    /**
     * Subtract minutes from current date/time.
     */
    public static String subtractMinutes(
            int minutes,
            String format) {

        return LocalDateTime.now()
                .minusMinutes(minutes)
                .format(
                        DateTimeFormatter.ofPattern(format)
                );
    }

    /**
     * Parse date string.
     */
    public static LocalDate parseDate(
            String date,
            String format) {

        return LocalDate.parse(
                date,
                DateTimeFormatter.ofPattern(format)
        );
    }

    /**
     * Compare two dates.
     */
    public static boolean isDateBefore(
            String date1,
            String date2,
            String format) {

        LocalDate first =
                parseDate(date1, format);

        LocalDate second =
                parseDate(date2, format);

        return first.isBefore(second);
    }

    /**
     * Compare two dates.
     */
    public static boolean isDateAfter(
            String date1,
            String date2,
            String format) {

        LocalDate first =
                parseDate(date1, format);

        LocalDate second =
                parseDate(date2, format);

        return first.isAfter(second);
    }

    /**
     * Check whether two dates are equal.
     */
    public static boolean isSameDate(
            String date1,
            String date2,
            String format) {

        LocalDate first =
                parseDate(date1, format);

        LocalDate second =
                parseDate(date2, format);

        return first.isEqual(second);
    }

    /**
     * Get current date/time for a specific timezone.
     */
    public static String getDateTimeForZone(
            String zone,
            String format) {

        return ZonedDateTime.now(
                ZoneId.of(zone)
        ).format(
                DateTimeFormatter.ofPattern(format)
        );
    }
}
