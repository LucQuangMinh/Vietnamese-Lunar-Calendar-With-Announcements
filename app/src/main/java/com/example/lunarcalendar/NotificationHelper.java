package com.example.lunarcalendar;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class NotificationHelper {
    private Context context;
    private static final String CHANNEL_ID = "lunar_calendar_channel";
    private static final String CHANNEL_NAME = "Lịch Âm Thông Báo";
    private static final String CHANNEL_DESCRIPTION = "Thông báo các ngày đặc biệt";

    public NotificationHelper(Context context) {
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
            channel.enableVibration(true);
            channel.enableLights(true);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Schedule notification for an event (6 hours before the event)
     */
    public void scheduleNotification(Event event) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        // Create intent for the notification
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("event_id", event.getId());
        intent.putExtra("event_name", event.getName());
        intent.putExtra("event_description", event.getDescription());
        
        // Use event ID as requestCode to ensure unique PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 
                (int) event.getId(), 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Calculate notification time (6 hours before event)
        Calendar notificationTime = Calendar.getInstance();
        notificationTime.set(Calendar.YEAR, event.getSolarYear());
        notificationTime.set(Calendar.MONTH, event.getSolarMonth() - 1); // Calendar.MONTH is 0-based
        notificationTime.set(Calendar.DAY_OF_MONTH, event.getSolarDay());
        notificationTime.set(Calendar.HOUR_OF_DAY, 0); // Start of day
        notificationTime.set(Calendar.MINUTE, 0);
        notificationTime.set(Calendar.SECOND, 0);
        
        // Subtract 6 hours
        notificationTime.add(Calendar.HOUR_OF_DAY, -6);

        // For recurring events (like birthdays), we need to handle differently
        if (event.isYearly()) {
            // For yearly recurring events, schedule for multiple years
            scheduleRecurringNotification(event);
        } else {
            // For one-time events, schedule for the specific year
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 
                            notificationTime.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, 
                            notificationTime.getTimeInMillis(), pendingIntent);
                }
            }
        }
    }

    /**
     * Cancel notification for an event
     */
    public void cancelNotification(long eventId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 
                (int) eventId, 
                intent, 
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    /**
     * Show immediate notification (for testing or manual trigger)
     */
    public void showNotification(Event event) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Lịch Âm Việt Nam")
                .setContentText(event.getName())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(event.getDescription()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        
        // Check runtime permission before showing notification
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) 
            == PackageManager.PERMISSION_GRANTED) {
            // Use event ID as notification ID to ensure uniqueness
            notificationManager.notify((int) event.getId(), builder.build());
        } else {
            // Handle case when permission is not granted
            android.util.Log.w("NotificationHelper", "Notification permission not granted");
        }
    }

    /**
     * Schedule yearly recurring notifications for events like birthdays
     */
    public void scheduleRecurringNotification(Event event) {
        // For recurring events, we need to schedule notifications for multiple years
        // This is a simplified version - in production, you might want to handle this differently
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        for (int year = event.getSolarYear(); year <= 2100; year++) {
            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("event_id", event.getId());
            intent.putExtra("event_name", event.getName());
            intent.putExtra("event_description", event.getDescription());
            intent.putExtra("is_recurring", true);
            intent.putExtra("notification_year", year);
            
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, 
                    (int) (event.getId() + year * 1000), // Unique request code for each year
                    intent, 
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            Calendar notificationTime = Calendar.getInstance();
            notificationTime.set(Calendar.YEAR, year);
            notificationTime.set(Calendar.MONTH, event.getSolarMonth() - 1);
            notificationTime.set(Calendar.DAY_OF_MONTH, event.getSolarDay());
            notificationTime.set(Calendar.HOUR_OF_DAY, 0);
            notificationTime.set(Calendar.MINUTE, 0);
            notificationTime.set(Calendar.SECOND, 0);
            notificationTime.add(Calendar.HOUR_OF_DAY, -6);

            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 
                            notificationTime.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, 
                            notificationTime.getTimeInMillis(), pendingIntent);
                }
            }
        }
    }
}