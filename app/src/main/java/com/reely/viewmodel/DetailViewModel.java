package com.reely.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.reely.api.ApiClient;
import com.reely.api.TmdbApiService;
import com.reely.models.CastItem;
import com.reely.models.Credits;
import com.reely.models.Movie;
import com.reely.models.MovieDetail;
import com.reely.models.Video;
import com.reely.models.VideoResponse;
import com.reely.repository.MovieRepository;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailViewModel extends AndroidViewModel {
    private final MovieRepository repository;
    private final TmdbApiService apiService;
    private final MutableLiveData<MovieDetail> movieDetail = new MutableLiveData<>();
    private final MutableLiveData<List<CastItem>> castList = new MutableLiveData<>();
    private final MutableLiveData<List<CastItem>> crewList = new MutableLiveData<>();
    private final MutableLiveData<Video> mainTrailer = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isInWatchlist = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isError = new MutableLiveData<>(false);
    private final MutableLiveData<String> snackbarMsg = new MutableLiveData<>();

    public DetailViewModel(@NonNull Application application) {
        super(application);
        repository = MovieRepository.getInstance(application);
        apiService = ApiClient.getService();
    }

    public void loadMovieDetail(int movieId) {
        isLoading.setValue(true);
        isError.setValue(false);
        loadDetail(movieId);
        loadCredits(movieId);
        loadVideos(movieId);
        checkWatchlistStatus(movieId);
    }

    private void loadDetail(int movieId) {
        repository.getMovieDetail(movieId, detail -> {
            isLoading.setValue(false);
            if (detail != null) {
                movieDetail.setValue(detail);
            } else {
                isError.setValue(true);
            }
        });
    }

    private void loadCredits(int movieId) {
        apiService.getMovieCredits(movieId, ApiClient.getApiKey(), "en-US")
                .enqueue(new Callback<Credits>() {
                    @Override
                    public void onResponse(Call<Credits> call, Response<Credits> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Credits credits = response.body();
                            List<CastItem> cast = credits.getCast();
                            if (cast != null && cast.size() > 15) cast = cast.subList(0, 15);
                            castList.setValue(cast);

                            List<CastItem> keyCrew = new ArrayList<>();
                            if (credits.getCrew() != null) {
                                for (CastItem c : credits.getCrew()) {
                                    String job = c.getJob();
                                    if ("Director".equals(job) || "Screenplay".equals(job) ||
                                            "Writer".equals(job) || "Producer".equals(job) ||
                                            "Original Music Composer".equals(job)) {
                                        keyCrew.add(c);
                                        if (keyCrew.size() >= 8) break;
                                    }
                                }
                            }
                            crewList.setValue(keyCrew);
                        }
                    }
                    @Override
                    public void onFailure(Call<Credits> call, Throwable t) {}
                });
    }

    private void loadVideos(int movieId) {
        apiService.getMovieVideos(movieId, ApiClient.getApiKey(), "en-US")
                .enqueue(new Callback<VideoResponse>() {
                    @Override
                    public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            mainTrailer.setValue(response.body().getMainTrailer());
                        }
                    }
                    @Override
                    public void onFailure(Call<VideoResponse> call, Throwable t) {}
                });
    }

    private void checkWatchlistStatus(int movieId) {
        repository.isInWatchlist(movieId, exists -> isInWatchlist.setValue(exists));
    }

    public void toggleWatchlist(MovieDetail detail) {
        Boolean current = isInWatchlist.getValue();
        if (current == null) return;
        if (current) {
            repository.removeFromWatchlist(detail.getId(), success -> {
                if (success) {
                    isInWatchlist.setValue(false);
                    snackbarMsg.setValue("Removed from watchlist");
                }
            });
        } else {
            Movie movie = detail.toMovie();
            repository.addToWatchlist(movie, success -> {
                if (success) {
                    isInWatchlist.setValue(true);
                    snackbarMsg.setValue("Added to your watchlist ✨");
                } else {
                    snackbarMsg.setValue("Failed to add to watchlist");
                }
            });
        }
    }

    public MutableLiveData<MovieDetail> getMovieDetail() { return movieDetail; }
    public MutableLiveData<List<CastItem>> getCastList() { return castList; }
    public MutableLiveData<List<CastItem>> getCrewList() { return crewList; }
    public MutableLiveData<Video> getMainTrailer() { return mainTrailer; }
    public MutableLiveData<Boolean> getIsInWatchlist() { return isInWatchlist; }
    public MutableLiveData<Boolean> getIsLoading() { return isLoading; }
    public MutableLiveData<Boolean> getIsError() { return isError; }
    public MutableLiveData<String> getSnackbarMsg() { return snackbarMsg; }
}