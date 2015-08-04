package com.example.gridimagesearch.gridimagesearch.models;

import java.io.Serializable;

/**
 * Created by hialanMacAir on 2015/8/5.
 */
public class SearchSetting implements Serializable {
    public String size;
    public String color;
    public String type;
    public String site;

    public String toString() {
        return "size: " + size + " color: " + color + " type: " + type + " site; " + site;
    }
}
