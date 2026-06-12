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
import com.reely.adapters.MovieAdapter;
import com.reely.databinding.ActivityMovieDetailBinding;
import com.reely.models.Genre;
import com.reely.models.MovieDetail;
import com.reely.utils.Constants;
import com.reely.utils.NetworkUtils;
import com.reely.viewmodel.DetailViewModel;

public class MovieDetailActivity extends AppCompatActivity {

    private ActivityMovieDetailBinding binding;
    private DetailViewModel viewModel;
    private CastAdapter castAdapter;
    private MovieAdapter recAdapter;
    private int movieId;
    private String currentTrailerKey;

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
        setupListeners();
        loadData();
    }

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

        recAdapter = new MovieAdapter(movie ->
                MovieDetailActivity.start(this, movie.getId()));
        binding.rvRecommendations.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvRecommendations.setAdapter(recAdapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(DetailViewModel.class);

        viewModel.getMovieDetail().observe(this, detail -> {
            if (detail != null) {
                populateUI(detail);
                showContent();
            }
        });

        viewModel.getCastList().observe(this, cast -> {
            if (cast != null && !cast.isEmpty()) {
                castAdapter.setCast(cast);
                binding.layoutCastSection.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getRecommendations().observe(this, movies -> {
            if (movies != null && !movies.isEmpty()) {
                recAdapter.setMovies(movies);
                binding.layoutRecSection.setVisibility(View.VISIBLE);
            } else {
                binding.layoutRecSection.setVisibility(View.GONE);
            }
        });

        viewModel.getMainTrailer().observe(this, trailer -> {
            if (trailer != null && trailer.isYouTube()) {
                currentTrailerKey = trailer.getKey();
                binding.btnPlayTrailer.setVisibility(View.VISIBLE);
                binding.btnTrailerAction.setVisibility(View.VISIBLE);
            } else {
                binding.btnPlayTrailer.setVisibility(View.GONE);
                binding.btnTrailerAction.setVisibility(View.GONE);
            }
        });

        viewModel.getIsLoading().observe(this, isLoading ->
                binding.progressDetail.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        viewModel.getIsError().observe(this, isError -> {
            binding.layoutDetailError.getRoot().setVisibility(isError ? View.VISIBLE : View.GONE);
            if (isError) setupRetryButton();
        });

        viewModel.getIsInWatchlist().observe(this, this::updateWatchlistButton);

        viewModel.getSnackbarMsg().observe(this, msg -> {
            if (msg != null && !msg.isEmpty()) {
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        View.OnClickListener playTrailerListener = v -> {
            if (currentTrailerKey != null) {
                String youtubeUrl = "https://www.youtube.com/watch?v=" + currentTrailerKey;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                intent.setPackage("com.google.android.youtube");
                if (intent.resolveActivity(getPackageManager()) == null) {
                    intent.setPackage(null);
                }
                startActivity(intent);
            }
        };
        
        binding.btnPlayTrailer.setOnClickListener(playTrailerListener);
        binding.btnTrailerAction.setOnClickListener(playTrailerListener);
        binding.btnSmallAddWatchlist.setOnClickListener(v -> {
            MovieDetail detail = viewModel.getMovieDetail().getValue();
            if (detail != null) viewModel.toggleWatchlist(detail);
        });
    }

    private void loadData() {
        if (NetworkUtils.isNotConnected(this)) {
            showError();
            return;
        }
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

    private void populateUI(MovieDetail detail) {
        Glide.with(this)
                .load(detail.getFullBackdropUrl(Constants.IMAGE_SIZE_W780))
                .transition(DrawableTransitionOptions.withCrossFade(400))
                .placeholder(R.drawable.ic_placeholder_movie)
                .centerCrop()
                .into(binding.ivDetailBackdrop);

        binding.tvDetailTitle.setText(detail.getTitle());
        binding.tvDetailRating.setText("★ " + detail.getFormattedRating());
        binding.tvDetailReleaseDate.setText(detail.getReleaseDate().split("-")[0]);
        binding.tvDetailRuntime.setText(detail.getFormattedRuntime());
        binding.tvDetailOverview.setText(detail.getOverview());

        if (detail.getTagline() != null && !detail.getTagline().isEmpty()) {
            binding.tvDetailTagline.setText("\"" + detail.getTagline() + "\"");
            binding.tvDetailTagline.setVisibility(View.VISIBLE);
        } else {
            binding.tvDetailTagline.setVisibility(View.GONE);
        }

        populateGenreChips(detail);

        if (detail.getBudget() > 0 || detail.getRevenue() > 0) {
            binding.tvDetailBudget.setText(detail.getFormattedBudget());
            binding.tvDetailRevenue.setText(detail.getFormattedRevenue());
            binding.layoutBudgetRevenue.setVisibility(View.VISIBLE);
        } else {
            binding.layoutBudgetRevenue.setVisibility(View.GONE);
        }
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

    private void updateWatchlistButton(boolean isInWatchlist) {
        if (isInWatchlist) {
            binding.btnSmallAddWatchlist.setText("Saved");
            binding.btnSmallAddWatchlist.setIconResource(R.drawable.ic_bookmark_filled);
        } else {
            binding.btnSmallAddWatchlist.setText("Save");
            binding.btnSmallAddWatchlist.setIconResource(R.drawable.ic_bookmark_outline);
        }
    }

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

    public static void start(Context context, int movieId) {
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra(Constants.EXTRA_MOVIE_ID, movieId);
        context.startActivity(intent);
    }
}
