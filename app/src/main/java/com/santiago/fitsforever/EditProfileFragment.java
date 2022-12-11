package com.santiago.fitsforever;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContentResolverCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoProvider;

import java.util.Map;

import javax.xml.transform.Result;

public class EditProfileFragment extends Fragment {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    TextView changeProfile;
    ImageView imageProfile;
    Uri imageUri;
    EditText editUser, editEmail, editWeight, editHeight;
    String username, email, weight, height, uId, imageUrl;
    Button btnChange;
    StorageReference mStorage;
    private static final int PICK_IMAGE_REQ = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference("uploads");
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        imageProfile = (ImageView) view.findViewById(R.id.imageProfile);
        changeProfile = (TextView) view.findViewById(R.id.txtChangeProfile);
        editUser = (EditText) view.findViewById(R.id.fullName);
        editEmail = (EditText) view.findViewById(R.id.email);
        editWeight = (EditText) view.findViewById(R.id.Weight);
        editHeight = (EditText) view.findViewById(R.id.height);
        btnChange = (Button) view.findViewById(R.id.btnChange);

        getCurrentProfile();
        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doChangeProfile();
            }
        });

        return view;

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQ);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQ && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageProfile);
        }

    }

    private void onSuccessEdit() {
        ProfileFragment profileFragment = new ProfileFragment();
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction()
                .replace(R.id.fragment_container, profileFragment, profileFragment.getTag())
                .commit();
    }

    private void getCurrentProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        uId = user.getUid();

        DocumentReference docRef = db.collection("Users").document(uId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                username = (documentSnapshot.getString("fullName"));
                email = (documentSnapshot.getString("email"));
                weight = (documentSnapshot.getString("weight"));
                height = (documentSnapshot.getString("height"));
                imageUrl = (documentSnapshot.getString("profileImage"));

                editUser.setText(username);
                editEmail.setText(email);
                editWeight.setText(weight);
                editHeight.setText(height);
                Picasso.get().load(imageUrl).into(imageProfile);
            }
        });

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void doChangeProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        uId = user.getUid();
        email = editEmail.getText().toString().trim();
        username = editUser.getText().toString().trim();
        weight = editWeight.getText().toString().trim();
        height = editHeight.getText().toString().trim();

        if (username.isEmpty()) {
            editUser.setError("Full Name is required");
            editUser.requestFocus();
            return;
        }
        if (username.length() < 5) {
            editUser.setError("Full name must be 5 character or more!");
            editUser.requestFocus();
            return;
        }
        if (imageUri != null) {
            StorageReference fileRef = mStorage.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageLoc = uri.toString();
                                    ProfileUser profileUser = new ProfileUser(username, email, weight, height, imageLoc);
                                    db.collection("Users").document(uId)
                                            .set(profileUser)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getContext(), "Profile successfully updated", Toast.LENGTH_LONG).show();
                                                        onSuccessEdit();
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
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "ERROR : " +
                                    e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            ProfileUser profileUser = new ProfileUser(username, email, weight, height, imageUrl);
            db.collection("Users").document(uId)
                    .set(profileUser)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Profile successfully updated", Toast.LENGTH_LONG).show();
                                onSuccessEdit();
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
}
