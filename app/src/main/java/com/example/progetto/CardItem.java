package com.example.progetto;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Class which represents every card item with its information (image, user_name, age, bio_description)
 */
@Entity(tableName="item")
public class CardItem {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "item_id")
    private int id;
    @ColumnInfo(name = "item_image")
    private String imageResource;
    @ColumnInfo(name = "user_name")
    private String user_name;
    @ColumnInfo(name = "birthday")
    private String birthday;
    @ColumnInfo(name = "place")
    private String place;
    @ColumnInfo(name = "bio_description")
    private String bio_description;

    public CardItem(String imageResource, String user_name, String birthday,
                    String place, String bio_description) {
        this.imageResource = imageResource;
        this.user_name = user_name;
        this.birthday = birthday;
        this.place = place;
        this.bio_description = bio_description;
    }

    public String getImageResource() {
        return imageResource;
    }

    public String getUser_name() { return user_name; }

    public String getBirthday() { return birthday; }

    public String getPlace() {
        return place;
    }

    public String getBio_description() {
        return bio_description;
    }

    //should i ask for birthday date or age an calculate it?
    // public String getDate() { return date; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
