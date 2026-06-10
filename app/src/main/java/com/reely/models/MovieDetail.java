package com.reely.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * REELY — MovieDetail (Enhanced with budget + revenue)
 */
public class MovieDetail {

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

    @SerializedName("runtime")
    private int runtime;

    @SerializedName("tagline")
    private String tagline;

    @SerializedName("status")
    private String status;

    @SerializedName("popularity")
    private double popularity;

    @SerializedName("original_language")
    private String originalLanguage;

    @SerializedName("genres")
    private List<Genre> genres;

    // ✅ BARU
    @SerializedName("budget")
    private long budget;

    @SerializedName("revenue")
    private long revenue;

    // ── Getters ───────────────────────────────────────────────────

    public int getId()                  { return id; }
    public String getTitle()            { return title; }
    public String getOverview()         { return overview; }
    public String getPosterPath()       { return posterPath; }
    public String getBackdropPath()     { return backdropPath; }
    public double getVoteAverage()      { return voteAverage; }
    public int getVoteCount()           { return voteCount; }
    public String getReleaseDate()      { return releaseDate; }
    public int getRuntime()             { return runtime; }
    public String getTagline()          { return tagline; }
    public String getStatus()           { return status; }
    public double getPopularity()       { return popularity; }
    public String getOriginalLanguage() { return originalLanguage; }
    public List<Genre> getGenres()      { return genres; }
    public long getBudget()             { return budget; }
    public long getRevenue()            { return revenue; }

    // ── Helpers ───────────────────────────────────────────────────

    public String getFullPosterUrl(String size) {
        if (posterPath == null || posterPath.isEmpty()) return null;
        return "https://image.tmdb.org/t/p/" + size + posterPath;
    }

    public String getFullBackdropUrl(String size) {
        if (backdropPath == null || backdropPath.isEmpty()) return null;
        return "https://image.tmdb.org/t/p/" + size + backdropPath;
    }

    public String getFormattedRating() { return String.format("%.1f", voteAverage); }

    public String getFormattedRuntime() {
        if (runtime <= 0) return "N/A";
        int h = runtime / 60, m = runtime % 60;
        return h == 0 ? m + "m" : h + "h " + m + "m";
    }

    public String getReleaseYear() {
        if (releaseDate == null || releaseDate.length() < 4) return "";
        return releaseDate.substring(0, 4);
    }

    public String getGenreString() {
        if (genres == null || genres.isEmpty()) return "Unknown";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < genres.size(); i++) {
            sb.append(genres.get(i).getName());
            if (i < genres.size() - 1) sb.append(" • ");
        }
        return sb.toString();
    }

    /**
     * Format angka besar: 150000000 → "$150M"
     */
    public String getFormattedBudget() {
        if (budget <= 0) return "N/A";
        return formatMoney(budget);
    }

    public String getFormattedRevenue() {
        if (revenue <= 0) return "N/A";
        return formatMoney(revenue);
    }

    private String formatMoney(long amount) {
        if (amount >= 1_000_000_000L)
            return String.format("$%.1fB", amount / 1_000_000_000.0);
        if (amount >= 1_000_000L)
            return String.format("$%.0fM", amount / 1_000_000.0);
        if (amount >= 1_000L)
            return String.format("$%.0fK", amount / 1_000.0);
        return "$" + amount;
    }

    public Movie toMovie() {
        Movie movie = new Movie();
        movie.setId(id);
        movie.setTitle(title);
        movie.setOverview(overview);
        movie.setPosterPath(posterPath);
        movie.setBackdropPath(backdropPath);
        movie.setVoteAverage(voteAverage);
        movie.setVoteCount(voteCount);
        movie.setReleaseDate(releaseDate);
        return movie;
    }
}