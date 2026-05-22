package com.reely.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.reely.models.Movie;
import com.reely.models.MovieDetail;
import com.reely.repository.MovieRepository;

public class DetailViewModel extends AndroidViewModel {

    private final MovieRepository repository;

    private final MutableLiveData<MovieDetail> movieDetail = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isInWatchlist = new MutableLiveData<>(false);

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isError   = new MutableLiveData<>(false);

    private final MutableLiveData<String> snackbarMessage = new MutableLiveData<>();

    public DetailViewModel(@NonNull Application application) {
        super(application);
        repository = MovieRepository.getInstance(application);
    }

    public void loadMovieDetail(int movieId) {
        isLoading.setValue(true);
        isError.setValue(false);

        repository.getMovieDetail(movieId).observeForever(detail -> {
            isLoading.setValue(false);

            if (detail != null) {
                movieDetail.setValue(detail);
                isError.setValue(false);
            } else {
                isError.setValue(true);
            }
        });

        checkWatchlistStatus(movieId);
    }

    public void checkWatchlistStatus(int movieId) {
        repository.isInWatchlist(movieId, exists -> {
            isInWatchlist.setValue(exists);
        });
    }

    public void toggleWatchlist(MovieDetail detail) {
        Boolean currentStatus = isInWatchlist.getValue();
        if (currentStatus == null) return;

        if (currentStatus) {
            repository.removeFromWatchlist(detail.getId(), success -> {
                if (success) {
                    isInWatchlist.setValue(false);
                    snackbarMessage.setValue("Removed from watchlist");
                }
            });
        } else {
            Movie movie = detail.toMovie();
            repository.addToWatchlist(movie, success -> {
                if (success) {
                    isInWatchlist.setValue(true);
                    snackbarMessage.setValue("Added to your watchlist ✨");
                }
            });
        }
    }

    public MutableLiveData<MovieDetail> getMovieDetail()    { return movieDetail; }
    public MutableLiveData<Boolean>     getIsInWatchlist()  { return isInWatchlist; }
    public MutableLiveData<Boolean>     getIsLoading()      { return isLoading; }
    public MutableLiveData<Boolean>     getIsError()        { return isError; }
    public MutableLiveData<String>      getSnackbarMessage(){ return snackbarMessage; }
}