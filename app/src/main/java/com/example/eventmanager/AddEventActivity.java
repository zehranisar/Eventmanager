package com.example.eventmanager;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventmanager.api.ApiConfig;
import com.example.eventmanager.api.ApiService;
import com.example.eventmanager.api.request.CreateEventRequest;
import com.example.eventmanager.api.response.EventDetailResponse;
import com.example.eventmanager.api.response.UserData;
import com.example.eventmanager.utils.SessionManager;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEventActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etDate, etTime, etLocation, etCategory;
    private Button btnAddEvent;
    private ApiService apiService;
    private SessionManager sessionManager;
    private UserData currentUser;
    private ProgressDialog progressDialog;
    
    // Update mode
    private boolean isUpdateMode = false;
    private int eventId;

    // Category options
    private String[] categories = {"academic", "cultural", "sports", "workshop", "seminar", "other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // Initialize API and session
        apiService = ApiConfig.getApiService(this);
        sessionManager = new SessionManager(this);
        currentUser = sessionManager.getCurrentUser();

        // Check if user is admin
        if (currentUser == null || !currentUser.isAdmin()) {
            Toast.makeText(this, "Only admins can add events", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Check if this is update mode
        eventId = getIntent().getIntExtra("event_id", -1);
        isUpdateMode = eventId != -1;

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(isUpdateMode ? "Updating event..." : "Creating event...");
        progressDialog.setCancelable(false);

        initViews();
        setupDateTimePickers();
        setupCategoryDropdown();
        
        // Load event data if in update mode
        if (isUpdateMode) {
            loadEventData();
        }
        
        setupButton();
    }

    private void initViews() {
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etLocation = findViewById(R.id.etLocation);
        etCategory = findViewById(R.id.etCategory);
        btnAddEvent = findViewById(R.id.btnAddEvent);
    }

    private void setupDateTimePickers() {
        // Date picker
        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        // Format: YYYY-MM-DD
                        String date = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        etDate.setText(date);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            // Set minimum date to today
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        // Time picker
        etTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    (view, hourOfDay, minute) -> {
                        // Format: HH:MM
                        String time = String.format(Locale.US, "%02d:%02d", hourOfDay, minute);
                        etTime.setText(time);
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
            );
            timePickerDialog.show();
        });

        // Make date and time fields non-editable by keyboard
        etDate.setFocusable(false);
        etTime.setFocusable(false);
    }

    private void setupCategoryDropdown() {
        // If using AutoCompleteTextView for category
        if (etCategory instanceof AutoCompleteTextView) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    categories
            );
            ((AutoCompleteTextView) etCategory).setAdapter(adapter);
        }
    }

    private void loadEventData() {
        // Load event data from intent extras
        String title = getIntent().getStringExtra("event_title");
        String description = getIntent().getStringExtra("event_description");
        String date = getIntent().getStringExtra("event_date");
        String time = getIntent().getStringExtra("event_time");
        String location = getIntent().getStringExtra("event_location");
        String category = getIntent().getStringExtra("event_category");
        
        if (title != null) etTitle.setText(title);
        if (description != null) etDescription.setText(description);
        if (date != null) etDate.setText(date);
        if (time != null) etTime.setText(time);
        if (location != null) etLocation.setText(location);
        if (category != null) etCategory.setText(category);
        
        // Update button text
        btnAddEvent.setText("Update Event");
    }

    private void setupButton() {
        btnAddEvent.setOnClickListener(v -> {
            if (isUpdateMode) {
                updateEvent();
            } else {
                addEvent();
            }
        });
    }

    private void addEvent() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String category = etCategory.getText().toString().trim().toLowerCase();

        // Validation
        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Title is required");
            etTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            etDescription.setError("Description is required");
            etDescription.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(date)) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(time)) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(location)) {
            etLocation.setError("Location is required");
            etLocation.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(category)) {
            etCategory.setError("Category is required");
            etCategory.requestFocus();
            return;
        }

        // Validate category
        boolean validCategory = false;
        for (String cat : categories) {
            if (cat.equalsIgnoreCase(category)) {
                validCategory = true;
                category = cat; // Use lowercase version
                break;
            }
        }

        if (!validCategory) {
            Toast.makeText(this, "Invalid category. Use: academic, cultural, sports, workshop, seminar, or other", 
                Toast.LENGTH_LONG).show();
            return;
        }

        // Show progress dialog
        progressDialog.show();

        // Create request
        CreateEventRequest request = new CreateEventRequest(
                title, description, date, time, location, category, 100
        );

        // Make API call
        apiService.createEvent(request).enqueue(new Callback<EventDetailResponse>() {
            @Override
            public void onResponse(Call<EventDetailResponse> call, Response<EventDetailResponse> response) {
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    EventDetailResponse eventResponse = response.body();

                    if (eventResponse.isSuccess()) {
                        Toast.makeText(AddEventActivity.this, 
                            "Event created successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddEventActivity.this, 
                            eventResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Failed to create event";
                    if (response.code() == 403) {
                        errorMsg = "You don't have permission to create events";
                    }
                    Toast.makeText(AddEventActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EventDetailResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(AddEventActivity.this, 
                    "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEvent() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String category = etCategory.getText().toString().trim().toLowerCase();

        // Validation (same as addEvent)
        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Title is required");
            etTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            etDescription.setError("Description is required");
            etDescription.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(date)) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(time)) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(location)) {
            etLocation.setError("Location is required");
            etLocation.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(category)) {
            etCategory.setError("Category is required");
            etCategory.requestFocus();
            return;
        }

        // Validate category
        boolean validCategory = false;
        for (String cat : categories) {
            if (cat.equalsIgnoreCase(category)) {
                validCategory = true;
                category = cat; // Use lowercase version
                break;
            }
        }

        if (!validCategory) {
            Toast.makeText(this, "Invalid category. Use: academic, cultural, sports, workshop, seminar, or other", 
                Toast.LENGTH_LONG).show();
            return;
        }

        // Show progress dialog
        progressDialog.setMessage("Updating event...");
        progressDialog.show();

        // Create request
        CreateEventRequest request = new CreateEventRequest(
                title, description, date, time, location, category, 100
        );

        // Make API call
        apiService.updateEvent(eventId, request).enqueue(new Callback<EventDetailResponse>() {
            @Override
            public void onResponse(Call<EventDetailResponse> call, Response<EventDetailResponse> response) {
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    EventDetailResponse eventResponse = response.body();

                    if (eventResponse.isSuccess()) {
                        Toast.makeText(AddEventActivity.this, 
                            "Event updated successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddEventActivity.this, 
                            eventResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Failed to update event";
                    if (response.code() == 403) {
                        errorMsg = "You don't have permission to update events";
                    }
                    Toast.makeText(AddEventActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EventDetailResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(AddEventActivity.this, 
                    "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
