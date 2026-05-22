package com.reely.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.reely.models.Movie;
import com.reely.models.MoodItem;
import com.reely.repository.MovieRepository;
import com.reely.utils.Constants;
import com.reely.utils.MoodMapper;
import java.util.List;

public class MoodViewModel extends AndroidViewModel {

    private final MovieRepository repository;
    private final MutableLiveData<List<MoodItem>> moodList = new MutableLiveData<>();
    private final MutableLiveData<List<Movie>> moodMovies = new MutableLiveData<>();

    private final MutableLiveData<String> selectedMoodKey =
            new MutableLiveData<>(Constants.MOOD_COMFORT);

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isError   = new MutableLiveData<>(false);

    public MoodViewModel(@NonNull Application application) {
        super(application);
        repository = MovieRepository.getInstance(application);

        loadMoodList();

        loadMoviesByMood(Constants.MOOD_COMFORT);
    }

    private void loadMoodList() {
        List<MoodItem> moods = MoodMapper.getAllMoods(getApplication());

        if (!moods.isEmpty()) {
            moods.get(0).setSelected(true);
        }

        moodList.setValue(moods);
    }

    public void setSelectedMood(String moodKey) {
        updateMoodSelection(moodKey);

        selectedMoodKey.setValue(moodKey);

        loadMoviesByMood(moodKey);
    }

    private void updateMoodSelection(String selectedKey) {
        List<MoodItem> moods = moodList.getValue();
        if (moods == null) return;

        for (MoodItem mood : moods) {
            mood.setSelected(mood.getKey().equals(selectedKey));
        }

        moodList.setValue(moods);
    }

    public void loadMoviesByMood(String moodKey) {
        isLoading.setValue(true);
        isError.setValue(false);
        moodMovies.setValue(null);

        repository.getMoviesByMood(moodKey).observeForever(movies -> {
            isLoading.setValue(false);

            if (movies != null && !movies.isEmpty()) {
                moodMovies.setValue(movies);
                isError.setValue(false);
            } else {
                moodMovies.setValue(null);
                isError.setValue(true);
            }
        });
    }

    public void retry() {
        String currentMood = selectedMoodKey.getValue();
        if (currentMood != null) {
            loadMoviesByMood(currentMood);
        }
    }

    public MutableLiveData<List<MoodItem>> getMoodList()      { return moodList; }
    public MutableLiveData<List<Movie>>    getMoodMovies()    { return moodMovies; }
    public MutableLiveData<String>         getSelectedMoodKey() { return selectedMoodKey; }
    public MutableLiveData<Boolean>        getIsLoading()     { return isLoading; }
    public MutableLiveData<Boolean>        getIsError()       { return isError; }
}