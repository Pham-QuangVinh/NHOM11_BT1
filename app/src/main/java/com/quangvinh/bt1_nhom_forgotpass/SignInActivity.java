package com.quangvinh.bt1_nhom_forgotpass;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // Đi tới SignUp
        findViewById(R.id.tvSignUpLink).setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        });

        // Đi tới EmailRequest (Quên mật khẩu)
        findViewById(R.id.tvForgot).setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, EmailRequestActivity.class));
            // không gọi finish(); để user có thể back về SignIn
        });
    }
}
