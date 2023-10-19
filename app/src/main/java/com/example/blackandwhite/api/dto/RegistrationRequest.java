package com.example.blackandwhite.api.dto;

import com.example.blackandwhite.api.model.RegistrationModel;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.MultipartBody;
import java.io.File;

public class RegistrationRequest {
    private RequestBody nik;
    private RequestBody nama;
    private RequestBody tempatLahir;
    private RequestBody tanggalLahir;
    private RequestBody jenisKelamin;
    private RequestBody alamat;
    private MultipartBody.Part image;

    public RegistrationRequest(RegistrationModel model) {
        this.nik = RequestBody.create(MediaType.parse("text/plain"), model.getNik());
        this.nama = RequestBody.create(MediaType.parse("text/plain"), model.getName());
        this.tempatLahir = RequestBody.create(MediaType.parse("text/plain"), model.getTempatLahir());
        this.tanggalLahir = RequestBody.create(MediaType.parse("text/plain"), model.getTanggalLahir());
        this.jenisKelamin = RequestBody.create(MediaType.parse("text/plain"), model.getJenisKelamin());
        this.alamat = RequestBody.create(MediaType.parse("text/plain"), model.getAlamat());

        // Create MultipartBody.Part for the image
        RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), model.getImage());
        this.image = MultipartBody.Part.createFormData("file", model.getImage().getName(), imageBody);
    }

    public RequestBody getNik() {
        return nik;
    }

    public RequestBody getNama() {
        return nama;
    }

    public RequestBody getTempatLahir() {
        return tempatLahir;
    }

    public RequestBody getTanggalLahir() {
        return tanggalLahir;
    }

    public RequestBody getJenisKelamin() {
        return jenisKelamin;
    }

    public RequestBody getAlamat() {
        return alamat;
    }

    public MultipartBody.Part getImage() {
        return image;
    }
}
