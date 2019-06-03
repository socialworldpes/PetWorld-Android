package com.petworld_madebysocialworld;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.Random;

class PushNotification {
    public void addNotification(Activity activity, String title, String text, int icon, Context context, String typeActivity, String id) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity, "1");
        mBuilder.setSmallIcon(icon);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(text);

        Intent resultIntent = null;
        TaskStackBuilder stackBuilder = null;
        switch (typeActivity){
            case "pendingFriend":
                resultIntent = new Intent(activity, FriendsActivity.class);
                stackBuilder = TaskStackBuilder.create(activity);
                stackBuilder.addParentStack(FriendsActivity.class);
                break;
            case "pendingMeeting":
                resultIntent = new Intent(activity, ViewMeetingActivity.class);
                stackBuilder = TaskStackBuilder.create(activity);
                stackBuilder.addParentStack(ViewMeetingActivity.class);
                resultIntent.putExtra("id", id);
                break;
        }

// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

// notificationID allows you to update the notification later on.
        Random idRandom = new Random();
        int idNotification = idRandom.nextInt(9999 - 1000) + 1000;
        mNotificationManager.notify(idNotification, mBuilder.build());

    }
}
