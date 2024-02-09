package com.example.holidayhub;

import java.util.Date;

public class PendingRequests {

     String userId;
     String firstName;
     String role;
     String startDate;
     String endDate;
     String reason;
     Date submittedAt; // Use Date for timestamp
     String type;
     String holidayRequestsId;

    // Required empty constructor for FireStore
    public PendingRequests() {

    }

    public PendingRequests(String userId, String firstName, String role, String startDate, String endDate, String reason, Date submittedAt, String type,String holidayRequestsId) {
        this.userId = userId;
        this.firstName = firstName;
        this.role = role;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.submittedAt = submittedAt;
        this.type = type;
        this.holidayRequestsId=holidayRequestsId;
    }
// Add getters and setters for each attribute

    public String getHolidayRequestsId() {
        return holidayRequestsId;
    }

    public void setHolidayRequestsId(String holidayRequestsId) {
        this.holidayRequestsId = holidayRequestsId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Date submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
