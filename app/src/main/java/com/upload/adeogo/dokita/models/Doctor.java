package com.upload.adeogo.dokita.models;

import java.util.List;

/**
 * Created by Adeogo on 10/23/2017.
 */

public class Doctor {
    private List<Review> Reviews;
    private String ImageUrl, Speciality, PhoneNumber, City, Country, Email, Name, Id, Status;
    private int Sex;

    public Doctor(){}

    public Doctor(List<Review> reviews, String imageUrl, String speciality, String phoneNumber, String city, String country, String email, String name, String id, String status, int sex) {
        Reviews = reviews;
        ImageUrl = imageUrl;
        Speciality = speciality;
        PhoneNumber = phoneNumber;
        City = city;
        Country = country;
        Email = email;
        Name = name;
        Id = id;
        Status = status;
        Sex = sex;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public void setSex(int sex) {
        Sex = sex;
    }

    public void setCity(String city) {
        City = city;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public void setSpeciality(String speciality) {
        Speciality = speciality;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setReviews(List<Review> reviews) {
        Reviews = reviews;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getId() {
        return Id;
    }

    public List<Review> getReviews() {
        return Reviews;
    }

    public String getEmail() {
        return Email;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public String getName() {
        return Name;
    }

    public String getSpeciality() {
        return Speciality;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public int getSex() {
        return Sex;
    }

    public String getCity() {
        return City;
    }

    public String getCountry() {
        return Country;
    }
}
