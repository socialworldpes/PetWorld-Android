package com.petworld_madebysocialworld;

import Models.Friend;
import Models.User;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private User u = User.getInstance();
    private FirebaseUser fu = FirebaseAuth.getInstance().getCurrentUser() ;
    private FirebaseFirestore db;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 2;
    private Activity act;
    private Context cont;
    private int friendsChangesCount, pendingRequestsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkAndRequestPermissions();
        act = this;
        cont = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        u.setmAuth(FirebaseAuth.getInstance());
        db = FirebaseFirestore.getInstance();

        if (u.getLogout()) {
            signOut();
        }

        connect();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        openMap(currentUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            loggedInFromIndent(data);
        }
    }

    private void connect(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();

        u.setmGoogleSignInClient(GoogleSignIn.getClient(this, gso));
        if (u.getGoogleSignInClient() != null) {
            Task<GoogleSignInAccount> task = u.getGoogleSignInClient().silentSignIn();
            try {
                // Google Sign In was successful, authenticate with Firebase
                if (task.isSuccessful()) {
                    listenToChanges();
                    firebaseAuthWithGoogle(task.getResult(ApiException.class));
                }
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                //openMap(null);
            }
        }
    }

    private void tokenRetrieve() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("token", token);
                        // Log and toast

                    }
                });

    }

    private void loggedInFromIndent(Intent data) {
        if (data != null) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                listenToChanges();
                firebaseAuthWithGoogle(task.getResult(ApiException.class));
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                //openMap(null);
            }

            fu = FirebaseAuth.getInstance().getCurrentUser();
            if (fu != null) {
                // User is signed in
                initializeFirebaseUser(fu);
            } else {
                // No user is signed in
            }
        }else{
            connect();
        }
    }

    private void initializeFirebaseUser(FirebaseUser fu){
        String uid = fu.getUid();
        Log.d(TAG, "UID: " + uid);
        DocumentReference docRef = db.collection("users").document(uid);
        u.setDocumentReference(docRef);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                FirebaseUser fu = FirebaseAuth.getInstance().getCurrentUser();
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        tokenRetrieve();
                    } else {
                        Map<String, Object> user = new HashMap<>();
                        user.put("favoriteRoutes", Arrays.asList());
                        user.put("friends", Arrays.asList());
                        user.put("groups", Arrays.asList());
                        user.put("meetings", Arrays.asList());
                        user.put("pets", Arrays.asList());
                        user.put("routes", Arrays.asList());
                        user.put("visibility", "public");
                        user.put("name", fu.getDisplayName());
                        Toast.makeText(MainActivity.this, "GetMail: " + fu.getEmail(), Toast.LENGTH_SHORT).show();
                        user.put("email", fu.getEmail());
                        user.put("imageURL", fu.getPhotoUrl().toString());
                        user.put("walks", Arrays.asList());

                        db.collection("users").document(fu.getUid())
                                .set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                        tokenRetrieve();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                    }
                    Log.d(TAG, "\"hola, he entrado para poner el document snapshot", task.getException());
                    u.setDocumentSnapshot(document);
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        u.getmAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            openMap(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //openMap(null);
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = u.getGoogleSignInClient().getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        if(checkAndRequestPermissions()){
        }else{
        }
    }

    public void signOut() {
        // Firebase sign out
        u.getmAuth().signOut();

        // Google sign out
        u.getGoogleSignInClient().signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        u.setLogOut(false);
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        u.getmAuth().signOut();

        // Google revoke access

        u.getGoogleSignInClient().revokeAccess().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //openMap(null);
            }
        });

    }

    private void openMap(FirebaseUser user) {
        if (user != null) {
            fu = FirebaseAuth.getInstance().getCurrentUser();
            if (fu != null) {
                // User is signed in
                initializeFirebaseUser(fu);
            } else {
                // No user is signed in
            }
            
            goToMap();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            signIn();
        }
    }

    //Function Button Map
    public void goToMap () {
        Intent nextActivity = new Intent(this, MapActivity.class);
        startActivity(nextActivity);
    }

    private void listenToChanges() {

        final FriendsSingleton friendsSingleton = FriendsSingleton.getInstance();

        //pending friends
        final Context context = cont;
        final Activity activity = act;
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("pendingFriends").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "listen:error", e);
                    return;
                }

                final List<DocumentChange> documentChanges = snapshots.getDocumentChanges();
                pendingRequestsCount = 0;

                if (documentChanges.size() == 0 && friendsSingleton.getRequestsListInfo().size() == 0) {
                    friendsSingleton.setNoRequests(true);
                }

                for (final DocumentChange dc : snapshots.getDocumentChanges()) {
                    db.document(dc.getDocument().getDocumentReference("reference").getPath()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    if (dc.getType() == DocumentChange.Type.ADDED) {
                                        PushNotification pushAux = new PushNotification();
                                        pushAux.addNotification(activity, "PetWorld", "Tienes una nueva solicitud de amistad de " + document.get("name"), R.drawable.ic_group, context);
                                        friendsSingleton.addRequestSnapshot(document.getId(), document.getData());
                                        Log.d("FriendsListener", "new request from " + document.get("name"));
                                    }

                                    ++pendingRequestsCount;
                                    if (friendsSingleton.friendsFragmentIni() && pendingRequestsCount == documentChanges.size()) friendsSingleton.updateRequestsSnapshots();
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                }
            }
        });

        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("friends").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "listen:error", e);
                    return;
                }

                final List<DocumentChange> documentChanges = snapshots.getDocumentChanges();
                friendsChangesCount = 0;

                if (documentChanges.size() == 0 && friendsSingleton.getFriendsListInfo().size() == 0) {
                    friendsSingleton.setNoFriends(true);
                }

                for (final DocumentChange dc : documentChanges) {
                    db.document(dc.getDocument().getDocumentReference("reference").getPath()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    if (dc.getType() == DocumentChange.Type.ADDED) {
                                        friendsSingleton.addFriendSnapshot(document.getId(), document.getData());
                                        Log.d("FriendsListener", "new friend added: " + document.get("name"));
                                    } else if (dc.getType() == DocumentChange.Type.REMOVED) {
                                        friendsSingleton.deleteFriendSnapshot(document.getId(), document.getData());
                                        Log.d("FriendsListener", "new friend removed: " + document.get("name"));
                                    }

                                    ++friendsChangesCount;
                                    if (friendsSingleton.friendsFragmentIni() && friendsChangesCount == documentChanges.size()) friendsSingleton.updateFriendsSnapshots();
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                }

            }
        });
    }

    private boolean checkAndRequestPermissions() {
        int readpermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writepermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int locationpermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (readpermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (locationpermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();

                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Log.d("ERROR014", "camera & location services permission granted");

                        // here you can do your logic all Permission Success Call

                    } else {
                        Log.d("ERROR015", "Some permissions are not granted ask again ");
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            showDialogOK("Some Permissions are required for Open Camera",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    dialog.dismiss();
                                                    break;
                                            }
                                        }
                                    });
                        } else {
                            explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?");
                        }
                    }
                }
            }
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void explain(String msg) {
        final android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
        dialog.setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        //Utils.startInstalledAppDetailsActivity(LoginActivity.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        dialog.create().dismiss();
                        finish();
                    }
                });
        dialog.show();
    }

}


