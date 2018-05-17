package com.example.srinivasprasad.instacare;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class UserInfoActivity extends AppCompatActivity {

    private TextView infoUserName,infoBlood,infoPhone,infoAddress;
    private ImageView infoUserImg;
    public String document_id;

    public FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        document_id=getIntent().getStringExtra("DOC_ID");

        Toolbar userInfoToolbar = findViewById(R.id.user_info_toolbar);
        setSupportActionBar(userInfoToolbar);
        getSupportActionBar().setTitle("User Info.");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        infoUserName=findViewById(R.id.info_user_name);
        infoBlood=findViewById(R.id.info_user_blood);
        infoPhone=findViewById(R.id.info_user_phone);
        infoAddress=findViewById(R.id.info_user_address);
        infoUserImg=findViewById(R.id.info_user_image);


        firebaseFirestore=FirebaseFirestore.getInstance();



    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);

        firebaseFirestore.collection("Users").document(document_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){


                    if(task.getResult().exists()){

                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");
                        String blood_group=task.getResult().getString("blood_group");
                        String phoneno=task.getResult().getString("phone_num");
                        String image_thumb= task.getResult().getString("thumb_url");
                        String address=task.getResult().getString("address");

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_avatar);
                        Glide.with(UserInfoActivity.this).setDefaultRequestOptions(placeholderRequest).load(image)
                                .thumbnail(Glide.with(UserInfoActivity.this).load(image_thumb)).into(infoUserImg);

                        infoUserName.setText(name);
                        infoBlood.setText(blood_group);
                        infoPhone.setText(phoneno);
                        infoAddress.setText(address);

                    }

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(UserInfoActivity.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();

                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_menu,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.info_call_btn:

                String phone_num=infoPhone.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+phone_num));
                startActivity(intent);
                return true;

            case R.id.info_msg_btn:

                String mblNumVar = infoPhone.getText().toString();

                Intent smsMsgAppVar = new Intent(Intent.ACTION_VIEW);
                smsMsgAppVar.setData(Uri.parse("sms:" +  mblNumVar));
                smsMsgAppVar.putExtra("sms_body", "Hello I need your help, please help me");
                startActivity(smsMsgAppVar);
                return true;

            default:
                return false;
        }
    }
}
