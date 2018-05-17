package com.example.srinivasprasad.instacare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class ChatActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private String current_user_id;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    private EditText textMsg;
    private ImageView sendBtn;

    private RecyclerView msg_list_view;
    private List<Messege> messege_list;
    private MessegeRecyclerAdapter messegeRecyclerAdapter;

    private MediaPlayer mediaPlayer;
    private Boolean sound=true;

    private FrameLayout imageFrameLayout;
    private ImageView addImageBtn;
    private TextView cancelFrame;

    private ProgressBar imageProgress;
    private ImageView imagePost;
    private EditText imageDesc;
    private TextView imageSendbtn;
    private TextView uploadText;

    private Uri chatImageUri = null;
    private Bitmap compressedImageFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messege_list=new ArrayList<>();
        msg_list_view=findViewById(R.id.msg_list_view);
        messegeRecyclerAdapter=new MessegeRecyclerAdapter(messege_list);
        msg_list_view.setLayoutManager(new LinearLayoutManager(this));
        msg_list_view.setAdapter(messegeRecyclerAdapter);


        mainToolbar = findViewById(R.id.main_toolBar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);

        storageReference= FirebaseStorage.getInstance().getReference();

        textMsg=findViewById(R.id.chat_messge);
        sendBtn=findViewById(R.id.send_btn);

        //frame Layout
        addImageBtn=findViewById(R.id.add_image_btn);
        cancelFrame=findViewById(R.id.image_cancel);
        imageFrameLayout=findViewById(R.id.image_fragment);

        imageProgress=findViewById(R.id.image_progress);
        imageDesc=findViewById(R.id.image_desc);
        imagePost=findViewById(R.id.image_post);
        imageSendbtn=findViewById(R.id.image_send_btn);
        uploadText=findViewById(R.id.upload_txt);

        imageFrameLayout.setVisibility(View.INVISIBLE);


        imagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .setAspectRatio(4, 3)
                        .start(ChatActivity.this);
            }
        });

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageFrameLayout.setVisibility(View.VISIBLE);
                textMsg.setEnabled(false);
                sendBtn.setEnabled(false);
            }
        });

        cancelFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageFrameLayout.setVisibility(View.INVISIBLE);
                textMsg.setEnabled(true);
                sendBtn.setEnabled(true);
                chatImageUri=null;
                imageDesc.setText(null);

            }
        });



        //Retrieving the messege and passing to Messege class-----------------------------------------------
        firebaseFirestore.collection("Chats").orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                try {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {


                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            Messege messege = doc.getDocument().toObject(Messege.class);
                            messege_list.add(messege);

                            messegeRecyclerAdapter.notifyDataSetChanged();
                            try {
                                if (sound) {
                                    mediaPlayer = MediaPlayer.create(ChatActivity.this, R.raw.received_message);
                                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            mediaPlayer.start();
                                        }
                                    });
                                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            mediaPlayer.release();
                                        }
                                    });
                                }else {
                                    sound=true;
                                }
                            }catch (Exception es){

                            }

                            msg_list_view.scrollToPosition(messegeRecyclerAdapter.getItemCount()-1);

                        }
                    }
                }catch (Exception ex){

                }
            }
        });




        //Adding the messages
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                sound=false;
                mediaPlayer = MediaPlayer.create(ChatActivity.this, R.raw.message_sent);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaPlayer.start();
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mediaPlayer.release();
                    }
                });
                String txtMsg = textMsg.getText().toString();

                if (!TextUtils.isEmpty(txtMsg)){


                    Map<String,Object> chatMap=new HashMap<>();
                    chatMap.put("messege",txtMsg);
                    chatMap.put("user_id",current_user_id);
                    chatMap.put("timestamp", FieldValue.serverTimestamp());

                    firebaseFirestore.collection("Chats").add(chatMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            if (task.isSuccessful()){

                                textMsg.setText(null);
                                Toast.makeText(ChatActivity.this, "Messege sent successfully", Toast.LENGTH_SHORT).show();

                            }else{

                                String Err=task.getException().getMessage();
                                Toast.makeText(ChatActivity.this, ""+Err, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



                }else{
                    Toast.makeText(ChatActivity.this, "Enter the messege", Toast.LENGTH_SHORT).show();
                }

            }
        });


        //Adding Images-------------------------------------------------------------------------------------------------

        imageSendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sound=false;
                mediaPlayer = MediaPlayer.create(ChatActivity.this, R.raw.message_sent);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaPlayer.start();
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mediaPlayer.release();
                    }
                });

                final String description=imageDesc.getText().toString();

                if(!TextUtils.isEmpty(description) && chatImageUri!=null){

                    imageProgress.setVisibility(View.VISIBLE);
                    uploadText.setVisibility(View.VISIBLE);
                    imageDesc.setEnabled(false);


                    final String randomName= UUID.randomUUID().toString();
                    StorageReference filePath= storageReference.child("chat_images").child(randomName+".jpg");
                    final String finalDescription = description;
                    filePath.putFile(chatImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            final String downloadUri= task.getResult().getDownloadUrl().toString();

                            if (task.isSuccessful()){

                                File newImageFile =new File(chatImageUri.getPath());

                                try{

                                    compressedImageFile = new Compressor(ChatActivity.this)
                                            .setMaxHeight(75)
                                            .setMaxWidth(100)
                                            .setQuality(2)
                                            .compressToBitmap(newImageFile);

                                }catch (Exception exp){

                                    String exErr=task.getException().getMessage();
                                    Toast.makeText(ChatActivity.this, "Error"+exErr, Toast.LENGTH_SHORT).show();
                                }

                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                compressedImageFile.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                                byte[] thumbData = byteArrayOutputStream.toByteArray();

                                UploadTask uploadTask=  storageReference.child("chat_images/thumbs").child(randomName+".jpg").putBytes(thumbData);
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        String downloadThumbUri = taskSnapshot.getDownloadUrl().toString();

                                        Map<String, Object> chatImageMap= new HashMap<>();
                                        chatImageMap.put("image_url",downloadUri);
                                        chatImageMap.put("thumb",downloadThumbUri);
                                        chatImageMap.put("desc", description);
                                        chatImageMap.put("user_id",current_user_id);
                                        chatImageMap.put("timestamp", FieldValue.serverTimestamp());

                                        firebaseFirestore.collection("Chats").add(chatImageMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                                if (task.isSuccessful()){

                                                    Toast.makeText(ChatActivity.this, "Image Added Successfully", Toast.LENGTH_SHORT).show();
                                                    imageFrameLayout.setVisibility(View.INVISIBLE);
                                                    textMsg.setEnabled(true);
                                                    sendBtn.setEnabled(true);
                                                    chatImageUri=null;
                                                    imageDesc.setText(null);
                                                    imageDesc.setEnabled(true);



                                                }else{

                                                    String errmsg=task.getException().getMessage();
                                                    Toast.makeText(ChatActivity.this, ""+errmsg, Toast.LENGTH_SHORT).show();
                                                }
                                                imageProgress.setVisibility(View.INVISIBLE);
                                                uploadText.setVisibility(View.INVISIBLE);
                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        String er=e.getMessage();
                                        Toast.makeText(ChatActivity.this, "Error:"+er, Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }else{

                                imageProgress.setVisibility(View.INVISIBLE);
                                uploadText.setVisibility(View.INVISIBLE);
                            }

                        }
                    });

                }else{

                    Toast.makeText(ChatActivity.this, "Add image", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser =mAuth.getCurrentUser();
        if (currentUser==null){
            Intent intent = new Intent(ChatActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }else{


            current_user_id=mAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){

                        if(!task.getResult().exists()){

                            Intent intent=new Intent(ChatActivity.this,AccountSetupActivity.class);
                            startActivity(intent);
                            // finish();

                        }

                    }else {
                        String er=task.getException().getMessage();
                        Toast.makeText(ChatActivity.this, "Error:"+er, Toast.LENGTH_SHORT).show();

                    }
                }
            });




        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_logout_btn:
                logOut();
                return true;

            case R.id.action_accsetup_btn:

                Intent settingsIntent = new Intent(ChatActivity.this, AccountSetupActivity.class);
                startActivity(settingsIntent);
                return true;

            default:
                return false;


        }

    }

    private void logOut() {
        mAuth.signOut();
        Intent intent = new Intent(ChatActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                chatImageUri=result.getUri();

                imagePost.setImageURI(chatImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }

}


