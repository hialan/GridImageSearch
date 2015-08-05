package com.example.gridimagesearch.gridimagesearch.models;

import java.io.Serializable;

/**
 * Created by hialanMacAir on 2015/8/5.
 */
public class SearchSetting implements Serializable {
    public int size;
    public int color;
    public int type;
    public String site;
    public String query;

    public String toString() {
        return "size: " + size + " color: " + color + " type: " + type + " site: " + site + " query: " + query;
    }

    public SearchSetting copy(SearchSetting newSetting) {
        this.size = newSetting.size;
        this.color = newSetting.color;
        this.type = newSetting.type;
        this.site = newSetting.site;
        this.query = newSetting.query;
        return this;
    }
}
