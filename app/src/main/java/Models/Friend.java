package Models;

public class Friend implements Comparable<Friend> {

    public static Friend NoFriends = new Friend("NoFriends", "No tienes amigos",
            "https://cdn.pixabay.com/photo/2016/11/01/03/28/magnifier-1787362_960_720.png");

    public static Friend NoPendingRequests = new Friend("NoPendingRequests", "No tienes solicitudes pendientes",
            "https://static.thenounproject.com/png/540056-200.png");

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
