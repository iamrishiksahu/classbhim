package com.sepl.classbhim.classes.models;

public class UserModel {

    public String name, email, phone, fcmToken;
    public Long role;

    public UserModel(String name, String email, String phone, String fcmToken, Long role) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.fcmToken = fcmToken;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public Long getRole() {
        return role;
    }

    public void setRole(Long role) {
        this.role = role;
    }
}
