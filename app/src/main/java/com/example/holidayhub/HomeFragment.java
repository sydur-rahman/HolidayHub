package com.example.holidayhub;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {
    String userId;
    // UI components
    TextView txtWelcome_UserName,txtHolidayMassage,txtApproveHolidays,txtPendingHolidays,txtRejectedHolidays;
    RecyclerView recycleUserUserHolidayRequestView;
    ArrayList<userRequestsView> userRequestsViewArrayList;

    userRequestsAdapter userRequestsAdapter;
    FirebaseFirestore db;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Retrieve user ID from arguments
        if (getArguments() != null) {
            userId = getArguments().getString("userId");



// Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_home, container, false);
            txtApproveHolidays=view.findViewById(R.id.txtApproveHolidays);
            txtPendingHolidays=view.findViewById(R.id.txtPendingHolidays);
            txtRejectedHolidays=view.findViewById(R.id.txtRejectedHolidays);


        // Initialize FireStore
         db= FirebaseFirestore.getInstance();

            txtWelcome_UserName = view.findViewById(R.id.txtWelcome_UserName);

            db.collection("users").document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error!=null){
                        Log.e("FireStore error: ", error.getMessage());
                        return;
                    }
                    if(value.exists()){
                        String userFirstName = value.getString("firstName");
                        txtWelcome_UserName.setText("Welcome, "+ userFirstName);
                    }
                }
            });


            //update UI with the approved days, Pending days and RemainingHolidays
            NumberOfApprovedHolidays();

            NumberOfPendingHolidays();

            NumberOfRejectedHolidays();

            txtHolidayMassage=view.findViewById(R.id.txtHolidayMassage);
            //HolidayRequests View
            recycleUserUserHolidayRequestView=view.findViewById(R.id.recycleUserUserHolidayRequestView);
            recycleUserUserHolidayRequestView.setHasFixedSize(true);

            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false);
            recycleUserUserHolidayRequestView.setLayoutManager(linearLayoutManager);

            userRequestsViewArrayList =new ArrayList<userRequestsView>();
            userRequestsAdapter= new userRequestsAdapter(requireContext(),userRequestsViewArrayList);

            ShowUserHolidayRequests();
            recycleUserUserHolidayRequestView.setAdapter(userRequestsAdapter);



            return view;
        } else {
            // Handle the case where no data is received
            Log.e("Fragment", "No arguments received.");
            return null; // or return a placeholder view
        }
    }


    // Update Number of Approved holidays
    private void NumberOfApprovedHolidays() {
        // Query for approved requests
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("users");
        DocumentReference userDocument = usersCollection.document(userId);

        // Check if the user document exists
        userDocument.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // The user document exists, now check for the "holidayRequests" collection
                        CollectionReference holidayRequestsCollection = userDocument.collection("holidayRequests");
                        Query approvedRequestsQuery = holidayRequestsCollection.whereEqualTo("status", "Accepted");

                        approvedRequestsQuery.get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    // Handle the results
                                    int approvedDays = 0;
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        String startDate = document.getString("startDate");
                                        String endDate = document.getString("endDate");

                                        int numberOfDays = calculateNumberOfDays(startDate, endDate);

                                        approvedDays += numberOfDays;
                                    }

                                    txtApproveHolidays.setText(String.valueOf(approvedDays));
                                })
                                .addOnFailureListener(e -> {
                                    // Handle errors
                                });
                    } else {
                        // The user document does not exist,
                        txtApproveHolidays.setText("0");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                });
    }


    // Update Number of Pending
    private void NumberOfPendingHolidays() {
        // Query for pending requests
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("users");
        DocumentReference userDocument = usersCollection.document(userId);

        // Check if the user document exists
        userDocument.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // The user document exists, now check for the "holidayRequests" subcollection
                        CollectionReference holidayRequestsCollection = userDocument.collection("holidayRequests");

                        holidayRequestsCollection.get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                        // The "holidayRequests" subcollection exists and is not empty
                                        Query pendingRequestsQuery = holidayRequestsCollection
                                                .whereEqualTo("status", "pending");

                                        pendingRequestsQuery.get()
                                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                                    // Handle the results
                                                    int pendingDays = 0;
                                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                        String startDate = document.getString("startDate");
                                                        String endDate = document.getString("endDate");

                                                        int numberOfDays = calculateNumberOfDays(startDate, endDate);

                                                        pendingDays += numberOfDays;
                                                    }

                                                    // Update the TextView with the calculated pending days
                                                    txtPendingHolidays.setText(String.valueOf(pendingDays));
                                                })
                                                .addOnFailureListener(e -> {
                                                    // Handle errors
                                                });
                                    } else {
                                        // The "holidayRequests" subcollection does not exist or is empty
                                        txtPendingHolidays.setText("0");
                                    }
                                });
                    } else {

                        txtPendingHolidays.setText("0");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                });
    }


    // Update  Number of Rejected
    private void NumberOfRejectedHolidays() {
        // Query for rejected requests
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("users");
        DocumentReference userDocument = usersCollection.document(userId);

        // Check if the user document exists
        userDocument.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // The user document exists, now check for the "holidayRequests" subcollection
                        CollectionReference holidayRequestsCollection = userDocument.collection("holidayRequests");

                        holidayRequestsCollection.get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                        // The "holidayRequests" subcollection exists and is not empty
                                        Query rejectedRequestsQuery = holidayRequestsCollection
                                                .whereEqualTo("status", "Rejected");

                                        rejectedRequestsQuery.get()
                                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                                    // Handle the results
                                                    int rejectedDays = 0;
                                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                        String startDate = document.getString("startDate");
                                                        String endDate = document.getString("endDate");

                                                        int numberOfDays = calculateNumberOfDays(startDate, endDate);

                                                        rejectedDays += numberOfDays;
                                                    }

                                                    // Update the TextView with the calculated rejected days
                                                    txtRejectedHolidays.setText(String.valueOf(rejectedDays));
                                                })
                                                .addOnFailureListener(e -> {
                                                    // Handle errors
                                                });
                                    } else {
                                        txtRejectedHolidays.setText("0");
                                    }
                                });
                    } else {
                        txtRejectedHolidays.setText("0");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                });
    }


    //Calculation Function
    private int calculateNumberOfDays(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            // Parse the start and end dates
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);

            // Calculate the difference in milliseconds
            long diffInMillies = Math.abs(end.getTime() - start.getTime());

            // Convert milliseconds to days
            long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            return (int) diffInDays;
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle parsing exceptions as needed
            return -1; // or any appropriate error value
        }
    }

    private void ShowUserHolidayRequests() {
// Clear the existing list to avoid duplicates
        userRequestsViewArrayList.clear();
        // Fetch all holiday requests for the user
        db.collection("users").document(userId)
                .collection("holidayRequests")
                .orderBy("submittedAt", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error !=null){

                            Log.e("Firestore Error",error.getMessage());
                            return;
                        }
                        if(value.isEmpty()){
                            txtHolidayMassage.setText("Your Holiday Requests Will Appare Here");
                            Log.d("No Holiday Requests", "No holiday requests found.");


                        }else {

                            for (DocumentChange dc : value.getDocumentChanges()) {
                                if (dc.getType() == DocumentChange.Type.ADDED) {
                                    userRequestsViewArrayList.add(dc.getDocument().toObject(userRequestsView.class));
                                }
                            }

                        }
                        userRequestsAdapter.notifyDataSetChanged();

                    }
                });

    }

}
