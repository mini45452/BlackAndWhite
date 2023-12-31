package com.example.blackandwhite;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchResultActivity extends Activity {
    private TextView backButton;
    private TextView activityInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        // Get the "nik" and "nama" values passed from the previous activity
        float[] similarities = getIntent().getFloatArrayExtra("similarities");
        ArrayList<String> niks = getIntent().getStringArrayListExtra("niks");
        ArrayList<String> namas = getIntent().getStringArrayListExtra("namas");

        // In the destination activity
        String imageUriString = getIntent().getStringExtra("imageUri");
        Uri imageUri = Uri.parse(imageUriString);
        ImageView imageView = findViewById(R.id.resultImageView);
        imageView.setImageURI(imageUri);

        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        for (int i = 0; i < niks.size(); i++) {
            final int position = i;

            // Inflate the "person_thumbnail" layout for each section
            View sectionLayout = getLayoutInflater().inflate(R.layout.person_thumbnail, null);

            // Get the components within the "person_thumbnail" layout
            TextView positionTextView = sectionLayout.findViewById(R.id.position);
            TextView nikTextView = sectionLayout.findViewById(R.id.nik);
            TextView namaTextView = sectionLayout.findViewById(R.id.nama);

            // Set the data for positionTextView, nikTextView, and namaTextView
            positionTextView.setText(String.valueOf(i + 1));
            nikTextView.setText("NIK: " + niks.get(i));
            namaTextView.setText("Nama: " + namas.get(i) + " (" + similarities[i] + ")");

            // Add the "person_thumbnail" layout to the parent LinearLayout
            linearLayout.addView(sectionLayout);

            // Set an OnClickListener for the sectionLayout
            sectionLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Action to perform when the sectionLayout is clicked
                    String message = "Section " + (position + 1) + " is clicked";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        }

        LayoutInflater inflater = getLayoutInflater();
        View footerLayout = inflater.inflate(R.layout.footer_layout, null);
        linearLayout.addView(footerLayout);

        backButton = findViewById(R.id.backButton);
        // Find the TextView for activity information
        activityInfo = findViewById(R.id.activityInfo);

        // Set the initial activity information text
        activityInfo.setText("Search Result Page");

        // Set an OnClickListener for the "backButton" TextView
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // This simulates the default back button behavior
            }
        });
    }

    public void goToMainActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToSearchActivity(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    // Do nothing method for Stats and Profile
    public void doNothing(View view) {
        // Do nothing for Stats and Profile for now.
    }
}

