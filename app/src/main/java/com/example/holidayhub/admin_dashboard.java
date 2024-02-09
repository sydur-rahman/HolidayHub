package com.example.holidayhub;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class admin_dashboard extends AppCompatActivity {
    TextView txtAdminWelcome_UserName;
    Button btnReviewPendingHolidays;
    RecyclerView recyclerEmployeeView;
    ArrayList<EmployeeList> employeeListArrayList;
    EmployeeListAdapter employeeListAdapter;
    FirebaseFirestore db=FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        btnReviewPendingHolidays =findViewById(R.id.btnReviewPendingHolidays);
        Intent toAdminHolidayReview =new Intent(admin_dashboard.this,AdminHolidayRequestReview.class);
        btnReviewPendingHolidays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(toAdminHolidayReview);
            }
        });


        txtAdminWelcome_UserName=findViewById(R.id.txtAdminWelcome_UserName);
        Intent intent= getIntent();
        String uid= intent.getStringExtra("userId");
        UserProfileData(uid);
        GetEmployeeData();
    }

    private void UserProfileData(String uId) {

        db.collection("users").document(uId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error !=null){
                    //
                    Log.e("FireStore error: ", error.getMessage());
                    return;
                }
                if (value.exists()){
                    String greetings="Welcome, ";
                    String firstName = value.getString("firstName");
                    txtAdminWelcome_UserName.setText(greetings+firstName);

                }

            }
        });



    }

    private void GetEmployeeData() {

        recyclerEmployeeView=findViewById(R.id.recyclerEmployeeView);
        recyclerEmployeeView.setHasFixedSize(true);
        recyclerEmployeeView.setLayoutManager(new LinearLayoutManager(this));

        employeeListArrayList =new ArrayList<EmployeeList>();
        employeeListAdapter =new EmployeeListAdapter(admin_dashboard.this,employeeListArrayList);

        recyclerEmployeeView.setAdapter(employeeListAdapter);
        //get data from Firestore
        db.collection("users").orderBy("firstName", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error !=null){
                    Log.e("FireStore error: ",error.getMessage());
                    return;
                }
                for (DocumentChange dc: value.getDocumentChanges()){

                    if (dc.getType()== DocumentChange.Type.ADDED){
                        employeeListArrayList.add(dc.getDocument().toObject(EmployeeList.class));
                    }

                }
                employeeListAdapter.notifyDataSetChanged();
            }
        });

    }
}