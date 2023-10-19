package com.example.blackandwhite.api.model;

import java.io.File;

public class SearchModel {
    private File file;
    private int fetchLimit;

    // Constructor
    public SearchModel(File file, int fetchLimit) {
        this.file = file;
        this.fetchLimit = fetchLimit;
    }

    // Getter for file
    public File getFile() {
        return file;
    }

    // Setter for file
    public void setFile(File file) {
        this.file = file;
    }

    // Getter for fetchLimit
    public int getFetchLimit() {
        return fetchLimit;
    }

    // Setter for fetchLimit
    public void setFetchLimit(int fetchLimit) {
        this.fetchLimit = fetchLimit;
    }
}

