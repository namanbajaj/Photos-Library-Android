package com.example.photosandroid;

/**
 * @author Naman Bajaj, Sharad Prasad
 */

import android.net.Uri;

import java.io.File;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;

/**
 * Class that represents a photo object
 */
public class Photo implements Serializable {
    /**
     * Location of photo on user's drive
     */
    public String location;


    /**
     * List of tags that are associated with the photo
     */
    public ArrayList<Tag> tags;


    /**
     * 2-arg constructor for Photo
     * <p>
     * Initializes location to user chosen location and caption
     * <p>
     * Initializes tags List and retrieves date from file system
     *
     * @param location Location of photo on drive
     */
    public Photo(String location) {
        this.location = location;
        tags = new ArrayList<>();
    }


    /**
     * Returns location of photo on user's drive
     *
     * @return location of photo on user's drive
     */
    public String getLocation() {
        return location;
    }
}
