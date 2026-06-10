package com.reely.models;

import com.google.gson.annotations.SerializedName;

/**
 * Model untuk video (trailer) dari TMDB.
 */
public class Video {

    @SerializedName("id")
    private String id;

    @SerializedName("key")
    private String key;       // YouTube video key

    @SerializedName("name")
    private String name;

    @SerializedName("site")
    private String site;      // "YouTube"

    @SerializedName("type")
    private String type;      // "Trailer", "Teaser", dll

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

    /**
     * URL thumbnail YouTube dari key.
     */
    public String getThumbnailUrl() {
        return "https://img.youtube.com/vi/" + key + "/hqdefault.jpg";
    }

    /**
     * URL YouTube untuk dibuka di browser/app.
     */
    public String getYouTubeUrl() {
        return "https://www.youtube.com/watch?v=" + key;
    }
}