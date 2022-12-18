package com.santiago.fitsforever;

import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class FragmentAccountInfo extends Fragment {
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    TextView txtUsername, txtEmail, txtHeight, txtWeight,
            txtBMI, txtLblHeight, txtLblWeight, txtLblBMI;
    ImageView profileImage;
    String username, email, imageURL, uId, weight, height, BMIString;
    int weightInt;
    double BMI, heightInt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_info, container, false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        txtUsername = (TextView) view.findViewById(R.id.username);
        txtEmail = (TextView) view.findViewById(R.id.userEmail);
        txtHeight = (TextView) view.findViewById(R.id.height);
        txtWeight = (TextView) view.findViewById(R.id.Weight);
        txtBMI = (TextView) view.findViewById(R.id.BMI);
        txtLblHeight = (TextView) view.findViewById(R.id.heightLabel);
        txtLblWeight = (TextView) view.findViewById(R.id.weightLabel);
        txtLblBMI = (TextView) view.findViewById(R.id.BMILabel);
        profileImage = (ImageView) view.findViewById(R.id.profileImage);
        weight = null;
        height = null;
        getAllData();


        return view;

    }

    private void getAllData() {
        FirebaseUser user = mAuth.getCurrentUser();
        uId = user.getUid();
        DocumentReference docRef = db.collection("Users").document(uId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                username = documentSnapshot.getString("fullName");
                email = documentSnapshot.getString("email");
                imageURL = documentSnapshot.getString("profileImage");
                weight = documentSnapshot.getString("weight");
                height = documentSnapshot.getString("height");
                Picasso.get().load(imageURL).into(profileImage);

                txtUsername.setText(username);
                txtEmail.setText(email);

                if (weight != null && height != null && !weight.equals("") && !height.equals("")) {
                    weightInt = Integer.parseInt(weight);
                    heightInt = Integer.parseInt(height);
                    txtWeight.setText(weight);
                    txtHeight.setText(height);
                    BMI = weightInt / Math.pow((heightInt / 100), 2);
                    if (BMI <= 18.5) {
                        BMIString = "Underweight";
                    } else if (BMI > 18.5 && BMI <= 24.9) {
                        BMIString = "Healthy";
                    } else if (BMI >= 25 && BMI <= 29.9) {
                        BMIString = "Overweight";
                    } else {
                        BMIString = "Obese";
                    }
//                    BMIString = Double.toString(BMI);
                    txtBMI.setText(BMIString);
                } else {
                    txtHeight.setVisibility(View.GONE);
                    txtWeight.setVisibility(View.GONE);
                    txtBMI.setVisibility(View.GONE);
                    txtLblBMI.setVisibility(View.GONE);
                    txtLblHeight.setVisibility(View.GONE);
                    txtLblWeight.setVisibility(View.GONE);
                }

            }
        });
    }
}
