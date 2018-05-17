package com.example.srinivasprasad.instacare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {


    private static int SPLASH_TIME_OUT = 2000;

    private FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       try {
           setContentView(R.layout.activity_splash);
       }catch (Exception ex){
           Toast.makeText(this, ""+ex.toString(), Toast.LENGTH_SHORT).show();
       }


        getWindow().setStatusBarColor(Color.WHITE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        try{
        if(currentUser==null){

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    SplashActivity.this.startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    SplashActivity.this.finish();
                }
            }, (long) SPLASH_TIME_OUT);
        }
        else {
            // Toast.makeText(MainActivity.this,"User currently login",Toast.LENGTH_LONG).show();
            SplashActivity.this.startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            SplashActivity.this.finish();

        }        }catch (Exception ex){
            Toast.makeText(this, ""+ex.toString(), Toast.LENGTH_SHORT).show();
        }

    }
}
