package com.upload.adeogo.dokita.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import static com.upload.adeogo.dokita.data.DokitariContract.DokitariEntry;
import static com.upload.adeogo.dokita.data.DokitariContract.BodyMeasurementEntry;
import static com.upload.adeogo.dokita.data.DokitariContract.VitalsEntry;


/**
 * Created by Adeogo on 4/27/2017.
 */

public class DokitariProvider extends android.content.ContentProvider {
    public static final int DOKITARI  = 100;
    public static final int DOKITARI_WITH_ID = 101;

    public static final int BODY_MEASUREMENT = 102;
    public static final int BODY_MEASUREMENT_WITH_ID = 103;

    public static final int VITALS = 104;
    public static final int VITALS_WITH_ID = 105;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
          All paths added to the UriMatcher have a corresponding int.
          For each kind of uri you may want to access, add the corresponding match with addURI.
          The two calls below add matches for the task directory and a single item by ID.
         */
        uriMatcher.addURI(DokitariContract.AUTHORITY, DokitariContract.PATH_DOKITARI, DOKITARI);
        uriMatcher.addURI(DokitariContract.AUTHORITY, DokitariContract.PATH_DOKITARI + "/#", DOKITARI_WITH_ID);

        uriMatcher.addURI(DokitariContract.AUTHORITY, DokitariContract.PATH_BODY_MEASUREMENT, BODY_MEASUREMENT);
        uriMatcher.addURI(DokitariContract.AUTHORITY, DokitariContract.PATH_BODY_MEASUREMENT + "/#", BODY_MEASUREMENT_WITH_ID);

        uriMatcher.addURI(DokitariContract.AUTHORITY, DokitariContract.PATH_VITALS, VITALS);
        uriMatcher.addURI(DokitariContract.AUTHORITY, DokitariContract.PATH_VITALS + "/#", VITALS_WITH_ID);
        return uriMatcher;
    }

    private DokitariDbHelper mDokitariDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDokitariDbHelper = new DokitariDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db =  mDokitariDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match){
            case DOKITARI:
                retCursor =  db.query(DokitariEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;

            case BODY_MEASUREMENT:
                retCursor =  db.query(BodyMeasurementEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;

            case VITALS:
                retCursor =  db.query(VitalsEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(),uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        // Get access to the task database (to write new data to)
        final SQLiteDatabase db = mDokitariDbHelper.getWritableDatabase();

        // Write URI matching code to identify the match for the tasks directory
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case DOKITARI:
                // Insert new values into the database
                // Inserting values into tasks table
                long id = db.insert(DokitariEntry.TABLE_NAME, null, contentValues);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(DokitariContract.DokitariEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            case BODY_MEASUREMENT:
                // Insert new values into the database
                // Inserting values into tasks table
                long id_0 = db.insert(BodyMeasurementEntry.TABLE_NAME, null, contentValues);
                if ( id_0 > 0 ) {
                    returnUri = ContentUris.withAppendedId(DokitariContract.BodyMeasurementEntry.CONTENT_URI, id_0);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            case VITALS:
                // Insert new values into the database
                // Inserting values into tasks table
                long id_1 = db.insert(VitalsEntry.TABLE_NAME, null, contentValues);
                if ( id_1 > 0 ) {
                    returnUri = ContentUris.withAppendedId(DokitariContract.VitalsEntry.CONTENT_URI, id_1);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // Set the value for the returnedUri and write the default case for unknown URI's
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

       /* Users of the delete method will expect the number of rows deleted to be returned. */
        int numRowsDeleted;

        /*
         * If we pass null as the selection to SQLiteDatabase#delete, our entire table will be
         * deleted. However, if we do pass null and delete all of the rows in the table, we won't
         * know how many rows were deleted. According to the documentation for SQLiteDatabase,
         * passing "1" for the selection will delete all rows and return the number of rows
         * deleted, which is what the caller of this method expects.
         */
        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {

            case DOKITARI:
                numRowsDeleted = mDokitariDbHelper.getWritableDatabase().delete(
                        DokitariContract.DokitariEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;


            case BODY_MEASUREMENT:
                numRowsDeleted = mDokitariDbHelper.getWritableDatabase().delete(
                        DokitariContract.BodyMeasurementEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;

            case VITALS:
                numRowsDeleted = mDokitariDbHelper.getWritableDatabase().delete(
                        DokitariContract.VitalsEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int numOfRowsChanged;
        switch (match) {

            case DOKITARI:
                numOfRowsChanged = mDokitariDbHelper.getWritableDatabase().update(DokitariContract.DokitariEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;

            case BODY_MEASUREMENT:
                numOfRowsChanged = mDokitariDbHelper.getWritableDatabase().update(DokitariContract.BodyMeasurementEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;

            case VITALS:
                numOfRowsChanged = mDokitariDbHelper.getWritableDatabase().update(DokitariContract.VitalsEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);

        }

        if(numOfRowsChanged !=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return numOfRowsChanged;

    }

    @Override
    public int bulkInsert(@NonNull Uri uri,  ContentValues[] values) {
        final SQLiteDatabase db = mDokitariDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {

            case DOKITARI:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(DokitariContract.DokitariEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;


            case BODY_MEASUREMENT:
                db.beginTransaction();
                int rowsInserted_0 = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(DokitariContract.BodyMeasurementEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted_0++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted_0 > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted_0;


            case VITALS:
                db.beginTransaction();
                int rowsInserted_1 = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(DokitariContract.VitalsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted_1++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted_1 > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted_1;

            default:
                return super.bulkInsert(uri, values);
        }
    }
}
