package com.example.android.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.ecommerce.Model.Users;
import com.example.android.ecommerce.Prevalent.Prevalent;
import com.example.android.ecommerce.ViewHolder.NewPasswordActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class OTPforResetPasswordActivity extends AppCompatActivity {

    private EditText pwd_phone_no, pwd_otp;
    private Button verify, generate;
    private String phoneNumber, otp;

    FirebaseAuth auth;
    private String verificationCode;
    private Users usersData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        pwd_phone_no=findViewById(R.id.pwd_phone_number);
        pwd_otp=findViewById(R.id.OTP);
        verify=findViewById(R.id.verify_otp_btn);
        generate=findViewById(R.id.generate_otp_btn);
        auth=FirebaseAuth.getInstance();


        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode();
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifySignInCode();
            }
        });


    }

    private void verifySignInCode() {
        String code = pwd_otp.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                    "Login Successful", Toast.LENGTH_LONG).show();
                            //here you can open new activity
                            Prevalent.currentOnlineUser = usersData;
                            startActivity(new Intent(OTPforResetPasswordActivity.this,NewPasswordActivity.class));
                            finish();

                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(),
                                        "Incorrect Verification Code ", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void sendVerificationCode() {
        phoneNumber = pwd_phone_no.getText().toString();


        if(phoneNumber.isEmpty()){
            pwd_phone_no.setError("Phone number is required");
            pwd_phone_no.requestFocus();
            return;
        }

        if(phoneNumber.length() < 10 ){
            pwd_phone_no.setError("Please enter a valid phone number");
            pwd_phone_no.requestFocus();
            return;
        }

//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phoneNumber,                     // Phone number to verify
//                60,                           // Timeout duration
//                TimeUnit.SECONDS,                // Unit of timeout
//                OTPforResetPasswordActivity.this,        // Activity (for callback binding)
//                mCallbacks);


       final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();


        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(phoneNumber).exists())
                {

                    usersData = dataSnapshot.child("Users").child(phoneNumber).getValue(Users.class);
                    phoneNumber="+91"+phoneNumber;
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,                     // Phone number to verify
                            60,                           // Timeout duration
                            TimeUnit.SECONDS,                // Unit of timeout
                            OTPforResetPasswordActivity.this,        // Activity (for callback binding)
                            mCallbacks);
                }
                else
                {
                    Toast.makeText(OTPforResetPasswordActivity.this, "Account with this " + phoneNumber + " number do not exists.", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationCode = s;
        }
    };
}
