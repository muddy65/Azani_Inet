package com.azani.service;

public class ValidationService {

    // Checks if any text field in a form is left blank
    public static boolean isFieldEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    // Ensures a phone number is exactly 10 digits (e.g., 0712345678)
    public static boolean isValidPhoneNumber(String phone) {
        if (isFieldEmpty(phone)) return false;
        return phone.matches("\\d{10}");
    }

    // Ensures the user typed a valid number for bandwidth or money
    public static boolean isNumeric(String str) {
        if (isFieldEmpty(str)) return false;
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}