package com.example.srinivasprasad.instacare;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.List;

public class DiseaseRecyclerAdaptor  extends RecyclerView.Adapter<DiseaseRecyclerAdaptor.ViewHolder> {


    public List<Diseases> diseasesList;
    public Context context;
    FirebaseFirestore firebaseFirestore;

    DiseaseRecyclerAdaptor(List<Diseases> diseasesList){
        this.diseasesList=diseasesList;

    }

    @NonNull
    @Override
    public DiseaseRecyclerAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        firebaseFirestore=FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);
        context=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.disease_list_layout,parent,false);

        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiseaseRecyclerAdaptor.ViewHolder holder, int position) {

        final String dname=diseasesList.get(position).getDis_name();
        holder.setDiseaseName(dname.toUpperCase());

        final String doc_id = diseasesList.get(position).getDoc_id();
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent detailIntent = new Intent(context,DiseaseDetailActivity.class);
                detailIntent.putExtra("DOC_ID",doc_id);
                detailIntent.putExtra("D_NAME",dname);
                context.startActivity(detailIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return diseasesList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView DiseaseName;

        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }


        public void setDiseaseName(String disName) {

            DiseaseName=mView.findViewById(R.id.disease_name);
            DiseaseName.setText(disName);

        }
    }

}
