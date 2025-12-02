package com.example.eventmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.R;
import com.example.eventmanager.api.response.EventData;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<EventData> events;
    private OnEventClickListener listener;

    public interface OnEventClickListener {
        void onEventClick(EventData event);
    }

    public EventAdapter(List<EventData> events, OnEventClickListener listener) {
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventData event = events.get(position);
        holder.tvTitle.setText(event.getTitle());
        holder.tvDate.setText(formatDate(event.getDate()));
        holder.tvTime.setText(formatTime(event.getTime()));
        holder.tvLocation.setText(event.getLocation());
        holder.tvCategory.setText(capitalizeFirst(event.getCategory()));

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEventClick(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
    }

    // Format date from "2025-12-15" to "Dec 15, 2025"
    private String formatDate(String date) {
        if (date == null || date.isEmpty()) return "";
        try {
            String[] parts = date.split("-");
            if (parts.length == 3) {
                String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                                   "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                int monthIndex = Integer.parseInt(parts[1]) - 1;
                return months[monthIndex] + " " + Integer.parseInt(parts[2]) + ", " + parts[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    // Format time from "14:00:00" to "2:00 PM"
    private String formatTime(String time) {
        if (time == null || time.isEmpty()) return "";
        try {
            String[] parts = time.split(":");
            if (parts.length >= 2) {
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);
                String amPm = hour >= 12 ? "PM" : "AM";
                if (hour > 12) hour -= 12;
                if (hour == 0) hour = 12;
                return hour + ":" + String.format("%02d", minute) + " " + amPm;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    // Capitalize first letter
    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    public void updateEvents(List<EventData> newEvents) {
        this.events = newEvents;
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTitle, tvDate, tvTime, tvLocation, tvCategory;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }
}
