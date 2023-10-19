package com.example.blackandwhite.api.dto;

import java.io.File;

import com.example.blackandwhite.api.model.SearchModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class SearchRequest {
    private MultipartBody.Part filePart;
    private RequestBody fetchLimit;

    public SearchRequest(SearchModel searchModel) {
        // Create MultipartBody.Part for the file
        RequestBody fileRequestBody = RequestBody.create(MediaType.parse("image/*"), searchModel.getFile());
        filePart = MultipartBody.Part.createFormData("file", searchModel.getFile().getName(), fileRequestBody);

        // Create RequestBody for the fetch_limit
        fetchLimit = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(searchModel.getFetchLimit()));
    }

    // Getter methods for filePart and fetchLimit

    public MultipartBody.Part getFilePart() {
        return filePart;
    }

    public RequestBody getFetchLimit() {
        return fetchLimit;
    }
}

