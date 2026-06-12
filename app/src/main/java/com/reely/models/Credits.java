package com.reely.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Credits {

    @SerializedName("cast")
    private List<CastItem> cast;

    @SerializedName("crew")
    private List<CastItem> crew;

    public List<CastItem> getCast() { return cast; }
    public List<CastItem> getCrew() { return crew; }

    public CastItem getDirector() {
        if (crew == null) return null;
        for (CastItem c : crew) {
            if ("Director".equals(c.getJob())) return c;
        }
        return null;
    }

    public CastItem getWriter() {
        if (crew == null) return null;
        for (CastItem c : crew) {
            if ("Screenplay".equals(c.getJob()) || "Writer".equals(c.getJob())) return c;
        }
        return null;
    }
}