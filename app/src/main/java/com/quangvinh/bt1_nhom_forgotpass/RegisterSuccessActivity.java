package com.quangvinh.bt1_nhom_forgotpass;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.quangvinh.bt1_nhom_forgotpass.databinding.ActivityRegisterSuccessBinding;

public class RegisterSuccessActivity extends AppCompatActivity {

    private ActivityRegisterSuccessBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // Hiển thị kèm email nếu có
        String email = getIntent().getStringExtra("email");
        if (email != null && !email.isEmpty()) {
            binding.tvSub.setText("Bạn đã đăng ký thành công cho " + email + ". Hãy quay về trang đăng nhập để tiếp tục.");
        }

        // Nút back: cũng đưa về SignIn và xóa stack giữa (tránh quay lại OTP)
        binding.btnBack.setOnClickListener(v -> goToSignIn());

        // Nút về đăng nhập
        binding.btnGoLogin.setOnClickListener(v -> goToSignIn());
    }

    private void goToSignIn() {
        Intent i = new Intent(this, SignInActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    // Nhấn nút back hệ thống cũng về SignIn (không quay lại OTP)
    @Override
    public void onBackPressed() {
        goToSignIn();
    }
}
