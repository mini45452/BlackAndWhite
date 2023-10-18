package com.example.blackandwhite;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageView;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RegistrationActivity extends AppCompatActivity {

    private ImageView selfImage;
    private EditText nameEditText;
    private EditText nikEditText;
    private OkHttpClient client = new OkHttpClient();

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri = null;

    private String name; // Variable to store the name
    private String nik;  // Variable to store the NIK

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        selfImage = findViewById(R.id.selfImage);
        nameEditText = findViewById(R.id.nameEditText);
        nikEditText = findViewById(R.id.nikEditText);

        Button chooseImageBtn = findViewById(R.id.chooseImageBtn);
        chooseImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        Button cropImageBtn = findViewById(R.id.cropImageBtn);
        cropImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedImageUri != null) {
                    cropImage(selectedImageUri);
                } else {
                    Toast.makeText(RegistrationActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button saveRegistrationBtn = findViewById(R.id.saveRegistrationBtn);
        saveRegistrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selfImage.getDrawable() == null) {
                    // ImageView is empty, show a toast message
                    Toast.makeText(getApplicationContext(), "Please select a self image", Toast.LENGTH_SHORT).show();
                    return; // Return and don't proceed further
                }

                // Retrieve the user inputs and store them in variables
                name = nameEditText.getText().toString();
                nik = nikEditText.getText().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String response = sendRegistrationData();

                            // Handle the response here
                            Intent intent = new Intent(RegistrationActivity.this, RegistrationResultActivity.class);
                            intent.putExtra("result", response);

                            startActivity(intent);
                        } catch (IOException e) {
                            Log.d("RegistrationResponse", "menggagal");
                            e.printStackTrace();

                            // Handle the exception if necessary
                        }
                    }
                }).start();

            }
        });
    }

    private void cropImage(Uri imageUri) {
        CropImageView cropImageView = new CropImageView(this);
        cropImageView.setImageUriAsync(imageUri);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setTitle("Crop Image")
                .setView(cropImageView)
                .setPositiveButton("Crop", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Bitmap croppedBitmap = cropImageView.getCroppedImage();
                        // Use the cropped bitmap as needed
                        if (croppedBitmap != null) {
                            selfImage.setImageBitmap(croppedBitmap);
                        }
                    }
                })
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            selfImage.setImageURI(selectedImageUri);
        }
    }

    public InputStream imageViewToInputStream(ImageView imageView) {
        // Get the drawable from the ImageView
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();

        if (bitmapDrawable != null) {
            // Convert the drawable to a Bitmap
            Bitmap bitmap = bitmapDrawable.getBitmap();

            // Create a ByteArrayOutputStream to write the Bitmap
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Compress the Bitmap to a PNG format with 100% quality
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            // Convert the ByteArrayOutputStream to ByteArrayInputStream
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

            return inputStream;
        }

        return null;
    }

    // Function to send registration data
    private String sendRegistrationData() throws IOException {
        String url = "https://web01.facereco.net:8000/dummy-reg";
        String tempatLahir = "Beji";
        String tanggalLahir = "2000-01-01";
        String jenisKelamin = "Male";
        String alamat = "Jl. Beji No. 43";

        // Create a request body with multipart data
//        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
        InputStream inputStream = imageViewToInputStream(selfImage);
        File imageFile = createImageFileFromInputStream(inputStream);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", imageFile.getName(),
                        RequestBody.create(MediaType.parse("image/*"), imageFile))
                .addFormDataPart("nik", nik)
                .addFormDataPart("nama", name)
                .addFormDataPart("tempatLahir", tempatLahir)
                .addFormDataPart("tanggalLahir", tanggalLahir)
                .addFormDataPart("jenisKelamin", jenisKelamin)
                .addFormDataPart("alamat", alamat)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.message();
//            if (response.isSuccessful()) {
//                String responseBody = response.body().string();
//                response.message();
//                // Assuming you are in the RegistrationActivity
//                return "OK";
//                // Process the response as needed
//                // ...
//            } else {
//                String deasyg = response.body().toString();
//                return "FAIL";
//            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Delete the image file, whether the request was successful or not
            if (imageFile.exists()) {
                imageFile.delete();
            }
        }

        return "aw";
    }

    // Helper method to create a temporary image file from an input stream
    private File createImageFileFromInputStream(InputStream inputStream) throws IOException {
        File outputDir = getCacheDir(); // Use the cache directory
        File imageFile = File.createTempFile("image", ".jpg", outputDir);

        try (FileOutputStream outputStream = new FileOutputStream(imageFile)) {
            byte[] buffer = new byte[4 * 1024]; // Adjust buffer size as needed
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        // Check if the file was created successfully
        if (imageFile.exists()) {
            return imageFile;
        } else {
            Log.e("RegistrationActivity", "Image file creation failed.");
            return null;
        }
    }

}
