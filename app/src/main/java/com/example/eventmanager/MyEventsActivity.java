package com.example.eventmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.api.ApiConfig;
import com.example.eventmanager.api.ApiService;
import com.example.eventmanager.api.response.EventData;
import com.example.eventmanager.api.response.MyRegistrationsResponse;
import com.example.eventmanager.api.response.RegistrationData;
import com.example.eventmanager.api.response.UserData;
import com.example.eventmanager.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyEventsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RegistrationAdapter adapter;
    private List<RegistrationData> myRegistrations = new ArrayList<>();
    private SessionManager sessionManager;
    private ApiService apiService;
    private UserData currentUser;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        // Initialize API and session
        apiService = ApiConfig.getApiService(this);
        sessionManager = new SessionManager(this);
        currentUser = sessionManager.getCurrentUser();

        if (currentUser == null) {
            finish();
            return;
        }

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading your events...");
        progressDialog.setCancelable(false);

        // Setup toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Registered Events");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RegistrationAdapter(myRegistrations);
        recyclerView.setAdapter(adapter);

        loadMyRegistrations();
    }

    private void loadMyRegistrations() {
        progressDialog.show();

        apiService.getMyRegistrations().enqueue(new Callback<MyRegistrationsResponse>() {
            @Override
            public void onResponse(Call<MyRegistrationsResponse> call, Response<MyRegistrationsResponse> response) {
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    MyRegistrationsResponse regResponse = response.body();

                    if (regResponse.isSuccess()) {
                        myRegistrations.clear();
                        if (regResponse.getRegistrations() != null) {
                            myRegistrations.addAll(regResponse.getRegistrations());
                        }
                        adapter.notifyDataSetChanged();

                        if (myRegistrations.isEmpty()) {
                            Toast.makeText(MyEventsActivity.this, 
                                "You haven't registered for any events yet", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MyEventsActivity.this, 
                            regResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MyEventsActivity.this, 
                        "Failed to load registrations", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MyRegistrationsResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MyEventsActivity.this, 
                    "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Hide my events menu item since we're already here
        MenuItem myEventsItem = menu.findItem(R.id.action_my_events);
        if (myEventsItem != null) {
            myEventsItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.action_logout) {
            sessionManager.logout();
            ApiConfig.clearTokens(this);
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MyEventsActivity.this, LoginActivity.class));
            finishAffinity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMyRegistrations();
    }

    // Inner adapter class for registrations
    private class RegistrationAdapter extends RecyclerView.Adapter<RegistrationAdapter.ViewHolder> {
        
        private List<RegistrationData> registrations;

        RegistrationAdapter(List<RegistrationData> registrations) {
            this.registrations = registrations;
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_event, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            RegistrationData reg = registrations.get(position);
            holder.tvTitle.setText(reg.getEventTitle() != null ? reg.getEventTitle() : "");
            holder.tvDate.setText(formatDate(reg.getEventDate()));
            holder.tvTime.setText(formatTime(reg.getEventTime()));
            holder.tvLocation.setText(reg.getEventLocation() != null ? reg.getEventLocation() : "");
            holder.tvCategory.setText("Registered ✓");
        }

        @Override
        public int getItemCount() {
            return registrations != null ? registrations.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDate, tvTime, tvLocation, tvCategory;

            ViewHolder(View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvDate = itemView.findViewById(R.id.tvDate);
                tvTime = itemView.findViewById(R.id.tvTime);
                tvLocation = itemView.findViewById(R.id.tvLocation);
                tvCategory = itemView.findViewById(R.id.tvCategory);
            }
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
    }
}
