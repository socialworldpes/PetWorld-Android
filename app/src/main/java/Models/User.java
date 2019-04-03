package Models;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;

public class User {
    private static final User ourInstance = new User();

    public static User getInstance() {
        return ourInstance;
    }

    private GoogleSignInAccount account;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private boolean logout = false;
    private DocumentReference docRef;


    public void setmAuth(FirebaseAuth instance) {
        mAuth = instance;
    }

    public boolean getLogout() {
        return logout;
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public void setAccount(GoogleSignInAccount result) {
        account = result;
    }

    public GoogleSignInAccount getAccount() {
        return account;
    }

    public void setmGoogleSignInClient(GoogleSignInClient client) {
        mGoogleSignInClient = client;
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return mGoogleSignInClient;
    }

    public void setLogOut(boolean b) {
        logout = b;
    }

    public void setDocumentReference(DocumentReference docRef) {
        this.docRef = docRef;
    }
}
