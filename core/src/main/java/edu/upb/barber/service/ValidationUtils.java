package edu.upb.barber.service;

import edu.upb.barber.service.exception.OperationException;

import java.util.regex.Pattern;

public final class ValidationUtils {

    private static final Pattern CONTROL_CHARS = Pattern.compile("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]");
    private static final Pattern SQL_META = Pattern.compile("(?i)(--|/\\*|\\*/|;\\s*(drop|delete|insert|update|alter|truncate|create|exec|execute)\\b)");

    private ValidationUtils() {
    }

    public static String cleanOptionalText(String value, int maxLength, String fieldName) throws OperationException {
        if (value == null) {
            return null;
        }
        String cleaned = value.trim().replaceAll("\\s+", " ");
        if (cleaned.isEmpty()) {
            return null;
        }
        validateSafeText(cleaned, maxLength, fieldName);
        return cleaned;
    }

    public static String requireText(String value, int maxLength, String fieldName) throws OperationException {
        String cleaned = cleanOptionalText(value, maxLength, fieldName);
        if (cleaned == null) {
            throw new OperationException("El campo " + fieldName + " es requerido");
        }
        return cleaned;
    }

    public static void validateSafeText(String value, int maxLength, String fieldName) throws OperationException {
        if (value.length() > maxLength) {
            throw new OperationException("El campo " + fieldName + " no puede superar " + maxLength + " caracteres");
        }
        if (CONTROL_CHARS.matcher(value).find() || SQL_META.matcher(value).find()) {
            throw new OperationException("El campo " + fieldName + " contiene caracteres no permitidos");
        }
    }
}
