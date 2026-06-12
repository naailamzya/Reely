package com.reely.api;

import com.reely.models.Credits;
import com.reely.models.GenreResponse;
import com.reely.models.MovieDetail;
import com.reely.models.MovieResponse;
import com.reely.models.VideoResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TmdbApiService {

    @GET("movie/now_playing")
    Call<MovieResponse> getNowPlayingMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page);

    @GET("movie/upcoming")
    Call<MovieResponse> getUpcomingMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page);

    @GET("trending/movie/week")
    Call<MovieResponse> getTrendingMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language);

    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page);

    @GET("discover/movie")
    Call<MovieResponse> discoverMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("with_genres") String genreIds,
            @Query("sort_by") String sortBy,
            @Query("page") int page,
            @Query("vote_count.gte") int minVoteCount);

    @GET("search/movie")
    Call<MovieResponse> searchMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("query") String query,
            @Query("page") int page);

    @GET("movie/{movie_id}")
    Call<MovieDetail> getMovieDetail(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey,
            @Query("language") String language);

    @GET("movie/{movie_id}/credits")
    Call<Credits> getMovieCredits(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey,
            @Query("language") String language);

    @GET("movie/{movie_id}/videos")
    Call<VideoResponse> getMovieVideos(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey,
            @Query("language") String language);

    @GET("genre/movie/list")
    Call<GenreResponse> getGenres(
            @Query("api_key") String apiKey,
            @Query("language") String language);

    @GET("movie/{movie_id}/recommendations")
    Call<MovieResponse> getRecommendations(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page);
}