package com.santiago.fitsforever;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    FirebaseFirestore db;
    String planType;
    private ArrayList<AllPlan> gainPlan = new ArrayList<>();
    private ArrayList<AllPlan> losePlan = new ArrayList<>();
    AdapterGain gainAdapter;
    AdapterLose loseAdapter;
    RecyclerView gainRecycler, loseRecycler;
    ImageView recImage;
    Button durationBtn;
    TextView nameRec, descRec;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        db = FirebaseFirestore.getInstance();
        recImage = (ImageView) view.findViewById(R.id.recImage);
        durationBtn = (Button) view.findViewById(R.id.recDuration);
        nameRec = (TextView) view.findViewById(R.id.nameRec);
        descRec = (TextView) view.findViewById(R.id.descRec);
        getDataGain();
        getDataLose();
        getDataRecommend();
        gainRecycler = view.findViewById(R.id.recycleGain);
        loseRecycler = view.findViewById(R.id.recycleLose);
        gainRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        loseRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        return view;

    }

    private void getDataRecommend() {
        planType = "1";
        db.collection("Plan").whereEqualTo("planType", planType)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Picasso.get().load(document.getString("imageUrl")).into(recImage);
                                durationBtn.setText(document.getString("Duration") + " weeks");
                                nameRec.setText(document.getString("Name"));
                                descRec.setText(document.getString("desc"));
                            }
                        }
                    }
                });
    }

    private void getDataGain() {
        planType = "2";
        db.collection("Plan").whereEqualTo("planType", planType)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                AllPlan allPlan = new AllPlan(document.getString("imageUrl"),
                                        document.getString("Name"), document.getString("Duration"));
                                gainPlan.add(allPlan);
                                gainAdapter = new AdapterGain(getActivity(), gainPlan);
                                gainAdapter.notifyDataSetChanged();
                                gainRecycler.setAdapter(gainAdapter);
                            }
                        }
                    }
                });
    }

    private void getDataLose() {
        planType = "3";
        db.collection("Plan").whereEqualTo("planType", planType)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                AllPlan allPlan = new AllPlan(document.getString("imageUrl"),
                                        document.getString("Name"), document.getString("Duration"));
                                losePlan.add(allPlan);
                                Log.d("hello", String.valueOf(gainPlan.size()));
                                loseAdapter = new AdapterLose(getActivity(), losePlan);
                                loseAdapter.notifyDataSetChanged();
                                loseRecycler.setAdapter(loseAdapter);
                            }
                        }
                    }
                });
    }
}
