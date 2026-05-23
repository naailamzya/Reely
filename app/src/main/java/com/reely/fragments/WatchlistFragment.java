package com.reely.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.snackbar.Snackbar;
import com.reely.activities.MovieDetailActivity;
import com.reely.adapters.WatchlistAdapter;
import com.reely.databinding.FragmentWatchlistBinding;
import com.reely.viewmodel.WatchlistViewModel;

public class WatchlistFragment extends Fragment {

    private FragmentWatchlistBinding binding;
    private WatchlistViewModel viewModel;
    private WatchlistAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentWatchlistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupViewModel();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadWatchlist();
    }

    private void setupRecyclerView() {
        adapter = new WatchlistAdapter(new WatchlistAdapter.OnWatchlistActionListener() {
            @Override
            public void onMovieClick(com.reely.models.Movie movie) {
                MovieDetailActivity.start(requireContext(), movie.getId());
            }

            @Override
            public void onRemoveClick(com.reely.models.Movie movie, int position) {
                viewModel.removeFromWatchlist(movie.getId());
                adapter.removeItem(position);
            }
        });

        binding.rvWatchlist.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.rvWatchlist.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(WatchlistViewModel.class);

        viewModel.getWatchlistMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null) {
                adapter.setMovies(movies);

                String countText = movies.size() + " movie" +
                        (movies.size() != 1 ? "s" : "") + " saved";
                binding.tvWatchlistCount.setText(countText);
            }
        });

        viewModel.getIsEmpty().observe(getViewLifecycleOwner(), isEmpty -> {
            binding.layoutEmptyWatchlist.setVisibility(
                    isEmpty ? View.VISIBLE : View.GONE);
            binding.rvWatchlist.setVisibility(
                    isEmpty ? View.GONE : View.VISIBLE);
        });

        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}