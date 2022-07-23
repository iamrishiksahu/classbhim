package com.sepl.classbhim.classes.models;

import java.util.List;

public class ResultDataModel {

    public String maxMarks, percentage, scoredMarks, studentName, testName;
    public Long correct, incorrect, unattempted;
    public List<String> attemptedData;

    public ResultDataModel(String maxMarks, String percentage, String scoredMarks, String studentName, String testName, Long correct, Long incorrect, Long unattempted, List<String> attemptedData) {
        this.maxMarks = maxMarks;
        this.percentage = percentage;
        this.scoredMarks = scoredMarks;
        this.studentName = studentName;
        this.testName = testName;
        this.correct = correct;
        this.incorrect = incorrect;
        this.unattempted = unattempted;
        this.attemptedData = attemptedData;
    }

    public String getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(String maxMarks) {
        this.maxMarks = maxMarks;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getScoredMarks() {
        return scoredMarks;
    }

    public void setScoredMarks(String scoredMarks) {
        this.scoredMarks = scoredMarks;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public Long getCorrect() {
        return correct;
    }

    public void setCorrect(Long correct) {
        this.correct = correct;
    }

    public Long getIncorrect() {
        return incorrect;
    }

    public void setIncorrect(Long incorrect) {
        this.incorrect = incorrect;
    }

    public Long getUnattempted() {
        return unattempted;
    }

    public void setUnattempted(Long unattempted) {
        this.unattempted = unattempted;
    }

    public List<String> getAttemptedData() {
        return attemptedData;
    }

    public void setAttemptedData(List<String> attemptedData) {
        this.attemptedData = attemptedData;
    }
}
