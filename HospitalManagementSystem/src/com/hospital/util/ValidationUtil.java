package com.hospital.util;

import java.util.regex.Pattern;

/** Common validators used by servlets and JSP forms (server-side). */
public final class ValidationUtil {

    private ValidationUtil() { }

    /** UserId: alphanumeric, min 8 chars. */
    public static final Pattern USERID = Pattern.compile("^[A-Za-z0-9]{8,}$");

    /**
     * Password: min 10 chars, at least 1 uppercase letter, 1 digit, and
     * 1 special character (non-alphanumeric).
     */
    public static final Pattern PASSWORD = Pattern.compile(
            "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{10,}$");

    public static final Pattern PATIENT_ID    = Pattern.compile("^\\d{7}$");
    public static final Pattern CONTACT_NO    = Pattern.compile("^\\d{10}$");
    public static final Pattern AADHAR_NUMBER = Pattern.compile("^\\d{12}$");
    public static final Pattern EMAIL         = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public static boolean isUserId(String s)   { return s != null && USERID.matcher(s).matches(); }
    public static boolean isPassword(String s) { return s != null && PASSWORD.matcher(s).matches(); }
    public static boolean isPatientId(String s){ return s != null && PATIENT_ID.matcher(s).matches(); }
    public static boolean isContactNo(String s){ return s != null && CONTACT_NO.matcher(s).matches(); }
    public static boolean isAadhar(String s)   { return s != null && AADHAR_NUMBER.matcher(s).matches(); }
    public static boolean isEmail(String s)    { return s != null && EMAIL.matcher(s).matches(); }

    public static boolean isBlank(String s)    { return s == null || s.trim().isEmpty(); }

    /** Validate and return parsed int, or throw IllegalArgumentException with a friendly message. */
    public static int parseInt(String value, String fieldLabel) {
        if (isBlank(value)) {
            throw new IllegalArgumentException(fieldLabel + " is required.");
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldLabel + " must be a whole number.");
        }
    }
}
