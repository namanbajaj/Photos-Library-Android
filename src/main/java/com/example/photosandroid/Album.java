package com.example.photosandroid;

/**
 * @author Naman Bajaj, Sharad Prasad
 */

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class that represents an album object
 */
public class Album implements Serializable {
    /**
     * Name of the album
     */
    public String name;

    /**
     * Size of the album
     */
    public int size;

    /**
     * List of photos that album contains
     */
    public ArrayList<Photo> photos;

    /**
     * One-arg constructor for album
     * <p>
     * Initializes name to client entered name, size to 0, initializes new List of photos, and sets range to default String value of ""
     *
     * @param name Name of album
     */
    public Album(String name) {
        this.name = name;
        this.size = 0;
        photos = new ArrayList<Photo>();
    }

    /**
     * Adds photo to Album object
     * <p>
     * Specifically, adds photo to photos List and increases size, while also changing date and range fields as needed
     *
     * @param p Location of Photo to be added
     */
    public void addPhoto(String p) {
        Photo add = new Photo(p);
        photos.add(add);
        size++;
    }

    /**
     * Returns name of the album
     *
     * @return name of the album
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns size of album
     *
     * @return size of the album
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Return string representation of album (for testing)
     *
     * @return string representation of album (for testing)
     */
    public String toString() {
        return this.name + " has " + size + " photos";
    }
}
