package com.example.echovision;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

//import com.mashood.kaudisorders.R;
//import com.mashood.kaudisorders.disorder.DisorderListActivity;
//import com.squareup.picasso.Picasso;

//import com.example.wecan.ui.dashboard.DashboardFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class useruploadadapter extends RecyclerView.Adapter<useruploadadapter.MyViewHolder> {


    private LayoutInflater inflater;
    private ArrayList<useruploadmodel> dataModelArrayList;
    private Context c;

    public useruploadadapter(Context ctx, ArrayList<useruploadmodel> dataModelArrayList) {
        c = ctx;
        inflater = LayoutInflater.from(c);
        this.dataModelArrayList = dataModelArrayList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.useruploadlist, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        final useruploadmodel omodel = dataModelArrayList.get(position);
        Picasso.get().load(config.imgurl + omodel.getPersonimage()).into(holder.image);


        holder.name.setText( "name:"+dataModelArrayList.get(position).getPersonname());
//        holder.exp.setText("Experience:"+ dataModelArrayList.get(position).getExperience());
//        holder.hos.setText( "Hospital:"+dataModelArrayList.get(position).getHospital());

//        holder.b1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(c, doctorfullactivity.class);
//                intent.putExtra("id",dataModelArrayList.get(position).getId());
//                intent.putExtra("name",dataModelArrayList.get(position).getName() );
//                intent.putExtra("district", dataModelArrayList.get(position).getDistrict());
//                intent.putExtra("email", dataModelArrayList.get(position).getEmail());
//                intent.putExtra("phonenumber",dataModelArrayList.get(position).getPhonenumber());
//                intent.putExtra("gender",dataModelArrayList.get(position).getGender());
//                intent.putExtra("hospital",dataModelArrayList.get(position).getHospital());
//                intent.putExtra("experience",dataModelArrayList.get(position).getExperience());
//                intent.putExtra("image",dataModelArrayList.get(position).getImage());
//
//
//                c.startActivity(intent);
//
////                        if (!dataModelArrayList.get(position).getImage().equals("")) {
////            Picasso.get.load(config.imgurl+dataModelArrayList.get(position).getImage()).into(holder.image);
//            }
//
//        });

    }


    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }


    public void filterList(ArrayList<useruploadmodel> filteredSongs) {
        this.dataModelArrayList = filteredSongs;
        notifyDataSetChanged();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {


        public
        CardView producta;
        TextView name,exp,hos;
        ImageView image,delete;
        Button b1;

        String id,cname;


        public MyViewHolder(View itemView) {
            super(itemView);
            producta =itemView.findViewById(R.id.uploadcardlist);
            name = itemView.findViewById(R.id.nameuploadlist);
            image= itemView.findViewById(R.id.imageuploadlist);


        }

    }
}