package com.upload.adeogo.dokita.models;

/**
 * Created by ademi on 12/22/17.
 */

public class KeyPair {

    String clientKey;
    String doctorKey;

    public KeyPair(String clientKey, String doctorKey) {
        this.clientKey = clientKey;
        this.doctorKey = doctorKey;
    }

    public KeyPair() {
    }

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getDoctorKey() {
        return doctorKey;
    }

    public void setDoctorKey(String doctorKey) {
        this.doctorKey = doctorKey;
    }
}
