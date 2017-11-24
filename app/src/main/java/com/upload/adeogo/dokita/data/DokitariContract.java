package com.upload.adeogo.dokita.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Adeogo on 4/26/2017.
 */

public class DokitariContract {

    private DokitariContract(){
    }
    public static final String AUTHORITY = "com.upload.adeogo.dokita";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_DOKITARI = "dokitari";
    public static final String PATH_BODY_MEASUREMENT = "body_measurement";
    public static final String PATH_VITALS = "vitals";


    public static final class DokitariEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DOKITARI).build();
        public static final String TABLE_NAME = "dokitari";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_REVIEWS = "reviews";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_SPECIALITY = "speciality";
        public static final String COLUMN_PHONE_NUMBER = "phone_number";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_COUNTRY = "country";
        public static final String COLUMN_SEX = "sex";
    }

    public static final class BodyMeasurementEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BODY_MEASUREMENT).build();
        public static final String TABLE_NAME = "body_measurement";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_BODY_FAT_PERCENTAGE = "body_fat_percentage";
        public static final String COLUMN_BODY_MASS_INDEX = "body_mass_index";
        public static final String COLUMN_HEIGHT = "height";
        public static final String COLUMN_LEAN_BODY_MASS = "lean_body_mass";
        public static final String COLUMN_WAIST_CIRCUMFERENCE = "waist_circumference";
        public static final String COLUMN_WEIGHT = "weight";
    }

    public static final class VitalsEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VITALS).build();
        public static final String TABLE_NAME = "vitals";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_BLOOD_PRESSURE = "blood_pressure";
        public static final String COLUMN_BODY_TEMPERATURE = "body_temperature";
        public static final String COLUMN_HEART_RATE = "heart_rate";
        public static final String COLUMN_RESPIRATORY_RATE = "respiratory_rate";
    }
}


