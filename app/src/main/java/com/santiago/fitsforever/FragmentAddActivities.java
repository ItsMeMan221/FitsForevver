package com.santiago.fitsforever;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FragmentAddActivities extends Fragment {

    // Initialize firebase
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    // Initialize all view
    EditText actNameEdit, repEdit, dateEdit, timeEdit, descEdit;
    AutoCompleteTextView typeExcEdit;
    Button cancel, submit;

    //Object Exercise
    ExcersiceType excersiceType;

    // Populate dropdown
    ArrayAdapter<ExcersiceType> itemAdapter;
    // Get id for dropdown
    Map<String, Object> codeValidator = new HashMap<>();
    Map<String, Object> temp = new HashMap<>();
    // value for all text
    String actName, typeExc, rep, date, time, desc;
    String tempType;
    Calendar calendar;
    private static final String PARAM_UID = "PARAM_UID";
    private static final String PARAM_TYPE = "PARAM_TYPE";
    private String paramId, paramTypeId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Getting instance for firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // Inflate the view
        View view = inflater.inflate(R.layout.fragment_add_activities, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Finding the id for all view
        actNameEdit = (EditText) view.findViewById(R.id.actName);
        typeExcEdit = (AutoCompleteTextView) view.findViewById(R.id.typeExc);
        repEdit = (EditText) view.findViewById(R.id.repeat);
        descEdit = (EditText) view.findViewById(R.id.description);

        //Start populating the dropdown data
        dateEdit = (EditText) view.findViewById(R.id.date);
        timeEdit = (EditText) view.findViewById(R.id.time);
        submit = (Button) view.findViewById(R.id.submit);
        cancel = (Button) view.findViewById(R.id.cancel);
        dateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(dateEdit);
            }
        });
        timeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog(timeEdit);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddActivity();

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitiesFragment activitiesFragment = new ActivitiesFragment();
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.fragment_container, activitiesFragment, activitiesFragment.getTag())
                        .commit();
            }
        });

        // Get the id of exercise type
        typeExcEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                for (Map.Entry<String, Object> entry : codeValidator.entrySet()) {
                    if (entry.getValue() == item) {
                        temp.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        });
        CreateNotificationChannel();

        if (getArguments() != null) {
            paramId = getArguments().getString(PARAM_UID);
            paramTypeId = getArguments().getString(PARAM_TYPE);
            getDataEdit();
        }
        getDataDropdown();
    }

    private void AddActivity() {

        actName = actNameEdit.getText().toString().trim();
        rep = repEdit.getText().toString().trim();
        date = dateEdit.getText().toString().trim();
        time = timeEdit.getText().toString().trim();
        desc = descEdit.getText().toString().trim();

        if (actName.isEmpty()) {
            actNameEdit.setError("Please enter activity name ");
            actNameEdit.requestFocus();
            return;
        }
        if (rep.isEmpty()) {
            repEdit.setError("Please enter number of repetition");
            repEdit.requestFocus();
            return;
        }
        if (temp.isEmpty()) {
            typeExcEdit.setError("Please select one of option");
            typeExcEdit.requestFocus();
            return;
        } else {
            for (String key : temp.keySet()) {
                typeExc = key;
            }
        }
        if (date.isEmpty()) {
            dateEdit.setError("Please enter a specific date ");
            dateEdit.requestFocus();
            return;
        }
        if (time.isEmpty()) {
            timeEdit.setError("Please enter a specific time");
            timeEdit.requestFocus();
            return;
        }
        if (desc.isEmpty()) {
            descEdit.setError("Please enter a description");
            descEdit.requestFocus();
            return;
        }
        if (desc.length() < 10) {
            descEdit.setError("Please enter a 10 character or more");
            descEdit.requestFocus();
            return;
        }
        if (desc.length() > 200) {
            descEdit.setError("Maximal character of description is 200 character");
            descEdit.requestFocus();
            return;
        }
        String uId = mAuth.getCurrentUser().getUid();
        Map<String, Object> storeData = new HashMap<>();
        storeData.put("uId", uId);
        storeData.put("nameAct", actName);
        storeData.put("excType", typeExc);
        storeData.put("repetition", rep);
        storeData.put("date", date);
        storeData.put("time", time);
        storeData.put("desc", desc);
        storeData.put("status", 1);

        if (getArguments() == null) {
            db.collection("UserActivity")
                    .add(storeData)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getActivity(), "Your activity successfully been added", Toast.LENGTH_LONG).show();
                            listAct();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "ERROR : " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
            alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getActivity(), AlarmReceiver.class);

            pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, 0);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            db.collection("UserActivity").document(paramId)
                    .update(storeData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Activity successfully updated", Toast.LENGTH_LONG).show();
                                listAct();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "ERROR : " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }

    }

    private void showTimeDialog(EditText timeEdit) {
        TimePickerDialog timePickerDialog;

        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                DateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
                String strTime = simpleDateFormat.format(calendar.getTime());
                timeEdit.setText(strTime);
            }
        };
        timePickerDialog = new TimePickerDialog(getActivity(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    private void showDateDialog(EditText dateEdit) {
        DatePickerDialog datePickerDialog;
        DatePickerDialog.OnDateSetListener dataSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String strDate = simpleDateFormat.format(calendar.getTime());
                dateEdit.setText(strDate);
            }
        };
        datePickerDialog = new DatePickerDialog(getActivity(), dataSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    // Method for populate the data
    public void getDataDropdown() {

        db.collection("Excersice")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            itemAdapter = new ArrayAdapter<ExcersiceType>(getActivity(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                excersiceType = new ExcersiceType(document.getId(), document.getString("Name"));
                                itemAdapter.add(excersiceType);
                                codeValidator.put(document.getId(), document.getString("Name"));
                            }
                            itemAdapter.notifyDataSetChanged();
                            if (paramTypeId != null) {
                                tempType = paramTypeId;
                                typeExcEdit.setText(tempType, false);
                            }
                            typeExcEdit.setAdapter(itemAdapter);
                        } else {
                            Log.d("Hello", "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    private void listAct() {
        ActivitiesFragment activitiesFragment = new ActivitiesFragment();
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction()
                .replace(R.id.fragment_container, activitiesFragment, activitiesFragment.getTag())
                .commit();

    }

    private void getDataEdit() {
        DocumentReference docRef = db.collection("UserActivity").document(paramId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        actNameEdit.setText(document.getString("nameAct"));
                        repEdit.setText(document.getString("repetition"));
                        dateEdit.setText(document.getString("date"));
                        timeEdit.setText(document.getString("time"));
                        descEdit.setText(document.getString("desc"));
                    } else {
                        Log.d("asd", "No such document");
                    }
                } else {
                    Log.d("asd", "get failed with ", task.getException());
                }
            }
        });
    }

    public static FragmentAddActivities newInstance(String Uid, String idType) {
        FragmentAddActivities fragment = new FragmentAddActivities();
        Bundle args = new Bundle();
        args.putString(PARAM_UID, Uid);
        args.putString(PARAM_TYPE, idType);
        fragment.setArguments(args);
        return fragment;
    }

    private void CreateNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "FitsForever";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("FitsForever", name, importance);

            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
