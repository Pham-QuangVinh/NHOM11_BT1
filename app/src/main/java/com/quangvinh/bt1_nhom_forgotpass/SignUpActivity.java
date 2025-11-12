package com.quangvinh.bt1_nhom_forgotpass;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etDob, etEmail, etAddress, etPhone, etPassword, etConfirm;
    private MaterialAutoCompleteTextView etGender;
    private CheckBox cbTerms;
    private MaterialButton btnSignUp;

    // ====== Regex & helper cho validate ======
    private static final Pattern VN_PHONE_REGEX = Pattern.compile("^(0)(3|5|7|8|9)\\d{8}$");
    private static final Pattern LOWER   = Pattern.compile(".*[a-z].*");
    private static final Pattern UPPER   = Pattern.compile(".*[A-Z].*");
    private static final Pattern DIGIT   = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL = Pattern.compile(".*[^A-Za-z0-9].*");
    private static final Pattern SPACE   = Pattern.compile(".*\\s.*");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // Back
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // --- Bind views ---
        etFullName = findViewById(R.id.etFullName);
        etDob      = findViewById(R.id.etDateOfBirth);
        etGender   = findViewById(R.id.etGender);
        etEmail    = findViewById(R.id.etEmail);         // optional
        etAddress  = findViewById(R.id.etAddress);
        etPhone    = findViewById(R.id.etPhoneNumber);
        etPassword = findViewById(R.id.etPassword);
        etConfirm  = findViewById(R.id.etConfirmPassword);
        cbTerms    = findViewById(R.id.cbTerms);
        btnSignUp  = findViewById(R.id.btnSignUp);

        // --- Dropdown Giới tính ---
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(
                this, R.array.genders, android.R.layout.simple_list_item_1);
        etGender.setAdapter(genderAdapter);

        // --- DatePicker cho Ngày sinh ---
        etDob.setOnClickListener(v -> {
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder
                    .datePicker().setTitleText("Select date of birth").build();
            picker.show(getSupportFragmentManager(), "dob");
            picker.addOnPositiveButtonClickListener(selection -> {
                String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(new Date(selection));
                etDob.setText(date);
            });
        });

        // --- Lắng nghe thay đổi để bật/tắt nút ---
        TextWatcher watcher = new SimpleTextWatcher(this::updateButtonState);
        etFullName.addTextChangedListener(watcher);
        etDob.addTextChangedListener(watcher);
        etGender.addTextChangedListener(watcher);
        etAddress.addTextChangedListener(watcher);
        etPhone.addTextChangedListener(watcher);
        etPassword.addTextChangedListener(watcher);
        etConfirm.addTextChangedListener(watcher);
        cbTerms.setOnCheckedChangeListener((b, checked) -> updateButtonState());

        // --- Gắn validate real-time chi tiết ---
        attachRealtimeValidation();

        // --- Click SIGN UP (flow: SignUp -> OTP -> Success -> SignIn) ---
        btnSignUp.setOnClickListener(v -> {
            if (!(isFormValid() && cbTerms.isChecked())) return;

            String email = textOf(etEmail); // optional
            String otp = String.format(Locale.US, "%06d", new Random().nextInt(1_000_000));

            // Hiển thị OTP demo 10 giây để test
            Snackbar.make(btnSignUp, "OTP demo (signup): " + otp, Snackbar.LENGTH_LONG)
                    .setDuration(10_000)
                    .show();

            // Chuyển sang OTP, gắn cờ source="signup" để OtpVerifyActivity rẽ nhánh
            Intent i = new Intent(this, OtpVerifyActivity.class);
            i.putExtra("source", "signup");
            i.putExtra("email", email);
            i.putExtra("otp", otp);
            startActivity(i);
        });

        updateButtonState(); // init
    }

    /** Bật nút khi form hợp lệ và đã tick checkbox; đồng thời đổi màu */
    private void updateButtonState() {
        boolean valid = isFormValid() && cbTerms.isChecked();
        btnSignUp.setEnabled(valid);
        int color = ContextCompat.getColor(this, valid ? R.color.signup_enabled : R.color.signup_disabled);
        btnSignUp.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    /** Điều kiện hợp lệ tối thiểu + kiểm tra nâng cao */
    private boolean isFormValid() {
        String fullName = textOf(etFullName);
        String dob      = textOf(etDob);
        String gender   = textOf(etGender);
        String address  = textOf(etAddress);
        String phone    = textOf(etPhone);
        String pass     = textOf(etPassword);
        String confirm  = textOf(etConfirm);
        String email    = textOf(etEmail); // optional

        boolean requiredFilled = !fullName.isEmpty() && !dob.isEmpty() && !gender.isEmpty()
                && !address.isEmpty() && !phone.isEmpty() && !pass.isEmpty() && !confirm.isEmpty();

        boolean passMatch  = pass.equals(confirm);
        boolean passStrong = isStrongPassword(pass);
        boolean phoneOK    = isValidVNPhone(phone);
        boolean emailOK    = email.isEmpty() || isValidEmail(email);

        return requiredFilled && passMatch && passStrong && phoneOK && emailOK;
    }

    // ====== VALIDATION REAL-TIME (hiển thị lỗi ngay khi nhập) ======
    private void attachRealtimeValidation() {
        // EMAIL (optional – chỉ báo lỗi nếu người dùng nhập nhưng sai định dạng)
        etEmail.addTextChangedListener(new SimpleTextWatcher(() -> {
            TextInputLayout til = findViewById(R.id.tilEmail);
            String email = textOf(etEmail);
            if (TextUtils.isEmpty(email)) {
                til.setError(null);
            } else if (!isValidEmail(email)) {
                til.setError("Email không hợp lệ");
            } else {
                til.setError(null);
            }
            updateButtonState();
        }));

        // PHONE VN
        etPhone.addTextChangedListener(new SimpleTextWatcher(() -> {
            TextInputLayout til = findViewById(R.id.tilPhoneNumber);
            String raw = textOf(etPhone);
            if (!isValidVNPhone(raw)) {
                til.setError("SĐT VN không hợp lệ (03/05/07/08/09 + 8 số)");
            } else {
                til.setError(null);
            }
            updateButtonState();
        }));

        // PASSWORD + STRENGTH
        etPassword.addTextChangedListener(new SimpleTextWatcher(() -> {
            TextInputLayout til = findViewById(R.id.tilPassword);
            String p = textOf(etPassword);

            int score = passwordScore(p);
            String label = "Độ mạnh: " + passwordLabel(score);
            til.setHelperText(label);

            if (!isStrongPassword(p)) {
                til.setError("≥8 ký tự, có thường/HOA/số/ký tự đặc biệt, không khoảng trắng");
            } else {
                til.setError(null);
            }

            // đồng bộ xác nhận
            validateConfirmPassword();
            updateButtonState();
        }));

        // CONFIRM PASSWORD
        etConfirm.addTextChangedListener(new SimpleTextWatcher(() -> {
            validateConfirmPassword();
            updateButtonState();
        }));
    }

    private void validateConfirmPassword() {
        TextInputLayout til = findViewById(R.id.tilConfirmPassword);
        String p1 = textOf(etPassword);
        String p2 = textOf(etConfirm);
        if (!p2.isEmpty() && !p1.equals(p2)) {
            til.setError("Mật khẩu không trùng khớp");
        } else {
            til.setError(null);
        }
    }

    // ====== EMAIL / PHONE / PASS helpers ======
    private static boolean isValidEmail(String email) {
        if (email == null) return false;
        String e = email.trim();
        return !e.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(e).matches();
    }

    private static String normalizeVNPhone(String raw) {
        if (raw == null) return "";
        String p = raw.replaceAll("\\s+", "");
        if (p.startsWith("+84")) p = "0" + p.substring(3);
        else if (p.startsWith("84")) p = "0" + p.substring(2);
        return p;
    }

    private static boolean isValidVNPhone(String raw) {
        String p = normalizeVNPhone(raw);
        return VN_PHONE_REGEX.matcher(p).matches();
    }

    private static boolean isStrongPassword(String pass) {
        if (pass == null) return false;
        return pass.length() >= 8
                && LOWER.matcher(pass).matches()
                && UPPER.matcher(pass).matches()
                && DIGIT.matcher(pass).matches()
                && SPECIAL.matcher(pass).matches()
                && !SPACE.matcher(pass).matches();
    }

    private static int passwordScore(String pass) {
        if (pass == null) return 0;
        int score = 0;
        if (pass.length() >= 8) score++;
        if (LOWER.matcher(pass).matches()) score++;
        if (UPPER.matcher(pass).matches()) score++;
        if (DIGIT.matcher(pass).matches()) score++;
        if (SPECIAL.matcher(pass).matches()) score++;
        return Math.min(score, 5); // 0..5
    }

    private static String passwordLabel(int score) {
        switch (score) {
            case 0: case 1: return "Rất yếu";
            case 2:         return "Yếu";
            case 3:         return "Khá";
            case 4:         return "Mạnh";
            default:        return "Rất mạnh";
        }
    }

    // ====== tiện ích đọc text ======
    private String textOf(TextInputEditText v) {
        return v.getText() == null ? "" : v.getText().toString().trim();
    }
    private String textOf(MaterialAutoCompleteTextView v) {
        return v.getText() == null ? "" : v.getText().toString().trim();
    }

    // Helper TextWatcher ngắn gọn
    private static class SimpleTextWatcher implements TextWatcher {
        private final Runnable after;
        SimpleTextWatcher(Runnable after) { this.after = after; }
        public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
        public void onTextChanged(CharSequence s, int st, int b, int c) {}
        public void afterTextChanged(Editable s) { after.run(); }
    }
}
