package com.example.srinivasprasad.instacare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class AccountSetupActivity extends AppCompatActivity {

    private CircleImageView setupImage;
    private Uri mainImageURI = null;

    private String user_id;

    private boolean isChanged = false;

    private EditText setupName;
    private TextView setupBloodGroup;
    private EditText setupPhoneNum;
    private EditText setupAddress;
    private EditText setupLocality;
    private Button setupBtn;
    private Button setBgBtn;
    private Spinner bgSelector;
    private ProgressBar setupProgress;

    private Bitmap compressedImageFile;

    //database
    private DatabaseReference mDatabase;

    //fireStore
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);


        Toolbar setupToolbar = findViewById(R.id.setup_toolBar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);

        storageReference = FirebaseStorage.getInstance().getReference();


        setupImage = findViewById(R.id.setup_img);
        setupBloodGroup= findViewById(R.id.setup_bloodGroup);
        setupPhoneNum= findViewById(R.id.setup_phoneNumber);
        setupName = findViewById(R.id.setup_username);
        setupAddress=findViewById(R.id.setup_address);
        setupLocality=findViewById(R.id.setup_locality);
        setupBtn = findViewById(R.id.setup_btn);
        setupProgress = findViewById(R.id.setup_progress);

        bgSelector=findViewById(R.id.spinner_bloodGroup);
        setBgBtn=findViewById(R.id.bg_set_btn);

        setupProgress.setVisibility(View.VISIBLE);
        setupBtn.setEnabled(false);


        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                setupImage.setEnabled(false);
                setBgBtn.setEnabled(false);
                setupName.setEnabled(false);
                setupPhoneNum.setEnabled(false);
                setupAddress.setEnabled(false);
                setupLocality.setEnabled(false);

                if(task.isSuccessful()){


                    if(task.getResult().exists()){

                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");
                        String blood_group=task.getResult().getString("blood_group");
                        String phoneno=task.getResult().getString("phone_num");
                        String image_thumb= task.getResult().getString("thumb_url");
                        String address=task.getResult().getString("address");
                        String locality=task.getResult().getString("locality");



                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_avatar);
                        Glide.with(AccountSetupActivity.this).setDefaultRequestOptions(placeholderRequest).load(image)
                                .thumbnail(Glide.with(AccountSetupActivity.this).load(image_thumb)).into(setupImage);

                        setupName.setText(name);
                        setupBloodGroup.setText(blood_group);
                        setupPhoneNum.setText(phoneno);
                        setupAddress.setText(address);
                        setupLocality.setText(locality.toLowerCase());


                    }

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(AccountSetupActivity.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();

                }

                setupProgress.setVisibility(View.INVISIBLE);

                setupImage.setEnabled(true);
                setBgBtn.setEnabled(true);
                setupName.setEnabled(true);
                setupPhoneNum.setEnabled(true);
                setupAddress.setEnabled(true);
                setupLocality.setEnabled(true);
                setupBtn.setEnabled(true);

            }
        });


        setBgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bGroupTxt=String.valueOf(bgSelector.getSelectedItem());
                setupBloodGroup.setText(bGroupTxt);
            }
        });

        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user_name = setupName.getText().toString();
                final String user_bloodGroup=setupBloodGroup.getText().toString();
                final String user_phno=setupPhoneNum.getText().toString();
                final String user_address=setupAddress.getText().toString();
                final String user_locality=setupLocality.getText().toString().toLowerCase();

                if (!TextUtils.isEmpty(user_name) && mainImageURI != null && !TextUtils.isEmpty(user_bloodGroup) && !TextUtils.isEmpty(user_phno) && !TextUtils.isEmpty(user_address)&& !TextUtils.isEmpty(user_locality) ) {

                    setupProgress.setVisibility(View.VISIBLE);

                    setupImage.setEnabled(false);
                    setBgBtn.setEnabled(false);
                    setupName.setEnabled(false);
                    setupPhoneNum.setEnabled(false);
                    setupAddress.setEnabled(false);
                    setupLocality.setEnabled(false);
                    setupBtn.setEnabled(false);

                    final String randomName= UUID.randomUUID().toString();

                    if (isChanged) {

                        user_id = firebaseAuth.getCurrentUser().getUid();

                        StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");
                        image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {


                                    File newImageFile =new File(mainImageURI.getPath());

                                    try {
                                        compressedImageFile = new Compressor(AccountSetupActivity.this)
                                                .setMaxHeight(100)
                                                .setMaxWidth(100)
                                                .setQuality(2)
                                                .compressToBitmap(newImageFile);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                                    byte[] thumbData = byteArrayOutputStream.toByteArray();

                                    UploadTask uploadTask=  storageReference.child("profile_images/thumbs").child(randomName+".jpg").putBytes(thumbData);
                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            String downloadThumbUri = taskSnapshot.getDownloadUrl().toString();

                                            storeFirestore(task, user_name,user_bloodGroup,user_phno,downloadThumbUri,user_address,user_locality);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });

                                    //   storeFirestore(task, user_name,user_usn,user_phno);

                                } else {

                                    String error = task.getException().getMessage();
                                    Toast.makeText(AccountSetupActivity.this, "(IMAGE Error) : " + error, Toast.LENGTH_LONG).show();

                                    setupImage.setEnabled(true);
                                    setBgBtn.setEnabled(true);
                                    setupName.setEnabled(true);
                                    setupPhoneNum.setEnabled(true);
                                    setupAddress.setEnabled(true);
                                    setupLocality.setEnabled(true);
                                    setupBtn.setEnabled(true);

                                    setupProgress.setVisibility(View.INVISIBLE);

                                }
                            }
                        });

                    } else {

                        storeFirestore(null, user_name,user_bloodGroup,user_phno,null,user_address,user_locality);

                    }

                }else{
                    Toast.makeText(AccountSetupActivity.this, "Complete the fields", Toast.LENGTH_SHORT).show();
                    Toast.makeText(AccountSetupActivity.this, "If you want update the details, please change your profile image too", Toast.LENGTH_SHORT).show();
                }

            }

        });



        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(AccountSetupActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        //Toast.makeText(AccountSetupActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(AccountSetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    }else{
                        BringImagePicker();
                    }
                }else{
                    BringImagePicker();
                }
            }
        });

    }

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, String user_name, String user_bloodgroup, String user_phno, final String thumb_uri, String user_address, final String user_locality) {

        setupProgress.setVisibility(View.VISIBLE);
        final Uri download_uri;

        if(task != null) {

            download_uri = task.getResult().getDownloadUrl();

        } else {

            download_uri = mainImageURI;

        }

        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", user_name);
        userMap.put("image", download_uri.toString());
        userMap.put("blood_group",user_bloodgroup);
        userMap.put("phone_num",user_phno);
        userMap.put("thumb_url",thumb_uri);
        userMap.put("address",user_address);
        userMap.put("locality",user_locality);
        userMap.put("doc_id",user_id);



        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    setupProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(AccountSetupActivity.this, "The user Settings are updated.", Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(AccountSetupActivity.this, HomeActivity.class);
                    startActivity(mainIntent);
                    finish();


                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(AccountSetupActivity.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();

                }

                setupImage.setEnabled(true);
                setBgBtn.setEnabled(true);
                setupName.setEnabled(true);
                setupPhoneNum.setEnabled(true);
                setupAddress.setEnabled(true);
                setupLocality.setEnabled(true);
                setupBtn.setEnabled(true);

                setupProgress.setVisibility(View.INVISIBLE);

            }
        });


    }

    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(AccountSetupActivity.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                setupImage.setImageURI(mainImageURI);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }

}