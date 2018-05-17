package com.example.srinivasprasad.instacare;


import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.graphics.Color.BLACK;

public class YouTubeActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{

    YouTubePlayerView youTubePlayerView;
   // YouTubePlayer.OnInitializedListener mOnInitializedListener;


    public FirebaseFirestore firebaseFirestore;
    public FirebaseAuth firebaseAuth;

    private String document_id,disease_name;
    private TextView diseaseName;

    private ProgressBar comntProgress;
    private TextView cmtCnl,cmtSend;
    private EditText commentTxt;
    private String CurUserId;
    private CircleImageView cmtUserImage;
    private TextView cmtUserName;

    private FloatingActionButton addCommentBtn;
    private CardView commentCard;

    private RecyclerView comments_list_view;
    private List<Comments>commentsList;
    private CommentRecyclerAdapter commentRecyclerAdapter;

    private  String link1,link2,link3,temp;

    List<String> videoslist = new ArrayList<>();



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube);

        getWindow().setStatusBarColor(BLACK);

        document_id=getIntent().getStringExtra("DOCUMENT_ID");
        disease_name=getIntent().getStringExtra("DISEASE_NAME");

        diseaseName=findViewById(R.id.disNameTxt);
        diseaseName.setText(disease_name);

        addCommentBtn=findViewById(R.id.add_comment_btn);
        commentCard=findViewById(R.id.comment_card);
        commentTxt=findViewById(R.id.comment_text);
        cmtSend=findViewById(R.id.comment_send);
        cmtCnl=findViewById(R.id.comment_cancel);
        cmtUserImage=findViewById(R.id.comment_user_image);
        cmtUserName=findViewById(R.id.comment_user_name);
        comntProgress=findViewById(R.id.comment_progressBar);

        comntProgress.setVisibility(View.INVISIBLE);

        commentsList=new ArrayList<>();
        comments_list_view=findViewById(R.id.cmt_recyclerView);
        commentRecyclerAdapter=new CommentRecyclerAdapter(commentsList);
        comments_list_view.setLayoutManager(new LinearLayoutManager(this));
        comments_list_view.setAdapter(commentRecyclerAdapter);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();

        CurUserId=firebaseAuth.getCurrentUser().getUid();


        youTubePlayerView=findViewById(R.id.youtube_view);
        youTubePlayerView.initialize(YouTubeConfig.getApiKey(),this);





        firebaseFirestore.collection("Healthissues/"+document_id+"/Comments").orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                try {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {


                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            Comments comments = doc.getDocument().toObject(Comments.class);
                            commentsList.add(0,comments);

                            commentRecyclerAdapter.notifyDataSetChanged();


                            comments_list_view.scrollToPosition(0);

                        }
                    }
                }catch (Exception ex){

                }
            }
        });


        addCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCommentBtn.setVisibility(View.INVISIBLE);
                commentCard.setVisibility(View.VISIBLE);
            }
        });

        cmtCnl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCommentBtn.setVisibility(View.VISIBLE);
                commentCard.setVisibility(View.INVISIBLE);
            }
        });

        cmtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cmtMsg=commentTxt.getText().toString();

                if (!TextUtils.isEmpty(cmtMsg)){

                    commentTxt.setEnabled(false);
                    comntProgress.setVisibility(View.VISIBLE);

                    Map<String,Object> commentMap = new HashMap<>();
                    commentMap.put("comment",cmtMsg);
                    commentMap.put("user_id",CurUserId);
                    commentMap.put("timestamp", FieldValue.serverTimestamp());


                    firebaseFirestore.collection("Healthissues/"+document_id+"/Comments").add(commentMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {


                            if (task.isSuccessful()){

                                commentTxt.setText(null);
                                commentTxt.setEnabled(true);
                                addCommentBtn.setVisibility(View.VISIBLE);
                                comntProgress.setVisibility(View.INVISIBLE);
                                commentCard.setVisibility(View.INVISIBLE);

                                Toast.makeText(YouTubeActivity.this, "Comment sent successfully", Toast.LENGTH_SHORT).show();

                            }else{

                                comntProgress.setVisibility(View.INVISIBLE);
                                commentTxt.setEnabled(true);
                                String Err=task.getException().getMessage();
                                Toast.makeText(YouTubeActivity.this, ""+Err, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }else{
                    Toast.makeText(YouTubeActivity.this, "Add comment", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseFirestore.collection("Users").document(CurUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    setUserImageName(task.getResult().get("name").toString(), task.getResult().get("image").toString(), task.getResult().get("thumb_url").toString());

                }else {
                    String err=task.getException().getMessage();
                    Toast.makeText(YouTubeActivity.this, ""+err, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setUserImageName(String name, String image, String thumb_url) {

        RequestOptions placeholderRequest = new RequestOptions();
        placeholderRequest.placeholder(R.drawable.profile_placeholder);

        cmtUserName.setText(name);
        Glide.with(YouTubeActivity.this).applyDefaultRequestOptions(placeholderRequest).load(image)
                .thumbnail(Glide.with(YouTubeActivity.this).load(thumb_url)).into(cmtUserImage);

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, final boolean b) {

        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(playbackEventListener);

        firebaseFirestore.collection("Healthissues").document(document_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    link1=task.getResult().getString("yt_link1");
                    link2=task.getResult().getString("yt_link2");
                    link3=task.getResult().getString("yt_link3");
                    videoslist.add(link1);
                    videoslist.add(link2);
                    videoslist.add(link3);
                    if (!b){

                        youTubePlayer.cueVideos(videoslist);
                        //youTubePlayer.cueVideo("Q1gashLCM1I");
                    }

                }else{
                    Toast.makeText(YouTubeActivity.this, "Error"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {


    }


    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onPlaying() {

        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onStopped() {

        }

        @Override
        public void onBuffering(boolean b) {

        }

        @Override
        public void onSeekTo(int i) {

        }
    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onLoading() {

        }

        @Override
        public void onLoaded(String s) {

        }

        @Override
        public void onAdStarted() {

        }

        @Override
        public void onVideoStarted() {

        }

        @Override
        public void onVideoEnded() {

        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {

        }
    };

}
