package com.example.srinivasprasad.instacare;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BloodSearchActivity extends AppCompatActivity{

    private EditText localitytxt;
    private Button bloodSerchBtn;
    private Spinner bloodSelector;

    private RecyclerView mUsersList;
    private List<Users> usersList;
    private UsersRecyclerAdapator usersRecyclerAdapator;

   // private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
   // private LinearLayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_search);

        Toolbar BsToolbar = findViewById(R.id.bs_toolbar);
        setSupportActionBar(BsToolbar);
        getSupportActionBar().setTitle("Blood Search");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);

        usersList=new ArrayList<>();
        usersRecyclerAdapator=new UsersRecyclerAdapator(usersList);
        mUsersList= findViewById(R.id.users_list);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));
        mUsersList.setAdapter(usersRecyclerAdapator);

       bloodSelector=findViewById(R.id.blood_spinner);
        localitytxt=findViewById(R.id.bs_mLocality);
        bloodSerchBtn=findViewById(R.id.bs_mSearch);

        firebaseFirestore.collection("Users").orderBy("locality",Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                try {

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {


                            Users users = doc.getDocument().toObject(Users.class);
                            usersList.add(users);

                            usersRecyclerAdapator.notifyDataSetChanged();

                        }

                    }
                    usersList.clear();
                } catch (Exception ex) {
                    mUsersList.removeAllViews();
                    // Toast.makeText(BloodSearchActivity.this, "Enter the details", Toast.LENGTH_SHORT).show();
                }

            }
        });


        bloodSerchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String local = localitytxt.getText().toString().toLowerCase();
                String bgroup = String.valueOf(bloodSelector.getSelectedItem());
                usersList.clear();
                //mUsersList.removeAllViews();

                if (!TextUtils.isEmpty(local) && !TextUtils.isEmpty(bgroup)) {


                    firebaseFirestore.collection("Users").orderBy("locality", Query.Direction.ASCENDING).startAt(local)
                            .endAt(local+"uf8ff")
                            .whereEqualTo("blood_group",bgroup)
                            .addSnapshotListener(BloodSearchActivity.this, new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                            try {

                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                    if (doc.getType() == DocumentChange.Type.ADDED) {


                                        Users users = doc.getDocument().toObject(Users.class);
                                        usersList.add(users);

                                        usersRecyclerAdapator.notifyDataSetChanged();

                                    }

                                }
                            } catch (Exception ex) {
                                mUsersList.removeAllViews();
                                // Toast.makeText(BloodSearchActivity.this, "Enter the details", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }else{

                    Toast.makeText(BloodSearchActivity.this, "Enter The Blood Group and Locality", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }



}
