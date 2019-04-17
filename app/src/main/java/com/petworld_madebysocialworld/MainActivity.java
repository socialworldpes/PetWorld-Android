package com.petworld_madebysocialworld;

import Models.User;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.*;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.*;
import android.support.v7.widget.Toolbar;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private SignInButton googleSignInButton;
    private GoogleApiClient mGoogleApiClient;
    private TextView statusTextView;
    private SignInButton signInButton;
    private Button signOutButton;
    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private User u;
    FirebaseFirestore db;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.d("PRUEBA", "Paso por el main activity");
            u = User.getInstance();
            setContentView(R.layout.activity_main);
            u.setmAuth(FirebaseAuth.getInstance());
            db = FirebaseFirestore.getInstance();
            findViewById(R.id.sign_in_button).setOnClickListener(this);
            if (u.getLogout())
                signOut();
            connect(null);


            // [START initialize_auth]
            // Initialize Firebase Auth
            // [END initialize_auth]
        }

        // [START on_start_check_user]
        @Override
        public void onStart() {
            super.onStart();
            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = User.getInstance().getmAuth().getCurrentUser();
            updateUI(currentUser);
        }
        // [END on_start_check_user]

        // [START onactivityresult]
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                connect(data);
            }
        }

    private void connect(Intent data) {
            if (data != null) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    Log.d(TAG, "Google sign in done");
                    u.setAccount(task.getResult(ApiException.class));
                    firebaseAuthWithGoogle(u.getAccount());
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e);
                    // [START_EXCLUDE]
                    updateUI(null);
                    // [END_EXCLUDE]
                }
                // Create a new user with a first and last name

                DocumentReference docRef = db.collection("users").document(u.getAccount().getId());
                u.setDocumentReference(docRef);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Map<String, Object> user = new HashMap<>();
                                user.put("favoriteRoutes", Arrays.asList());
                                user.put("friends", Arrays.asList());
                                user.put("groups", Arrays.asList());
                                user.put("meetings", Arrays.asList());
                                user.put("pets", Arrays.asList());
                                user.put("routes", Arrays.asList());
                                user.put("visibility", "public");
                                user.put("walks", Arrays.asList());

                                db.collection("users").document(u.getAccount().getId())
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error writing document", e);
                                            }
                                        });
                            }
                            u.setDocumentSnapshot(document);
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }
            else {
                // [START config_signin]
                // Configure Google Sign In
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                // [END config_signin]
                u.setmGoogleSignInClient(GoogleSignIn.getClient(this, gso));
                if (u.getGoogleSignInClient() != null) {
                    Task<GoogleSignInAccount> task = u.getGoogleSignInClient().silentSignIn();
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        if (task.isSuccessful()){
                            u.setAccount(task.getResult(ApiException.class));
                            firebaseAuthWithGoogle(u.getAccount());
                        }
                    } catch (ApiException e) {
                        // Google Sign In failed, update UI appropriately
                        Log.w(TAG, "Google sign in failed", e);
                        // [START_EXCLUDE]
                        updateUI(null);
                        // [END_EXCLUDE]
                    }

                }
            }
    }
    // [END onactivityresult]

        // [START auth_with_google]
        private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
            Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
            // [START_EXCLUDE silent]
            //showProgressDialog();
            // [END_EXCLUDE]

            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            u.getmAuth().signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCredential:success");
                                FirebaseUser user = u.getmAuth().getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                            // [START_EXCLUDE]
                            //hideProgressDialog();
                            // [END_EXCLUDE]
                        }
                    });
        }
        // [END auth_with_google]

        // [START signin]
        private void signIn() {
            Intent signInIntent = u.getGoogleSignInClient().getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
        // [END signin]

        public void signOut() {
            // Firebase sign out
            u.getmAuth().signOut();

            // Google sign out
            u.getGoogleSignInClient().signOut().addOnCompleteListener(this,
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            updateUI(null);
                        }
                    });
        }

        private void revokeAccess() {
            // Firebase sign out
            u.getmAuth().signOut();

            // Google revoke access
            u.getGoogleSignInClient().revokeAccess().addOnCompleteListener(this,
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            updateUI(null);
                        }
                    });
        }

        private void updateUI(FirebaseUser user) {
            if (user != null) {
                //mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
                //mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

                goToMap(null);
            }
                // findViewById(R.id.signOutAndDisconnect).setVisibility(View.VISIBLE);
        }

        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.sign_in_button) {
                signIn();
            }
        }

    //Function Button Map
    public void goToMap (View view) {
        Intent nextActivity = new Intent(this, MapActivity.class);
        startActivity(nextActivity);
    }

}


