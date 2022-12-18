package com.santiago.fitsforever;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SignUp extends AppCompatActivity {
    //Initialize all widgets
    private EditText email, fullName, password, conPass;
    private Button google, register;
    private TextView haveAccount;

    //Value of the string
    private String emailV, fullNameV, passwordV, conPassV;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Map<String, Object> users = new HashMap<>();
    private final static int RC_SIGN_IN = 123;
    private GoogleSignInClient googleSignInClient;


    private FirebaseAuth mAuth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();

        // Config the google signin
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

//        Hide the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Getting value from the widgets
        email = (EditText) findViewById(R.id.email);
        fullName = (EditText) findViewById(R.id.fullName);
        password = (EditText) findViewById(R.id.password);
        conPass = (EditText) findViewById(R.id.conPass);

//        Button Register
        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Google Register
        google = (Button) findViewById(R.id.googleButton);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN);

            }
        });

        //Have Account
        haveAccount = (TextView) findViewById(R.id.haveAccount);
        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
            }
        });

    }

    private void registerUser() {

        // Get the string
        emailV = email.getText().toString().trim();
        fullNameV = fullName.getText().toString().trim();
        passwordV = password.getText().toString().trim();
        conPassV = conPass.getText().toString().trim();

//      Validation

        if (emailV.isEmpty()) {
            email.setError("Email is required");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailV).matches()) {
            email.setError("Please enter a valid email");
            email.requestFocus();
            return;
        }
        if (fullNameV.isEmpty()) {
            fullName.setError("Full Name is required");
            fullName.requestFocus();
            return;
        }
        if (fullNameV.length() < 5) {
            fullName.setError("Full Name at least have to be 5 character");
            fullName.requestFocus();
            return;
        }
        if (passwordV.isEmpty()) {
            password.setError("Password is required");
            password.requestFocus();
            return;
        }
        if (passwordV.length() < 8) {
            password.setError("Password must be 8 character or more");
            password.requestFocus();
            return;
        }
        if (!passwordV.equals(conPassV)) {
            conPass.setError("Confirmation password must be the same as password");
            conPass.requestFocus();
            return;
        }
        progressDialog = new ProgressDialog(getBaseContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("\t Loading...");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(emailV, passwordV)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            User user = new User(fullNameV, emailV);
                            db.collection("Users")
                                    .document(uId)
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            if(progressDialog.isShowing()) {
                                                progressDialog.dismiss();
                                            }
                                            Toast.makeText(getApplicationContext(), "User has been registered successfully", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(getApplicationContext(), SignIn.class);
                                            startActivity(intent);
                                            email.setText("");
                                            fullName.setText("");
                                            password.setText("");
                                            conPass.setText("");
                                            finish();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            if(progressDialog.isShowing()) {
                                                progressDialog.dismiss();
                                            }
                                            Toast.makeText(getApplicationContext(), "Failed to register! Try Again!", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } else {
                            if(progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), "Failed to register! Try Again!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String uId = firebaseUser.getUid();
                        String email = firebaseUser.getEmail();
                        String username = firebaseUser.getDisplayName();
                        User user = new User(username, email);
                        if (authResult.getAdditionalUserInfo().isNewUser()) {
                            db.collection("Users")
                                    .document(uId)
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            if(progressDialog.isShowing()) {
                                                progressDialog.dismiss();
                                            }
                                            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                            startActivity(intent);
                                            finish();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast.makeText(getApplicationContext(), "Failed to register! Try Again!", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } else {

                            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Fail" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}