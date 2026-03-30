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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SpecialDayDialog extends DialogFragment {
    
    private EditText etSpecialDayName;
    private EditText etSpecialDayNotes;
    private int day, month, year;
    private int specialDayId = -1;
    private String specialDayName = "";
    private String specialDayNotes = "";
    private OnSpecialDayActionListener listener;
    
    public interface OnSpecialDayActionListener {
        void onSpecialDayCreated(String name, String notes, int day, int month, int year);
        void onSpecialDayUpdated(int id, String newName, String newNotes);
        void onSpecialDayDeleted(int id);
    }
    
    public static SpecialDayDialog newInstance(int day, int month, int year) {
        return newInstance(day, month, year, -1, "", "");
    }
    
    public static SpecialDayDialog newInstance(int day, int month, int year, int specialDayId, String specialDayName, String specialDayNotes) {
        SpecialDayDialog dialog = new SpecialDayDialog();
        Bundle args = new Bundle();
        args.putInt("day", day);
        args.putInt("month", month);
        args.putInt("year", year);
        args.putInt("specialDayId", specialDayId);
        args.putString("specialDayName", specialDayName);
        args.putString("specialDayNotes", specialDayNotes);
        dialog.setArguments(args);
        return dialog;
    }
    
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnSpecialDayActionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnSpecialDayActionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            day = getArguments().getInt("day");
            month = getArguments().getInt("month");
            year = getArguments().getInt("year");
            specialDayId = getArguments().getInt("specialDayId", -1);
            specialDayName = getArguments().getString("specialDayName", "");
            specialDayNotes = getArguments().getString("specialDayNotes", "");
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_special_day, null);
        
        etSpecialDayName = view.findViewById(R.id.etSpecialDayName);
        etSpecialDayNotes = view.findViewById(R.id.etSpecialDayNotes);
        TextView tvDialogTitle = view.findViewById(R.id.tvDialogTitle);
        TextView tvDialogDate = view.findViewById(R.id.tvDialogDate);
        View btnDeleteSpecialDay = view.findViewById(R.id.btnDeleteSpecialDay);
        View btnCancelSpecialDay = view.findViewById(R.id.btnCancelSpecialDay);
        View btnSaveSpecialDay = view.findViewById(R.id.btnSaveSpecialDay);
        
        boolean isEditMode = (specialDayId != -1);
        tvDialogTitle.setText(isEditMode ? "Thông tin ngày đặc biệt" : "Tạo ngày đặc biệt");
        tvDialogDate.setText("Ngày: " + day + "/" + month + "/" + year);
        
        if (isEditMode) {
            etSpecialDayName.setText(specialDayName);
            etSpecialDayNotes.setText(specialDayNotes);
            btnDeleteSpecialDay.setVisibility(View.VISIBLE);
        }
        
        AlertDialog dialog = builder.setView(view).create();
        
        // Transparent background so card corner radius shows properly
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        if (isEditMode) {
            btnDeleteSpecialDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(requireContext())
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa ngày đặc biệt này không?")
                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int w) {
                                listener.onSpecialDayDeleted(specialDayId);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
                }
            });
        }
        
        btnCancelSpecialDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSaveSpecialDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etSpecialDayName.getText().toString().trim();
                String notes = etSpecialDayNotes.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    if (isEditMode) {
                        listener.onSpecialDayUpdated(specialDayId, name, notes);
                    } else {
                        listener.onSpecialDayCreated(name, notes, day, month, year);
                    }
                    dialog.dismiss();
                } else {
                    Toast.makeText(requireContext(), "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        return dialog;
    }
}