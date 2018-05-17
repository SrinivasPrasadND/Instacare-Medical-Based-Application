package com.example.srinivasprasad.instacare;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {

    private EditText resetEmail;
    private TextView resetTxt;
    private Button resetBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);


        mAuth=FirebaseAuth.getInstance();

        resetEmail= findViewById(R.id.reset_email);
        resetTxt= findViewById(R.id.reset_text);
        resetBtn= findViewById(R.id.reset_btn);


        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Useremail=resetEmail.getText().toString();

                if(!TextUtils.isEmpty(Useremail)){

                    mAuth.sendPasswordResetEmail(Useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(PasswordResetActivity.this, "Password Reset Mail sent successfully, check your mailbox. ", Toast.LENGTH_SHORT).show();
                                resetTxt.setText("Mail sent Successfully");
                                resetTxt.setTextColor(Color.DKGRAY);
                                resetEmail.setText(null);
                            }
                            else{
                                String err=task.getException().getMessage();
                                Toast.makeText(PasswordResetActivity.this, "Error:"+err, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    Toast.makeText(PasswordResetActivity.this, "Enter your Email ID", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
