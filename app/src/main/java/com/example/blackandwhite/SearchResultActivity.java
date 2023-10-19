package com.example.blackandwhite;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchResultActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        // Get the "nik" and "nama" values passed from the previous activity
        ArrayList<String> niks = getIntent().getStringArrayListExtra("niks");
        ArrayList<String> namas = getIntent().getStringArrayListExtra("namas");

        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        for (int i = 0; i < niks.size(); i++) {
            // Create a TextView for each person's "nik" and "nama"
            TextView textView = new TextView(this);
            textView.setText("Person " + (i + 1) + "\n" +
                    "NIK: " + niks.get(i) + "\n" +
                    "Nama: " + namas.get(i) + "\n");

            // Add the TextView to the LinearLayout
            linearLayout.addView(textView);
        }

    }
}
