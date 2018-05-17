package com.example.srinivasprasad.instacare;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersRecyclerAdapator  extends RecyclerView.Adapter<UsersRecyclerAdapator.ViewHolder>{

    public List<Users>  usersList;
    public Context context;
    FirebaseFirestore firebaseFirestore;

    public UsersRecyclerAdapator(List<Users> usersList){

        this.usersList=usersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);
        context=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String Username=usersList.get(position).getName();
        holder.setUserName(Username);

        String Bloodgroup = usersList.get(position).getBlood_group();
        holder.setBloodGroup(Bloodgroup);

        String Local=usersList.get(position).getLocality();
        holder.setLocality(Local.toUpperCase());

        String image = usersList.get(position).getImage();
        String thumb = usersList.get(position).getThumb_url();
        holder.setImage(image,thumb);

        final String user_id = usersList.get(position).getDoc_id();
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(context, user_id, Toast.LENGTH_LONG).show();
                Intent infoIntent = new Intent(context,UserInfoActivity.class);
                infoIntent.putExtra("DOC_ID",user_id);
                context.startActivity(infoIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{


        private View mView;

        private TextView UserName;
        private TextView BloodGroup;
        private TextView Locality;
        private CircleImageView UserImage;


        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setUserName(String username) {

            UserName=mView.findViewById(R.id.sUserName);
            UserName.setText(username);
        }

        public void setBloodGroup(String bloodgroup) {

        BloodGroup=mView.findViewById(R.id.sBloodGroup);
        BloodGroup.setText(bloodgroup);

        }

        public void setLocality(String local) {

        Locality=mView.findViewById(R.id.sLocality);
        Locality.setText(local);

        }

        public void setImage(String image, String thumb) {

            UserImage=mView.findViewById(R.id.sUserImage);

            RequestOptions placeholderRequest = new RequestOptions();
            placeholderRequest.placeholder(R.drawable.default_avatar);
            Glide.with(context).setDefaultRequestOptions(placeholderRequest).load(image).thumbnail(Glide.with(context).load(thumb)).into(UserImage);

        }
    }

}
