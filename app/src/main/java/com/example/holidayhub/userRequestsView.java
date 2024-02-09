package com.example.holidayhub;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class userRequestsView {

    String startDate;
    String endDate;
    String reason;
    String type;
    String status;
    Date submittedAt;

    public userRequestsView() {
        // Default constructor required for Firestore
    }

    public userRequestsView(String requestId, String startDate, String endDate, String reason, String type, String status, Date submittedAt, Date managerDecisionAt) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.type = type;
        this.status = status;
        this.submittedAt = submittedAt;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getReason() {
        return reason;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public Date getSubmittedAt() {
        return submittedAt;
    }

    // Updated method to parse string dates to Date objects
    public Date parseDateString(String dateString) {
        if (dateString != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            try {
                return dateFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace(); // Handle the parse exception as needed
            }
        }
        return null; // or handle it as needed
    }

    // Updated getters for startDate and endDate to use parseDateString
    public Date getParsedStartDate() {
        return parseDateString(startDate);
    }

    public Date getParsedEndDate() {
        return parseDateString(endDate);
    }
}
