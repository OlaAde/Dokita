package com.upload.adeogo.dokita.models;

/**
 * Created by Adeogo on 11/5/2017.
 */

public class Time {
    private String Time;
    private int Picked;

    public Time(){}

    public Time(String time, int picked){
        Time = time;
        Picked = picked;
    }

    public int getPicked() {
        return Picked;
    }

    public String getTime() {
        return Time;
    }

    public void setPicked(int picked) {
        Picked = picked;
    }

    public void setTime(String time) {
        Time = time;
    }
}
