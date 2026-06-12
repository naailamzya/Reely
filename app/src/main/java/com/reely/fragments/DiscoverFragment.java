package com.reely.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import com.google.android.material.chip.Chip;
import com.reely.R;
import com.reely.activities.MovieDetailActivity;
import com.reely.adapters.MovieGridAdapter;
import com.reely.databinding.FragmentDiscoverBinding;
import com.reely.utils.Constants;
import com.reely.utils.NetworkUtils;
import com.reely.viewmodel.DiscoverViewModel;

public class DiscoverFragment extends Fragment {

    private FragmentDiscoverBinding binding;
    private DiscoverViewModel viewModel;
    private MovieGridAdapter adapter;

    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private static final long SEARCH_DELAY_MS = 500L;

    private static final int[] GENRE_IDS = {
            0, 28, 35, 18, 27, 878, 53, 10749, 10751, 16, 80
    };
    private static final String[] GENRE_NAMES = {
            "All", "Action", "Comedy", "Drama", "Horror",
            "Sci-Fi", "Thriller", "Romance", "Family", "Animation", "Crime"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDiscoverBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ✅ FIX: Gunakan padding pada header agar tidak menempel ke Status Bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.layoutDiscoverHeader, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            // Gunakan header_margin_top dari dimens (36dp) + tinggi status bar
            int basePaddingTop = getResources().getDimensionPixelSize(R.dimen.header_margin_top);
            v.setPadding(v.getPaddingLeft(), statusBarHeight + basePaddingTop,
                    v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        setupRecyclerView();
        setupGenreChips();
        setupSearch();
        setupSortButtons();
        setupViewModel();
        setupLoadMore();

        if (NetworkUtils.isNotConnected(requireContext())) {
            showError();
        } else {
            viewModel.loadInitial();
        }
    }

    private void setupRecyclerView() {
        adapter = new MovieGridAdapter(movie ->
                MovieDetailActivity.start(requireContext(), movie.getId()));

        GridLayoutManager gridManager = new GridLayoutManager(requireContext(), 2);
        binding.rvDiscover.setLayoutManager(gridManager);
        binding.rvDiscover.setAdapter(adapter);
        binding.rvDiscover.setNestedScrollingEnabled(false);
    }

    private void setupGenreChips() {
        for (int i = 0; i < GENRE_NAMES.length; i++) {
            Chip chip = new Chip(requireContext());
            chip.setText(GENRE_NAMES[i]);
            chip.setCheckable(true);
            chip.setChecked(i == 0);
            chip.setChipBackgroundColorResource(R.color.color_surface);
            chip.setTextColor(ContextCompat.getColor(requireContext(),
                    R.color.color_text_secondary));
            chip.setCheckedIconVisible(false);
            chip.setChipStrokeColorResource(R.color.color_stroke);
            chip.setChipStrokeWidth(1f);

            final int genreId = GENRE_IDS[i];
            chip.setOnClickListener(v -> {
                updateChipStyles(chip);
                viewModel.filterByGenre(genreId);
            });

            binding.chipGroupGenre.addView(chip);
        }
    }

    private void updateChipStyles(Chip selectedChip) {
        for (int i = 0; i < binding.chipGroupGenre.getChildCount(); i++) {
            Chip chip = (Chip) binding.chipGroupGenre.getChildAt(i);
            chip.setChipBackgroundColorResource(R.color.color_surface);
            chip.setTextColor(ContextCompat.getColor(requireContext(),
                    R.color.color_text_secondary));
            chip.setChipStrokeColorResource(R.color.color_stroke);
        }
        selectedChip.setChipBackgroundColor(
                android.content.res.ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.color_primary)));
        selectedChip.setTextColor(ContextCompat.getColor(requireContext(),
                R.color.color_on_primary));
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHandler.removeCallbacksAndMessages(null);
                searchHandler.postDelayed(() ->
                        viewModel.search(s.toString()), SEARCH_DELAY_MS);
            }
        });
    }

    private void setupSortButtons() {
        updateSortUI(DiscoverViewModel.SORT_POPULARITY);
        binding.btnSortPopularity.setOnClickListener(v -> {
            viewModel.sortBy(DiscoverViewModel.SORT_POPULARITY);
            updateSortUI(DiscoverViewModel.SORT_POPULARITY);
        });
        binding.btnSortRating.setOnClickListener(v -> {
            viewModel.sortBy(DiscoverViewModel.SORT_RATING);
            updateSortUI(DiscoverViewModel.SORT_RATING);
        });
    }

    private void updateSortUI(String activeSort) {
        boolean isPopular = DiscoverViewModel.SORT_POPULARITY.equals(activeSort);
        binding.btnSortPopularity.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(),
                                isPopular ? R.color.color_primary : R.color.color_surface)));
        binding.btnSortPopularity.setTextColor(
                ContextCompat.getColor(requireContext(),
                        isPopular ? R.color.color_on_primary : R.color.color_text_secondary));

        binding.btnSortRating.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(),
                                !isPopular ? R.color.color_primary : R.color.color_surface)));
        binding.btnSortRating.setTextColor(
                ContextCompat.getColor(requireContext(),
                        !isPopular ? R.color.color_on_primary : R.color.color_text_secondary));
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(DiscoverViewModel.class);
        viewModel.getNewPage().observe(getViewLifecycleOwner(), movies -> {
            if (movies == null) return;
            if (viewModel.getCurrentPage() == 1) {
                adapter.setMovies(movies);
                binding.nestedScrollDiscover.smoothScrollTo(0, 0);
            } else {
                adapter.appendMovies(movies);
            }
            showContent();
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressDiscover.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (isLoading) {
                binding.nestedScrollDiscover.setVisibility(View.GONE);
                binding.layoutEmpty.setVisibility(View.GONE);
                binding.layoutDiscoverError.getRoot().setVisibility(View.GONE);
            }
        });

        viewModel.getIsLoadingMore().observe(getViewLifecycleOwner(), isLoadingMore -> {
            binding.progressLoadMore.setVisibility(isLoadingMore ? View.VISIBLE : View.GONE);
            if (!isLoadingMore) {
                binding.btnLoadMore.setVisibility(
                        Boolean.TRUE.equals(viewModel.getHasMore().getValue())
                                ? View.VISIBLE : View.GONE);
            } else {
                binding.btnLoadMore.setVisibility(View.GONE);
            }
        });

        viewModel.getIsError().observe(getViewLifecycleOwner(), isError -> {
            if (isError) showError();
        });

        viewModel.getIsEmpty().observe(getViewLifecycleOwner(), isEmpty -> {
            binding.layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            binding.nestedScrollDiscover.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        });

        viewModel.getHasMore().observe(getViewLifecycleOwner(), hasMore -> {
            if (!Boolean.TRUE.equals(viewModel.getIsLoadingMore().getValue())) {
                binding.btnLoadMore.setVisibility(hasMore ? View.VISIBLE : View.GONE);
                binding.tvEndOfList.setVisibility(hasMore ? View.GONE : View.VISIBLE);
            }
        });

        viewModel.getTotalResults().observe(getViewLifecycleOwner(), total -> {
            if (total != null && total > 0) {
                binding.tvResultCount.setText(total + " movies found");
            } else {
                binding.tvResultCount.setText("All Movies");
            }
        });
    }

    private void setupLoadMore() {
        binding.btnLoadMore.setOnClickListener(v -> {
            if (NetworkUtils.isNotConnected(requireContext())) return;
            viewModel.loadNextPage();
        });
    }

    private void showContent() {
        binding.nestedScrollDiscover.setVisibility(View.VISIBLE);
        binding.progressDiscover.setVisibility(View.GONE);
        binding.layoutDiscoverError.getRoot().setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.GONE);
    }

    private void showError() {
        binding.layoutDiscoverError.getRoot().setVisibility(View.VISIBLE);
        binding.progressDiscover.setVisibility(View.GONE);
        binding.nestedScrollDiscover.setVisibility(View.GONE);
        binding.layoutDiscoverError.getRoot()
                .findViewById(R.id.btnRetry)
                .setOnClickListener(v -> {
                    if (NetworkUtils.isNotConnected(requireContext())) return;
                    binding.layoutDiscoverError.getRoot().setVisibility(View.GONE);
                    viewModel.retry();
                });
    }

    @Override
    public void onDestroyView() {
        searchHandler.removeCallbacksAndMessages(null);
        super.onDestroyView();
        binding = null;
    }
}
