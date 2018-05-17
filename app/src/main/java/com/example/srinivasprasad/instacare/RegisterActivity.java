package com.example.srinivasprasad.instacare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private EditText reg_email_field;
    private EditText reg_pass_field;
    private EditText reg_pass_conf_field;
    private Button registerBtn;
    private Button regHaveAccBtn;
    private ProgressBar regProgressBar;

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "RegisterPage";

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    SignInButton signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        mAuth=FirebaseAuth.getInstance();

        reg_email_field=(EditText)findViewById(R.id.reg_email);
        reg_pass_field=(EditText)findViewById(R.id.reg_password);
        reg_pass_conf_field=(EditText)findViewById(R.id.reg_password_confirm);
        registerBtn=(Button)findViewById(R.id.reg_btn);
        regHaveAccBtn=(Button)findViewById(R.id.reg_acc_exist_btn);
        regProgressBar=(ProgressBar)findViewById(R.id.reg_progress);

        this.signin = (SignInButton) findViewById(R.id.reg_google_btn);
        findViewById(R.id.reg_google_btn).setOnClickListener(this);
        this.mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()).build();


        regHaveAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=reg_email_field.getText().toString();
                String pass=reg_pass_field.getText().toString();
                String passcon=reg_pass_conf_field.getText().toString();
                regProgressBar.setVisibility(View.VISIBLE);

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(passcon)){

                    if(pass.equals(passcon)){

                        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){
                                    regProgressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                    sendToSetup();

                                }else{
                                    String err=task.getException().getMessage();
                                    regProgressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(RegisterActivity.this, "Error:"+err, Toast.LENGTH_SHORT).show();
                                }

                            }
                        });


                    }else{

                        Toast.makeText(RegisterActivity.this, "Password and confirm Password Doesn't match", Toast.LENGTH_SHORT).show();
                        regProgressBar.setVisibility(View.INVISIBLE);
                    }


                }else{
                    Toast.makeText(RegisterActivity.this, "Please complete the fields", Toast.LENGTH_SHORT).show();
                    regProgressBar.setVisibility(View.INVISIBLE);
                }

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null){
            sendToHome();

        }
    }

    private void sendToHome() {
        Intent intent=new Intent(RegisterActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToSetup() {
        Intent intent=new Intent(RegisterActivity.this,AccountSetupActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.reg_google_btn){
            signIn();
        }

    }

    public void signIn() {
        startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(this.mGoogleApiClient), RC_SIGN_IN);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    firebaseAuthWithGoogle(result.getSignInAccount());
                }
            }
        } catch (Exception e) {
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        try {
            // regProgressBar.setVisibility(View.VISIBLE);
            final ProgressDialog progressDialog = ProgressDialog.show(this, "Please wait...", "Processing...", true);
            Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            Toast.makeText(this, BuildConfig.VERSION_NAME + credential.getProvider(), Toast.LENGTH_LONG).show();

            this.mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(RegisterActivity.TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        // regProgressBar.setVisibility(View.INVISIBLE);
                        sendToHome();
                        return;
                    }else{
                        progressDialog.dismiss();
                        //regProgressBar.setVisibility(View.INVISIBLE);
                        String err=task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, "Error:"+err, Toast.LENGTH_LONG).show();
                    }

                }
            });
        } catch (Exception e) {
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
