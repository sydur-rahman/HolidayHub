package com.example.holidayhub;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class SettingsFragment extends Fragment {

Button btnChangePassword,btnLogOut;
String userId;
TextView userFirstName,userLastName,userMobile,userEmail,userRole;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_settings, container, false);

        btnChangePassword=view.findViewById(R.id.btnChangePassword);
        btnLogOut=view.findViewById(R.id.btnLogOut);
        userFirstName=view.findViewById(R.id.userFirstName);
        userLastName=view.findViewById(R.id.userLastName);
        userMobile=view.findViewById(R.id.userMobile);
        userEmail=view.findViewById(R.id.userEmail);
        userRole=view.findViewById(R.id.userRole);

        if (getArguments() != null) {
            userId = getArguments().getString("userId");}


        FirebaseFirestore db= FirebaseFirestore.getInstance();

        db.collection("users").document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    Log.e("FireStore error: ", error.getMessage());
                    return;
                }
                if(value.exists()){
                    String urFirstName = value.getString("firstName");
                    String urLastName=value.getString("lastName");
                    String urMobile=value.getString("mobile");
                    String urEmail=value.getString("email");
                    String urRole=value.getString("role");
                    userFirstName.setText("First Name: "+urFirstName);
                    userLastName.setText("Last Name: "+urLastName);
                    userMobile.setText("Mobile: "+urMobile);
                    userEmail.setText("Email: "+urEmail);
                    userRole.setText("Role: "+urRole);
                }
            }
        });


        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Alert Box: Confirm that the user wants to change the password
                new AlertDialog.Builder(requireContext())
                        .setTitle("Change Password")
                        .setMessage("Are you sure you want to Change your password?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // User clicked Yes, proceed with sending reset password link

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                if (user != null) {
                                    String email = user.getEmail();

                                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Password reset email sent successfully
                                                        // Display a message or handle it as needed
                                                        Toast.makeText(requireContext(), "Password change link sent to your email", Toast.LENGTH_SHORT).show();

                                                    } else {
                                                        // If sending email fails, handle the error
                                                        // You can log it or display a message to the user
                                                        Toast.makeText(requireContext(), "Failed to send reset link. Try again later.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });




        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(requireContext(),welcome_Screen.class));
                requireActivity().finish();
            }
        });









        return view;

    }

}