package com.reely.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.reely.R;
import com.reely.databinding.ItemWatchlistMovieBinding;
import com.reely.models.Movie;
import com.reely.utils.Constants;
import java.util.ArrayList;
import java.util.List;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.WatchlistViewHolder> {

    private List<Movie> movies = new ArrayList<>();
    private final OnWatchlistActionListener listener;

    public interface OnWatchlistActionListener {
        void onMovieClick(Movie movie);
        void onRemoveClick(Movie movie, int position);
    }

    public WatchlistAdapter(OnWatchlistActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public WatchlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWatchlistMovieBinding binding = ItemWatchlistMovieBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new WatchlistViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchlistViewHolder holder, int position) {
        holder.bind(movies.get(position), position);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void setMovies(List<Movie> newMovies) {
        this.movies.clear();
        if (newMovies != null) {
            this.movies.addAll(newMovies);
        }
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < movies.size()) {
            movies.remove(position);
            notifyItemRemoved(position);
            // Perbaiki: update posisi dari position hingga akhir
            notifyItemRangeChanged(position, movies.size() - position);
        }
    }

    public List<Movie> getMovies() {
        return movies;
    }

    class WatchlistViewHolder extends RecyclerView.ViewHolder {
        private final ItemWatchlistMovieBinding binding;
        private int currentPosition = RecyclerView.NO_POSITION;

        WatchlistViewHolder(ItemWatchlistMovieBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Movie movie, int position) {
            this.currentPosition = position;
            Context context = binding.getRoot().getContext();

            binding.tvWatchlistTitle.setText(movie.getTitle());
            binding.tvWatchlistRating.setText("★ " + movie.getFormattedRating());
            binding.tvWatchlistYear.setText(movie.getReleaseYear());

            if (movie.getOverview() != null && !movie.getOverview().isEmpty()) {
                binding.tvWatchlistOverview.setText(movie.getOverview());
            } else {
                binding.tvWatchlistOverview.setText("No overview available.");
            }

            String posterUrl = movie.getFullPosterUrl(Constants.IMAGE_SIZE_W185);
            Glide.with(context)
                    .load(posterUrl)
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .placeholder(R.drawable.ic_placeholder_movie)
                    .error(R.drawable.ic_placeholder_movie)
                    .centerCrop()
                    .into(binding.ivWatchlistPoster);

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMovieClick(movie);
                }
            });

            binding.btnRemoveWatchlist.setOnClickListener(v -> {
                if (listener != null && currentPosition != RecyclerView.NO_POSITION) {
                    listener.onRemoveClick(movie, currentPosition);
                }
            });
        }
    }
}