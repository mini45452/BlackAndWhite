package com.example.blackandwhite;

import static com.example.blackandwhite.ImageUtils.detectFaces;
import static com.example.blackandwhite.ImageUtils.drawFacesWithBoundingBoxes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.Rect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {

    private Button selectImageButton;
    private Button accessResourceButton;
    private Button sendRequestButton;
    private Bitmap selectedImageBitmap;

    private List<String> niksList = new ArrayList<>();
    private List<String> namasList = new ArrayList<>();

    private ImageView imageView;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        selectImageButton = findViewById(R.id.selectImageButton);
        accessResourceButton = findViewById(R.id.accessResourceButton);

        imageView = findViewById(R.id.imageView);

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        accessResourceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String resourceFilePath = getResourceFilePath(R.raw.haarcascade_frontalface_default, "haarcascade_frontalface_default.xml");
                if (imageView.getDrawable() == null) {
                    // Image not selected, show a toast message
                    Toast.makeText(SearchActivity.this, "Image not selected", Toast.LENGTH_SHORT).show();
                    return; // Exit the function
                }

                imageView.setDrawingCacheEnabled(true);
                imageView.buildDrawingCache();
                selectedImageBitmap = Bitmap.createBitmap(imageView.getDrawingCache());
                imageView.setDrawingCacheEnabled(false);

                if (selectedImageBitmap != null) {
                    List<Rect> faceRectangles = detectFaces(selectedImageBitmap, resourceFilePath);

                    if (faceRectangles.isEmpty()) {
                        // No faces detected, show a toast message
                        Toast.makeText(SearchActivity.this, "No faces detected", Toast.LENGTH_SHORT).show();
                    } else {
                        // Crop the first detected face
                        Rect firstFaceRect = faceRectangles.get(0);
                        Bitmap croppedFace = Bitmap.createBitmap(selectedImageBitmap, firstFaceRect.x, firstFaceRect.y, firstFaceRect.width, firstFaceRect.height);

                        // Save the cropped face to a temporary file
                        File tempFile = createTempImageFile();
                        if (tempFile != null) {
                            saveBitmapToFile(croppedFace, tempFile);

                            // Load and display the cropped face from the temporary file
                            imageView.setImageBitmap(BitmapFactory.decodeFile(tempFile.getAbsolutePath()));
                        }
                    }
                }
            }
        });

        sendRequestButton = findViewById(R.id.sendRequestButton);

        // Set a click listener for the send request button
        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImageBitmap != null) {
                    File tempFile = createTempImageFile();
                    saveBitmapToFile(selectedImageBitmap, tempFile);
                    niksList.clear();
                    namasList.clear();
                    // Call the method to send the POST request and process the response
                    sendPostRequest(tempFile, 10);
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void sendPostRequest(final File imageFile, final int fetchLimit) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    // Modify the URL as needed
                    String url = "https://web01.facereco.net:8000/dummy-query";
                    OkHttpClient client = new OkHttpClient();

                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file", imageFile.getName(), RequestBody.create(MediaType.parse("image/*"), imageFile))
                            .addFormDataPart("fetch_limit", String.valueOf(fetchLimit)) // Include the fetch_limit parameter
                            .build();

                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (response.isSuccessful()) {
                            String responseData = response.body().string();
                            // Process the response as JSON
                            JSONArray jsonArray = new JSONArray(responseData);

                            // Process the top fetchLimit similar persons
                            for (int i = 0; i < fetchLimit && i < jsonArray.length(); i++) {
                                JSONObject person = jsonArray.getJSONObject(i);
                                JSONObject personDetail = person.getJSONObject("personDetail");

                                String nik = personDetail.getString("nik");
                                String nama = personDetail.getString("nama");

                                // Add the values to the arrays
                                niksList.add(nik);
                                namasList.add(nama);

                                // Use 'nik' and 'nama' as needed
                            }

                            return true; // Indicate success
                        } else {
                            // Handle the case where the HTTP request is not successful
                            // You can use a Handler to display an error message if needed
                            return false; // Indicate failure
                        }
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    return false; // Indicate failure
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    // Data retrieval and processing are complete
                    // Proceed to the next activity
                    Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                    String[] niks = niksList.toArray(new String[0]);
                    String[] namas = namasList.toArray(new String[0]);
                    intent.putExtra("niks", niks);
                    intent.putExtra("namas", namas);
                    startActivity(intent);
                } else {
                    // Handle the case where the network request or processing fails
                }
            }
        }.execute();
    }

    private File createTempImageFile() {
        try {
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (storageDir != null) {
                return File.createTempFile("cropped_face", ".jpg", storageDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveBitmapToFile(Bitmap bitmap, File file) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String getResourceFilePath(int resourceId, String fileName) {
        String filePath = getFilesDir() + File.separator + fileName;
        File file = new File(filePath);

        if (!file.exists()) {
            try {
                InputStream inputStream = getResources().openRawResource(resourceId);
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);

                FileOutputStream outputStream = new FileOutputStream(filePath);
                outputStream.write(buffer);
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null; // Error occurred while copying the file
            }
        }

        return filePath;
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                try {
                    // Convert the selected image URI to a Bitmap
                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

                    // Update your ImageView (replace 'imageView' with your actual ImageView's ID)
                    imageView.setImageBitmap(selectedImageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
