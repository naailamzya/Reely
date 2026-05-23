package com.reely.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.reely.R;
import com.reely.databinding.ActivityMovieDetailBinding;
import com.reely.models.Genre;
import com.reely.models.MovieDetail;
import com.reely.utils.Constants;
import com.reely.utils.NetworkUtils;
import com.reely.viewmodel.DetailViewModel;

public class MovieDetailActivity extends AppCompatActivity {

    private ActivityMovieDetailBinding binding;
    private DetailViewModel viewModel;
    private int movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        movieId = getIntent().getIntExtra(Constants.EXTRA_MOVIE_ID, -1);

        if (movieId == -1) {
            finish();
            return;
        }

        setupToolbar();
        setupViewModel();
        loadMovieDetail();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbarDetail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        binding.toolbarDetail.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(DetailViewModel.class);

        viewModel.getMovieDetail().observe(this, detail -> {
            if (detail != null) {
                populateUI(detail);
                showContent();
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressDetail.setVisibility(
                    isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getIsError().observe(this, isError -> {
            binding.layoutDetailError.setVisibility(
                    isError ? View.VISIBLE : View.GONE);
        });

        viewModel.getIsInWatchlist().observe(this, isInWatchlist -> {
            updateWatchlistButton(isInWatchlist);
        });

        viewModel.getSnackbarMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMovieDetail() {
        if (NetworkUtils.isNotConnected(this)) {
            showError();
            setupRetryButton();
            return;
        }

        binding.progressDetail.setVisibility(View.VISIBLE);
        viewModel.loadMovieDetail(movieId);
    }

    private void setupRetryButton() {
        binding.layoutDetailError.findViewById(R.id.btnRetry)
                .setOnClickListener(v -> {
                    binding.layoutDetailError.setVisibility(View.GONE);
                    loadMovieDetail();
                });
    }

    private void populateUI(MovieDetail detail) {

        Glide.with(this)
                .load(detail.getFullBackdropUrl(Constants.IMAGE_SIZE_W780))
                .transition(DrawableTransitionOptions.withCrossFade(400))
                .placeholder(R.drawable.ic_placeholder_movie)
                .centerCrop()
                .into(binding.ivDetailBackdrop);

        Glide.with(this)
                .load(detail.getFullPosterUrl(Constants.IMAGE_SIZE_W342))
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .placeholder(R.drawable.ic_placeholder_movie)
                .centerCrop()
                .into(binding.ivDetailPoster);

        binding.tvDetailTitle.setText(detail.getTitle());
        binding.tvDetailRating.setText("★ " + detail.getFormattedRating());
        binding.tvDetailVoteCount.setText("(" + formatVoteCount(detail.getVoteCount()) + ")");
        binding.tvDetailReleaseDate.setText(detail.getReleaseDate());
        binding.tvDetailRuntime.setText(detail.getFormattedRuntime());
        binding.tvDetailOverview.setText(detail.getOverview());

        if (detail.getTagline() != null && !detail.getTagline().isEmpty()) {
            binding.tvDetailTagline.setText("\"" + detail.getTagline() + "\"");
            binding.tvDetailTagline.setVisibility(View.VISIBLE);
        }

        binding.collapsingToolbar.setTitle(detail.getTitle());

        populateGenreChips(detail);

        binding.btnAddWatchlist.setOnClickListener(v ->
                viewModel.toggleWatchlist(detail));
    }

    private void populateGenreChips(MovieDetail detail) {
        binding.layoutGenreChips.removeAllViews();

        if (detail.getGenres() == null) return;

        for (Genre genre : detail.getGenres()) {
            Chip chip = new Chip(this);
            chip.setText(genre.getName());
            chip.setTextSize(11f);
            chip.setChipBackgroundColorResource(R.color.night_bg_surface);
            chip.setTextColor(getColor(R.color.night_text_secondary));
            chip.setChipStrokeColorResource(R.color.night_stroke);
            chip.setChipStrokeWidth(1f);
            chip.setClickable(false);
            chip.setCheckable(false);
            binding.layoutGenreChips.addView(chip);
        }
    }

    private void updateWatchlistButton(boolean isInWatchlist) {
        if (isInWatchlist) {
            binding.btnAddWatchlist.setText(getString(R.string.detail_remove_watchlist));
            binding.btnAddWatchlist.setIconResource(R.drawable.ic_bookmark_filled);
        } else {
            binding.btnAddWatchlist.setText(getString(R.string.detail_add_watchlist));
            binding.btnAddWatchlist.setIconResource(R.drawable.ic_bookmark_outline);
        }
    }

    private void showContent() {
        binding.layoutDetailContent.setVisibility(View.VISIBLE);
        binding.layoutDetailError.setVisibility(View.GONE);
        binding.progressDetail.setVisibility(View.GONE);
    }

    private void showError() {
        binding.layoutDetailError.setVisibility(View.VISIBLE);
        binding.layoutDetailContent.setVisibility(View.GONE);
        binding.progressDetail.setVisibility(View.GONE);
    }

    private String formatVoteCount(int count) {
        if (count >= 1_000_000) return String.format("%.1fM", count / 1_000_000f);
        if (count >= 1_000) return String.format("%.1fk", count / 1_000f);
        return String.valueOf(count);
    }

    public static void start(android.content.Context context, int movieId) {
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra(Constants.EXTRA_MOVIE_ID, movieId);
        context.startActivity(intent);
    }
}