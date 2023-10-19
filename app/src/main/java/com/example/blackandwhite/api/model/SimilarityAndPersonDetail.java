package com.example.blackandwhite.api.model;

public class SimilarityAndPersonDetail {
    private float similarity;
    private PersonDetail personDetail;

    public SimilarityAndPersonDetail() {
        // Default constructor
    }

    public SimilarityAndPersonDetail(float similarity, PersonDetail personDetail) {
        this.similarity = similarity;
        this.personDetail = personDetail;
    }

    public float getSimilarity() {
        return similarity;
    }

    public void setSimilarity(float similarity) {
        this.similarity = similarity;
    }

    public PersonDetail getPersonDetail() {
        return personDetail;
    }

    public void setPersonDetail(PersonDetail personDetail) {
        this.personDetail = personDetail;
    }
}

