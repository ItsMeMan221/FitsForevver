package com.santiago.fitsforever;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RecycleActivitiesAdapter extends RecyclerView.Adapter<RecycleActivitiesAdapter.MyViewHolder> {
    Context context;
    ArrayList<UserActivities> userActivitiesArrayList;
    FirebaseFirestore db;
    private DataListener dataListener;

    public RecycleActivitiesAdapter(Context context, ArrayList<UserActivities> userActivitiesArrayList, DataListener dataListener) {
        this.context = context;
        this.userActivitiesArrayList = userActivitiesArrayList;
        this.dataListener = dataListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.activities_recycle, parent, false);
        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        db = FirebaseFirestore.getInstance();
        UserActivities userActivities = userActivitiesArrayList.get(position);
        holder.actName.setText(userActivities.nameAct);
        holder.typeExc.setText(userActivities.excType);
        holder.desc.setText(userActivities.desc);
        holder.date.setText(userActivities.date);
        holder.clock.setText(userActivities.time);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteActivities(userActivities.uId);

            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                String Uid = userActivities.uId.toString();
                String excType = userActivities.excType.toString();
                Fragment fragmentAddActivities = FragmentAddActivities.newInstance(Uid, excType);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentAddActivities).addToBackStack(null).commit();

            }
        });


    }

    public void update(ArrayList<UserActivities> userActivities) {

        this.userActivitiesArrayList = userActivities;
        this.notifyDataSetChanged();

    }

    private void deleteActivities(String Uid) {
        new AlertDialog.Builder(context)
                .setMessage("Are you sure to delete this activity?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.collection("UserActivity").document(Uid)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context.getApplicationContext(), "Activity has been deleted", Toast.LENGTH_LONG).show();
                                        dataListener.onChangedListener();
                                        notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context.getApplicationContext(), "Activity cannot be deleted" + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });

                    }
                })
                .setNegativeButton("No", null)
                .show();

    }

    @Override
    public int getItemCount() {
        return userActivitiesArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView actName, typeExc, desc;
        Button date, clock;
        Button edit, delete;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            actName = itemView.findViewById(R.id.actName);
            typeExc = itemView.findViewById(R.id.typeExc);
            desc = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date);
            clock = itemView.findViewById(R.id.clock);
            edit = itemView.findViewById(R.id.Edit);
            delete = itemView.findViewById(R.id.Delete);

        }
    }
}
