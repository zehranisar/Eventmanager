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

import com.example.eventmanager.adapters.EventAdapter;
import com.example.eventmanager.api.ApiConfig;
import com.example.eventmanager.api.ApiService;
import com.example.eventmanager.api.response.EventData;
import com.example.eventmanager.api.response.EventListResponse;
import com.example.eventmanager.api.response.UserData;
import com.example.eventmanager.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements EventAdapter.OnEventClickListener {

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<EventData> events = new ArrayList<>();
    private SessionManager sessionManager;
    private ApiService apiService;
    private Toolbar toolbar;
    private FloatingActionButton fabAddEvent;
    private UserData currentUser;
    private ProgressDialog progressDialog;
    private TextView tvTitle, tvSubtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize session manager and API service
        sessionManager = new SessionManager(this);
        apiService = ApiConfig.getApiService(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            sessionManager.logout();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading events...");
        progressDialog.setCancelable(false);

        // Setup toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            String title = currentUser.isAdmin() ? "Admin Dashboard" : "Student Dashboard";
            getSupportActionBar().setTitle(title);
        }

        // Initialize views
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        
        if (tvTitle != null) {
            tvTitle.setText(getString(R.string.upcoming_events));
        }
        if (tvSubtitle != null) {
            tvSubtitle.setText("Welcome, " + currentUser.getName() + "!");
        }

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter(events, this);
        recyclerView.setAdapter(adapter);

        // Setup FAB - Only show for admin users
        fabAddEvent = findViewById(R.id.fabAddEvent);
        if (currentUser.isAdmin()) {
            fabAddEvent.setVisibility(View.VISIBLE);
            fabAddEvent.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, AddEventActivity.class));
            });
        } else {
            fabAddEvent.setVisibility(View.GONE);
        }

        // Load events from backend
        loadEvents();
    }

    private void loadEvents() {
        progressDialog.show();

        apiService.getEvents().enqueue(new Callback<EventListResponse>() {
            @Override
            public void onResponse(Call<EventListResponse> call, Response<EventListResponse> response) {
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    EventListResponse eventListResponse = response.body();

                    if (eventListResponse.isSuccess()) {
                        events.clear();
                        if (eventListResponse.getEvents() != null) {
                            events.addAll(eventListResponse.getEvents());
                        }
                        adapter.notifyDataSetChanged();

                        if (events.isEmpty()) {
                            Toast.makeText(MainActivity.this, 
                                "No events available", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, 
                            eventListResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle 401 - Unauthorized (token expired)
                    if (response.code() == 401) {
                        Toast.makeText(MainActivity.this, 
                            "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
                        sessionManager.logout();
                        ApiConfig.clearTokens(MainActivity.this);
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, 
                            "Failed to load events", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<EventListResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, 
                    "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEventClick(EventData event) {
        Intent intent = new Intent(MainActivity.this, EventDetailActivity.class);
        intent.putExtra("event_id", event.getId());
        intent.putExtra("event_title", event.getTitle());
        intent.putExtra("event_description", event.getDescription());
        intent.putExtra("event_date", event.getDate());
        intent.putExtra("event_time", event.getTime());
        intent.putExtra("event_location", event.getLocation());
        intent.putExtra("event_category", event.getCategory());
        intent.putExtra("event_is_registered", event.isRegistered());
        intent.putExtra("event_has_reminder", event.hasReminder());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        
        // Show/hide menu items based on user role
        if (currentUser != null) {
            if (currentUser.isAdmin()) {
                // Show admin dashboard, hide "My Events"
                MenuItem adminDashboardItem = menu.findItem(R.id.action_admin_dashboard);
                if (adminDashboardItem != null) {
                    adminDashboardItem.setVisible(true);
                }
                MenuItem myEventsItem = menu.findItem(R.id.action_my_events);
                if (myEventsItem != null) {
                    myEventsItem.setVisible(false);
                }
            } else {
                // Hide admin dashboard, show "My Events"
                MenuItem adminDashboardItem = menu.findItem(R.id.action_admin_dashboard);
                if (adminDashboardItem != null) {
                    adminDashboardItem.setVisible(false);
                }
                MenuItem myEventsItem = menu.findItem(R.id.action_my_events);
                if (myEventsItem != null) {
                    myEventsItem.setVisible(true);
                }
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_admin_dashboard) {
            startActivity(new Intent(MainActivity.this, AdminDashboardActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.action_my_events) {
            startActivity(new Intent(MainActivity.this, MyEventsActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        sessionManager.logout();
        ApiConfig.clearTokens(this);
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finishAffinity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning from other activities
        if (sessionManager.isLoggedIn()) {
            loadEvents();
        }
    }
}
