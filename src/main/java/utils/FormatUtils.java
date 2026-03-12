package utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for formatting currency and dates.
 */
public class FormatUtils {
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,###");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Formats a BigDecimal to currency string with VND suffix.
     * Example: 100000 -> 100.000 VND
     */
    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 VND";
        return CURRENCY_FORMAT.format(amount) + " VND";
    }

    /**
     * Formats a BigDecimal to currency string without suffix.
     * Example: 100000 -> 100.000
     */
    public static String formatNumber(BigDecimal amount) {
        if (amount == null) return "0";
        return CURRENCY_FORMAT.format(amount);
    }
    
    /**
     * Parse currency string back to BigDecimal.
     */
    public static BigDecimal parseCurrency(String text) {
        if (text == null || text.trim().isEmpty()) return BigDecimal.ZERO;
        String clean = text.replaceAll("[^0-9]", "");
        if (clean.isEmpty()) return BigDecimal.ZERO;
        return new BigDecimal(clean);
    }

    /**
     * Formats a LocalDate to dd/MM/yyyy string.
     */
    public static String formatDate(LocalDate date) {
        if (date == null) return "";
        return date.format(DATE_FORMAT);
    }

    /**
     * Formats a LocalDateTime to dd/MM/yyyy HH:mm string.
     */
    public static String formatDateTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DATE_TIME_FORMAT);
    }
}
