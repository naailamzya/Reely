package com.reely.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import com.reely.R;
import com.reely.adapters.MovieGridAdapter;
import com.reely.databinding.ActivityAllMoviesBinding;
import com.reely.utils.NetworkUtils;
import com.reely.viewmodel.AllMoviesViewModel;

public class AllMoviesActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY = "extra_category";
    public static final String EXTRA_TITLE    = "extra_title";

    private ActivityAllMoviesBinding binding;
    private AllMoviesViewModel viewModel;
    private MovieGridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllMoviesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.appBarLayout, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(0, statusBarHeight, 0, 0);
            return insets;
        });

        String category = getIntent().getStringExtra(EXTRA_CATEGORY);
        String title    = getIntent().getStringExtra(EXTRA_TITLE);

        if (category == null) { finish(); return; }

        setupToolbar(title);
        setupRecyclerView();
        setupViewModel(category);
        setupLoadMore();
    }

    private void setupToolbar(String title) {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        binding.tvCategoryTitle.setText(title != null ? title : "Movies");
    }

    private void setupRecyclerView() {
        adapter = new MovieGridAdapter(movie ->
                MovieDetailActivity.start(this, movie.getId()));

        GridLayoutManager gridManager = new GridLayoutManager(this, 2);
        binding.rvAllMovies.setLayoutManager(gridManager);
        binding.rvAllMovies.setAdapter(adapter);
        binding.rvAllMovies.setHasFixedSize(false);
        binding.rvAllMovies.setNestedScrollingEnabled(false);
    }

    private void setupViewModel(String category) {
        viewModel = new ViewModelProvider(this).get(AllMoviesViewModel.class);

        viewModel.getNewPage().observe(this, movies -> {
            if (movies != null && !movies.isEmpty()) {
                if (viewModel.getCurrentPage() == 1) {
                    adapter.setMovies(movies);
                } else {
                    adapter.appendMovies(movies);
                }
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.rvAllMovies.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        });

        viewModel.getIsLoadingMore().observe(this, isLoadingMore -> {
            binding.progressLoadMore.setVisibility(
                    isLoadingMore ? View.VISIBLE : View.GONE);
            binding.btnLoadMore.setVisibility(
                    isLoadingMore ? View.GONE : View.VISIBLE);
        });

        viewModel.getIsError().observe(this, isError -> {
            binding.layoutError.getRoot().setVisibility(
                    isError ? View.VISIBLE : View.GONE);
            if (isError) {
                binding.layoutError.getRoot()
                        .findViewById(com.reely.R.id.btnRetry)
                        .setOnClickListener(v -> {
                            if (NetworkUtils.isNotConnected(this)) return;
                            binding.layoutError.getRoot().setVisibility(View.GONE);
                            viewModel.retry();
                        });
            }
        });

        viewModel.getHasMore().observe(this, hasMore -> {
            binding.btnLoadMore.setVisibility(
                    hasMore ? View.VISIBLE : View.GONE);
            binding.tvEndOfList.setVisibility(
                    hasMore ? View.GONE : View.VISIBLE);
        });

        if (NetworkUtils.isNotConnected(this)) {
            binding.layoutError.getRoot().setVisibility(View.VISIBLE);
        } else {
            viewModel.init(category);
        }
    }

    private void setupLoadMore() {
        binding.btnLoadMore.setOnClickListener(v -> {
            if (NetworkUtils.isNotConnected(this)) return;
            viewModel.loadNextPage();
        });
    }

    public static void start(Context context, String category, String title) {
        Intent intent = new Intent(context, AllMoviesActivity.class);
        intent.putExtra(EXTRA_CATEGORY, category);
        intent.putExtra(EXTRA_TITLE, title);
        context.startActivity(intent);
    }
}