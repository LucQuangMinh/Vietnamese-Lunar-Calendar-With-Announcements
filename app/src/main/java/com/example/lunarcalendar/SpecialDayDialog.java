package com.example.lunarcalendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SpecialDayDialog extends DialogFragment {
    
    private EditText etSpecialDayName;
    private int day, month, year;
    private OnSpecialDayCreatedListener listener;
    
    public interface OnSpecialDayCreatedListener {
        void onSpecialDayCreated(String name, int day, int month, int year);
    }
    
    public static SpecialDayDialog newInstance(int day, int month, int year) {
        SpecialDayDialog dialog = new SpecialDayDialog();
        Bundle args = new Bundle();
        args.putInt("day", day);
        args.putInt("month", month);
        args.putInt("year", year);
        dialog.setArguments(args);
        return dialog;
    }
    
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnSpecialDayCreatedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnSpecialDayCreatedListener");
        }
    }

    
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            day = getArguments().getInt("day");
            month = getArguments().getInt("month");
            year = getArguments().getInt("year");
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_special_day, null);
        
        etSpecialDayName = view.findViewById(R.id.etSpecialDayName);
        
        builder.setView(view)
                .setTitle("Tạo ngày đặc biệt")
                .setMessage("Ngày: " + day + "/" + month + "/" + year)
                .setPositiveButton("Tạo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = etSpecialDayName.getText().toString().trim();
                        if (!TextUtils.isEmpty(name)) {
                            listener.onSpecialDayCreated(name, day, month, year);
                        }
                    }
                })
                .setNegativeButton("Hủy", null);
        
        return builder.create();
    }

    public void onSpecialDayCreated(String name, int day, int month, int year) {
        // Convert solar date to lunar date for storage
        int[] lunarDate = LunarCalendar.convertSolarToLunar(day, month, year);
        int lunarDay = lunarDate[0];
        int lunarMonth = lunarDate[1];
        int lunarYear = lunarDate[2];
        boolean isLeapMonth = lunarDate[3] == 1;

        // Save as yearly recurring event (lunar-based only)
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        EventManager eventManager = new EventManager(getActivity());
        
        // Sửa: Lưu cả solar date và lunar date đầy đủ để có thể tìm kiếm theo cả hai cách
        Event event = new Event(day, month, year, lunarDay, lunarMonth, lunarYear,
                name, "", "Ngày đặc biệt", false, isLeapMonth);
        event.setYearly(true);
        
        long result = eventManager.addEvent(event);
        if (result > 0) {
            Toast.makeText(getActivity(), "Đã tạo ngày đặc biệt: " + name, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Tạo thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}