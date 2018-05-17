package com.example.srinivasprasad.instacare;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddDetailActivity extends AppCompatActivity {

    private EditText disNAme;
    private EditText disDesc;
    private EditText disSym;
    private EditText disRemide;
    private Button SaveBtn;

    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_detail);

        disNAme=findViewById(R.id.ad_disease_name);
        disDesc=findViewById(R.id.ad_diesease_description);
        disSym=findViewById(R.id.ad_disease_symtoms);
        disRemide=findViewById(R.id.ad_diesease_remide);
        SaveBtn=findViewById(R.id.save_btn);

        firebaseFirestore=FirebaseFirestore.getInstance();

        SaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String disName=disNAme.getText().toString().toLowerCase();
                String disDescrip=disDesc.getText().toString();
                String disSymptm=disSym.getText().toString();
                String disRem=disRemide.getText().toString();

                if (!TextUtils.isEmpty(disName) && !TextUtils.isEmpty(disDescrip) && !TextUtils.isEmpty(disSymptm) && !TextUtils.isEmpty(disRem)){

                    Map<String,String> addMap =new HashMap<>();
                    addMap.put("dis_name",disName);
                    addMap.put("dis_desc",disDescrip);
                    addMap.put("dis_sym",disSymptm);
                    addMap.put("dis_med",disRem);

                    firebaseFirestore.collection("Healthissues").add(addMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            if (task.isSuccessful()){

                                Toast.makeText(AddDetailActivity.this, "successfull", Toast.LENGTH_SHORT).show();
                            }
                            else{

                                Toast.makeText(AddDetailActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                }else{

                    Toast.makeText(AddDetailActivity.this, "Complete the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });





    }
}
