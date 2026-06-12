package com.reely.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class VideoResponse {

    @SerializedName("results")
    private List<Video> results;

    public List<Video> getResults() { return results; }

    public Video getMainTrailer() {
        if (results == null) return null;
        for (Video v : results) {
            if (v.isYouTube() && v.isTrailer() && v.isOfficial()) return v;
        }
        for (Video v : results) {
            if (v.isYouTube() && v.isTrailer()) return v;
        }
        for (Video v : results) {
            if (v.isYouTube()) return v;
        }
        return null;
    }
}