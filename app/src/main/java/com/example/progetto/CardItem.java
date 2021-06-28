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
    @ColumnInfo(name = "item_user")
    private String username;
    @ColumnInfo(name = "item_date")
    private String date;
    @ColumnInfo(name = "item_place")
    private String place;
    @ColumnInfo(name = "item_description")
    private String description;

    public CardItem(String imageResource, String username, String date, String place, String description) {
        this.username = username;
        this.imageResource = imageResource;
        this.place = place;
        this.description = description;
        this.date = date;
    }


    public String getUsername() { return username; }

    public String getImageResource() {
        return imageResource;
    }

    public String getPlace() {
        return place;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
