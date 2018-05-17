package com.example.srinivasprasad.instacare;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder>{


    public List<Comments> commentsList;
    public FirebaseFirestore firebaseFirestore;
    public Context context;

    public CommentRecyclerAdapter(List<Comments> commentsList){
        this.commentsList=commentsList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();

        final String User_Id=commentsList.get(position).getUser_id();
        firebaseFirestore.collection("Users").document(User_Id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                  holder.setUserImageAndName(task.getResult().getString("name"),task.getResult().getString("image"),task.getResult().getString("thumb_url"));

                }else {
                    Toast.makeText(context, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        String comnt=commentsList.get(position).getComment();
        holder.setComments(comnt);

        try {
            String cmtTime = commentsList.get(position).getTimestamp().toString();
            holder.setCommentTime(cmtTime);

        } catch (Exception ex) {


            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            holder.setCommentTime(currentDateTimeString);

        }

    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;

        private CircleImageView UserimageView;
        private TextView CommentTime;
        private TextView CommentUserName;
        private TextView commentMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setComments(String comnt) {

        commentMessage=mView.findViewById(R.id.cmt_cmtMsg);
        commentMessage.setText(comnt);
        }

        public void setCommentTime(String cmtTime) {

            CommentTime=mView.findViewById(R.id.cmt_timeStamp);
            CommentTime.setText(cmtTime);
        }

        public void setUserImageAndName(String name, String image, String thumb_url) {

            RequestOptions placeholderRequest = new RequestOptions();
            placeholderRequest.placeholder(R.drawable.profile_placeholder);

            CommentUserName=mView.findViewById(R.id.cmt_userName);
            UserimageView=mView.findViewById(R.id.cmt_userImage);

            CommentUserName.setText(name);
            Glide.with(context).applyDefaultRequestOptions(placeholderRequest).load(image).thumbnail(Glide.with(context).load(thumb_url)).into(UserimageView);
        }
    }

}
