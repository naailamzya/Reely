package com.reely.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.reely.models.Movie;
import com.reely.repository.MovieRepository;
import com.reely.utils.Constants;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final MovieRepository repository;
    private final MutableLiveData<List<Movie>> nowPlayingMovies  = new MutableLiveData<>();
    private final MutableLiveData<List<Movie>> upcomingMovies    = new MutableLiveData<>();
    private final MutableLiveData<List<Movie>> trendingMovies    = new MutableLiveData<>();
    private final MutableLiveData<List<Movie>> topRatedMovies    = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isError   = new MutableLiveData<>(false);

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = MovieRepository.getInstance(application);
    }

    public void loadAllHomeData() {
        isLoading.setValue(true);
        isError.setValue(false);

        loadNowPlaying();
        loadUpcoming();
        loadTrending();
        loadTopRated();
    }

    public void refresh() {
        loadAllHomeData();
    }

    private void loadNowPlaying() {
        repository.getNowPlayingMovies(Constants.DEFAULT_PAGE).observeForever(movies -> {
            nowPlayingMovies.setValue(movies);
            checkLoadingComplete();
        });
    }

    private void loadUpcoming() {
        repository.getUpcomingMovies(Constants.DEFAULT_PAGE).observeForever(movies -> {
            upcomingMovies.setValue(movies);
            checkLoadingComplete();
        });
    }

    private void loadTrending() {
        repository.getTrendingMovies().observeForever(movies -> {
            trendingMovies.setValue(movies);
            checkLoadingComplete();
        });
    }

    private void loadTopRated() {
        repository.getTopRatedMovies(Constants.DEFAULT_PAGE).observeForever(movies -> {
            topRatedMovies.setValue(movies);
            checkLoadingComplete();
        });
    }

    private void checkLoadingComplete() {
        boolean nowPlayingDone  = nowPlayingMovies.getValue() != null;
        boolean upcomingDone    = upcomingMovies.getValue() != null;
        boolean trendingDone    = trendingMovies.getValue() != null;
        boolean topRatedDone    = topRatedMovies.getValue() != null;

        if (nowPlayingDone && upcomingDone && trendingDone && topRatedDone) {
            isLoading.setValue(false);
            boolean hasError =
                    (nowPlayingMovies.getValue() == null || nowPlayingMovies.getValue().isEmpty()) &&
                            (trendingMovies.getValue() == null || trendingMovies.getValue().isEmpty());

            isError.setValue(hasError);
        }
    }

    public MutableLiveData<List<Movie>> getNowPlayingMovies()  { return nowPlayingMovies; }
    public MutableLiveData<List<Movie>> getUpcomingMovies()    { return upcomingMovies; }
    public MutableLiveData<List<Movie>> getTrendingMovies()    { return trendingMovies; }
    public MutableLiveData<List<Movie>> getTopRatedMovies()    { return topRatedMovies; }
    public MutableLiveData<Boolean>     getIsLoading()         { return isLoading; }
    public MutableLiveData<Boolean>     getIsError()           { return isError; }
}
