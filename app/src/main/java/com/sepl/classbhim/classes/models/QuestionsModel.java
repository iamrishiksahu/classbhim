package com.sepl.classbhim.classes.models;

public class QuestionsModel {

    public String a, b, c, d, q;
    public Long  status, selectedResponse, correct;
    public Double negative, positive;
    public Boolean isSubjective;

    public QuestionsModel(){
        //required empty constructor
    }

    public QuestionsModel(String a, String b, String c, String d, Long correct, String q, Double negative, Double positive, Long status, Boolean isSubjective, Long selectedResponse) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.correct = correct;
        this.q = q;
        this.negative = negative;
        this.positive = positive;
        this.status = status;
        this.isSubjective = isSubjective;
        this.selectedResponse = selectedResponse;
    }

    public Long getSelectedResponse() {
        return selectedResponse;
    }

    public void setSelectedResponse(Long attemptedOption) {
        this.selectedResponse = attemptedOption;
    }

    public Boolean getSubjective() {
        return isSubjective;
    }

    public void setSubjective(Boolean subjective) {
        isSubjective = subjective;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public Long getCorrect() {
        return correct;
    }

    public void setCorrect(Long correct) {
        this.correct = correct;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public Double getNegative() {
        return negative;
    }

    public void setNegative(Double negative) {
        this.negative = negative;
    }

    public Double getPositive() {
        return positive;
    }

    public void setPositive(Double positive) {
        this.positive = positive;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

}
