package com.quangvinh.bt1_nhom_forgotpass;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.quangvinh.bt1_nhom_forgotpass.databinding.ActivityOtpVerifyBinding;

import java.util.Locale;
import java.util.Random;

public class OtpVerifyActivity extends AppCompatActivity {

    private ActivityOtpVerifyBinding binding;
    private String email;
    private String otp; // mock OTP
    private CountDownTimer timer;
    private static final long RESEND_MS = 60_000L;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpVerifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar()!=null) getSupportActionBar().hide();

        email = getIntent().getStringExtra("email");
        otp = getIntent().getStringExtra("otp"); // từ màn EmailRequest

        setupOtpBoxes();
        startResendCountdown();

        binding.btnVerify.setOnClickListener(v -> onVerify());
        binding.tvResend.setOnClickListener(v -> onResend());
    }

    private void onVerify(){
        String code = collectOtp();
        if (code.length()!=6){
            Snackbar.make(binding.btnVerify, "Nhập đủ 6 số OTP", Snackbar.LENGTH_LONG).show();
            return;
        }
        setLoading(true);
        // Mock kiểm tra
        setLoading(false);
        if (code.equals(otp)) {
            Snackbar.make(binding.btnVerify, "OTP hợp lệ (demo).", Snackbar.LENGTH_SHORT).show();
            Intent i = new Intent(this, ResetPasswordActivity.class);
            i.putExtra("email", email);
            startActivity(i);
        } else {
            Snackbar.make(binding.btnVerify, "OTP không đúng.", Snackbar.LENGTH_LONG).show();
        }
    }

    private void onResend(){
        if (!binding.tvResend.isEnabled()) return;
        // Mock gửi lại
        otp = String.format(Locale.US, "%06d", new Random().nextInt(1_000_000));
        Snackbar.make(binding.tvResend, "Đã 'gửi lại' OTP demo: " + otp, Snackbar.LENGTH_LONG).show();
        clearOtp();
        binding.otp1.requestFocus();
        startResendCountdown();
    }

    private void startResendCountdown(){
        binding.tvResend.setEnabled(false);
        if (timer!=null) timer.cancel();
        timer = new CountDownTimer(RESEND_MS, 1000){
            @Override public void onTick(long ms) {
                binding.tvCountdown.setText(String.format(Locale.getDefault(),"Gửi lại sau %ds", ms/1000));
            }
            @Override public void onFinish() {
                binding.tvCountdown.setText("Bạn có thể gửi lại OTP");
                binding.tvResend.setEnabled(true);
            }
        }.start();
    }

    private void setLoading(boolean b){
        binding.progress.setVisibility(b? View.VISIBLE: View.GONE);
        binding.btnVerify.setEnabled(!b);
        setOtpEnabled(!b);
    }

    // ===== OTP helpers =====
    private EditText[] boxes(){
        return new EditText[]{ binding.otp1,binding.otp2,binding.otp3,binding.otp4,binding.otp5,binding.otp6 };
    }
    private void setOtpEnabled(boolean e){
        for (EditText ed: boxes()) ed.setEnabled(e);
    }
    private void clearOtp(){ for (EditText ed: boxes()) ed.setText(""); }

    private String collectOtp(){
        StringBuilder sb = new StringBuilder(6);
        for (EditText ed: boxes()){
            CharSequence c = ed.getText();
            sb.append(c!=null && c.length()==1 ? c : "");
        }
        return sb.toString();
    }

    private void setupOtpBoxes(){
        EditText[] b = boxes();
        for (EditText e : b) {
            e.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(1) });
        }
        for (int i=0;i<b.length;i++){
            final int idx=i;
            b[i].addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(Editable s) {
                    if (s.length()==1 && idx < b.length-1) b[idx+1].requestFocus();
                }
            });
        }
        // Dán 6 số vào ô đầu → tự phân bổ
        b[0].addTextChangedListener(new TextWatcher() {
            boolean distributing=false;
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (distributing) return;
                String t = s.toString();
                if (t.length()>1){
                    String digits = t.replaceAll("\\D", "");
                    if (digits.length()>=6){
                        distributing=true;
                        for (int k=0;k<6;k++) b[k].setText(String.valueOf(digits.charAt(k)));
                        b[5].requestFocus();
                        distributing=false;
                    }
                }
            }
        });
    }

    @Override protected void onDestroy() {
        if (timer!=null) timer.cancel();
        super.onDestroy();
    }
}