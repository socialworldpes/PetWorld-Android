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

    private View view;
    private ListView friendsList;
    private Context context;
    private ArrayList<Map<String, String>> friendsListInfo = new ArrayList<Map<String, String>>();
    private FriendsListAdapter customAdapter;
    private int numDone;

    public FriendsFragment(Context context) {
        this.context = context;
        numDone = 0;
        // PARA TESTEAR
        // friendsListInfoTestData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends, container, false);

        getFriendsListAndSetAdapter();

        return view;
    }

    private void getFriendsListAndSetAdapter() {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        final ArrayList<DocumentReference> friendsRef = (ArrayList<DocumentReference>) document.get("friends");

                        for (final DocumentReference dr: friendsRef) {
                            db.document(dr.getPath()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Map<String, Object> data = document.getData();
                                            Map<String, String> map = new HashMap<String, String>();
                                            Log.d("OMG123", (String) data.get("name") + " " + (String) data.get("imageURL"));
                                            map.put("id", dr.getId());
                                            map.put("name", (String) data.get("name"));
                                            map.put("imageURL", (String) data.get("imageURL"));
                                            friendsListInfo.add(map);
                                            if (numDone == 0) {
                                                friendsList = (ListView) view.findViewById(R.id.list);
                                                customAdapter = new FriendsListAdapter(context, R.layout.fragment_friends, friendsListInfo);
                                                friendsList.setAdapter(customAdapter);
                                            } else if (numDone == friendsRef.size()) customAdapter.notifyDataSetChanged();
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

