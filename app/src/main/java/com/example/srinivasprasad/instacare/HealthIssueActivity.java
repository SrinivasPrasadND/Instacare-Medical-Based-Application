package com.example.srinivasprasad.instacare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HealthIssueActivity extends AppCompatActivity {

   private Toolbar toobarHeathIssue;
    private RecyclerView disList;
    private Button searchBtn;
    private EditText hiText;

    private List<Diseases> diseasesList;
    private DiseaseRecyclerAdaptor diseaseRecyclerAdaptor;

    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_issue);

        toobarHeathIssue=findViewById(R.id.healthIssue_toolbar);
        setSupportActionBar(toobarHeathIssue);
        getSupportActionBar().setTitle("Health Care");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        hiText=findViewById(R.id.hs_dis_name);
        searchBtn=findViewById(R.id.hs_search_btn);

        firebaseFirestore=FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);

        diseasesList=new ArrayList<>();
        diseaseRecyclerAdaptor=new DiseaseRecyclerAdaptor(diseasesList);
        disList=findViewById(R.id.hs_recycler);
        disList.setLayoutManager(new LinearLayoutManager(this));
        disList.setAdapter(diseaseRecyclerAdaptor);



       searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String DisName=hiText.getLayout().getText().toString().toLowerCase();
                diseasesList.clear();
                disList.removeAllViews();

                if (!TextUtils.isEmpty(DisName)){

                    firebaseFirestore.collection("Healthissues").orderBy("dis_name").startAt(DisName).endAt(DisName+"\uf8ff")
                            .addSnapshotListener(HealthIssueActivity.this,new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                         try{


                             for(DocumentChange doc : documentSnapshots.getDocumentChanges()){

                                 if (doc.getType() == DocumentChange.Type.ADDED) {

                                     Diseases diseases = doc.getDocument().toObject(Diseases.class);
                                     diseasesList.add(diseases);

                                     diseaseRecyclerAdaptor.notifyDataSetChanged();

                                 }


                             }


                         }catch (Exception ex){

                             disList.removeAllViews();
                         }

                        }
                    });





                }else{
                    Toast.makeText(HealthIssueActivity.this, "Please enter the disease name", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }
}
