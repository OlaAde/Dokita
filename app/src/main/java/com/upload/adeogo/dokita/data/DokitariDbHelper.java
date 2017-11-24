package com.upload.adeogo.dokita.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.upload.adeogo.dokita.data.DokitariContract.DokitariEntry;
import com.upload.adeogo.dokita.data.DokitariContract.BodyMeasurementEntry;
import com.upload.adeogo.dokita.data.DokitariContract.VitalsEntry;

/**
 * Created by Adeogo on 4/27/2017.
 */

public class DokitariDbHelper extends SQLiteOpenHelper {
    // The name of the database
    private static final String DATABASE_NAME = "dokitariDb.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 1;
    public DokitariDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create tasks table (careful to follow SQL formatting rules)
        final String CREATE_TABLE_DOCTOR = "CREATE TABLE "  + DokitariEntry.TABLE_NAME + " (" +
                DokitariEntry._ID                + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DokitariEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                DokitariEntry.COLUMN_EMAIL    + " TEXT, " +
                DokitariEntry.COLUMN_REVIEWS + " TEXT, " +
                DokitariEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL, " +
                DokitariEntry.COLUMN_SPECIALITY + " TEXT NOT NULL, " +
                DokitariEntry.COLUMN_PHONE_NUMBER + " TEXT NOT NULL, " +
                DokitariEntry.COLUMN_CITY  + " TEXT NOT NULL, " +
                DokitariEntry.COLUMN_COUNTRY  + " TEXT NOT NULL, " +
                DokitariEntry.COLUMN_SEX + " INTEGER NOT NULL);";

        final String CREATE_TABLE_BODY_MEASUREMENT = "CREATE TABLE "  + BodyMeasurementEntry.TABLE_NAME + " (" +
                BodyMeasurementEntry._ID                + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BodyMeasurementEntry.COLUMN_USER_ID+ " TEXT NOT NULL, " +
                BodyMeasurementEntry.COLUMN_BODY_FAT_PERCENTAGE    + " INTEGER NOT NULL, " +
                BodyMeasurementEntry.COLUMN_BODY_MASS_INDEX + " INTEGER NOT NULL, " +
                BodyMeasurementEntry.COLUMN_HEIGHT + " INTEGER NOT NULL, " +
                BodyMeasurementEntry.COLUMN_LEAN_BODY_MASS + " INTEGER NOT NULL, " +
                BodyMeasurementEntry.COLUMN_WAIST_CIRCUMFERENCE + " INTEGER NOT NULL, " +
                BodyMeasurementEntry.COLUMN_WEIGHT + " INTEGER NOT NULL);";

        final String CREATE_TABLE_VITALS = "CREATE TABLE "  + VitalsEntry.TABLE_NAME + " (" +
                VitalsEntry._ID                + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VitalsEntry.COLUMN_USER_ID + " TEXT NOT NULL, " +
                VitalsEntry.COLUMN_BLOOD_PRESSURE    + " TEXT NOT NULL, " +
                VitalsEntry.COLUMN_BODY_TEMPERATURE + " TEXT NOT NULL, " +
                VitalsEntry.COLUMN_HEART_RATE + " TEXT NOT NULL, " +
                VitalsEntry.COLUMN_RESPIRATORY_RATE + " TEXT NOT NULL);";

        Log.v("Create_State,ent", CREATE_TABLE_DOCTOR);
        Log.v("Create_State,ent", CREATE_TABLE_BODY_MEASUREMENT);
        Log.v("Create_State,ent", CREATE_TABLE_VITALS);

        sqLiteDatabase.execSQL(CREATE_TABLE_DOCTOR);
        sqLiteDatabase.execSQL(CREATE_TABLE_BODY_MEASUREMENT);
        sqLiteDatabase.execSQL(CREATE_TABLE_VITALS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DokitariEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BodyMeasurementEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VitalsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
