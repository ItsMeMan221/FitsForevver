package com.santiago.fitsforever;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterLose extends RecyclerView.Adapter<AdapterLose.GainViewHolder> {
    Context context;
    ArrayList<AllPlan> gainArrayList;

    public AdapterLose(Context context, ArrayList<AllPlan> gainArrayList) {
        this.context = context;
        this.gainArrayList = gainArrayList;
    }

    @NonNull
    @Override
    public AdapterLose.GainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycle_lose, parent, false);
        return new GainViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLose.GainViewHolder holder, int position) {
        AllPlan allPlan = gainArrayList.get(position);
        holder.planName.setText(allPlan.name);
        holder.durationPlan.setText(allPlan.duration + " weeks");
        Picasso.get().load(allPlan.imageUrl).into(holder.planImage);
    }

    @Override
    public int getItemCount() {
        return gainArrayList.size();
    }

    public class GainViewHolder extends RecyclerView.ViewHolder {
        ImageView planImage;
        TextView planName;
        Button durationPlan;

        public GainViewHolder(@NonNull View itemView) {
            super(itemView);
            planImage = itemView.findViewById(R.id.imagePlan);
            planName = itemView.findViewById(R.id.planName);
            durationPlan = itemView.findViewById(R.id.planDuration);
        }
    }
}
