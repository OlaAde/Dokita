package com.work.adeogo.dokita.models;

/**
 * Created by Adeogo on 10/22/2017.
 */

public class BodyMeasurement {
    private String UserId;
    private int BodyFatPercentage;
    private int BodyMassIndex;
    private int Height;
    private int LeanBodyMass;
    private int WaistCircumference;
    private int Weight;

    public BodyMeasurement(String UserId, int BodyFatPercentage, int BodyMassIndex,
                           int Height, int LeanBodyMass, int WaistCircumference, int Weight){
        this.UserId = UserId;
        this.BodyFatPercentage = BodyFatPercentage;
        this.BodyMassIndex = BodyMassIndex;
        this.Height = Height;
        this.LeanBodyMass = LeanBodyMass;
        this.WaistCircumference = WaistCircumference;
        this.Weight = Weight;
    }

    public BodyMeasurement(){}

    public int getBodyFatPercentage() {
        return BodyFatPercentage;
    }

    public int getBodyMassIndex() {
        return BodyMassIndex;
    }

    public String getUserId() {
        return UserId;
    }

    public int getHeight() {
        return Height;
    }

    public int getLeanBodyMass() {
        return LeanBodyMass;
    }

    public int getWaistCircumference() {
        return WaistCircumference;
    }

    public int getWeight() {
        return Weight;
    }

    public void setBodyFatPercentage(int bodyFatPercentage) {
        BodyFatPercentage = bodyFatPercentage;
    }

    public void setBodyMassIndex(int bodyMassIndex) {
        BodyMassIndex = bodyMassIndex;
    }

    public void setHeight(int height) {
        Height = height;
    }

    public void setLeanBodyMass(int leanBodyMass) {
        LeanBodyMass = leanBodyMass;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public void setWaistCircumference(int waistCircumference) {
        WaistCircumference = waistCircumference;
    }

    public void setWeight(int weight) {
        Weight = weight;
    }
}
