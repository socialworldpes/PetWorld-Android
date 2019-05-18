package Models;

public class Friend implements Comparable<Friend> {

    private String id;
    private String name;
    private String imageURL;

    public Friend(String id, String name, String imageURL) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }

    @Override
    public int compareTo(Friend friend) {
        return this.id.compareTo(friend.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Friend)) return false;
        else return ((Friend) o).getId().equals(this.id);
    }
}
