package com.petworld_madebysocialworld;

import java.util.UUID;

/**
 * Entidad Lead
 */
public class Lead {

    private String Id;
    private String Name;
    private String Place;
    private String Description;
    private int Image;

    public Lead(String name, String place, String description, int image) {
        Id = UUID.randomUUID().toString();
        Name = name;
        Place = place;
        Description = description;
        Image = image;
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
        return "Lead{" +
                "ID='" + Id + '\'' +
                ", Name='" + Name + '\'' +
                ", Place='" + Place + '\'' +
                ", Description='" + Description + '\'' +
                '}';
    }
}
