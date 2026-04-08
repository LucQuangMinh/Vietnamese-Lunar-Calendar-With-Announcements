package com.example.lunarcalendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class SpecialDayAdapter extends ArrayAdapter<SpecialDay> {
    
    public SpecialDayAdapter(Context context, List<SpecialDay> specialDays) {
        super(context, 0, specialDays);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_special_day, parent, false);
        }
        
        SpecialDay specialDay = getItem(position);
        
        TextView tvSpecialDayName = convertView.findViewById(R.id.tvSpecialDayName);
        TextView tvSpecialDayDate = convertView.findViewById(R.id.tvSpecialDayDate);
        TextView tvSpecialDayTime = convertView.findViewById(R.id.tvSpecialDayTime);
        
        tvSpecialDayName.setText(specialDay.getName());
        
        // Convert solar date to lunar date for display
        int[] lunarDate = LunarCalendar.convertSolarToLunar(specialDay.getDay(), specialDay.getMonth(), 2026);
        String lunarDateString = "Âm lịch: " + lunarDate[0] + "/" + lunarDate[1];
        if (lunarDate[3] == 1) {
            lunarDateString += " (Nhuận)";
        }
        
        tvSpecialDayDate.setText("Ngày: " + specialDay.getDay() + "/" + specialDay.getMonth() + "  •  " + lunarDateString);
        tvSpecialDayTime.setText("Báo thức lúc " + specialDay.getNotificationTime() + " ngày trước đó");
        
        return convertView;
    }
}