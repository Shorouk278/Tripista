package com.iti.intake40.tripista.core.model;

public class UserModel {
    String id;
    String name;
    String phone;
    String passWord;
    String imageUrl;
    String email;

    public UserModel() {

    }

    public UserModel(String id, String email, String name, String phone, String passWord, String imageUrl) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.passWord = passWord;
        this.imageUrl = imageUrl;
    }

    public UserModel(String id, String name, String email, String phone, String passWord) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.passWord = passWord;
        this.email = email;
    }

    public UserModel(String id, String name, String imageUrl, String email) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
