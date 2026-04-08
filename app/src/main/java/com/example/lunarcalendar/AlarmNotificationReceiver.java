package com.example.lunarcalendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");
            int notificationId = 999;
            
            if (title != null && content != null) {
                SpecialDayNotificationHelper helper = new SpecialDayNotificationHelper(context);
                helper.showNotification(title, content, notificationId);
            }
        }
    }
}
