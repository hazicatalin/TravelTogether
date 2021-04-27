package com.example.traveltogether;

public class User {
    public String email, name, phoneNumber, description;
    public User(){

    }
    public User(String email, String name){
        this.email = email;
        this.name = name;

    }

    public String get_name(){ return name; }

    public String get_email(){ return email;}

}
