package com.example.blackandwhite;

import static com.example.blackandwhite.ImageUtils.detectFaces;
import static com.example.blackandwhite.ImageUtils.drawFacesWithBoundingBoxes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.blackandwhite.api.client.ApiClient;
import com.example.blackandwhite.api.dto.SearchRequest;
import com.example.blackandwhite.api.dto.SearchResponse;
import com.example.blackandwhite.api.model.SimilarityAndPersonDetail;
import com.example.blackandwhite.api.services.SearchService;
import com.example.blackandwhite.api.model.SearchModel;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchActivity extends AppCompatActivity {

    private Button selectImageButton;
    private Button accessResourceButton;
    private Button sendRequestButton;
    private Bitmap selectedImageBitmap;

    private List<String> niksList = new ArrayList<>();
    private List<String> namasList = new ArrayList<>();

    private ImageView imageView;
    private TextView backButton;
    private TextView activityInfo;

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
                } else {
                    Toast.makeText(getApplicationContext(), "Please select image", Toast.LENGTH_SHORT).show();
                }
            }
        });
        backButton = findViewById(R.id.backButton);
        // Find the TextView for activity information
        activityInfo = findViewById(R.id.activityInfo);

        // Set the initial activity information text
        activityInfo.setText("Search Page");

        // Set an OnClickListener for the "backButton" TextView
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // This simulates the default back button behavior
            }
        });
    }

    private void sendPostRequest(final File imageFile, final int fetchLimit) {
        Retrofit retrofit = ApiClient.getRetrofitInstance();

        SearchService searchService = retrofit.create(SearchService.class);

        SearchModel searchModel = new SearchModel(imageFile, fetchLimit);
        SearchRequest searchRequest = new SearchRequest(searchModel);

        Call<List<SearchResponse>> call = searchService.performSearch(searchRequest.getFilePart(), searchRequest.getFetchLimit());

        call.enqueue(new Callback<List<SearchResponse>>() {
            @Override
            public void onResponse(Call<List<SearchResponse>> call, Response<List<SearchResponse>> response) {
                if (response.isSuccessful()) {
                    List<SearchResponse> searchResponse = response.body();

                    // Extract the data you want to pass to SearchResultActivity
                    List<String> niks = new ArrayList<>();
                    List<String> namas = new ArrayList<>();
                    for (SearchResponse item : searchResponse) {
                        // Extract nik and nama from searchResponse and add them to the lists
                        niks.add(item.getPersonDetail().getNik());
                        namas.add(item.getPersonDetail().getNama());
                    }

                    // Save the Bitmap to a temporary file
                    try {
                        Bitmap imageViewBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                        File imageFile = new File(getCacheDir(), "image.jpg");
                        FileOutputStream out = new FileOutputStream(imageFile);
                        imageViewBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out); // Adjust quality as needed
                        out.close();

                        // Create an Intent to start SearchResultActivity
                        Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);

                        // Pass the data as extras to the intent;
                        Uri imageUri = Uri.fromFile(imageFile);
                        intent.putExtra("imageUri", imageUri.toString());
                        intent.putStringArrayListExtra("niks", (ArrayList<String>) niks);
                        intent.putStringArrayListExtra("namas", (ArrayList<String>) namas);

                        // Start SearchResultActivity
                        startActivity(intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle the error response
                    try {
                        if (response.errorBody() != null) {
                            String errorResponseJson = response.errorBody().string();

                        }
                    } catch (IOException e) {

                    }
                }
            }

            @Override
            public void onFailure(Call<List<SearchResponse>> call, Throwable t) {
                int deasygg = 4;
            }
        });

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
