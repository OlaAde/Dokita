package com.upload.adeogo.dokita.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.upload.adeogo.dokita.R;
import com.upload.adeogo.dokita.activities.ProfileActivity;

/**
 * Created by ademi on 12/16/17.
 */

public class NotifyService extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        NotificationManager mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//        Notification notification = new Notification(R.drawable.icon_client, "Notify Alarm strart", System.currentTimeMillis());
//        Intent myIntent = new Intent(this , ProfileActivity.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, myIntent, 0);
//        notification.setLatestEventInfo(this, "Notify label", "Notify text", contentIntent);
//        mNM.notify(NOTIFICATION, notification);
    }
}
