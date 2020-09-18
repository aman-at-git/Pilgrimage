package com.android.pilgrimage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

public class Signup extends AppCompatActivity {

    private Button signIn;
    private EditText username;
    private EditText phoneNo;
    private EditText password;
    private ImageView eye;
    private TextView alreadyHaveAcc;
    private TextView resend_otp;
    private TextView timer;
    private String mPhoneNumber;
    private String mUsername;
    private String mPassword;
    private DatabaseReference mDatabase;
    private String verificationCodeBySystem;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mDatabasePhone;
    Boolean passwordVisible=false;
    private EditText otp;
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
            Toast.makeText(Signup.this, "Couldn't send OTP", Toast.LENGTH_SHORT);

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
                .addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = current_user.getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("phoneNumber", mPhoneNumber);
                            userMap.put("name", mUsername);
                            userMap.put("pass",mPassword);
                            userMap.put("postCount", "0");
                            //userMap.put("status", "Hi there! I'm using ChatApp.");
                            //userMap.put("image", "default");
                            //userMap.put("thumb_image", "default");
                            mDatabase.setValue(userMap);

                            mDatabasePhone = FirebaseDatabase.getInstance().getReference().child("Number");
                            mDatabasePhone.child(mPhoneNumber).setValue(mPassword);

                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Signup.this, "Authentication Success.", Toast.LENGTH_SHORT).show();
                                        Intent mainIntent = new Intent(Signup.this, NavBar.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        finish();
                                    }
                                }
                            });
                            Intent intent = new Intent(Signup.this, NavBar.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("phoneNumber", mPhoneNumber);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(Signup.this, "Unable to verify", Toast.LENGTH_SHORT);
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signIn = findViewById(R.id.signup_btn);
        username = findViewById(R.id.user_name);
        phoneNo = findViewById(R.id.editTextPhone);
        password = findViewById(R.id.editTextTextPassword);
        eye = findViewById(R.id.password_visible);
        alreadyHaveAcc = findViewById(R.id.already_have_acc_text);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            startActivity(new Intent(getApplicationContext(), NavBar.class));
            finish();
        }

        phoneNo.requestFocus();
        showKeybord(phoneNo);

        alreadyHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
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

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPhoneNumber = phoneNo.getText().toString();
                mUsername = username.getText().toString();
                mPassword = password.getText().toString();

                if (TextUtils.isEmpty(mUsername)) {
                    username.setError("Name can't be empty");
                    username.requestFocus();
                } else if (TextUtils.isEmpty(mPhoneNumber)) {
                    phoneNo.setError("Enter phone number");
                    phoneNo.requestFocus();
                } else if (!TextUtils.isDigitsOnly(mPhoneNumber) || mPhoneNumber.length() != 10) {
                    phoneNo.setError("Invalid phone number");
                    phoneNo.requestFocus();
                } else if (password.length()<6) {
                    password.setError("Password too small");
                    password.requestFocus();
                } else if (TextUtils.isDigitsOnly(mPhoneNumber) && !(TextUtils.isEmpty(mPhoneNumber))) {

                    mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Number");
                    mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(mPhoneNumber)) {
                                phoneNo.setError("Number already registered.\nPlease login");
                                phoneNo.requestFocus();
                            } else {

                                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(Signup.this);
                                final View mView = getLayoutInflater().inflate(R.layout.otp_dialog, null);
                                otp = mView.findViewById(R.id.editTextOtp);
                                Button verify = mView.findViewById(R.id.otp_verify_btn);
                                ImageView close = mView.findViewById(R.id.close_btn);
                                resend_otp = mView.findViewById(R.id.resend_otp);

                                sendVerificationCodeToUser(mPhoneNumber);

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
                                        sendVerificationCodeToUser(mPhoneNumber);
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
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(Signup.this, "Some error occurred", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }



    Void showKeybord(EditText editText){

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        return null;
    }



}