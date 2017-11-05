package com.work.adeogo.dokita.models;

/**
 * Created by Adeogo on 10/24/2017.
 */

public class Appointment {
    private String ClientId;
    private String Time;
    private int AcceptanceStatus;
    private int CompletionStatus;
    private int Year;
    private int Month;
    private int Day;
    private String Description;
    private String DoctorName;
    private String Location;

    public Appointment(){
    }

    public Appointment(String clientId, String time, int acceptanceStatus, int completionStatus, int year, int month,
                       int day, String description, String doctorName, String location ){
        ClientId = clientId;
        Time = time;
        AcceptanceStatus = acceptanceStatus;
        CompletionStatus = completionStatus;
        Year = year;
        Month = month;
        Day = day;
        Description = description;
        DoctorName = doctorName;
        Location = location;
    }

    public void setAcceptanceStatus(int acceptanceStatus) {
        AcceptanceStatus = acceptanceStatus;
    }

    public void setClientId(String clientId) {
        ClientId = clientId;
    }

    public void setCompletionStatus(int completionStatus) {
        CompletionStatus = completionStatus;
    }

    public void setDay(int day) {
        Day = day;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setMonth(int month) {
        Month = month;
    }

    public void setDoctorName(String doctorName) {
        DoctorName = doctorName;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public void setTime(String time) {
        Time = time;
    }

    public void setYear(int year) {
        Year = year;
    }

    public int getAcceptanceStatus() {
        return AcceptanceStatus;
    }

    public int getCompletionStatus() {
        return CompletionStatus;
    }

    public int getDay() {
        return Day;
    }

    public int getMonth() {
        return Month;
    }

    public String getTime() {
        return Time;
    }

    public int getYear() {
        return Year;
    }

    public String getDoctorName() {
        return DoctorName;
    }

    public String getLocation() {
        return Location;
    }

    public String getClientId() {
        return ClientId;
    }

    public String getDescription() {
        return Description;
    }
}
