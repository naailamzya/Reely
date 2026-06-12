package com.reely.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.reely.models.Movie;
import com.reely.repository.MovieRepository;
import java.util.List;

public class WatchlistViewModel extends AndroidViewModel {
    private final MovieRepository repository;
    private final MutableLiveData<List<Movie>> watchlistMovies = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isEmpty = new MutableLiveData<>(false);
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();

    public WatchlistViewModel(@NonNull Application application) {
        super(application);
        repository = MovieRepository.getInstance(application);
    }

    public void loadWatchlist() {
        repository.getAllWatchlistMovies(movies -> {
            watchlistMovies.setValue(movies);
            isEmpty.setValue(movies == null || movies.isEmpty());
        });
    }

    public void removeFromWatchlist(int movieId) {
        repository.removeFromWatchlist(movieId, success -> {
            if (success) {
                toastMessage.setValue("Removed from watchlist");
                loadWatchlist(); // reload ulang
            } else {
                toastMessage.setValue("Failed to remove");
            }
        });
    }

    public MutableLiveData<List<Movie>> getWatchlistMovies() { return watchlistMovies; }
    public MutableLiveData<Boolean> getIsEmpty() { return isEmpty; }
    public MutableLiveData<String> getToastMessage() { return toastMessage; }
}