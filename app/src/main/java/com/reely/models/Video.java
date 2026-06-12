package com.reely.models;

import com.google.gson.annotations.SerializedName;

public class Video {

    @SerializedName("id")
    private String id;

    @SerializedName("key")
    private String key;

    @SerializedName("name")
    private String name;

    @SerializedName("site")
    private String site;

    @SerializedName("type")
    private String type;

    @SerializedName("official")
    private boolean official;

    public String getId()      { return id; }
    public String getKey()     { return key; }
    public String getName()    { return name; }
    public String getSite()    { return site; }
    public String getType()    { return type; }
    public boolean isOfficial(){ return official; }

    public boolean isYouTube() { return "YouTube".equals(site); }
    public boolean isTrailer() { return "Trailer".equals(type); }

    public String getThumbnailUrl() {
        return "https://img.youtube.com/vi/" + key + "/hqdefault.jpg";
    }

    public String getYouTubeUrl() {
        return "https://www.youtube.com/watch?v=" + key;
    }
}