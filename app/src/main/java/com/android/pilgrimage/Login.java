package com.android.pilgrimage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {

    private Button login;
    private EditText username;
    private EditText password;
    private ImageView eye;
    private TextView create_acc;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    String pass;
    String userName;
    String correctPassword;
    Boolean passwordVisible=false;
    private String verificationCodeBySystem;
    private EditText otp;
    private TextView resend_otp;
    private int counter=0;

    private void sendVerificationCodeToUser (String mPhoneNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mPhoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCodeBySystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(Login.this, "Couldn't send OTP", Toast.LENGTH_SHORT);

        }
    };

    private void verifyCode(String codeByUser) {

        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
            signInTheUserByCredentials(credential);
        }

        catch (Exception e){

            otp.requestFocus();
            otp.setError("Wrong OTP");
        }

    }

    private void signInTheUserByCredentials(PhoneAuthCredential credential) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent mainIntent = new Intent(Login.this, NavBar.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mainIntent.putExtra("phoneNumber", userName);
                            startActivity(mainIntent);
                        }
                        else {
                            Toast.makeText(Login.this, "Unable to verify", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login= findViewById(R.id.login_btn);
        username = findViewById(R.id.editTextPhone);
        password = findViewById(R.id.editTextTextPassword);
        eye = findViewById(R.id.password_visible);
        create_acc = findViewById(R.id.dont_have_acc_text);
        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        username.requestFocus();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            startActivity(new Intent(getApplicationContext(), NavBar.class));
            finish();
        }


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login_user();
            }

            private void login_user() {

                userName = username.getText().toString().trim();
                pass = password.getText().toString().trim();

                if (TextUtils.isEmpty(userName)) {
                    username.requestFocus();
                    username.setError("Phone number is empty");
                    return;
                }

                if (TextUtils.isEmpty(pass)) {
                    password.requestFocus();
                    password.setError("Password is empty");
                    return;
                }

                if (!TextUtils.isDigitsOnly(userName) || userName.length() != 10) {
                    username.setError("Invalid phone number");
                    username.requestFocus();
                    return;
                }

                mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Number");
                mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(userName)){
                            correctPassword = dataSnapshot.child(userName).getValue().toString();

                            if(pass.equals(correctPassword)){

                                    final AlertDialog.Builder mBuilder = new AlertDialog.Builder(Login.this);
                                    final View mView = getLayoutInflater().inflate(R.layout.otp_dialog, null);
                                    otp = mView.findViewById(R.id.editTextOtp);
                                    Button verify = mView.findViewById(R.id.otp_verify_btn);
                                    ImageView close = mView.findViewById(R.id.close_btn);
                                    resend_otp = mView.findViewById(R.id.resend_otp);

                                    sendVerificationCodeToUser(userName);

                                    verify.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String enteredOtp = otp.getText().toString();

                                            if (TextUtils.isEmpty(enteredOtp)) {
                                                otp.setError("Enter OTP");
                                            } else if (!TextUtils.isDigitsOnly(enteredOtp) || enteredOtp.length() != 6) {
                                                otp.setError("Invalid OTP");
                                            } else if (TextUtils.isDigitsOnly(enteredOtp) && !(TextUtils.isEmpty(enteredOtp))) {
                                                verifyCode(enteredOtp);
                                            }
                                        }
                                    });
                                    mBuilder.setView(mView);
                                    final AlertDialog dialog = mBuilder.create();
                                    dialog.show();
                                    resend_otp.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            final CountDownTimer otpCounter = new CountDownTimer (60000, 1000) {
                                                public void onTick(long millisUntilFinished){
                                                    counter++;
                                                } public void onFinish() {
                                                    resend_otp.setEnabled(true);
                                                    resend_otp.setTextColor(getResources().getColor(R.color.colorPrimary, null));
                                                }
                                            };
                                            sendVerificationCodeToUser(userName);
                                            counter=0;
                                            otpCounter.start();
                                            resend_otp.setEnabled(false);
                                            resend_otp.setTextColor(Color.GRAY);
                                        }
                                    });
                                    close.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });

                            }

                            else {
                                password.requestFocus();
                                password.setError("Incorrect password");
                            }
                        }
                        else {
                            username.requestFocus();
                            username.setError("Number not registered");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        username.requestFocus();
                        username.setError("Phone number not registered");
                    }
                });
            }
        });

        create_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Signup.class));
                finish();
            }
        });

        eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordVisible) {
                    password.setTransformationMethod(new PasswordTransformationMethod());
                    eye.setImageResource(R.drawable.eye_not_visible);
                    passwordVisible=false;
                }

                else {
                    eye.setImageResource(R.drawable.eye_visible);
                    password.setTransformationMethod(null);
                    passwordVisible=true;
                }
                password.requestFocus();
                password.setSelection(password.getText().length());
            }
        });
    }
}