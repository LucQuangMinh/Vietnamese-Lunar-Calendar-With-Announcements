package com.example.lunarcalendar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.example.lunarcalendar.SpecialDay;

public class CalendarAdapter extends BaseAdapter {
    private Context context;
    private List<CalendarCell> calendarCells;
    private LayoutInflater inflater;
    private DatabaseHelper dbHelper;

    public CalendarAdapter(Context context, List<CalendarCell> calendarCells) {
        this.context = context;
        this.calendarCells = calendarCells;
        this.inflater = LayoutInflater.from(context);
        this.dbHelper = new DatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return calendarCells.size();
    }

    @Override
    public Object getItem(int position) {
        return calendarCells.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.calendar_cell, parent, false);
            holder = new ViewHolder();
            holder.tvSolarDate = convertView.findViewById(R.id.tvSolarDate);
            holder.tvLunarDate = convertView.findViewById(R.id.tvLunarDate);
            holder.tvSpecialDayIndicator = convertView.findViewById(R.id.tvSpecialDayIndicator);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CalendarCell cell = calendarCells.get(position);

        // Set solar date
        if (cell.getSolarDay() > 0) {
            holder.tvSolarDate.setText(String.valueOf(cell.getSolarDay()));
            holder.tvSolarDate.setVisibility(View.VISIBLE);
        } else {
            holder.tvSolarDate.setVisibility(View.INVISIBLE);
        }

        // Set lunar date
        if (cell.getLunarDay() > 0) {
            holder.tvLunarDate.setText(cell.getLunarDay() + " / " + cell.getLunarMonth());
            holder.tvLunarDate.setVisibility(View.VISIBLE);
        } else {
            holder.tvLunarDate.setVisibility(View.INVISIBLE);
        }

        // Highlight current day
        if (cell.isCurrentDay()) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.current_day_bg));
            holder.tvSolarDate.setTextColor(Color.WHITE);
            holder.tvLunarDate.setTextColor(Color.WHITE);
        } else {
            convertView.setBackgroundColor(Color.WHITE);
            holder.tvSolarDate.setTextColor(Color.BLACK);
            holder.tvLunarDate.setTextColor(context.getResources().getColor(R.color.lunar_text));
        }

        // Dim dates not in current month
        if (!cell.isCurrentMonth()) {
            holder.tvSolarDate.setAlpha(0.3f);
            holder.tvLunarDate.setAlpha(0.3f);
        } else {
            holder.tvSolarDate.setAlpha(1.0f);
            holder.tvLunarDate.setAlpha(1.0f);
        }

        // Check if this day is a special day based on lunar date (for yearly recurring)
        int[] lunarDate = LunarCalendar.convertSolarToLunar(cell.getSolarDay(), 
                ((MainActivity) context).selectedMonth, 
                ((MainActivity) context).selectedYear);
        int lunarDay = lunarDate[0];
        int lunarMonth = lunarDate[1];
        boolean isLeapMonth = lunarDate[3] == 1;

        boolean hasSpecialDay = false;

        // Check for SpecialDay by lunar date
        SpecialDay specialDay = dbHelper.getSpecialDayByLunarDate(lunarDay, lunarMonth, isLeapMonth);
        if (specialDay != null) {
            hasSpecialDay = true;
        }

        // Check for yearly recurring events (lunar-based only)
        List<Event> yearlyEvents = dbHelper.getEventsByLunarDate(lunarDay, lunarMonth, 
                ((MainActivity) context).selectedYear, isLeapMonth);
        
        if (yearlyEvents != null && !yearlyEvents.isEmpty()) {
            hasSpecialDay = true;
        }

        if (hasSpecialDay) {
            holder.tvSpecialDayIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.tvSpecialDayIndicator.setVisibility(View.GONE);
        }

        // Handle click event
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cell.isCurrentMonth() && cell.getSolarDay() > 0) {
                    // Open special day dialog
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).showSpecialDayDialog(cell.getSolarDay(), 
                                ((MainActivity) context).selectedMonth, 
                                ((MainActivity) context).selectedYear);
                    }
                }
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView tvSolarDate;
        TextView tvLunarDate;
        TextView tvSpecialDayIndicator;
    }

    public void updateData(List<CalendarCell> newCalendarCells) {
        this.calendarCells = newCalendarCells;
        notifyDataSetChanged();
    }
}
