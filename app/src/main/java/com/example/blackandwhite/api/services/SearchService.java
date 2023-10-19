package com.example.blackandwhite.api.services;

import com.example.blackandwhite.api.dto.SearchResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface SearchService {
    @Multipart
    @POST("dummy-query")
    Call<List<SearchResponse>> performSearch(
            @Part MultipartBody.Part file,
            @Part("fetch_limit") RequestBody fetchLimit
    );
}
