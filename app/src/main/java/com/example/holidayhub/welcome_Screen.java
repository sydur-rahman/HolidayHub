package com.example.holidayhub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class welcome_Screen extends AppCompatActivity {
Button btnSignUp, btnLogin;
TextView txtForgetPassword;
EditText editTextEmail,editTextPassword;
FirebaseAuth fUserAuth;  //auth the user
FirebaseFirestore db; // to retrieve user role
String userId,userRole;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        btnSignUp =findViewById(R.id.btnSignUp);
        btnLogin =findViewById(R.id.btnLogin);
        editTextEmail=findViewById(R.id.editTextEmail);
        editTextPassword=findViewById(R.id.editTextPassword);
        txtForgetPassword=findViewById(R.id.txtForgetPassword);

        fUserAuth= FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();



        Intent toCreateAccount = new Intent(welcome_Screen.this,CreateAccount.class);
        //Intent toAdminDashboard = new Intent(welcome_Screen.this,admin_dashboard.class);

        //Sign Up Button
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //passing to CreateAccount
                if (fUserAuth.getCurrentUser() != null) {
                    // Log out the current user
                    fUserAuth.signOut();
                }
                startActivity(toCreateAccount);

            }
        });




        txtForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(welcome_Screen.this);
                builder.setTitle("Reset Password");
                builder.setMessage("Please enter your email:");

                // Set up the input
                final EditText input = new EditText(welcome_Screen.this);
                builder.setView(input);

                builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = input.getText().toString();
                        fUserAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(welcome_Screen.this, "Reset link ent to your Email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(welcome_Screen.this, "Reset link is Not Sent: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel(); // Close the dialog
                    }
                });

                // Show the AlertDialog
                builder.show();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email,password;
                email=editTextEmail.getText().toString();
                password=editTextPassword.getText().toString();
                /* Validating User input */
                if(TextUtils.isEmpty(email)){
                    editTextEmail.setError("Email Is Required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    editTextPassword.setError("Password is Require");
                    return;
                }
                if (password.length()<6){
                    editTextPassword.setError("Password must be more than 6 Character");
                }
            //auth user
            fUserAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                       Toast.makeText(welcome_Screen.this, "Login Successful", Toast.LENGTH_SHORT).show();
                       userId=fUserAuth.getCurrentUser().getUid();
                       DocumentReference documentReference= db.collection("users").document(userId);
                       documentReference.addSnapshotListener(welcome_Screen.this, new EventListener<DocumentSnapshot>() {
                           @Override
                           public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                               if (documentSnapshot.exists()){
                               //editTextEmail.setText
                                   userRole=documentSnapshot.getString("role");

                                   Intent toAdminDashboard = new Intent(welcome_Screen.this,admin_dashboard.class);
                                   Intent toMain = new Intent(welcome_Screen.this,MainActivity.class);


                                   if (userRole.equals("Operation Manager")){

                                       //pass user to the AdminDashboard, and userid to the data
                                       toAdminDashboard.putExtra("userId",userId);
                                       startActivity(toAdminDashboard);
                                       finish();
                                   }else if (userRole.equals("Employee")){
                                        //pass firebase user id

                                       toMain.putExtra("userId",userId) ;
                                       //pass user to the Main Activity/ HomeFragment
                                       startActivity(toMain);
                                       finish();
                                   }
                                   else {
                                       startActivity(toMain);
                                       finish();
                                   }

                           }   else {
                                    Toast.makeText(welcome_Screen.this, "User document does not exist", Toast.LENGTH_SHORT).show();
                                }
                           }
                       });

                    }else {
                        Toast.makeText(welcome_Screen.this, "Error !! "+ task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });


            }
        });

    }

}
