package com.reely.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.reely.R;
import com.reely.activities.MovieDetailActivity;
import com.reely.adapters.WatchlistAdapter;
import com.reely.viewmodel.WatchlistViewModel;

public class WatchlistFragment extends Fragment {

    private WatchlistViewModel viewModel;
    private WatchlistAdapter adapter;
    private RecyclerView rvWatchlist;
    private TextView tvWatchlistCount;
    private View layoutEmptyWatchlist;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_watchlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvWatchlist = view.findViewById(R.id.rvWatchlist);
        tvWatchlistCount = view.findViewById(R.id.tvWatchlistCount);
        layoutEmptyWatchlist = view.findViewById(R.id.layoutEmptyWatchlist);

        setupRecyclerView();
        setupViewModel();
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
                if (adapter.getItemCount() == 0) {
                    showEmptyState(true);
                }
            }
        });

        if (rvWatchlist != null) {
            rvWatchlist.setLayoutManager(new LinearLayoutManager(requireContext()));
            rvWatchlist.setAdapter(adapter);
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(WatchlistViewModel.class);
        
        viewModel.getWatchlistMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null) {
                adapter.setMovies(movies);
                if (tvWatchlistCount != null) {
                    tvWatchlistCount.setText(movies.size() + " movies saved");
                }
                showEmptyState(movies.isEmpty());
            }
        });
    }

    private void showEmptyState(boolean isEmpty) {
        if (layoutEmptyWatchlist != null) {
            layoutEmptyWatchlist.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
        if (rvWatchlist != null) {
            rvWatchlist.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.loadWatchlist();
        }
    }
}
