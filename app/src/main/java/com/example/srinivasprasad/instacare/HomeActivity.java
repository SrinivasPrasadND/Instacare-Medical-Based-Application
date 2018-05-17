package com.example.srinivasprasad.instacare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.PrivateKey;
import java.util.zip.Inflater;

public class HomeActivity extends AppCompatActivity {

    private Toolbar testTool;
    private CollapsingToolbarLayout toolbarLayout;

    private String current_user_id;

    private ImageView bloodBtn;
    private ImageView healthBtn;
    private ImageView chatBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    //delete later
    private Button AddDetBtn;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getWindow().setStatusBarColor(Color.rgb(21,28,32));



        testTool=findViewById(R.id.testtoolbar);
        setSupportActionBar(testTool);
        getSupportActionBar().setTitle("InstaCare");

        mAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        bloodBtn=findViewById(R.id.home_blood_btn);
        healthBtn=findViewById(R.id.home_health_btn);
        chatBtn=findViewById(R.id.home_chat_btn);

        //delete later
        AddDetBtn=findViewById(R.id.addDetailsBtn);
        AddDetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this,AddDetailActivity.class);
                startActivity(i);
            }
        });

        bloodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this,BloodSearchActivity.class);
                startActivity(i);

            }
        });

        healthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this,HealthIssueActivity.class);
                startActivity(i);
            }
        });

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this,ChatActivity.class);
                startActivity(i);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser==null){
            Intent i=new Intent(HomeActivity.this,LoginActivity.class);
            startActivity(i);
            finish();

        }else{

            current_user_id=mAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){

                        if(!task.getResult().exists()){

                            Intent intent=new Intent(HomeActivity.this,AccountSetupActivity.class);
                            startActivity(intent);
                            // finish();

                        }

                    }else {
                        String er=task.getException().getMessage();
                        Toast.makeText(HomeActivity.this, "Error:"+er, Toast.LENGTH_SHORT).show();

                    }
                }
            });


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_logout_btn:
                logOut();
                return true;

            case R.id.action_accsetup_btn:

                Intent settingsIntent = new Intent(HomeActivity.this, AccountSetupActivity.class);
                startActivity(settingsIntent);
                return true;


            default:
                return false;


        }

    }

    private void logOut() {
        mAuth.signOut();
        sendToLogin();

    }

    private void sendToLogin() {

        Intent loginIntent = new Intent(HomeActivity.this,LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }



}
