package com.santiago.fitsforever;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecycleActivitiesAdapter extends RecyclerView.Adapter<RecycleActivitiesAdapter.MyViewHolder> {
    Context context;
    ArrayList<UserActivities> userActivitiesArrayList;

    public RecycleActivitiesAdapter(Context context, ArrayList<UserActivities> userActivitiesArrayList) {
        this.context = context;
        this.userActivitiesArrayList = userActivitiesArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.activities_recycle, parent, false);
        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserActivities userActivities = userActivitiesArrayList.get(position);
//        holder.textView.setText(userActivities.text);
        holder.actName.setText(userActivities.nameAct);
        holder.typeExc.setText(userActivities.excType);
        holder.desc.setText(userActivities.desc);
        holder.date.setText(userActivities.date);
        holder.clock.setText(userActivities.time);
    }

    @Override
    public int getItemCount() {
        return userActivitiesArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView actName, typeExc, desc;
        Button date, clock;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            actName = itemView.findViewById(R.id.actName);
            typeExc = itemView.findViewById(R.id.typeExc);
            desc = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date);
            clock = itemView.findViewById(R.id.clock);

        }
    }
}
