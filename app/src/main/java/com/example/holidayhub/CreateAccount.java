package com.example.holidayhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateAccount extends AppCompatActivity {
Button btnCreateAccount,btnAlreadyHaveAnAcc;
EditText editTextFirstName, editTextLastName, editTextMobile,editTextEmail,editTextPassword,editTextConfirmPassword;
Spinner spinnerRole;

//fireBase
FirebaseAuth userAuth; //to auth the user
FirebaseFirestore userDetails;  //to store user details
String userId; // get the unique user id from our cloud

String userPosition; //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        btnCreateAccount =findViewById(R.id.btnCreateAccount);
        editTextFirstName =findViewById(R.id.editTextFirstName);
        editTextLastName =findViewById(R.id.editTextLastName);
        editTextMobile=findViewById(R.id.editTextMobile);
        editTextEmail =findViewById(R.id.editTextEmail);
        editTextPassword=findViewById(R.id.editTextPassword);
        editTextConfirmPassword=findViewById(R.id.editTextConfirmPassword);
        btnAlreadyHaveAnAcc=findViewById(R.id.btnHaveAnAcc);
        spinnerRole =findViewById(R.id.spinnerRole);

        userAuth= FirebaseAuth.getInstance();
        userDetails =FirebaseFirestore.getInstance();

        Intent toMain = new Intent(CreateAccount.this,MainActivity.class);
        Intent toWelcomeScreen = new Intent(CreateAccount.this,welcome_Screen.class);

        //validating if the user is already logged in
        if (userAuth.getCurrentUser() != null) {
            // Log out the current user
            userAuth.signOut();
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.Position,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerRole.setAdapter(adapter);


        // Set the listener for item selection
        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected position as a string
                userPosition = parentView.getItemAtPosition(position).toString();
                }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // nothing here
            }
        });

//button Create Account
btnCreateAccount.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String firstName, lastName, mobile, email,password, confirmPassword;

        firstName=editTextFirstName.getText().toString();
        lastName =editTextLastName.getText().toString();
        mobile =editTextMobile.getText().toString();
        email =editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();
        confirmPassword =editTextConfirmPassword.getText().toString();


            /* Validating User input */
        if(TextUtils.isEmpty(email)){
        editTextEmail.setError("Email Is Required");
        return;
        }
        if(TextUtils.isEmpty(userPosition)){
            Toast.makeText(CreateAccount.this, "Please Select a Role", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) ){
            editTextPassword.setError("Password is Require");
            editTextConfirmPassword.setError("Password is Require");
            return;
        }
        if (confirmPassword.equals(password)){

        }else {
            editTextConfirmPassword.setError("Passwords do not match");
            editTextPassword.setError("Passwords do not match");
            editTextConfirmPassword.setText("");
            return;
        }
        if (PasswordValidator.isValidPassword(confirmPassword)){

        }else {
            editTextConfirmPassword.setError("Minimum length of 8 characters.\n" +
                    "Contains at least one uppercase letter.\n" +
                    "Contains at least one lowercase letter.\n" +
                    "Contains at least one digit.\n" +
                    "Contains at least one special character.");
            return;
        }


        //auth
         userAuth.createUserWithEmailAndPassword(email,confirmPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
             @Override
             public void onComplete(@NonNull Task<AuthResult> task) {
                 if (task.isSuccessful()){
                     sendEmailVerification();
                     userId=userAuth.getCurrentUser().getUid();
                     DocumentReference documentReference=userDetails.collection("users").document(userId);

                     Map<String,Object> user=new HashMap();
                     user.put("firstName",firstName);
                     user.put("lastName",lastName);
                     user.put("mobile",mobile);
                     user.put("email",email);
                     user.put("role",userPosition);

                     documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                         @Override
                         public void onSuccess(Void unused) {

                         }
                     });
                     Toast.makeText(CreateAccount.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                     startActivity(toWelcomeScreen);
                 }else  {
                     Toast.makeText(CreateAccount.this, "Error!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                 }
             }
         });

    }
});
btnAlreadyHaveAnAcc.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        onBackPressed();
    }
});

    }
    // Helper method to send email verification
    private void sendEmailVerification() {
        userAuth.getCurrentUser().sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Email sent successfully
                            Toast.makeText(CreateAccount.this, "Verification email sent.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Email not sent, display error message
                            Toast.makeText(CreateAccount.this, "Error sending verification email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



}