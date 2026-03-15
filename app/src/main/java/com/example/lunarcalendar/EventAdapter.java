package com.example.lunarcalendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    
    private Context context;
    private List<Event> eventList;
    private EventManager eventManager;
    private OnEventActionListener listener;

    public EventAdapter(Context context, List<Event> eventList, EventManager eventManager) {
        this.context = context;
        this.eventList = eventList;
        this.eventManager = eventManager;
    }

    public void setOnEventActionListener(OnEventActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        
        // Set event name
        holder.tvEventName.setText(event.getName());
        
        // Set event date
        String dateText = String.format("%02d/%02d/%d (%02d/%02d AL)", 
                event.getSolarDay(), event.getSolarMonth(), event.getSolarYear(),
                event.getLunarDay(), event.getLunarMonth());
        if (event.isLeapMonth()) {
            dateText += " (Nhuận)";
        }
        holder.tvEventDate.setText(dateText);
        
        // Set event description
        if (event.getDescription() != null && !event.getDescription().isEmpty()) {
            holder.tvEventDescription.setText(event.getDescription());
            holder.tvEventDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvEventDescription.setVisibility(View.GONE);
        }
        
        // Set event type
        holder.tvEventType.setText(event.getEventType());
        holder.tvEventType.setBackgroundColor(eventManager.getEventTypeColor(event.getEventType()));
        
        // Set notification icon visibility
        holder.ivNotification.setVisibility(event.isHasNotification() ? View.VISIBLE : View.GONE);
        
        // Set event type indicator color
        holder.viewEventType.setBackgroundColor(eventManager.getEventTypeColor(event.getEventType()));

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEventClick(event);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEventActivity.class);
            intent.putExtra("event_id", event.getId());
            ((EventListActivity) context).startActivityForResult(intent, 1);
        });

        holder.btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog(event));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    private void showDeleteConfirmationDialog(Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa sự kiện này không?");
        
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            eventManager.deleteEvent(event);
            eventList.remove(event);
            notifyDataSetChanged();
            Toast.makeText(context, "Đã xóa sự kiện", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName, tvEventDate, tvEventDescription, tvEventType;
        ImageView ivNotification;
        View viewEventType;
        ImageButton btnEdit, btnDelete;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);
            tvEventDescription = itemView.findViewById(R.id.tvEventDescription);
            tvEventType = itemView.findViewById(R.id.tvEventType);
            ivNotification = itemView.findViewById(R.id.ivNotification);
            viewEventType = itemView.findViewById(R.id.viewEventType);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public interface OnEventActionListener {
        void onEventClick(Event event);
    }
}