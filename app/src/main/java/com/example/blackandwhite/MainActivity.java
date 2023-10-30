package com.example.blackandwhite;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Rect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

//import com.

public class MainActivity extends Activity {
    private Button registrationButton;
    private Button searchButton;

    private TextView backButton;
    private TextView activityInfo;

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageView;
    private Button selectImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registrationButton = findViewById(R.id.registration_button);
        searchButton = findViewById(R.id.search_button);

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open RegistrationActivity
                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open SearchActivity
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        backButton = findViewById(R.id.backButton);
        // Find the TextView for activity information
        activityInfo = findViewById(R.id.activityInfo);

        // Set the initial activity information text
        activityInfo.setText("Main Page");

        // Set an OnClickListener for the "backButton" TextView
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // This simulates the default back button behavior
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                detectAndDrawFaces(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void detectAndDrawFaces(Bitmap bitmap) {
        String trainModelFileName = "haarcascade_frontalface_default.xml";
        List<Rect> faceRects = ImageUtils.detectFaces(bitmap, getResourceFilePath(trainModelFileName));

        // Draw bounding boxes around the detected faces
        Bitmap resultBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(resultBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        for (Rect rect : faceRects) {
            canvas.drawRect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, paint);
        }

        // Display the image with bounding boxes
        imageView.setImageBitmap(resultBitmap);
    }

    public String getResourceFilePath(String resourceName) {
        int resourceId = R.raw.haarcascade_frontalface_default;

        String filePath = null;
        try {
            InputStream inputStream = getResources().openRawResource(resourceId);
            String fileName = resourceName; // Use the resource name as the file name
            File outputFile = new File(getFilesDir(), fileName);
            filePath = outputFile.getAbsolutePath();

            // Copy the content of the InputStream to the output file
            try (OutputStream os = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }

            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePath;
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