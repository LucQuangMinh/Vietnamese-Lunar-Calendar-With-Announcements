package com.example.lunarcalendar;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class NotificationReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        // Get event data from intent
        long eventId = intent.getLongExtra("event_id", 0);
        String eventName = intent.getStringExtra("event_name");
        String eventDescription = intent.getStringExtra("event_description");
        boolean isRecurring = intent.getBooleanExtra("is_recurring", false);
        int notificationYear = intent.getIntExtra("notification_year", 0);

        // Create notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "lunar_calendar_channel")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Lịch Âm Việt Nam")
                .setContentText(eventName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true);

        // Add description if available
        if (eventDescription != null && !eventDescription.isEmpty()) {
            builder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(eventDescription));
        }

        // Create intent to open the app when notification is clicked
        Intent openAppIntent = new Intent(context, MainActivity.class);
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 
                0, 
                openAppIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        builder.setContentIntent(pendingIntent);

        // Show notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        
        // Check runtime permission before showing notification
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) 
            == PackageManager.PERMISSION_GRANTED) {
            // Use a unique notification ID
            int notificationId = isRecurring ? (int) (eventId + notificationYear * 1000) : (int) eventId;
            notificationManager.notify(notificationId, builder.build());
        } else {
            // Handle case when permission is not granted
            android.util.Log.w("NotificationReceiver", "Notification permission not granted");
        }
    }
}