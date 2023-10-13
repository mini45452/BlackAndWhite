package com.example.blackandwhite;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private ImageView originalImageView;
    private ImageView filteredImageView;
    private Button pickImageButton;

    private Bitmap originalBitmap;
    private Bitmap filteredBitmap;

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        originalImageView = findViewById(R.id.originalImageView);
        filteredImageView = findViewById(R.id.filteredImageView);
        pickImageButton = findViewById(R.id.pickImageButton);

        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the image gallery to pick an image
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, PICK_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            try {
                // Convert the selected image to a Bitmap and display it in the originalImageView
                originalBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                originalImageView.setImageBitmap(originalBitmap);

                // Convert the originalBitmap to a grayscale bitmap and display it in the filteredImageView
                filteredBitmap = ImageUtils.convertToGrayscale(originalBitmap);
                filteredImageView.setImageBitmap(filteredBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}