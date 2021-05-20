package com.example.traveltogether;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Post implements Serializable {
    public String destination, startLocation, description, creatorId, travelType, date;
    public Post(){

    }

    public Post(String destination, String startLocation, String description, String creatorId, String travelType, String date){
        this.destination = destination;
        this.startLocation = startLocation;
        this.description = description;
        this.creatorId = creatorId;
        this.travelType = travelType;
        this.date = date;
    }

    public String get_destination(){
        return destination;
    }

    public String get_start_location(){
        return startLocation;
    }

    public String get_description(){
        return description;
    }

    public String get_creator_id(){
        return creatorId;
    }

    public String get_travel_type(){
        return travelType;
    }

    public String get_date(){
        return date;
    }
}
