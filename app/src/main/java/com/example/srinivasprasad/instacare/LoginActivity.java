package com.example.srinivasprasad.instacare;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailTxt;
    private EditText loginPassTxt;
    private Button loginBtn;
    private Button loginRegBtn;
    private ProgressBar loginProgressBar;
    private TextView passforget;

    private DatabaseReference mUserDatabse;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmailTxt= findViewById(R.id.login_email);
        loginPassTxt= findViewById(R.id.login_password);
        loginBtn= findViewById(R.id.login_btn);
        loginRegBtn= findViewById(R.id.login_reg_btn);
        passforget= findViewById(R.id.login_pass_forgot);
        loginProgressBar= findViewById(R.id.login_progress);

        mAuth=FirebaseAuth.getInstance();
        mUserDatabse= FirebaseDatabase.getInstance().getReference().child("Users");


        passforget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,PasswordResetActivity.class);
                startActivity(i);
            }
        });

        loginRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProgressBar.setVisibility(View.VISIBLE);

                String userEmail=loginEmailTxt.getText().toString();
                String userPass=loginPassTxt.getText().toString();
                if(!TextUtils.isEmpty(userEmail)&& !TextUtils.isEmpty(userPass)){


                    mAuth.signInWithEmailAndPassword(userEmail,userPass).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                loginProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(LoginActivity.this,"Login Successful",Toast.LENGTH_LONG).show();
                                sendToNext();

                            }else {
                                String error=task.getException().getMessage();
                                Toast.makeText(LoginActivity.this,"Error:"+error,Toast.LENGTH_LONG).show();
                                loginProgressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }else{
                    Toast.makeText(LoginActivity.this,"Please complete the fields", Toast.LENGTH_LONG).show();
                    loginProgressBar.setVisibility(View.INVISIBLE);
                }

            }
        });
    }


    public  void sendToNext(){
        Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
