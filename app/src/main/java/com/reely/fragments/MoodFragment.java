package com.reely.fragments;

import android.animation.ValueAnimator;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.reely.activities.MovieDetailActivity;
import com.reely.adapters.MoodAdapter;
import com.reely.adapters.MovieAdapter;
import com.reely.databinding.FragmentMoodBinding;
import com.reely.utils.MoodMapper;
import com.reely.utils.NetworkUtils;
import com.reely.viewmodel.MoodViewModel;

public class MoodFragment extends Fragment {

    private FragmentMoodBinding binding;
    private MoodViewModel viewModel;
    private MoodAdapter moodAdapter;
    private MovieAdapter moodMoviesAdapter;

    private int currentGradientStart;
    private int currentGradientEnd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMoodBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentGradientStart = ContextCompat.getColor(
                requireContext(), com.reely.R.color.mood_comfort_start);
        currentGradientEnd = ContextCompat.getColor(
                requireContext(), com.reely.R.color.mood_comfort_end);

        setupRecyclerViews();
        setupViewModel();
    }

    private void setupRecyclerViews() {
        moodAdapter = new MoodAdapter((mood, position) ->
                viewModel.setSelectedMood(mood.getKey()));
        binding.rvMoodSelector.setLayoutManager(
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.HORIZONTAL, false));
        binding.rvMoodSelector.setAdapter(moodAdapter);

        moodMoviesAdapter = new MovieAdapter(movie ->
                MovieDetailActivity.start(requireContext(), movie.getId()));
        binding.rvMoodMovies.setLayoutManager(
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.HORIZONTAL, false));
        binding.rvMoodMovies.setAdapter(moodMoviesAdapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(MoodViewModel.class);

        viewModel.getMoodList().observe(getViewLifecycleOwner(), moods -> {
            if (moods != null) moodAdapter.setMoods(moods);
        });

        viewModel.getSelectedMoodKey().observe(getViewLifecycleOwner(), moodKey -> {
            if (moodKey != null) updateMoodAtmosphere(moodKey);
        });

        viewModel.getMoodMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null && !movies.isEmpty()) {
                moodMoviesAdapter.setMovies(movies);
                binding.rvMoodMovies.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressMood.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (isLoading) binding.rvMoodMovies.setVisibility(View.GONE);
        });

        viewModel.getIsError().observe(getViewLifecycleOwner(), isError -> {
            binding.layoutMoodError.getRoot().setVisibility(
                    isError ? View.VISIBLE : View.GONE);
            if (isError) setupRetryButton();
        });
    }

    private void updateMoodAtmosphere(String moodKey) {
        int newStart = ContextCompat.getColor(requireContext(),
                MoodMapper.getGradientStartColor(moodKey));
        int newEnd = ContextCompat.getColor(requireContext(),
                MoodMapper.getGradientEndColor(moodKey));

        animateGradientBackground(newStart, newEnd);
        updateQuoteWithFade(moodKey);

        int accentColor = ContextCompat.getColor(requireContext(),
                MoodMapper.getAccentColor(moodKey));
        binding.viewQuoteAccent.setBackgroundColor(accentColor);
    }

    private void animateGradientBackground(int newStart, int newEnd) {
        // ✅ FIX: gunakan array untuk share state antar 2 animator
        // sebelumnya crash karena endColorAnim.getAnimatedValue() null
        // saat startColorAnim listener pertama kali dipanggil
        final int[] animatedColors = {currentGradientStart, currentGradientEnd};

        // Animator start color — pakai ofArgb() agar tidak null
        ValueAnimator startAnim = ValueAnimator.ofArgb(currentGradientStart, newStart);
        startAnim.setDuration(600);
        startAnim.addUpdateListener(animation -> {
            animatedColors[0] = (int) animation.getAnimatedValue();
            applyGradient(animatedColors[0], animatedColors[1]);
        });

        // Animator end color
        ValueAnimator endAnim = ValueAnimator.ofArgb(currentGradientEnd, newEnd);
        endAnim.setDuration(600);
        endAnim.addUpdateListener(animation -> {
            animatedColors[1] = (int) animation.getAnimatedValue();
            applyGradient(animatedColors[0], animatedColors[1]);
        });

        startAnim.start();
        endAnim.start();

        currentGradientStart = newStart;
        currentGradientEnd = newEnd;
    }

    private void applyGradient(int startColor, int endColor) {
        if (binding == null) return;
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{startColor, endColor});
        gradient.setCornerRadius(0f);
        binding.viewMoodGradient.setBackground(gradient);
    }

    private void updateQuoteWithFade(String moodKey) {
        binding.tvMoodQuote.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> {
                    if (binding == null) return;
                    String quote = getString(MoodMapper.getQuoteResId(moodKey));
                    binding.tvMoodQuote.setText(quote);
                    binding.tvMoodQuote.animate().alpha(1f).setDuration(300).start();
                }).start();
    }

    private void setupRetryButton() {
        binding.layoutMoodError.getRoot()
                .findViewById(com.reely.R.id.btnRetry)
                .setOnClickListener(v -> {
                    if (NetworkUtils.isNotConnected(requireContext())) return;
                    binding.layoutMoodError.getRoot().setVisibility(View.GONE);
                    viewModel.retry();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}