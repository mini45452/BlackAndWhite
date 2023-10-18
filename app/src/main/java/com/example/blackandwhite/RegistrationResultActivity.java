package com.example.blackandwhite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RegistrationResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_result);

        // Get the result from the intent
        String result = getIntent().getStringExtra("result");

        // Display the result message in your activity
        TextView resultTextView = findViewById(R.id.resultTextView);
        resultTextView.setText("Registration " + result);

        Button backToHomeButton = findViewById(R.id.backToHomeButton);
        backToHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to MainActivity
                Intent intent = new Intent(RegistrationResultActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
