package com.example.lunarcalendar;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class SpecialDayNotificationHelper {
    private Context context;
    private static final String CHANNEL_ID = "special_day_channel";
    private static final String CHANNEL_NAME = "Thông báo ngày đặc biệt";
    private static final String CHANNEL_DESCRIPTION = "Thông báo mùng một và rằm";
    private static final int NOTIFICATION_ID = 999;

    public SpecialDayNotificationHelper(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    /**
     * Create notification channel for Android O and above
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableLights(true);
            channel.enableVibration(false);
            channel.setSound(null, null);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Schedule daily check (Daily Planner) for 00:01 AM every day
     */
    public static void scheduleDailyPlanner(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        Intent intent = new Intent(context, SpecialDayNotificationReceiver.class);
        intent.setAction("DAILY_PLANNER");
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 
                1000, 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Schedule for 00:01 AM every day
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 1);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
        
        // Cần phải chạy Planner ngay bây giờ để hẹn giờ cho sự kiện trong ngày hôm nay!
        Intent runNowIntent = new Intent(context, SpecialDayNotificationReceiver.class);
        runNowIntent.setAction("DAILY_PLANNER");
        context.sendBroadcast(runNowIntent);
    }

    /**
     * Schedule a specific exact alarm for today
     */
    public static void scheduleSpecificAlarm(Context context, int hour, int minute, int requestCode, String title, String content) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        Intent intent = new Intent(context, AlarmNotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 
                requestCode, 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        Calendar alarmCal = Calendar.getInstance();
        alarmCal.set(Calendar.HOUR_OF_DAY, hour);
        alarmCal.set(Calendar.MINUTE, minute);
        alarmCal.set(Calendar.SECOND, 0);
        
        // Nếu giờ này đã qua trong ngày thì bỏ qua (hoặc bạn có thể cho phép nổ ngay cũng được)
        if (alarmCal.before(Calendar.getInstance())) {
            return;
        }

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmCal.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmCal.getTimeInMillis(), pendingIntent);
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmCal.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmCal.getTimeInMillis(), pendingIntent);
                }
            }
        }
    }

    /**
     * Get notification time from SharedPreferences
     */
    private int[] getNotificationTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        int hour = sharedPreferences.getInt("notification_hour", 18); // Default 18:00
        int minute = sharedPreferences.getInt("notification_minute", 0);
        return new int[]{hour, minute};
    }

    /**
     * Generic show notification method
     */
    public void showNotification(String title, String content, int id) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        
        if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) 
            == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(id, builder.build());
        }
    }

    /**
     * Dismiss special day notification
     */
    public void dismissNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(NOTIFICATION_ID);
    }
}