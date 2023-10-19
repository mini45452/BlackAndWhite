package com.example.blackandwhite.api.dto;

public class RegistrationResponseError {
    private int status;
    private String message;
    private String timeStamp;

    // Constructors
    public RegistrationResponseError() {
        // Default constructor
    }

    public RegistrationResponseError(int status, String message, String timeStamp) {
        this.status = status;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    // Getters
    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    // Setters
    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}

