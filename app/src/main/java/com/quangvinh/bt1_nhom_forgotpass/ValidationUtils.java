package com.quangvinh.bt1_nhom_forgotpass;

import android.util.Patterns;

import java.util.regex.Pattern;

public final class ValidationUtils {
    private ValidationUtils() {}

    // Email: dùng Android Patterns + cắt khoảng trắng
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        String e = email.trim();
        return !e.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(e).matches();
    }

    // Phone VN:
    // Nhận: "098...", "03...", "+8498...", "8498..."
    // Chuẩn hoá: +84 -> 0. Yêu cầu 10 số và đầu 03/05/07/08/09
    private static final Pattern VN_PHONE_PATTERN =
            Pattern.compile("^(0)(3|5|7|8|9)\\d{8}$");

    public static String normalizeVNPhone(String raw) {
        if (raw == null) return "";
        String p = raw.replaceAll("\\s+", "");
        if (p.startsWith("+84")) p = "0" + p.substring(3);
        else if (p.startsWith("84")) p = "0" + p.substring(2);
        return p;
    }

    public static boolean isValidVNPhone(String raw) {
        String p = normalizeVNPhone(raw);
        return VN_PHONE_PATTERN.matcher(p).matches();
    }

    // Password mạnh: >=8, có chữ thường, HOA, số, ký tự đặc biệt, KHÔNG khoảng trắng
    private static final Pattern LOWER   = Pattern.compile(".*[a-z].*");
    private static final Pattern UPPER   = Pattern.compile(".*[A-Z].*");
    private static final Pattern DIGIT   = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL = Pattern.compile(".*[^A-Za-z0-9].*");
    private static final Pattern SPACE   = Pattern.compile(".*\\s.*");

    public static boolean isStrongPassword(String pass) {
        if (pass == null) return false;
        return pass.length() >= 8
                && LOWER.matcher(pass).matches()
                && UPPER.matcher(pass).matches()
                && DIGIT.matcher(pass).matches()
                && SPECIAL.matcher(pass).matches()
                && !SPACE.matcher(pass).matches();
    }

    // Thang điểm độ mạnh (0–4) + nhãn
    public static int passwordScore(String pass) {
        if (pass == null) return 0;
        int score = 0;
        if (pass.length() >= 8) score++;
        if (LOWER.matcher(pass).matches()) score++;
        if (UPPER.matcher(pass).matches()) score++;
        if (DIGIT.matcher(pass).matches()) score++;
        if (SPECIAL.matcher(pass).matches()) score++;
        // trả về 0..5 (bạn có thể map sang Yếu/Vừa/Khá/ Mạnh/ Rất mạnh)
        return Math.min(score, 5);
    }

    public static String passwordLabel(int score) {
        switch (score) {
            case 0: case 1: return "Rất yếu";
            case 2:         return "Yếu";
            case 3:         return "Khá";
            case 4:         return "Mạnh";
            default:        return "Rất mạnh";
        }
    }
}
