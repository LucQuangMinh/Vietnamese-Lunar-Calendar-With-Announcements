package com.example.lunarcalendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import java.util.Calendar;
import java.util.List;

public class SpecialDayNotificationReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("DISMISS".equals(intent.getAction())) {
            SpecialDayNotificationHelper helper = new SpecialDayNotificationHelper(context);
            helper.dismissNotification();
            return;
        }

        if ("DAILY_PLANNER".equals(intent.getAction())) {
            // Lấy Global preference time cho Mùng 1 / Rằm
            SharedPreferences prefs = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
            int globalHour = prefs.getInt("notification_hour", 18);
            int globalMinute = prefs.getInt("notification_minute", 0);

            // Ngày mai theo Dương lịch
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_MONTH, 1);
            int solarDay = tomorrow.get(Calendar.DAY_OF_MONTH);
            int solarMonth = tomorrow.get(Calendar.MONTH) + 1; 
            int solarYear = tomorrow.get(Calendar.YEAR);

            // Chuyển ngày mai sang Âm lịch
            int[] lunarDate = LunarCalendar.convertSolarToLunar(solarDay, solarMonth, solarYear);
            int lunarDay = lunarDate[0];
            int lunarMonth = lunarDate[1];
            int lunarYear = lunarDate[2];
            boolean isLeapMonth = lunarDate[3] == 1;

            // Kiểm tra mùng 1, rằm
            if (lunarDay == 1) {
                String title = "Thông báo ngày Mùng Một";
                String content = "Mai là ngày mùng một tháng " + lunarMonth + " năm " + lunarYear;
                SpecialDayNotificationHelper.scheduleSpecificAlarm(context, globalHour, globalMinute, 9001, title, content);
            } else if (lunarDay == 15) {
                String title = "Thông báo ngày Rằm";
                String content = "Mai là ngày rằm tháng " + lunarMonth + " năm " + lunarYear;
                SpecialDayNotificationHelper.scheduleSpecificAlarm(context, globalHour, globalMinute, 9015, title, content);
            }

            // Kiểm tra các sự kiện thiết lập bởi user (SpecialDays)
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            List<SpecialDay> allSpecialDays = dbHelper.getAllSpecialDays();
            
            for (SpecialDay sd : allSpecialDays) {
                // Ánh xạ sang Âm lịch năm 2026 (Theo logic cũ của hệ thống)
                int[] sdLunar = LunarCalendar.convertSolarToLunar(sd.getDay(), sd.getMonth(), 2026);
                int sdLunarDay = sdLunar[0];
                int sdLunarMonth = sdLunar[1];
                boolean sdIsLeap = sdLunar[3] == 1;
                
                // Trùng ngày Âm lịch của ngày mai
                if (sdLunarDay == lunarDay && sdLunarMonth == lunarMonth && sdIsLeap == isLeapMonth) {
                    
                    String time = sd.getNotificationTime();
                    int sdHour = 18;
                    int sdMinute = 0;
                    if (time != null && time.contains(":")) {
                        try {
                            String[] parts = time.split(":");
                            sdHour = Integer.parseInt(parts[0]);
                            sdMinute = Integer.parseInt(parts[1]);
                        } catch (Exception ignored) {}
                    }
                    
                    String title = "Thông báo ngày đặc biệt";
                    String content = "Mai là ngày " + sd.getName();
                    SpecialDayNotificationHelper.scheduleSpecificAlarm(context, sdHour, sdMinute, sd.getId(), title, content);
                }
            }
        }
    }
}