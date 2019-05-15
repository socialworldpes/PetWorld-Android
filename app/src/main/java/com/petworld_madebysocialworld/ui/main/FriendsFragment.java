package com.petworld_madebysocialworld.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.petworld_madebysocialworld.Lead;
import com.petworld_madebysocialworld.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

@SuppressLint("ValidFragment")
public class FriendsFragment extends Fragment {

    ListView friendsList;
    Context context;
    ArrayList<Map<String, String>> friendsListInfo = new ArrayList<Map<String, String>>();

    public FriendsFragment(Context context) {
        this.context = context;
        // PARA TESTEAR
        // friendsListInfoTestData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        friendsList = (ListView) view.findViewById(R.id.list);

        FriendsListAdapter customAdapter = new FriendsListAdapter(context, friendsListInfo);
        friendsList.setAdapter(customAdapter);

        return view;
    }

    private void saveFriend(Map<String, String> map) {
        friendsListInfo.add(map);
    }

    private void getFriendsList() {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<DocumentReference> friendsRef = (ArrayList<DocumentReference>) document.get("friends");
                        for (final DocumentReference dr: friendsRef) {
                            db.document(dr.getPath()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Map<String, Object> data = document.getData();
                                            Map<String, String> map = new HashMap<String, String>();
                                            map.put("id", dr.getId());
                                            map.put("name", (String) data.get("name"));
                                            map.put("imageURL", (String) data.get("imageURL"));
                                            saveFriend(map);
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }

        });
    }

    public void friendsListInfoTestData() {

        friendsListInfo.add(new HashMap<String, String>() {{
            put("name", "JAVA");
            put("imageURL", "https://www.tutorialspoint.com/java/images/java-mini-logo.jpg");
        }});
        friendsListInfo.add(new HashMap<String, String>() {{
            put("name", "Python");
            put("imageURL", "https://www.tutorialspoint.com/python/images/python-mini.jpg");
        }});
        friendsListInfo.add(new HashMap<String, String>() {{
            put("name", "Javascript");
            put("imageURL", "https://www.tutorialspoint.com/javascript/images/javascript-mini-logo.jpg");
        }});
        friendsListInfo.add(new HashMap<String, String>() {{
            put("name", "C++");
            put("imageURL", "https://www.tutorialspoint.com/cplusplus/images/cpp-mini-logo.jpg");
        }});
        friendsListInfo.add(new HashMap<String, String>() {{
            put("name", "Android");
            put("imageURL", "https://www.tutorialspoint.com/android/images/android-mini-logo.jpg");
        }});
        friendsListInfo.add(new HashMap<String, String>() {{
            put("name", "JAVA");
            put("imageURL", "https://www.tutorialspoint.com/java/images/java-mini-logo.jpg");
        }});
        friendsListInfo.add(new HashMap<String, String>() {{
            put("name", "Python");
            put("imageURL", "https://www.tutorialspoint.com/python/images/python-mini.jpg");
        }});
        friendsListInfo.add(new HashMap<String, String>() {{
            put("name", "Javascript");
            put("imageURL", "https://www.tutorialspoint.com/javascript/images/javascript-mini-logo.jpg");
        }});
        friendsListInfo.add(new HashMap<String, String>() {{
            put("name", "C++");
            put("imageURL", "https://www.tutorialspoint.com/cplusplus/images/cpp-mini-logo.jpg");
        }});
        friendsListInfo.add(new HashMap<String, String>() {{
            put("name", "Android");
            put("imageURL", "https://www.tutorialspoint.com/android/images/android-mini-logo.jpg");
        }});
        friendsListInfo.add(new HashMap<String, String>() {{
            put("name", "JAVA");
            put("imageURL", "https://www.tutorialspoint.com/java/images/java-mini-logo.jpg");
        }});
        friendsListInfo.add(new HashMap<String, String>() {{
            put("name", "Python");
            put("imageURL", "https://www.tutorialspoint.com/python/images/python-mini.jpg");
        }});
        friendsListInfo.add(new HashMap<String, String>() {{
            put("name", "Javascript");
            put("imageURL", "https://www.tutorialspoint.com/javascript/images/javascript-mini-logo.jpg");
        }});
        friendsListInfo.add(new HashMap<String, String>() {{
            put("name", "C++");
            put("imageURL", "https://www.tutorialspoint.com/cplusplus/images/cpp-mini-logo.jpg");
        }});
        friendsListInfo.add(new HashMap<String, String>() {{
            put("name", "Android");
            put("imageURL", "https://www.tutorialspoint.com/android/images/android-mini-logo.jpg");
        }});
    }
}

