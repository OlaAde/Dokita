package com.upload.adeogo.dokita.models;

/**
 * Created by Adeogo on 10/22/2017.
 */

public class Vitals {
    private String UserId;
    private int BloodPressure;
    private int BodyTemperature;
    private int HeartRate;
    private int RespiratoryRate;

    public Vitals(){}

    public Vitals(String UserId, int BloodPressure, int BodyTemperature, int HeartRate, int RespiratoryRate){
        this.UserId = UserId;
        this.BloodPressure = BloodPressure;
        this.BodyTemperature = BodyTemperature;
        this.HeartRate = HeartRate;
        this.RespiratoryRate = RespiratoryRate;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public void setBloodPressure(int bloodPressure) {
        BloodPressure = bloodPressure;
    }

    public void setBodyTemperature(int bodyTemperature) {
        BodyTemperature = bodyTemperature;
    }

    public void setHeartRate(int heartRate) {
        HeartRate = heartRate;
    }

    public void setRespiratoryRate(int respiratoryRate) {
        RespiratoryRate = respiratoryRate;
    }

    public String getUserId() {
        return UserId;
    }

    public int getBloodPressure() {
        return BloodPressure;
    }

    public int getBodyTemperature() {
        return BodyTemperature;
    }

    public int getHeartRate() {
        return HeartRate;
    }

    public int getRespiratoryRate() {
        return RespiratoryRate;
    }
}
