package com.quangvinh.bt1_nhom_forgotpass;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.quangvinh.bt1_nhom_forgotpass.databinding.ActivityResetPasswordBinding;

public class ResetPasswordActivity extends AppCompatActivity {

    private ActivityResetPasswordBinding binding;
    private String email;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar()!=null) getSupportActionBar().hide();

        email = getIntent().getStringExtra("email");
        binding.btnSave.setOnClickListener(v -> onSave());
    }

    private void onSave(){
        binding.tilNewPass.setError(null);
        binding.tilConfirm.setError(null);

        String p1 = binding.edtNewPass.getText()!=null ? binding.edtNewPass.getText().toString() : "";
        String p2 = binding.edtConfirm.getText()!=null ? binding.edtConfirm.getText().toString() : "";

        if (p1.length()<6){
            binding.tilNewPass.setError("Mật khẩu tối thiểu 6 ký tự");
            return;
        }
        if (!p1.equals(p2)){
            binding.tilConfirm.setError("Mật khẩu không trùng khớp");
            return;
        }

        setLoading(true);

        // MOCK: coi như lưu thành công
        setLoading(false);
        Snackbar.make(binding.btnSave, "Đổi mật khẩu thành công cho " + (email!=null?email:"tài khoản"), Snackbar.LENGTH_LONG).show();

        // Quay về Login, xoá stack screen giữa
        /*Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();*/
    }

    private void setLoading(boolean b){
        binding.progress.setVisibility(b? View.VISIBLE: View.GONE);
        binding.btnSave.setEnabled(!b);
        binding.edtNewPass.setEnabled(!b);
        binding.edtConfirm.setEnabled(!b);
    }
}
