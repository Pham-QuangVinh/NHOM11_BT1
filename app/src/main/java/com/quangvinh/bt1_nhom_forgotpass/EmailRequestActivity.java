package com.quangvinh.bt1_nhom_forgotpass;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.quangvinh.bt1_nhom_forgotpass.databinding.ActivityEmailRequestBinding;

import java.util.Locale;
import java.util.Random;

public class EmailRequestActivity extends AppCompatActivity {

    private ActivityEmailRequestBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmailRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // 🔙 Xử lý nút Back
        if (binding.btnBack != null) {
            binding.btnBack.setOnClickListener(v -> finish());
        }

        // 🔘 Gửi OTP
        binding.btnSendOtp.setOnClickListener(v -> onSendOtp());
    }

    private void onSendOtp() {
        binding.tilEmail.setError(null);
        String email = binding.edtEmail.getText() != null
                ? binding.edtEmail.getText().toString().trim()
                : "";

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError("Email không hợp lệ");
            return;
        }

        setLoading(true);

        // MOCK tạo OTP 6 số
        String otp = String.format(Locale.US, "%06d", new Random().nextInt(1_000_000));

        Log.d("OTP_DEBUG", "OTP demo: " + otp);
        // Hiển thị demo OTP (chỉ để kiểm tra)
        setLoading(false);
        Snackbar.make(binding.btnSendOtp, "OTP demo: " + otp, Snackbar.LENGTH_LONG).show();

        // Điều hướng sang màn OTP (truyền email & OTP mock)
        Intent i = new Intent(this, OtpVerifyActivity.class);
        i.putExtra("email", email);
        i.putExtra("otp", otp);
        startActivity(i);
    }

    private void setLoading(boolean b) {
        binding.progress.setVisibility(b ? View.VISIBLE : View.GONE);
        binding.btnSendOtp.setEnabled(!b);
        binding.edtEmail.setEnabled(!b);
    }
}
