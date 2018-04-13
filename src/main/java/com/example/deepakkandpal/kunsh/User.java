package com.example.deepakkandpal.kunsh;

/**
 * Created by mahe on 06-Apr-18.
 */

public class User {

    String userid;
    String name;
    String email;
    String number;

    public User(){

    }

    public User(String userid, String name, String email, String number) {
        this.userid = userid;
        this.name = name;
        this.email = email;
        this.number = number;
    }

    public String getUserid() {
        return userid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getNumber() {
        return number;
    }
}




