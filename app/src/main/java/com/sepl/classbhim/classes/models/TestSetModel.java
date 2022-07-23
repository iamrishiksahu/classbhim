package com.sepl.classbhim.classes.models;

public class TestSetModel {


    public String activeAt, createdAt, updatedAt, id, title, noEntry;
    public boolean resultOnSubmission, resultPublished;

    public TestSetModel(String activeAt, String createdAt, String updatedAt, String noEntry, String id, String title, boolean resultOnSubmission, boolean resultPublished) {
        this.activeAt = activeAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.id = id;
        this.title = title;
        this.resultOnSubmission = resultOnSubmission;
        this.resultPublished = resultPublished;
        this.noEntry =noEntry;
    }

    public String getNoEntry() {
        return noEntry;
    }

    public void setNoEntry(String noEntry) {
        this.noEntry = noEntry;
    }

    public String getActiveAt() {
        return activeAt;
    }

    public void setActiveAt(String activeAt) {
        this.activeAt = activeAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isResultOnSubmission() {
        return resultOnSubmission;
    }

    public void setResultOnSubmission(boolean resultOnSubmission) {
        this.resultOnSubmission = resultOnSubmission;
    }

    public boolean isResultPublished() {
        return resultPublished;
    }

    public void setResultPublished(boolean resultPublished) {
        this.resultPublished = resultPublished;
    }
}
