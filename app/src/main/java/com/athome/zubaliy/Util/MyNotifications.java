package com.athome.zubaliy.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.athome.zubaliy.mylifeontheroad.MainActivity_;
import com.athome.zubaliy.mylifeontheroad.R;

/**
 * Util to create notifications
 */
public class MyNotifications {


    public static Notification createNotificationInTheStatusBar(Context context) {
        // Prepare Notification onClick behaviour
        // Open MainActivity Class on Notification Click
        Intent intent = new Intent(context, MainActivity_.class);
        // 2 next lines are need to start or to bring to the front the
        // MainActivity.
        // Without them one extra MainActivity would be created.
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        // Open Activity
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create Notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                // Set Icon
                .setSmallIcon(R.drawable.icon_notification)
                // Set Ticker Message
                .setTicker(context.getString(R.string.notificationticker))
                // Set Title
                .setContentTitle(context.getString(R.string.notificationtitle))
                // Set Text
                .setContentText(context.getString(R.string.notificationtext))
                // Add an Action Button below Notification
                // .addAction(R.drawable.ic_launcher, "Action Button", pIntent)
                // Set PendingIntent into Notification
                .setContentIntent(pIntent).setProgress(0, 0, true).setOngoing(true)
                // .setDefaults(android.app.Notification.DEFAULT_ALL)
                // sound, vibrate, light
                .setLights(3, 500, 1000)
                // Dismiss Notification
                .setAutoCancel(false);

        // Create Notification Manager
//		NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        // ID = 0, if same ID, then refresh notification
//		notificationmanager.notify(0, builder.build());
        return builder.build();

    }

    public static void cancelAll(Context context) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
    }


}
