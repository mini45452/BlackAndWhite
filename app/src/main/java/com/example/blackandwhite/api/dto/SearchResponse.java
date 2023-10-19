package com.example.blackandwhite.api.dto;

import com.example.blackandwhite.api.model.PersonDetail;
import com.example.blackandwhite.api.model.SimilarityAndPersonDetail;

import java.util.List;

public class SearchResponse {

    private float similarity;
    private PersonDetail personDetail;

    // Empty Constructor
    public SearchResponse() {
        // Initialize any default values for your fields here if needed
    }

    // Parameterized Constructor
    public SearchResponse(float similarity, PersonDetail personDetail) {
        this.similarity = similarity;
        this.personDetail = personDetail;
    }

    // Getter for similarity
    public float getSimilarity() {
        return similarity;
    }

    // Setter for similarity
    public void setSimilarity(float similarity) {
        this.similarity = similarity;
    }

    // Getter for personDetail
    public PersonDetail getPersonDetail() {
        return personDetail;
    }

    // Setter for personDetail
    public void setPersonDetail(PersonDetail personDetail) {
        this.personDetail = personDetail;
    }
}
