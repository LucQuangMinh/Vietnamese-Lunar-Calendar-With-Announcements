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
    
    private OnDeleteClickListener listener;
    
    public interface OnDeleteClickListener {
        void onDeleteClick(SpecialDay specialDay);
    }
    
    public SpecialDayAdapter(Context context, List<SpecialDay> specialDays, OnDeleteClickListener listener) {
        super(context, 0, specialDays);
        this.listener = listener;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_special_day, parent, false);
        }
        
        SpecialDay specialDay = getItem(position);
        
        TextView tvSpecialDayName = convertView.findViewById(R.id.tvSpecialDayName);
        TextView tvSpecialDayDate = convertView.findViewById(R.id.tvSpecialDayDate);
        Button btnDelete = convertView.findViewById(R.id.btnDelete);
        
        tvSpecialDayName.setText(specialDay.getName());
        
        // Convert solar date to lunar date for display
        int[] lunarDate = LunarCalendar.convertSolarToLunar(specialDay.getDay(), specialDay.getMonth(), 2026);
        String lunarDateString = "Ngày âm: " + lunarDate[0] + "/" + lunarDate[1];
        if (lunarDate[3] == 1) {
            lunarDateString += " (Nhảy)";
        }
        
        tvSpecialDayDate.setText("Ngày dương: " + specialDay.getDay() + "/" + specialDay.getMonth() + "\n" + lunarDateString);
        
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    new android.app.AlertDialog.Builder(getContext())
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa ngày đặc biệt này không?")
                        .setPositiveButton("Xóa", new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                listener.onDeleteClick(specialDay);
                            }
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
                }
            }
        });
        
        return convertView;
    }
}