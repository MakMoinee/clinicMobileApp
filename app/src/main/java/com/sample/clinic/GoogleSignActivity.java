package com.sample.clinic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.FirebaseAuth;
import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Models.Users;
import com.sample.clinic.Preferrences.MyUserPreferrence;
import com.sample.clinic.Services.LocalFireStore;
import com.sample.clinic.Services.LocalFirestoreImpl;
import com.sample.clinic.Services.LocalMail;
import com.sample.clinic.databinding.ActivityGoogleSignInBinding;

public class GoogleSignActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ActivityGoogleSignInBinding binding;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;
    private LocalFireStore fs;
    private LocalMail mail = new LocalMail();

    ActivityResultLauncher<IntentSenderRequest> oneTapLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoogleSignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSignOut();
        setSignIn();
    }


    private void setSignOut() {
        try {
            FirebaseAuth.getInstance().signOut();
        } catch (Exception e) {

        }
    }

    private void setSignIn() {
        mAuth = FirebaseAuth.getInstance();
        fs = new LocalFirestoreImpl(GoogleSignActivity.this);
        oneTapLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                            String idToken = credential.getGoogleIdToken();
                            String username = credential.getId();
                            String password = credential.getPassword();
                            if (idToken != null) {
                                // Got an ID token from Google. Use it to authenticate
                                // with your backend.
                                Log.e("err", "Got ID token.");
                                Users users = new Users();
                                users.setEmail(username);
                                users.setFirstName(credential.getGivenName());
                                users.setLastName(credential.getFamilyName());
                                users.setPassword("default");
                                users.setUserType(2);
                                fs.getLogin(users, new FireStoreListener() {
                                    @Override
                                    public void onAddUserSuccess(Users u1) {
                                        Toast.makeText(GoogleSignActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                        new MyUserPreferrence(GoogleSignActivity.this).saveUser(u1);
                                        Intent intent = new Intent(GoogleSignActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onAddUserError(Exception e) {
                                        fs.insertUserRecord(users, new FireStoreListener() {
                                            @Override
                                            public void onAddUserSuccess(Users u) {
                                                Toast.makeText(GoogleSignActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                                new MyUserPreferrence(GoogleSignActivity.this).saveUser(users);
                                                mail.sendEmail(users.getEmail(), "PQ MEDFIND- Account Created", String.format("Hi %s, Welcome to PQ MEDFIND where you can book and create appointments to health doctors of your own preference.\n\n Thank You For Choosing PQ MEDFIND", credential.getDisplayName()));
                                                Intent intent = new Intent(GoogleSignActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();

                                            }

                                            @Override
                                            public void onAddUserError(Exception e) {
                                                Log.e("LOGIN_ERR", e.getMessage());
                                                Toast.makeText(GoogleSignActivity.this, "Failed to login using Google Account", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            } else if (password != null) {
                                // Got a saved username and password. Use them to authenticate
                                // with your backend.
                                Log.e("err", "Got password.");

                            }
                        } catch (ApiException e) {
                            Log.e("err", e.getLocalizedMessage());
                            Toast.makeText(GoogleSignActivity.this, "Failed to login using Google Account", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(GoogleSignActivity.this, "Failed to login using Google Account", Toast.LENGTH_SHORT).show();
                    }
                });
        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.web_client_id))
                        // Show all accounts on the device.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();


        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(beginSignInResult -> {
                    try {
                        oneTapLauncher.launch(new IntentSenderRequest.Builder(beginSignInResult.getPendingIntent().getIntentSender()).build());
                    } catch (Exception e) {
                        Log.e("oneTapClientFail", "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                    }
                })
                .addOnFailureListener(e -> Log.e("oneTapClientFail", e.getLocalizedMessage()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(GoogleSignActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
