package com.reely.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.reely.activities.AllMoviesActivity;
import com.reely.activities.MovieDetailActivity;
import com.reely.adapters.HeroAdapter;
import com.reely.adapters.MovieAdapter;
import com.reely.databinding.FragmentHomeBinding;
import com.reely.utils.NetworkUtils;
import com.reely.utils.SessionManager;
import com.reely.viewmodel.AllMoviesViewModel;
import com.reely.viewmodel.HomeViewModel;
import java.util.Calendar;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private HeroAdapter heroAdapter;
    private MovieAdapter nowPlayingAdapter;
    private MovieAdapter upcomingAdapter;
    private MovieAdapter trendingAdapter;
    private MovieAdapter topRatedAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Apply status bar inset ke greeting
        ViewCompat.setOnApplyWindowInsetsListener(binding.layoutGreeting, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(v.getPaddingLeft(), statusBarHeight + 8,
                    v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        setupGreeting();
        setupRecyclerViews();
        setupViewModel();
        setupSwipeRefresh();
        setupSeeAllButtons(); // ← NEW
        loadData();
    }

    private void setupGreeting() {
        SessionManager session = new SessionManager(requireContext());
        String username = session.getUsername();
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour < 12) greeting = getString(com.reely.R.string.home_greeting_morning);
        else if (hour < 17) greeting = getString(com.reely.R.string.home_greeting_afternoon);
        else greeting = getString(com.reely.R.string.home_greeting_evening);
        binding.tvGreeting.setText(greeting);
        binding.tvUsername.setText(username.isEmpty() ? "there" : username);
    }

    private void setupRecyclerViews() {
        heroAdapter = new HeroAdapter(movie ->
                MovieDetailActivity.start(requireContext(), movie.getId()));
        binding.rvHeroBanner.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvHeroBanner.setAdapter(heroAdapter);

        nowPlayingAdapter = new MovieAdapter(movie ->
                MovieDetailActivity.start(requireContext(), movie.getId()));
        binding.rvNowPlaying.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvNowPlaying.setAdapter(nowPlayingAdapter);

        upcomingAdapter = new MovieAdapter(movie ->
                MovieDetailActivity.start(requireContext(), movie.getId()));
        binding.rvUpcoming.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvUpcoming.setAdapter(upcomingAdapter);

        trendingAdapter = new MovieAdapter(movie ->
                MovieDetailActivity.start(requireContext(), movie.getId()));
        binding.rvTrending.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvTrending.setAdapter(trendingAdapter);

        topRatedAdapter = new MovieAdapter(movie ->
                MovieDetailActivity.start(requireContext(), movie.getId()));
        binding.rvTopRated.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvTopRated.setAdapter(topRatedAdapter);
    }

    // ─────────────────────────────────────────────────────────────
    //  SEE ALL BUTTONS — buka AllMoviesActivity
    // ─────────────────────────────────────────────────────────────

    private void setupSeeAllButtons() {
        binding.tvSeeAllNowPlaying.setOnClickListener(v ->
                AllMoviesActivity.start(requireContext(),
                        AllMoviesViewModel.CATEGORY_NOW_PLAYING,
                        getString(com.reely.R.string.home_section_nowplaying)));

        binding.tvSeeAllUpcoming.setOnClickListener(v ->
                AllMoviesActivity.start(requireContext(),
                        AllMoviesViewModel.CATEGORY_UPCOMING,
                        getString(com.reely.R.string.home_section_upcoming)));

        binding.tvSeeAllTrending.setOnClickListener(v ->
                AllMoviesActivity.start(requireContext(),
                        AllMoviesViewModel.CATEGORY_TRENDING,
                        getString(com.reely.R.string.home_section_trending)));

        binding.tvSeeAllTopRated.setOnClickListener(v ->
                AllMoviesActivity.start(requireContext(),
                        AllMoviesViewModel.CATEGORY_TOP_RATED,
                        getString(com.reely.R.string.home_section_toprated)));
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        viewModel.getNowPlayingMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null && !movies.isEmpty()) {
                heroAdapter.setMovies(movies);
                nowPlayingAdapter.setMovies(movies);
            }
        });

        viewModel.getUpcomingMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null) upcomingAdapter.setMovies(movies);
        });

        viewModel.getTrendingMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null) trendingAdapter.setMovies(movies);
        });

        viewModel.getTopRatedMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null) topRatedAdapter.setMovies(movies);
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.swipeRefreshHome.setRefreshing(false);
            binding.layoutShimmer.getRoot().setVisibility(
                    isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getIsError().observe(getViewLifecycleOwner(), isError -> {
            binding.layoutErrorState.getRoot().setVisibility(
                    isError ? View.VISIBLE : View.GONE);
            if (isError) setupRetryButton();
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshHome.setColorSchemeResources(
                com.reely.R.color.night_accent_primary);
        binding.swipeRefreshHome.setProgressBackgroundColorSchemeResource(
                com.reely.R.color.night_bg_card);
        binding.swipeRefreshHome.setOnRefreshListener(() -> {
            if (NetworkUtils.isNotConnected(requireContext())) {
                binding.swipeRefreshHome.setRefreshing(false);
                showError();
            } else {
                viewModel.refresh();
            }
        });
    }

    private void loadData() {
        if (NetworkUtils.isNotConnected(requireContext())) {
            showError();
            return;
        }
        viewModel.loadAllHomeData();
    }

    private void setupRetryButton() {
        binding.layoutErrorState.getRoot()
                .findViewById(com.reely.R.id.btnRetry)
                .setOnClickListener(v -> {
                    binding.layoutErrorState.getRoot().setVisibility(View.GONE);
                    loadData();
                });
    }

    private void showError() {
        binding.layoutErrorState.getRoot().setVisibility(View.VISIBLE);
        binding.layoutShimmer.getRoot().setVisibility(View.GONE);
        setupRetryButton();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}