package com.mmking.todolist;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Yang on 12/6/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String contentText = intent.getStringExtra(context.getString(R.string.data_TITLE));

        Notification.Builder mBuilder =
                new Notification.Builder(context)
                .setSmallIcon(R.drawable.todo_icon)
                .setContentTitle("To Do List")
                .setContentText(contentText);

        Intent resultIntent = new Intent(context, ToDoManagerActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        // Sets an ID for the notification
        int mNotificationId = intent.getIntExtra(context.getString(R.string.data_POSITION), -1);

        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
