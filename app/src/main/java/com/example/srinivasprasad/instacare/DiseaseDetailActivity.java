package com.example.srinivasprasad.instacare;

import android.content.Intent;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class DiseaseDetailActivity extends AppCompatActivity {

    private TextView disDesc,disSytm,disMed;
    private String disename,documid;
    private ImageView youTubeBtn,pharmBtn,hospitalBtn,DoctorBtn;
    private EditText placeTxt;

    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_detail);

        documid =getIntent().getStringExtra("DOC_ID");
        disename=getIntent().getStringExtra("D_NAME");

        Toolbar detailDiseaseToolbar = findViewById(R.id.disdetail_toolbar);
        setSupportActionBar(detailDiseaseToolbar);
        getSupportActionBar().setTitle(disename.toUpperCase());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        disDesc=findViewById(R.id.dis_desc);
        disSytm=findViewById(R.id.dis_sytm);
        disMed=findViewById(R.id.dis_med);
        youTubeBtn=findViewById(R.id.yt_btn);
        pharmBtn=findViewById(R.id.pharmay_btn);
        hospitalBtn=findViewById(R.id.hospital_btn);
        DoctorBtn=findViewById(R.id.doctor_btn);
        placeTxt=findViewById(R.id.place_txt);



        firebaseFirestore=FirebaseFirestore.getInstance();

      youTubeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youTubrLink)));
                Intent intent = new Intent(DiseaseDetailActivity.this, YouTubeActivity.class);
                intent.putExtra("DOCUMENT_ID",documid);
                intent.putExtra("DISEASE_NAME",disename);
                startActivity(intent);

            }
        });


        pharmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri gmmIntentUri = Uri.parse("geo:0,0" + "?q=pharmacy near me");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        hospitalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri gmmIntentUri = Uri.parse("geo:0,0" + "?q=hospitals near me");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        DoctorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String place=placeTxt.getText().toString();
                String search="";
                if (!TextUtils.isEmpty(place)){
                    search=disename+" doctors in "+place;
                }else {

                    search=disename+" doctors";
                    Toast.makeText(DiseaseDetailActivity.this, "You can also sepcify the other loacation if you do not find the doctors in your locality ", Toast.LENGTH_SHORT).show();
                }

                Uri gmmIntentUri = Uri.parse("geo:0,0" + "?q="+search);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);
        firebaseFirestore.collection("Healthissues").document(documid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    if(task.getResult().exists()){


                        String disDescription=task.getResult().getString("dis_desc");
                        String disSymptoms=task.getResult().getString("dis_sym");
                        String disMedicine=task.getResult().getString("dis_med");

                        disDesc.setText(disDescription);
                        disSytm.setText(disSymptoms);
                        disMed.setText(disMedicine);

                    }



                }else{
                    String error = task.getException().getMessage();
                    Toast.makeText(DiseaseDetailActivity.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();

                }
            }
        });


    }
}
