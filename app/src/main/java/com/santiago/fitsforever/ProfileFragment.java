package com.santiago.fitsforever;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    TextView username, email;
    String uId, imageUrl;
    Button editProfile, logout;
    MaterialCardView accountInfo, devCredit;
    ImageView profileImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        username = (TextView) view.findViewById(R.id.username);
        email = (TextView) view.findViewById(R.id.userEmail);
        logout = (Button) view.findViewById(R.id.btnLogout);
        editProfile = (Button) view.findViewById(R.id.btn_editProfile);
        profileImage = (ImageView) view.findViewById(R.id.profileImage);
        accountInfo = (MaterialCardView) view.findViewById(R.id.accountInfo);
        devCredit = (MaterialCardView) view.findViewById(R.id.creditDev);
        devCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSeeProfileDev();
            }
        });
        accountInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSeeInfoAccount();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogout();
            }
        });
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doEditProfile();
            }
        });
        getCurrentUser();
        return view;
    }

    private void doLogout() {
        mAuth.signOut();
        checkUser();
    }

    private void getCurrentUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        uId = user.getUid();

        DocumentReference docRef = db.collection("Users").document(uId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                username.setText(documentSnapshot.getString("fullName"));
                email.setText(documentSnapshot.getString("email"));
                imageUrl = documentSnapshot.getString("profileImage");
                Picasso.get().load(imageUrl).into(profileImage);
            }
        });
    }

    private void doSeeProfileDev() {
        FragmentDevProfile fragmentDevProfile = new FragmentDevProfile();
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction()
                .replace(R.id.fragment_container, fragmentDevProfile, fragmentDevProfile.getTag())
                .commit();
    }

    private void doEditProfile() {
        EditProfileFragment editProfileFragment = new EditProfileFragment();
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction()
                .replace(R.id.fragment_container, editProfileFragment, editProfileFragment.getTag())
                .commit();
    }

    private void doSeeInfoAccount() {
        FragmentAccountInfo fragmentAccountInfo = new FragmentAccountInfo();
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction()
                .replace(R.id.fragment_container, fragmentAccountInfo, fragmentAccountInfo.getTag())
                .commit();
    }

    private void checkUser() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(getActivity(), MainActivity.class));

        }
    }
}
