package com.reely.models;

import com.google.gson.annotations.SerializedName;

public class CastItem {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("character")
    private String character;

    @SerializedName("job")
    private String job;

    @SerializedName("department")
    private String department;

    @SerializedName("profile_path")
    private String profilePath;

    @SerializedName("order")
    private int order;

    public int getId()             { return id; }
    public String getName()        { return name; }
    public String getCharacter()   { return character; }
    public String getJob()         { return job; }
    public String getDepartment()  { return department; }
    public String getProfilePath() { return profilePath; }
    public int getOrder()          { return order; }

    public String getProfileUrl() {
        if (profilePath == null || profilePath.isEmpty()) return null;
        return "https://image.tmdb.org/t/p/w185" + profilePath;
    }

    public String getSubtitle() {
        if (character != null && !character.isEmpty()) return character;
        if (job != null && !job.isEmpty()) return job;
        return department != null ? department : "";
    }
}