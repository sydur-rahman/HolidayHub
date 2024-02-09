package com.example.holidayhub;

import android.content.Intent;
import android.health.connect.datatypes.units.Length;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class AdminHolidayRequestReview extends AppCompatActivity {

    Button btnBack;
    RecyclerView recyclerAdminPendingHolidayRequestView;
    PendingRequestsAdapter pendingRequestsAdapter;
    ArrayList<PendingRequests> pendingRequestsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_holiday_request_review);

        recyclerAdminPendingHolidayRequestView = findViewById(R.id.recyclerAdminPendingHolidayRequestView);
        btnBack = findViewById(R.id.btnBack);

        // Set up RecyclerView
        recyclerAdminPendingHolidayRequestView.setLayoutManager(new LinearLayoutManager(this));
        pendingRequestsList = new ArrayList<>();
        pendingRequestsAdapter = new PendingRequestsAdapter(this, pendingRequestsList); // Adjust parameters accordingly
        // Initialize the adapter
        recyclerAdminPendingHolidayRequestView.setAdapter(pendingRequestsAdapter);

        // Retrieve and display pending holiday requests
        retrievePendingRequests();


        // Back button click listener
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // ItemTouchHelper for swipe gestures
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerAdminPendingHolidayRequestView);

    }

    private void retrievePendingRequests() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collectionGroup("holidayRequests")
                .whereEqualTo("status", "pending")
                .orderBy("submittedAt", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("FireStore Error", error.getMessage());
                        return;
                    }

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            DocumentSnapshot document = dc.getDocument();

                            // Assuming "startDate", "endDate", "status", "submittedAt" are fields in your holidayRequests document
                            String holidayRequestsId = document.getId();
                            String startDate = document.getString("startDate");
                            String endDate = document.getString("endDate");
                            String status = document.getString("status");
                            Date submittedAt = document.getDate("submittedAt");
                            String type = document.getString("type");
                            String reason = document.getString("reason");


                            // Retrieve user details using the reference
                            DocumentReference userRef = document.getReference().getParent().getParent();
                            userRef.get().addOnSuccessListener(userDocument -> {
                                Log.d("User Document***", userDocument.getData().toString());
                                // Assuming "firstName", "role", "email" are fields in your user document
                                String userID=userDocument.getId();
                                Log.d("User ID***", userID);
                                String firstName = userDocument.getString("firstName");
                                String role = userDocument.getString("role");
                                //String email = userDocument.getString("email");

                                // Create a PendingRequests object and add it to the list
                                PendingRequests pendingRequest = dc.getDocument().toObject(PendingRequests.class);
                                pendingRequest.setUserId(userID);
                                pendingRequest.setHolidayRequestsId(holidayRequestsId);
                                pendingRequest.setFirstName(firstName);
                                pendingRequest.setRole(role);
                                pendingRequestsList.add(pendingRequest);

                                // Notify the adapter that the data set has changed
                                pendingRequestsAdapter.notifyDataSetChanged();
                            }).addOnFailureListener(e -> {
                                // Handle errors
                                Log.e("FireStore Error", "Error getting user document: ", e);
                            });
                        }
                    }
                });
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false; // We are not handling move events
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.LEFT:
                    // Swipe left to reject
                    Log.d("Holiday Document***", pendingRequestsAdapter.getHolidayRequestsID(position));
                    rejectRequest(position,pendingRequestsAdapter.getUserID(position),pendingRequestsAdapter.getHolidayRequestsID(position));
                    break;
                case ItemTouchHelper.RIGHT:
                    // Swipe right to accept
                    acceptRequest(position,pendingRequestsAdapter.getUserID(position), pendingRequestsAdapter.getHolidayRequestsID(position));
                    break;
            }
        }
    };


    private void acceptRequest(int position,String userId, String holidayRequestsId){


        DocumentReference holidayRequestRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("holidayRequests")
                .document(holidayRequestsId);

// Update the status field
        holidayRequestRef.update("status", "Accepted")
                .addOnSuccessListener(aVoid -> {
                    // Update successful
                    pendingRequestsList.remove(position);
                    pendingRequestsAdapter.notifyItemRemoved(position);
                    Log.d("Firestore Update", "Document updated successfully");
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Log.e("Firestore Error", "Error updating document", e);
                });



    }
    private void rejectRequest(int position, String userId,String holidayRequestsId){
        DocumentReference holidayRequestRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("holidayRequests")
                .document(holidayRequestsId);

// Update the status field
        holidayRequestRef.update("status", "Rejected")
                .addOnSuccessListener(aVoid -> {
                    // Update successful
                    pendingRequestsList.remove(position);
                    pendingRequestsAdapter.notifyItemRemoved(position);
                    Log.d("Firestore Update", "Document updated successfully");
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Log.e("Firestore Error", "Error updating document", e);
                });
    }

}
