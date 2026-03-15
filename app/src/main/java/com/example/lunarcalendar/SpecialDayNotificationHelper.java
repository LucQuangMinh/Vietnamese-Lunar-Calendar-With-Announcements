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
     * Schedule daily check for special days (mùng một and rằm)
     */
    public void scheduleDailyCheck(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        // Create intent for the notification
        Intent intent = new Intent(context, SpecialDayNotificationReceiver.class);
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 
                1000, 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Get notification time from SharedPreferences
        int[] notificationTime = getNotificationTime(context);
        int hour = notificationTime[0];
        int minute = notificationTime[1];

        // Schedule for the specified time every day (Vietnam timezone UTC+7)
        Calendar notificationCalendar = Calendar.getInstance();
        notificationCalendar.set(Calendar.HOUR_OF_DAY, hour);
        notificationCalendar.set(Calendar.MINUTE, minute);
        notificationCalendar.set(Calendar.SECOND, 0);

        // If it's already past the notification time today, schedule for tomorrow
        Calendar now = Calendar.getInstance();
        if (notificationCalendar.before(now)) {
            notificationCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12+ requires checking for exact alarm permission
                if (alarmManager.canScheduleExactAlarms()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 
                                notificationCalendar.getTimeInMillis(), pendingIntent);
                    } else {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, 
                                notificationCalendar.getTimeInMillis(), pendingIntent);
                    }
                } else {
                    // Fallback to non-exact alarm if permission not granted
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 
                            notificationCalendar.getTimeInMillis(), pendingIntent);
                }
            } else {
                // For Android versions below 12, use exact alarm
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 
                            notificationCalendar.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, 
                            notificationCalendar.getTimeInMillis(), pendingIntent);
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
     * Show special day notification
     */
    public void showSpecialDayNotification(int lunarDay, int lunarMonth, int lunarYear) {
        String title = "Thông báo ngày đặc biệt";
        String content;
        
        if (lunarDay == 1) {
            content = "Mai là ngày mùng một tháng " + lunarMonth + " năm " + lunarYear;
        } else if (lunarDay == 15) {
            content = "Mai là ngày rằm tháng " + lunarMonth + " năm " + lunarYear;
        } else {
            return; // Should not happen, but just in case
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true)
                .setOngoing(false)
                .setFullScreenIntent(null, true);

        // Add dismiss action
        Intent dismissIntent = new Intent(context, SpecialDayNotificationReceiver.class);
        dismissIntent.setAction("DISMISS");
        
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(
                context, 
                1001, 
                dismissIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        builder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "X", dismissPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        
        // Check runtime permission before showing notification
        if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) 
            == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    /**
     * Show special day notification with custom name
     */
    public void showSpecialDayNotification(String specialDayName) {
        String title = "Thông báo ngày đặc biệt";
        String content = "Mai là ngày " + specialDayName;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true)
                .setOngoing(false)
                .setFullScreenIntent(null, true);

        // Add dismiss action
        Intent dismissIntent = new Intent(context, SpecialDayNotificationReceiver.class);
        dismissIntent.setAction("DISMISS");
        
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(
                context, 
                1001, 
                dismissIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        builder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "X", dismissPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        
        // Check runtime permission before showing notification
        if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) 
            == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
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