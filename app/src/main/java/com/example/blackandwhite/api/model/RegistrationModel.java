package com.example.blackandwhite.api.model;

import java.io.File;

public class RegistrationModel {
    private String nik;
    private String name;
    private String tempatLahir = "Beji";
    private String tanggalLahir = "2000-01-01";
    private String jenisKelamin = "Male";
    private String alamat = "Jl. Kabel Kusut No. 27";
    private File image;

    public RegistrationModel() {

    }

    public RegistrationModel(String nik, String name, String tempatLahir, String tanggalLahir, String jenisKelamin, String alamat, File image) {
        this.nik = nik;
        this.name = name;
        this.tempatLahir = tempatLahir;
        this.tanggalLahir = tanggalLahir;
        this.jenisKelamin = jenisKelamin;
        this.alamat = alamat;
        this.image = image;
    }

    // Getter methods
    public String getNik() {
        return nik;
    }

    public String getName() {
        return name;
    }

    public String getTempatLahir() {
        return tempatLahir;
    }

    public String getTanggalLahir() {
        return tanggalLahir;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public String getAlamat() {
        return alamat;
    }

    public File getImage() {
        return image;
    }

    // Setter methods
    public void setNik(String nik) {
        this.nik = nik;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTempatLahir(String tempatLahir) {
        this.tempatLahir = tempatLahir;
    }

    public void setTanggalLahir(String tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public void setImage(File image) {
        this.image = image;
    }
}
