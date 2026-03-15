package com.example.lunarcalendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import com.example.lunarcalendar.SpecialDay;

public class SpecialDayNotificationReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        // Check if this is a dismiss action
        if ("DISMISS".equals(intent.getAction())) {
            SpecialDayNotificationHelper helper = new SpecialDayNotificationHelper(context);
            helper.dismissNotification();
            return;
        }

        // Get tomorrow's date
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        
        int solarDay = tomorrow.get(Calendar.DAY_OF_MONTH);
        int solarMonth = tomorrow.get(Calendar.MONTH) + 1; // Calendar.MONTH is 0-based
        int solarYear = tomorrow.get(Calendar.YEAR);

        // Convert to lunar date
        int[] lunarDate = LunarCalendar.convertSolarToLunar(solarDay, solarMonth, solarYear);
        int lunarDay = lunarDate[0];
        int lunarMonth = lunarDate[1];
        int lunarYear = lunarDate[2];

        // Check if tomorrow is mùng một (1) or rằm (15)
        if (lunarDay == 1 || lunarDay == 15) {
            SpecialDayNotificationHelper helper = new SpecialDayNotificationHelper(context);
            helper.showSpecialDayNotification(lunarDay, lunarMonth, lunarYear);
        }

        // Check for special days
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SpecialDay specialDay = dbHelper.getSpecialDayByDate(solarDay, solarMonth);
        
        if (specialDay != null) {
            SpecialDayNotificationHelper helper = new SpecialDayNotificationHelper(context);
            helper.showSpecialDayNotification(specialDay.getName());
        }
    }
}