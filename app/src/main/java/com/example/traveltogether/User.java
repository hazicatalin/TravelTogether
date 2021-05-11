package com.example.traveltogether;

import android.util.Log;

public class User {
    public String email, name, phoneNumber, description;
    public User(){

    }
    public User(String email, String name){
        this.email = email;
        this.name = name;
        this.phoneNumber = "-";
        this.description = "-";

    }

    public User(String email, String name, String phoneNumber, String description){
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.description = description;
        Log.d("phone", phoneNumber);
    }

    public String get_name(){ return name; }

    public String get_email(){ return email;}

    public String get_description(){return description;}

    public String get_phoneNumber(){return phoneNumber;}

    public void set_description(String description){
        this.description = description;
    }

    public void set_phoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

}
