package com.jinwoo.android.firebasefcm;

/**
 * Created by JINWOO on 2017-03-15.
 */

public class User {
    String id;
    String password;
    String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
