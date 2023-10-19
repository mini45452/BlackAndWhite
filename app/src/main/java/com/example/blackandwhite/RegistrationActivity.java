package com.example.blackandwhite;

import com.example.blackandwhite.api.dto.RegistrationRequest;
import com.example.blackandwhite.api.dto.RegistrationResponse;
import com.example.blackandwhite.api.dto.RegistrationResponseError;
import com.example.blackandwhite.api.model.RegistrationModel;
import com.example.blackandwhite.api.client.ApiClient;
import com.example.blackandwhite.api.services.RegistrationService;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
//import okhttp3.Response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegistrationActivity extends AppCompatActivity {

    private ImageView selfImage;
    private EditText nameEditText;
    private EditText nikEditText;
    private OkHttpClient client = new OkHttpClient();

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri = null;

    private String name; // Variable to store the name
    private String nik;  // Variable to store the NIK

    private boolean isImageSet = false;

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
                if (!isImageSet) {
                    // ImageView is empty, show a toast message
                    Toast.makeText(getApplicationContext(), "Please select a self image", Toast.LENGTH_SHORT).show();
                    return; // Return and don't proceed further
                }

                // Retrieve the user inputs and store them in variables
                try {
                    name = nameEditText.getText().toString();
                    nik = nikEditText.getText().toString();
                    InputStream inputStream = imageViewToInputStream(selfImage);
                    File imageFile = createImageFileFromInputStream(inputStream);
                    RegistrationModel registrationModel = new RegistrationModel();
                    registrationModel.setName(name);
                    registrationModel.setNik(nik);
                    registrationModel.setImage(imageFile);
                    registerUserWithModel(registrationModel);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
                            isImageSet = true;
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
            isImageSet = true;
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
//    private String sendRegistrationData() throws IOException {
//        String url = "https://web01.facereco.net:8000/dummy-reg";
//        String tempatLahir = "Beji";
//        String tanggalLahir = "2000-01-01";
//        String jenisKelamin = "Male";
//        String alamat = "Jl. Beji No. 43";
//
//        // Create a request body with multipart data
//        InputStream inputStream = imageViewToInputStream(selfImage);
//        File imageFile = createImageFileFromInputStream(inputStream);
//
//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("file", imageFile.getName(),
//                        RequestBody.create(MediaType.parse("image/*"), imageFile))
//                .addFormDataPart("nik", nik)
//                .addFormDataPart("nama", name)
//                .addFormDataPart("tempatLahir", tempatLahir)
//                .addFormDataPart("tanggalLahir", tanggalLahir)
//                .addFormDataPart("jenisKelamin", jenisKelamin)
//                .addFormDataPart("alamat", alamat)
//                .build();
//
//        Request request = new Request.Builder()
//                .url(url)
//                .post(requestBody)
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            return response.message();
////            if (response.isSuccessful()) {
////                String responseBody = response.body().string();
////                response.message();
////                // Assuming you are in the RegistrationActivity
////                return "OK";
////                // Process the response as needed
////                // ...
////            } else {
////                String deasyg = response.body().toString();
////                return "FAIL";
////            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            // Delete the image file, whether the request was successful or not
//            if (imageFile.exists()) {
//                imageFile.delete();
//            }
//        }
//
//        return "aw";
//    }

    public void registerUserWithModel(RegistrationModel registrationModel) {
        // Create a Retrofit instance using your ApiClient
        Retrofit retrofit = ApiClient.getRetrofitInstance();

        // Create the RegistrationService using the Retrofit instance
        RegistrationService service = retrofit.create(RegistrationService.class);

        // Construct RegistrationRequest from RegistrationModel
        RegistrationRequest registrationRequest = new RegistrationRequest(registrationModel);

        // Make the API call using Retrofit
        Call<RegistrationResponse> call = service.registerUser(
                registrationRequest.getNik(),
                registrationRequest.getNama(),
                registrationRequest.getTempatLahir(),
                registrationRequest.getTanggalLahir(),
                registrationRequest.getJenisKelamin(),
                registrationRequest.getAlamat(),
                registrationRequest.getImage()
        );

        call.enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                if (response.isSuccessful()) {
                    RegistrationResponse registrationResponse = response.body();
                    String nama = registrationResponse.getNama();
                    String nik = registrationResponse.getNik();

                    // Concatenate status, nama, and nik for success message
                    String successMessage = "Nama: " + nama + "\nNIK: " + nik;

                    // Create an intent to navigate to RegistrationResultActivity
                    Intent intent = new Intent(RegistrationActivity.this, RegistrationResultActivity.class);
                    intent.putExtra("isSuccess", true); // Indicate success
                    intent.putExtra("message", successMessage);
                    startActivity(intent);
                } else {
                    // Get the error response body as a JSON string
                    try {
                        if (response.errorBody() != null) {
                            String errorResponseJson = response.errorBody().string();

                            // Parse the error response JSON into the RegistrationResponseError object
                            RegistrationResponseError registrationResponseError =
                                    new Gson().fromJson(errorResponseJson, RegistrationResponseError.class);

                            // Now you can access the error information
                            int status = registrationResponseError.getStatus();
                            String message = registrationResponseError.getMessage();

                            // Create an intent to navigate to RegistrationResultActivity
                            Intent intent = new Intent(RegistrationActivity.this, RegistrationResultActivity.class);
                            intent.putExtra("isSuccess", false); // Indicate failure
                            intent.putExtra("message", message);
                            startActivity(intent);
                        }
                    } catch (IOException e) {
                        // Handle JSON parsing or IO error
                    }
                }
            }

            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                // Handle network or other errors here
            }
        });
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
