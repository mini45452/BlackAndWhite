package com.example.blackandwhite.api.model;

public class PersonDetail {
    private String nik;
    private String nama;

    // Default constructor
    public PersonDetail() {
    }

    // Constructor
    public PersonDetail(String nik, String nama) {
        this.nik = nik;
        this.nama = nama;
    }

    // Getter and Setter for nik
    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    // Getter and Setter for nama
    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}
