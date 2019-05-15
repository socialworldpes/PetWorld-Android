package com.petworld_madebysocialworld.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.petworld_madebysocialworld.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("ValidFragment")
public class FriendsFragment extends Fragment {

    ListView friendsList;
    ArrayList<Map<String, String>> friendsListInfo = new ArrayList<Map<String, String>>();
    Context context;

    public FriendsFragment(Context context) {
        this.context = context;
        friendsListInfoTestData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        friendsList = (ListView) view.findViewById(R.id.list);

        FriendsListAdapter customAdapter = new FriendsListAdapter(context, friendsListInfo);
        friendsList.setAdapter(customAdapter);

        return view;
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

