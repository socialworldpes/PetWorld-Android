package Models;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Map;

public class Meeting implements Comparable<Meeting> {
    private String description;
    private String name;
    private String image;
    private Timestamp start;
    private String id;

    public Meeting(Map<String, Object> data, String id) {
        this.name =(String)data.get("name");
        this.description=(String)data.get("description");
        this.image=((ArrayList<String>)data.get("images")).get(0);
        this.start =(Timestamp)data.get("start");
        this.id = id;
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

    public String getId() {return id;}

    @Override
    public int compareTo(Meeting meeting) {
        return this.start.compareTo(meeting.getStart());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Walk)) return false;
        else return ((Walk) o).getStart().equals(this.getStart());
    }
}
