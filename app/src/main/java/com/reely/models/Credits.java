package com.reely.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Wrapper untuk response credits dari TMDB.
 * GET /movie/{id}/credits
 */
public class Credits {

    @SerializedName("cast")
    private List<CastItem> cast;

    @SerializedName("crew")
    private List<CastItem> crew;

    public List<CastItem> getCast() { return cast; }
    public List<CastItem> getCrew() { return crew; }

    /**
     * Ambil sutradara dari crew list.
     */
    public CastItem getDirector() {
        if (crew == null) return null;
        for (CastItem c : crew) {
            if ("Director".equals(c.getJob())) return c;
        }
        return null;
    }

    /**
     * Ambil penulis naskah dari crew.
     */
    public CastItem getWriter() {
        if (crew == null) return null;
        for (CastItem c : crew) {
            if ("Screenplay".equals(c.getJob()) || "Writer".equals(c.getJob())) return c;
        }
        return null;
    }
}