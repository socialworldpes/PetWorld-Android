package com.petworld_madebysocialworld;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import java.util.UUID;

/**
 * Entidad Route
 */
public class Route {

    private GeoPoint PlaceLocation;
    private String Id;
    private String Name;
    private String Place;
    private String Description;
    private int Image;

    public Route(String name, String place, String description, int image, GeoPoint placeLocation, String id) {
        Id = id;
        Name = name;
        Place = place;
        Description = description;
        Image = image;
        PlaceLocation = placeLocation;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public GeoPoint getPlaceLocation() {
        return PlaceLocation;
    }

    public void setPlaceLocation(GeoPoint placeLocation) {
        PlaceLocation = placeLocation;
    }

    public String getPlace() {
        return Place;
    }

    public void setPlace(String place) {
        Place = place;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getImage() {
        return Image;
    }

    public void setImage(int image) {
        Image = image;
    }

    @Override
    public String toString() {
        return "Route{" +
                "ID='" + Id + '\'' +
                ", Name='" + Name + '\'' +
                ", Place='" + Place + '\'' +
                ", Description='" + Description + '\'' +
                '}';
    }
}
