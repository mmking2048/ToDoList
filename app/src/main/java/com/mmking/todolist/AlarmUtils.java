package com.mmking.todolist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Date;

/**
 * Created by Yang on 12/8/2015.
 */
public class AlarmUtils {

    public static int millisInMin = 60 * 1000;

    public static void setAlarm(Context context, int position, Date date, String title){

//        long alarmMillis = date.getTime() - System.currentTimeMillis();
//
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(context, AlarmReceiver.class);
//        intent.putExtra(context.getString(R.string.data_TITLE), title);
//        intent.putExtra(context.getString(R.string.data_POSITION), position);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, position, intent, 0);
//
//        alarmManager.set(AlarmManager.RTC, alarmMillis, pendingIntent);
//
//        Toast.makeText(context, "Alarm set for " + alarmMillis / millisInMin + " minutes from now",
//                Toast.LENGTH_SHORT).show();
    }

    public static void cancelAlarm(Context context, int position){

//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(context, AlarmReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, position, intent, 0);
//
//        alarmManager.cancel(pendingIntent);
//
//        Toast.makeText(context, "Alarm cancelled", Toast.LENGTH_SHORT).show();
    }
}
