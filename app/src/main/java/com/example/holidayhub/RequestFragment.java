package com.example.holidayhub;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RequestFragment extends Fragment {
    TextView txtStartDate, txtEndDate;
    CardView cardViewCalender;
    EditText edtCommentBox;
    Button btnSendRequest;
    String startDate,endDate, reasonForHoliday,typeOfHoliday;
    Spinner spinnerTypeOfHoliday;

    public RequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the custom layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request, container, false);
        // Find your UI components in the custom layout
        txtStartDate = view.findViewById(R.id.txtStartDate);
        txtEndDate = view.findViewById(R.id.txtEndDate);
        cardViewCalender=view.findViewById(R.id.cardViewCalender);
        spinnerTypeOfHoliday=view.findViewById(R.id.spinnerTypeOfHoliday);
        edtCommentBox=view.findViewById(R.id.edtCommentBox);
        btnSendRequest=view.findViewById(R.id.btnSendRequest);


        setupSpinner();


        cardViewCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });



        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isInPutEmpty()==true){
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Confirmation");
                    builder.setMessage("Are you sure you want to send the request?");

                    reasonForHoliday=edtCommentBox.getText().toString();
                    typeOfHoliday= (String) spinnerTypeOfHoliday.getSelectedItem();
                    builder.setPositiveButton("Confirm Send", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Call the request function
                            holidayRequest();
                        }
                    });

                    // Set up "Cancel" button
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Handle cancel action
                            // clear the input
                            txtStartDate.setText("");
                            txtEndDate.setText("");
                            startDate="";
                            endDate="";

                            dialog.dismiss();
                        }
                    });

                    // Create and show the AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }else {
                    Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }

            }

        });
        return view;
    }


    private void holidayRequest() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        // Create a reference to the user's holidayRequests subcollection
        CollectionReference holidayRequestsRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("holidayRequests");

        // Create a new holiday request document
        Map<String, Object> holidayRequest = new HashMap<>();
        holidayRequest.put("startDate", startDate);
        holidayRequest.put("endDate", endDate);
        holidayRequest.put("reason", reasonForHoliday);
        holidayRequest.put("type", typeOfHoliday);
        holidayRequest.put("status", "pending");
        holidayRequest.put("submittedAt", FieldValue.serverTimestamp());

        // Add the holiday request document to the subcollection
        holidayRequestsRef
                .add(holidayRequest)
                .addOnSuccessListener(documentReference -> {
                    // Holiday request added successfully
                    Toast.makeText(requireContext(), "Request sent successfully!", Toast.LENGTH_SHORT).show();

                    // Clear input fields
                    edtCommentBox.setText("");
                    startDate = "";
                    endDate = "";
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(requireContext(), "Failed to send request. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }




    private void showDatePicker() {

        long currentTime = System.currentTimeMillis();
        // Calculate the milliseconds for the next day
        long nextDay = currentTime + TimeUnit.DAYS.toMillis(1);
        // Create MaterialDatePicker for date range selection
        MaterialDatePicker<Pair<Long, Long>> requestDatePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select date range")
                .setTheme(R.style.DatePickerDialogStyle)
                .setSelection(Pair.create(currentTime,nextDay))
                .build();

        // Set an OnPositiveButtonClick listener to handle date range selection
        requestDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long,Long> selection) {
                // Format selected dates
                startDate = formatDate(selection.first);
                endDate = formatDate(selection.second);

                // Update TextViews
                txtStartDate.setText(startDate);
                txtEndDate.setText(endDate);
            }
        });

        // Show the date picker
        requestDatePicker.show(getChildFragmentManager(), "date_range_picker");
    }

    private String formatDate(long dateInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateInMillis);
        return DateFormat.format("dd-MM-yyyy", calendar).toString();
    }

    // Helper method to check if an EditText is empty
    private boolean isInPutEmpty(){
        
     // Validate txtStartDate
          if (txtStartDate.getText().toString().trim().isEmpty()) {
              return false;
          }

            // Validate txtEndDate
          if (txtEndDate.getText().toString().trim().isEmpty()) {
                return false;
          }

            // Validate spinnerTypeOfHoliday
          String selectedHolidayType = (String) spinnerTypeOfHoliday.getSelectedItem();
          if (selectedHolidayType == null || selectedHolidayType.trim().isEmpty()) {
                return false;
            }
            // Validate edtCommentBox
          if (edtCommentBox.getText().toString().trim().isEmpty()) {
                return false;
          }

            // All fields are not empty
          return true;
        }
        

    private void setupSpinner() {
        // Define an array of holiday types
        String[] holidayTypes = {"Paid Holiday", "Unpaid Holiday", "Off Day", "Other"};

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, holidayTypes);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinnerTypeOfHoliday.setAdapter(adapter);
    }







}
