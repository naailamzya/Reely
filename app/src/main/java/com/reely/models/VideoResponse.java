package com.reely.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class VideoResponse {

    @SerializedName("results")
    private List<Video> results;

    public List<Video> getResults() { return results; }

    /**
     * Ambil trailer utama (official + YouTube).
     */
    public Video getMainTrailer() {
        if (results == null) return null;
        // Cari official trailer dulu
        for (Video v : results) {
            if (v.isYouTube() && v.isTrailer() && v.isOfficial()) return v;
        }
        // Fallback: trailer apapun
        for (Video v : results) {
            if (v.isYouTube() && v.isTrailer()) return v;
        }
        // Fallback: video apapun dari YouTube
        for (Video v : results) {
            if (v.isYouTube()) return v;
        }
        return null;
    }
}