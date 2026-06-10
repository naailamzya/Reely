package com.reely.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.reely.api.ApiClient;
import com.reely.api.TmdbApiService;
import com.reely.database.WatchlistDao;
import com.reely.models.Movie;
import com.reely.models.MovieDetail;
import com.reely.models.MovieResponse;
import com.reely.utils.AppExecutors;
import com.reely.utils.Constants;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * REELY — MovieRepository
 */
public class MovieRepository {

    private final TmdbApiService apiService;
    private final WatchlistDao watchlistDao;
    private static MovieRepository instance;

    private MovieRepository(Context context) {
        apiService = ApiClient.getService();
        watchlistDao = new WatchlistDao(context);
    }

    public static synchronized MovieRepository getInstance(Context context) {
        if (instance == null) {
            instance = new MovieRepository(context.getApplicationContext());
        }
        return instance;
    }

    public MutableLiveData<List<Movie>> getNowPlayingMovies(int page) {
        MutableLiveData<List<Movie>> data = new MutableLiveData<>();
        apiService.getNowPlayingMovies(ApiClient.getApiKey(), Constants.DEFAULT_LANGUAGE, page)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null)
                            data.setValue(response.body().getResults());
                        else data.setValue(null);
                    }
                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        data.setValue(null);
                    }
                });
        return data;
    }

    public MutableLiveData<List<Movie>> getUpcomingMovies(int page) {
        MutableLiveData<List<Movie>> data = new MutableLiveData<>();
        apiService.getUpcomingMovies(ApiClient.getApiKey(), Constants.DEFAULT_LANGUAGE, page)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null)
                            data.setValue(response.body().getResults());
                        else data.setValue(null);
                    }
                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        data.setValue(null);
                    }
                });
        return data;
    }

    public MutableLiveData<List<Movie>> getTrendingMovies() {
        MutableLiveData<List<Movie>> data = new MutableLiveData<>();
        apiService.getTrendingMovies(ApiClient.getApiKey(), Constants.DEFAULT_LANGUAGE)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null)
                            data.setValue(response.body().getResults());
                        else data.setValue(null);
                    }
                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        data.setValue(null);
                    }
                });
        return data;
    }

    public MutableLiveData<List<Movie>> getTopRatedMovies(int page) {
        MutableLiveData<List<Movie>> data = new MutableLiveData<>();
        apiService.getTopRatedMovies(ApiClient.getApiKey(), Constants.DEFAULT_LANGUAGE, page)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null)
                            data.setValue(response.body().getResults());
                        else data.setValue(null);
                    }
                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        data.setValue(null);
                    }
                });
        return data;
    }

    public MutableLiveData<MovieDetail> getMovieDetail(int movieId) {
        MutableLiveData<MovieDetail> data = new MutableLiveData<>();
        apiService.getMovieDetail(movieId, ApiClient.getApiKey(), Constants.DEFAULT_LANGUAGE)
                .enqueue(new Callback<MovieDetail>() {
                    @Override
                    public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                        if (response.isSuccessful() && response.body() != null)
                            data.setValue(response.body());
                        else data.setValue(null);
                    }
                    @Override
                    public void onFailure(Call<MovieDetail> call, Throwable t) {
                        data.setValue(null);
                    }
                });
        return data;
    }

    public void addToWatchlist(Movie movie, RepositoryCallback<Boolean> callback) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            boolean success = watchlistDao.addToWatchlist(movie);
            AppExecutors.getInstance().mainThread(() -> callback.onResult(success));
        });
    }

    public void removeFromWatchlist(int movieId, RepositoryCallback<Boolean> callback) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            boolean success = watchlistDao.removeFromWatchlist(movieId);
            AppExecutors.getInstance().mainThread(() -> callback.onResult(success));
        });
    }

    public void getAllWatchlistMovies(RepositoryCallback<List<Movie>> callback) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            List<Movie> movies = watchlistDao.getAllWatchlistMovies();
            AppExecutors.getInstance().mainThread(() -> callback.onResult(movies));
        });
    }

    public void isInWatchlist(int movieId, RepositoryCallback<Boolean> callback) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            boolean exists = watchlistDao.isInWatchlist(movieId);
            AppExecutors.getInstance().mainThread(() -> callback.onResult(exists));
        });
    }

    public interface RepositoryCallback<T> {
        void onResult(T result);
    }
}