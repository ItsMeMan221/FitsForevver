package com.santiago.fitsforever;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class ActivitiesFragment extends Fragment implements DataListener {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    TextView textViewUsername;
    String username;
    String Uid;
    RecyclerView recyclerView;
    Button addAct;
    String excType;
    private ArrayList<UserActivities> userActivities;
    RecycleActivitiesAdapter myAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        View view = inflater.inflate(R.layout.fragment_activites, container, false);
        textViewUsername = (TextView) view.findViewById(R.id.username);
        getCurrentUser();
        dataInitialized();
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        myAdapter = new RecycleActivitiesAdapter(getContext(), userActivities, this);
        addAct = view.findViewById(R.id.addActivity);
        addAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addActivity();
            }
        });

    }

    private void dataInitialized() {
        FirebaseUser user = mAuth.getCurrentUser();
        Uid = user.getUid();
        userActivities = new ArrayList<>();

        db.collection("UserActivity")
                .whereEqualTo("uId", Uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String excId = document.getString("excType");
                                db.collection("Excersice").document(excId)
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot dc = task.getResult();
                                                    if (dc.exists()) {
                                                        excType = dc.getString("Name");
                                                    }
                                                }
                                                UserActivities userAct = new UserActivities(document.getString("date"),
                                                        document.getString("desc"),
                                                        excType,
                                                        document.getString("nameAct"),
                                                        document.getString("time"),
                                                        document.getString("repetition"),
                                                        document.getId());

                                                userActivities.add(userAct);
                                                myAdapter.update(userActivities);
                                                myAdapter.notifyDataSetChanged();
                                                recyclerView.setAdapter(myAdapter);
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error getting documents: " + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void updateData() {
        FirebaseUser user = mAuth.getCurrentUser();
        Uid = user.getUid();
        userActivities = new ArrayList<>();
        userActivities.clear();
        myAdapter.update(userActivities);
        myAdapter.notifyDataSetChanged();

        db.collection("UserActivity")
                .whereEqualTo("uId", Uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String excId = document.getString("excType");
                                db.collection("Excersice").document(excId)
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot dc = task.getResult();
                                                    if (dc.exists()) {
                                                        excType = dc.getString("Name");
                                                    }
                                                }
                                                UserActivities userAct = new UserActivities(document.getString("date"),
                                                        document.getString("desc"),
                                                        excType,
                                                        document.getString("nameAct"),
                                                        document.getString("time"),
                                                        document.getString("repetition"),
                                                        document.getId());
                                                userActivities.add(userAct);
                                                myAdapter.update(userActivities);
                                                recyclerView.setAdapter(myAdapter);
                                                myAdapter.notifyDataSetChanged();
                                            }
                                        });

                            }
                        } else {
                            Toast.makeText(getActivity(), "Error getting documents: " + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void addActivity() {
        FragmentAddActivities fragmentAddActivities = new FragmentAddActivities();
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction()
                .replace(R.id.fragment_container, fragmentAddActivities, fragmentAddActivities.getTag())
                .commit();
    }

    public void getCurrentUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        Uid = user.getUid();
        DocumentReference docRef = db.collection("Users").document(Uid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                username = documentSnapshot.getString("fullName");
                textViewUsername.setText(String.format("Hello %s", username));
            }
        });
    }

    public static ActivitiesFragment newInstance() {
        ActivitiesFragment fragment = new ActivitiesFragment();
        return fragment;
    }

    @Override
    public void onChangedListener() {
        updateData();
    }
}
