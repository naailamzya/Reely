package com.reely.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.reely.api.ApiClient;
import com.reely.api.TmdbApiService;
import com.reely.models.Genre;
import com.reely.models.Movie;
import com.reely.models.MovieResponse;
import com.reely.repository.MovieRepository;
import com.reely.utils.Constants;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscoverViewModel extends AndroidViewModel {

    public static final String SORT_POPULARITY = "popularity.desc";
    public static final String SORT_RATING     = "vote_average.desc";

    private final TmdbApiService apiService;
    private final MovieRepository repository;

    private String currentQuery   = "";     // search keyword
    private int    currentGenreId = 0;      // 0 = All genres
    private String currentSort    = SORT_POPULARITY;
    private int    currentPage    = 0;

    private final MutableLiveData<List<Movie>> newPage      = new MutableLiveData<>();
    private final MutableLiveData<List<Genre>> genres       = new MutableLiveData<>();
    private final MutableLiveData<Boolean>     isLoading    = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean>     isLoadingMore= new MutableLiveData<>(false);
    private final MutableLiveData<Boolean>     isError      = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean>     isEmpty      = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean>     hasMore      = new MutableLiveData<>(true);
    private final MutableLiveData<Integer>     totalResults = new MutableLiveData<>(0);

    public DiscoverViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getService();
        repository = MovieRepository.getInstance(application);
    }

    public void loadInitial() {
        loadGenres();
        resetAndLoad();
    }

    private void loadGenres() {
        repository.getGenres(genreList -> {
            if (genreList != null) {
                List<Genre> fullList = new ArrayList<>();
                fullList.add(new Genre(0, "All"));
                fullList.addAll(genreList);
                genres.setValue(fullList);
            }
        });
    }

    public void search(String query) {
        this.currentQuery = query == null ? "" : query.trim();
        resetAndLoad();
    }

    public void filterByGenre(int genreId) {
        this.currentGenreId = genreId;
        resetAndLoad();
    }

    public void sortBy(String sortOption) {
        this.currentSort = sortOption;
        resetAndLoad();
    }

    public void loadNextPage() {
        Boolean loading     = isLoading.getValue();
        Boolean loadingMore = isLoadingMore.getValue();
        Boolean more        = hasMore.getValue();
        if ((loading != null && loading) ||
                (loadingMore != null && loadingMore) ||
                (more != null && !more)) return;

        isLoadingMore.setValue(true);
        fetchPage(currentPage + 1);
    }

    public void retry() { resetAndLoad(); }

    private void resetAndLoad() {
        currentPage = 0;
        hasMore.setValue(true);
        isEmpty.setValue(false);
        isError.setValue(false);
        newPage.setValue(null);
        isLoading.setValue(true);
        fetchPage(1);
    }

    private void fetchPage(int page) {
        Call<MovieResponse> call;

        if (!currentQuery.isEmpty()) {
            call = apiService.searchMovies(
                    ApiClient.getApiKey(),
                    Constants.DEFAULT_LANGUAGE,
                    currentQuery,
                    page);
        } else {
            String genreParam = currentGenreId > 0
                    ? String.valueOf(currentGenreId) : null;
            call = apiService.discoverMovies(
                    ApiClient.getApiKey(),
                    Constants.DEFAULT_LANGUAGE,
                    genreParam,
                    currentSort,
                    page,
                    page == 1 ? 50 : 0);
        }

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                isLoading.setValue(false);
                isLoadingMore.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getResults();
                    totalResults.setValue(response.body().getTotalResults());

                    if (movies == null || movies.isEmpty()) {
                        if (page == 1) isEmpty.setValue(true);
                        hasMore.setValue(false);
                    } else {
                        currentPage = page;
                        newPage.setValue(movies);
                        isEmpty.setValue(false);

                        if (movies.size() < 20) hasMore.setValue(false);
                    }
                } else {
                    if (page == 1) isError.setValue(true);
                    else hasMore.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                isLoading.setValue(false);
                isLoadingMore.setValue(false);
                if (page == 1) isError.setValue(true);
            }
        });
    }

    public MutableLiveData<List<Movie>> getNewPage()       { return newPage; }
    public MutableLiveData<List<Genre>> getGenres()        { return genres; }
    public MutableLiveData<Boolean>     getIsLoading()     { return isLoading; }
    public MutableLiveData<Boolean>     getIsLoadingMore() { return isLoadingMore; }
    public MutableLiveData<Boolean>     getIsError()       { return isError; }
    public MutableLiveData<Boolean>     getIsEmpty()       { return isEmpty; }
    public MutableLiveData<Boolean>     getHasMore()       { return hasMore; }
    public MutableLiveData<Integer>     getTotalResults()  { return totalResults; }
    public int                          getCurrentPage()   { return currentPage; }
    public String                       getCurrentSort()   { return currentSort; }
}