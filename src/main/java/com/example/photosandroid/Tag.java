package com.example.photosandroid;

/**
 * @author Naman Bajaj, Sharad Prasad
 */

import java.io.Serializable;

/**
 * Tag class that represents a Tag Object
 */
public class Tag implements Serializable {
    /**
     * Name (Key) of tag
     */
    public String name;

    /**
     * Value of tag
     */
    public String value;

    /**
     * 2-arg constructor for Tag class
     * <p>
     * Initializes name and value to user chosen values
     *
     * @param name  Name (key) of tag
     * @param value Value of tag
     */
    public Tag(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Returns string representation of tag
     *
     * @return string representation of tag
     */
    public String toString() {
        return name + " - " + value;
    }
}
