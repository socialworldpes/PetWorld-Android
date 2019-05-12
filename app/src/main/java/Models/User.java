package Models;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import org.w3c.dom.Document;

public class User {
    private static final User ourInstance = new User();

    public static User getInstance() {
        return ourInstance;
    }

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private boolean logout = false;
    private DocumentReference docRef;
    private DocumentSnapshot docSnap;


    public void setmAuth(FirebaseAuth instance) {
        mAuth = instance;
    }

    public boolean getLogout() {
        return logout;
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public DocumentSnapshot getDocSnap() {
        return docSnap;
    }

    public FirebaseUser getAccount() {
        return FirebaseAuth.getInstance().getCurrentUser();
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

    public void setDocumentSnapshot(DocumentSnapshot docSnap) {this.docSnap = docSnap; }
}
