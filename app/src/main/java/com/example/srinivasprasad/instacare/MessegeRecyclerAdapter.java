package com.example.srinivasprasad.instacare;


import android.content.Context;

import android.support.annotation.NonNull;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.srinivasprasad.instacare.R.layout.msg_left_layout;


public class MessegeRecyclerAdapter extends RecyclerView.Adapter<MessegeRecyclerAdapter.ViewHolder> {

    public List<Messege> messege_list=null;
    public FirebaseFirestore firebaseFirestore;
    public FirebaseAuth firebaseAuth;
    public Context context;
    public  String curUserId;

    public Boolean RightMsgImage=false;
    public Boolean LeftMsgImage=false;


    public MessegeRecyclerAdapter(List<Messege> messege_list){

        this.messege_list=messege_list;

    }

    @Override
    public int getItemViewType(int position) {
        firebaseAuth= FirebaseAuth.getInstance();
        curUserId = firebaseAuth.getCurrentUser().getUid();
        if (curUserId.equals(messege_list.get(position).getUser_id())){

            if (messege_list.get(position).getImage_url()!=null){

                RightMsgImage=true;
                return 3;
            }else {
                return 1;
            }
        }else
        {
            if (messege_list.get(position).getImage_url()!=null){

                LeftMsgImage=true;
                return 4;
            }else {
                return 2;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int layoutRes=0;
        context = parent.getContext();
        firebaseAuth = FirebaseAuth.getInstance();
        curUserId = firebaseAuth.getCurrentUser().getUid();

        switch (viewType){
            case 1:
                layoutRes=R.layout.msg_right_layout;
                break;
            case 2:
                layoutRes= msg_left_layout;
                break;
            case 3:
                layoutRes=R.layout.image_right_layout;
                break;
            case 4:
                layoutRes=R.layout.image_left_layout;
                break;
        }


        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return new ViewHolder(view);


    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {


        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();


        final String user_id = messege_list.get(position).getUser_id();

        if(curUserId.equals(messege_list.get(position).getUser_id())){

            if(messege_list.get(position).getImage_url()!=null){


                firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){

                            holder.setRIGHTMyImgUserName(task.getResult().get("name").toString(), task.getResult().get("image").toString(), task.getResult().get("thumb_url").toString());

                        }else{
                            String Er = task.getException().getMessage();
                            Toast.makeText(context, "Error:" + Er, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                String imgDesc=messege_list.get(position).getDesc();
                holder.setMyImageDesc(imgDesc);

                try {
                    String msgTime = messege_list.get(position).getTimestamp().toString();
                    holder.setImageRightTime(msgTime);

                } catch (Exception ex) {


                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    holder.setImageRightTime(currentDateTimeString);

                }

                String imageUrl=messege_list.get(position).getImage_url();
                String imageThumbUrl=messege_list.get(position).getImage_thumb();
                holder.setImageRightImage(imageUrl,imageThumbUrl);


            }else {


                firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                        if (task.isSuccessful()) {

                            holder.setMyImgUserName(task.getResult().get("name").toString(), task.getResult().get("image").toString(), task.getResult().get("thumb_url").toString());

                        } else {
                            String Er = task.getException().getMessage();
                            Toast.makeText(context, "Error:" + Er, Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                String userMessege = messege_list.get(position).getMessege();
                holder.setMyUserMsg(userMessege);


                try {
                    String msgTime = messege_list.get(position).getTimestamp().toString();
                    holder.setMyMsgTime(msgTime);

                } catch (Exception ex) {


                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    holder.setMyMsgTime(currentDateTimeString);

                }

            }



        }else {


            if (messege_list.get(position).getImage_url()!=null){



                firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                        if (task.isSuccessful()) {
                            holder.setLeftImageImgName(task.getResult().get("name").toString(), task.getResult().get("image").toString(), task.getResult().get("thumb_url").toString());
                        } else {
                            String Er = task.getException().getMessage();
                            Toast.makeText(context, "Error:" + Er, Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                try {
                    String imageDesc = messege_list.get(position).getDesc();
                    holder.setLeftImageDesc(imageDesc);
                }catch (Exception e){

                }
                try {
                    String msgTime = messege_list.get(position).getTimestamp().toString();
                    holder.setLeftMsgTime(msgTime);
                } catch (Exception ex) {

                }

                String ImageUrl=messege_list.get(position).getImage_url();
                String ImageThumbUrl=messege_list.get(position).getImage_thumb();

                try {
                    holder.setLeftchatImage(ImageUrl, ImageThumbUrl);
                }catch (Exception e){

                }


            }else {


                firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                        if (task.isSuccessful()) {
                            holder.setImgUserName(task.getResult().get("name").toString(), task.getResult().get("image").toString(), task.getResult().get("thumb_url").toString());
                        } else {
                            String Er = task.getException().getMessage();
                            Toast.makeText(context, "Error:" + Er, Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                String userMessege = messege_list.get(position).getMessege();
                holder.setUserMsg(userMessege);


                try {
                    String msgTime = messege_list.get(position).getTimestamp().toString();
                    holder.setMsgTime(msgTime);
                } catch (Exception ex) {

                    Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
                }
            }

        }


    }

    @Override
    public int getItemCount() {
        return messege_list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;

        private TextView UserMsg;
        private TextView UserTime;
        private TextView UserName;
        private CircleImageView UserImg;

        private TextView MyMsg;
        private TextView MyTime;
        private TextView MyName;
        private CircleImageView MyImg;


        private TextView ImageUserMsg;
        private TextView ImageUserTime;
        private TextView ImageUserName;
        private CircleImageView ImageUserImg;
        private ImageView ImageChatUserImg;


        private ImageView ImageChatMyImg;
        private TextView ImageMyMsg;
        private TextView ImageMyTime;
        private TextView ImagemyName;
        private CircleImageView ImageMyImg;


        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setUserMsg(String userMessege) {

            UserMsg=mView.findViewById(R.id.msgLeftTextMsg);
            UserMsg.setText(userMessege);
        }

        public void setMsgTime(String msgTime) {
            UserTime = mView.findViewById(R.id.msgLeftTimeStamp);
            UserTime.setText(msgTime);
        }

        public void setImgUserName(String name, String image, String thumb_url) {


            RequestOptions placeholderRequest = new RequestOptions();
            placeholderRequest.placeholder(R.drawable.profile_placeholder);

            //left messege
            UserName = mView.findViewById(R.id.msgLeftUserName);
            UserImg = mView.findViewById(R.id.msgLeftcircleImageView);
            UserName.setText(name);
            Glide.with(context).applyDefaultRequestOptions(placeholderRequest).load(image).thumbnail(Glide.with(context).load(thumb_url)).into(UserImg);



        }

        public void setMyImgUserName(String name, String image, String thumb_url) {

            RequestOptions placeholderRequest = new RequestOptions();
            placeholderRequest.placeholder(R.drawable.profile_placeholder);


            MyName=mView.findViewById(R.id.msgRytUserName);
            MyImg=mView.findViewById(R.id.msgRytcircleImageView);

            MyName.setText(name);
            Glide.with(context).applyDefaultRequestOptions(placeholderRequest).load(image).thumbnail(Glide.with(context).load(thumb_url)).into(MyImg);

        }

        public void setMyUserMsg(String userMessege) {

            MyMsg=mView.findViewById(R.id.msgRytTextMsg);
            MyMsg.setText(userMessege);

        }

        public void setMyMsgTime(String msgTime) {
            MyTime = mView.findViewById(R.id.msgRytTimeStamp);
            MyTime.setText(msgTime);
        }

        public void setRIGHTMyImgUserName(String name, String image, String thumb_url) {

            RequestOptions placeholderRequest = new RequestOptions();
            placeholderRequest.placeholder(R.drawable.profile_placeholder);

            ImageMyImg=mView.findViewById(R.id.right_image_user_image);
            ImagemyName=mView.findViewById(R.id.right_image_user_name);

            ImagemyName.setText(name);
            Glide.with(context).applyDefaultRequestOptions(placeholderRequest).load(image).thumbnail(Glide.with(context).load(thumb_url)).into(ImageMyImg);


        }

        public void setMyImageDesc(String imgDesc) {

            ImageMyMsg=mView.findViewById(R.id.right_image_imgDesc);
            ImageMyMsg.setText(imgDesc);
        }

        public void setImageRightTime(String msgTime) {

        ImageMyTime=mView.findViewById(R.id.right_image_msgtime);
        ImageMyTime.setText(msgTime);
        }

        public void setImageRightImage(String imageUrl, String imageThumbUrl) {

            RequestOptions placeholderRequest = new RequestOptions();
            placeholderRequest.placeholder(R.drawable.image_placeholder);

            ImageChatMyImg=mView.findViewById(R.id.right_image_chatimg);
            Glide.with(context).applyDefaultRequestOptions(placeholderRequest).load(imageUrl).thumbnail(Glide.with(context).load(imageThumbUrl)).into(ImageChatMyImg);


        }

        public void setLeftImageImgName(String name, String image, String thumb_url) {

            RequestOptions placeholderRequest = new RequestOptions();
            placeholderRequest.placeholder(R.drawable.profile_placeholder);

            ImageUserImg=mView.findViewById(R.id.left_image_user_img);
            ImageUserName=mView.findViewById(R.id.left_image_user_name);

            ImageUserName.setText(name);
            Glide.with(context).applyDefaultRequestOptions(placeholderRequest).load(image).thumbnail(Glide.with(context).load(thumb_url)).into(ImageUserImg);

        }

        public void setLeftImageDesc(String imageDesc) {

            ImageUserMsg=mView.findViewById(R.id.left_image_chat_imgDesc);
            ImageUserMsg.setText(imageDesc);
        }

        public void setLeftMsgTime(String msgTime) {

            ImageUserTime=mView.findViewById(R.id.left_image_userTime);
            ImageUserTime.setText(msgTime);

        }

        public void setLeftchatImage(String imageUrl, String imageThumbUrl) {

            RequestOptions placeholderRequest = new RequestOptions();
            placeholderRequest.placeholder(R.drawable.image_placeholder);

            ImageChatUserImg=mView.findViewById(R.id.left_image_chat_img);
            Glide.with(context).applyDefaultRequestOptions(placeholderRequest).load(imageUrl).thumbnail(Glide.with(context).load(imageThumbUrl)).into(ImageChatUserImg);


        }



    }


}