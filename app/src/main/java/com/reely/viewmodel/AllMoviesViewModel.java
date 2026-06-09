package com.reely.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.reely.models.Movie;
import com.reely.repository.MovieRepository;
import java.util.ArrayList;
import java.util.List;

/**
 * REELY — AllMoviesViewModel
 *
 * Mengelola pagination untuk halaman "See All".
 * Mendukung kategori: now_playing, upcoming, trending, top_rated
 *
 * Flow:
 *   1. loadPage(1) → set allMovies
 *   2. User tap "Load More" → loadPage(2) → append ke allMovies
 *   3. Ulangi sampai hasMore = false
 */
public class AllMoviesViewModel extends AndroidViewModel {

    public static final String CATEGORY_NOW_PLAYING = "now_playing";
    public static final String CATEGORY_UPCOMING    = "upcoming";
    public static final String CATEGORY_TRENDING    = "trending";
    public static final String CATEGORY_TOP_RATED   = "top_rated";

    private final MovieRepository repository;

    // ── LiveData ──────────────────────────────────────────────────
    private final MutableLiveData<List<Movie>> allMovies   = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Movie>> newPage     = new MutableLiveData<>();
    private final MutableLiveData<Boolean>     isLoading   = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean>     isLoadingMore = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean>     isError     = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean>     hasMore     = new MutableLiveData<>(true);

    // ── State ─────────────────────────────────────────────────────
    private String currentCategory = CATEGORY_NOW_PLAYING;
    private int currentPage = 0;
    private static final int MOVIES_PER_PAGE = 20; // TMDB default

    public AllMoviesViewModel(@NonNull Application application) {
        super(application);
        repository = MovieRepository.getInstance(application);
    }

    // ─────────────────────────────────────────────────────────────
    //  INIT — set kategori dan load page pertama
    // ─────────────────────────────────────────────────────────────

    public void init(String category) {
        this.currentCategory = category;
        this.currentPage = 0;
        allMovies.setValue(new ArrayList<>());
        hasMore.setValue(true);
        loadNextPage();
    }

    // ─────────────────────────────────────────────────────────────
    //  LOAD NEXT PAGE — dipanggil saat tap "Load More"
    // ─────────────────────────────────────────────────────────────

    public void loadNextPage() {
        Boolean loading = isLoading.getValue();
        Boolean loadingMore = isLoadingMore.getValue();
        Boolean more = hasMore.getValue();

        if ((loading != null && loading) ||
                (loadingMore != null && loadingMore) ||
                (more != null && !more)) return;

        int nextPage = currentPage + 1;

        if (currentPage == 0) {
            isLoading.setValue(true);
        } else {
            isLoadingMore.setValue(true);
        }
        isError.setValue(false);

        fetchPage(nextPage);
    }

    private void fetchPage(int page) {
        MutableLiveData<List<Movie>> source;

        switch (currentCategory) {
            case CATEGORY_UPCOMING:
                source = repository.getUpcomingMovies(page);
                break;
            case CATEGORY_TRENDING:
                source = repository.getTrendingMovies();
                break;
            case CATEGORY_TOP_RATED:
                source = repository.getTopRatedMovies(page);
                break;
            case CATEGORY_NOW_PLAYING:
            default:
                source = repository.getNowPlayingMovies(page);
                break;
        }

        source.observeForever(movies -> {
            isLoading.setValue(false);
            isLoadingMore.setValue(false);

            if (movies != null && !movies.isEmpty()) {
                currentPage++;

                // Append ke list yang ada
                List<Movie> current = allMovies.getValue();
                if (current == null) current = new ArrayList<>();
                current.addAll(movies);
                allMovies.setValue(new ArrayList<>(current));

                // Expose page baru untuk append di adapter
                newPage.setValue(movies);

                // Kalau hasil < 20, berarti sudah halaman terakhir
                if (movies.size() < MOVIES_PER_PAGE) {
                    hasMore.setValue(false);
                }
            } else {
                if (currentPage == 0) {
                    isError.setValue(true);
                } else {
                    // Tidak ada data lagi
                    hasMore.setValue(false);
                }
            }
        });
    }

    public void retry() {
        loadNextPage();
    }

    // ── Getters ───────────────────────────────────────────────────
    public MutableLiveData<List<Movie>> getAllMovies()     { return allMovies; }
    public MutableLiveData<List<Movie>> getNewPage()      { return newPage; }
    public MutableLiveData<Boolean>     getIsLoading()    { return isLoading; }
    public MutableLiveData<Boolean>     getIsLoadingMore(){ return isLoadingMore; }
    public MutableLiveData<Boolean>     getIsError()      { return isError; }
    public MutableLiveData<Boolean>     getHasMore()      { return hasMore; }
    public int getCurrentPage()                           { return currentPage; }
}