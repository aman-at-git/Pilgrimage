package com.android.pilgrimage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
                                Intent mainIntent = new Intent(Login.this, NavBar.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                mainIntent.putExtra("phoneNumber", userName);
                                startActivity(mainIntent);
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