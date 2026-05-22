package com.reely.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
public class Movie {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("overview")
    private String overview;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("vote_count")
    private int voteCount;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("genre_ids")
    private List<Integer> genreIds;

    @SerializedName("popularity")
    private double popularity;

    @SerializedName("original_language")
    private String originalLanguage;

    @SerializedName("adult")
    private boolean adult;

    public Movie() {}
    public Movie(int id, String title, String posterPath, double voteAverage) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
    }

    public int getId() { return id; }

    public String getTitle() { return title; }

    public String getOverview() { return overview; }

    public String getPosterPath() { return posterPath; }

    public String getBackdropPath() { return backdropPath; }

    public double getVoteAverage() { return voteAverage; }

    public int getVoteCount() { return voteCount; }

    public String getReleaseDate() { return releaseDate; }

    public List<Integer> getGenreIds() { return genreIds; }

    public double getPopularity() { return popularity; }

    public String getOriginalLanguage() { return originalLanguage; }

    public boolean isAdult() { return adult; }

    public void setId(int id) { this.id = id; }

    public void setTitle(String title) { this.title = title; }

    public void setOverview(String overview) { this.overview = overview; }

    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }

    public void setBackdropPath(String backdropPath) { this.backdropPath = backdropPath; }

    public void setVoteAverage(double voteAverage) { this.voteAverage = voteAverage; }

    public void setVoteCount(int voteCount) { this.voteCount = voteCount; }

    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public void setGenreIds(List<Integer> genreIds) { this.genreIds = genreIds; }

    public void setPopularity(double popularity) { this.popularity = popularity; }

    public String getFullPosterUrl(String size) {
        if (posterPath == null || posterPath.isEmpty()) return null;
        return "https://image.tmdb.org/t/p/" + size + posterPath;
    }

    public String getFullBackdropUrl(String size) {
        if (backdropPath == null || backdropPath.isEmpty()) return null;
        return "https://image.tmdb.org/t/p/" + size + backdropPath;
    }

    public String getFormattedRating() {
        return String.format("%.1f", voteAverage);
    }

    public String getReleaseYear() {
        if (releaseDate == null || releaseDate.length() < 4) return "";
        return releaseDate.substring(0, 4);
    }
}