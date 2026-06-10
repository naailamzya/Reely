package com.reely.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.reely.R;
import com.reely.adapters.CastAdapter;
import com.reely.databinding.ActivityMovieDetailBinding;
import com.reely.models.Genre;
import com.reely.models.MovieDetail;
import com.reely.models.Video;
import com.reely.utils.Constants;
import com.reely.utils.NetworkUtils;
import com.reely.viewmodel.DetailViewModel;

public class MovieDetailActivity extends AppCompatActivity {

    private ActivityMovieDetailBinding binding;
    private DetailViewModel viewModel;
    private CastAdapter castAdapter;
    private CastAdapter crewAdapter;
    private int movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        movieId = getIntent().getIntExtra(Constants.EXTRA_MOVIE_ID, -1);
        if (movieId == -1) { finish(); return; }

        setupToolbar();
        setupRecyclerViews();
        setupViewModel();
        loadData();
    }

    // ─────────────────────────────────────────────────────────────
    //  SETUP
    // ─────────────────────────────────────────────────────────────

    private void setupToolbar() {
        setSupportActionBar(binding.toolbarDetail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        binding.toolbarDetail.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerViews() {
        castAdapter = new CastAdapter();
        binding.rvCast.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvCast.setAdapter(castAdapter);

        crewAdapter = new CastAdapter();
        binding.rvCrew.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvCrew.setAdapter(crewAdapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(DetailViewModel.class);

        // ── Detail film ───────────────────────────────────────────
        viewModel.getMovieDetail().observe(this, detail -> {
            if (detail != null) {
                populateUI(detail);
                showContent();
            }
        });

        // ── Cast ──────────────────────────────────────────────────
        viewModel.getCastList().observe(this, cast -> {
            if (cast != null && !cast.isEmpty()) {
                castAdapter.setCast(cast);
                binding.layoutCastSection.setVisibility(View.VISIBLE);
            }
        });

        // ── Crew ──────────────────────────────────────────────────
        viewModel.getCrewList().observe(this, crew -> {
            if (crew != null && !crew.isEmpty()) {
                crewAdapter.setCast(crew);
                binding.layoutCrewSection.setVisibility(View.VISIBLE);
            }
        });

        // ── Trailer ───────────────────────────────────────────────
        viewModel.getMainTrailer().observe(this, trailer -> {
            if (trailer != null) {
                setupTrailer(trailer);
                binding.layoutTrailerSection.setVisibility(View.VISIBLE);
            }
        });

        // ── Loading ───────────────────────────────────────────────
        viewModel.getIsLoading().observe(this, isLoading ->
                binding.progressDetail.setVisibility(
                        isLoading ? View.VISIBLE : View.GONE));

        // ── Error ─────────────────────────────────────────────────
        viewModel.getIsError().observe(this, isError -> {
            binding.layoutDetailError.getRoot().setVisibility(
                    isError ? View.VISIBLE : View.GONE);
            if (isError) setupRetryButton();
        });

        // ── Watchlist status ──────────────────────────────────────
        viewModel.getIsInWatchlist().observe(this, this::updateWatchlistButton);

        // ── Snackbar ──────────────────────────────────────────────
        viewModel.getSnackbarMsg().observe(this, msg -> {
            if (msg != null && !msg.isEmpty())
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_SHORT).show();
        });
    }

    // ─────────────────────────────────────────────────────────────
    //  LOAD
    // ─────────────────────────────────────────────────────────────

    private void loadData() {
        if (NetworkUtils.isNotConnected(this)) {
            showError();
            return;
        }
        binding.progressDetail.setVisibility(View.VISIBLE);
        viewModel.loadMovieDetail(movieId);
    }

    private void setupRetryButton() {
        binding.layoutDetailError.getRoot()
                .findViewById(R.id.btnRetry)
                .setOnClickListener(v -> {
                    binding.layoutDetailError.getRoot().setVisibility(View.GONE);
                    loadData();
                });
    }

    // ─────────────────────────────────────────────────────────────
    //  POPULATE UI
    // ─────────────────────────────────────────────────────────────

    private void populateUI(MovieDetail detail) {

        // Backdrop
        Glide.with(this)
                .load(detail.getFullBackdropUrl(Constants.IMAGE_SIZE_W780))
                .transition(DrawableTransitionOptions.withCrossFade(400))
                .placeholder(R.drawable.ic_placeholder_movie)
                .centerCrop()
                .into(binding.ivDetailBackdrop);

        // Poster
        Glide.with(this)
                .load(detail.getFullPosterUrl(Constants.IMAGE_SIZE_W342))
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .placeholder(R.drawable.ic_placeholder_movie)
                .centerCrop()
                .into(binding.ivDetailPoster);

        // Teks
        binding.tvDetailTitle.setText(detail.getTitle());
        binding.tvDetailRating.setText("★ " + detail.getFormattedRating());
        binding.tvDetailVoteCount.setText("(" + formatVoteCount(detail.getVoteCount()) + ")");
        binding.tvDetailReleaseDate.setText("📅 " + detail.getReleaseDate());
        binding.tvDetailRuntime.setText("⏱ " + detail.getFormattedRuntime());
        binding.tvDetailOverview.setText(detail.getOverview());
        binding.collapsingToolbar.setTitle(detail.getTitle());

        // Tagline
        if (detail.getTagline() != null && !detail.getTagline().isEmpty()) {
            binding.tvDetailTagline.setText("\"" + detail.getTagline() + "\"");
            binding.tvDetailTagline.setVisibility(View.VISIBLE);
        }

        // Genre chips
        populateGenreChips(detail);

        // Budget + Revenue
        if (detail.getBudget() > 0 || detail.getRevenue() > 0) {
            binding.tvDetailBudget.setText(detail.getFormattedBudget());
            binding.tvDetailRevenue.setText(detail.getFormattedRevenue());
            binding.layoutBudgetRevenue.setVisibility(View.VISIBLE);
        }

        // Watchlist button
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
            chip.setChipBackgroundColorResource(R.color.color_surface);
            chip.setTextColor(getColor(R.color.color_text_secondary));
            chip.setChipStrokeColorResource(R.color.color_stroke);
            chip.setChipStrokeWidth(1f);
            chip.setClickable(false);
            chip.setCheckable(false);
            binding.layoutGenreChips.addView(chip);
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  TRAILER
    // ─────────────────────────────────────────────────────────────

    private void setupTrailer(Video trailer) {
        // Load thumbnail YouTube
        Glide.with(this)
                .load(trailer.getThumbnailUrl())
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .centerCrop()
                .into(binding.ivTrailerThumbnail);

        // Tap → buka YouTube
        binding.cardTrailer.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(trailer.getYouTubeUrl()));
            intent.setPackage("com.google.android.youtube");

            // Coba buka di YouTube app dulu, fallback ke browser
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(trailer.getYouTubeUrl())));
            }
        });
    }

    // ─────────────────────────────────────────────────────────────
    //  WATCHLIST BUTTON
    // ─────────────────────────────────────────────────────────────

    private void updateWatchlistButton(boolean isInWatchlist) {
        if (isInWatchlist) {
            binding.btnAddWatchlist.setText(getString(R.string.detail_remove_watchlist));
            binding.btnAddWatchlist.setIconResource(R.drawable.ic_bookmark_filled);
        } else {
            binding.btnAddWatchlist.setText(getString(R.string.detail_add_watchlist));
            binding.btnAddWatchlist.setIconResource(R.drawable.ic_bookmark_outline);
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  STATE HELPERS
    // ─────────────────────────────────────────────────────────────

    private void showContent() {
        binding.layoutDetailContent.setVisibility(View.VISIBLE);
        binding.layoutDetailError.getRoot().setVisibility(View.GONE);
        binding.progressDetail.setVisibility(View.GONE);
    }

    private void showError() {
        binding.layoutDetailError.getRoot().setVisibility(View.VISIBLE);
        binding.layoutDetailContent.setVisibility(View.GONE);
        binding.progressDetail.setVisibility(View.GONE);
        setupRetryButton();
    }

    private String formatVoteCount(int count) {
        if (count >= 1_000_000) return String.format("%.1fM", count / 1_000_000f);
        if (count >= 1_000)     return String.format("%.1fk", count / 1_000f);
        return String.valueOf(count);
    }

    // ─────────────────────────────────────────────────────────────
    //  STATIC HELPER
    // ─────────────────────────────────────────────────────────────

    public static void start(Context context, int movieId) {
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra(Constants.EXTRA_MOVIE_ID, movieId);
        context.startActivity(intent);
    }
}