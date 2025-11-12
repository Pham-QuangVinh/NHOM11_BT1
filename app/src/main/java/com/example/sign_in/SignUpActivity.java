package com.example.sign_in;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Back
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // --- Dropdown Giới tính ---
        MaterialAutoCompleteTextView etGender = findViewById(R.id.etGender);
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.genders,               // thêm mảng này trong strings.xml
                android.R.layout.simple_list_item_1
        );
        etGender.setAdapter(genderAdapter);

        // --- DatePicker cho Ngày sinh ---
        TextInputEditText etDob = findViewById(R.id.etDateOfBirth);
        etDob.setOnClickListener(v -> {
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder
                    .datePicker()
                    .setTitleText("Select date of birth")
                    .build();
            picker.show(getSupportFragmentManager(), "dob");
            picker.addOnPositiveButtonClickListener(selection -> {
                String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(new Date(selection));
                etDob.setText(date);
            });
        });
    }
}
