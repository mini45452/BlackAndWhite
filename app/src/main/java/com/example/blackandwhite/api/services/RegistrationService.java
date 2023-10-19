package com.example.blackandwhite.api.services;

import com.example.blackandwhite.api.dto.RegistrationRequest;
import com.example.blackandwhite.api.dto.RegistrationResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import okhttp3.MultipartBody;
import retrofit2.http.Part;

import java.io.File;

public interface RegistrationService {
    @Multipart
    @POST("dummy-reg")
    Call<RegistrationResponse> registerUser(@Part("nik") RequestBody nik,
                                            @Part("nama") RequestBody nama,
                                            @Part("tempatLahir") RequestBody tempatLahir,
                                            @Part("tanggalLahir") RequestBody tanggalLahir,
                                            @Part("jenisKelamin") RequestBody jenisKelamin,
                                            @Part("alamat") RequestBody alamat,
                                            @Part MultipartBody.Part image); // Use @Part with "file" as the key
}