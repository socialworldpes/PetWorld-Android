package Models;

import com.google.firebase.Timestamp;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class Walk implements Comparable<Walk> {

    public static Friend NoFriends = new Friend("NoFriends", "No tienes amigos",
            "https://cdn.pixabay.com/photo/2016/11/01/03/28/magnifier-1787362_960_720.png");

    public static Friend NoPendingRequests = new Friend("NoPendingRequests", "No tienes solicitudes pendientes",
            "https://static.thenounproject.com/png/540056-200.png");

    private String description;
    private String name;
    private String image;
    private Timestamp start;

    public Walk(Map<String, Object> data) {
        this.name =(String)data.get("name");
        this.description=(String)data.get("description");
        this.image=((ArrayList<String>)data.get("images")).get(0);
        this.start =(Timestamp)data.get("start");
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return image;
    }

    public Timestamp getStart() {return start;}

    @Override
    public int compareTo(Walk walk) {
        return this.start.compareTo(walk.getStart());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Walk)) return false;
        else return ((Walk) o).getStart().equals(this.getStart());
    }

}
