package com.reely.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.reely.R;
import com.reely.databinding.ItemMovieGridBinding;
import com.reely.models.Movie;
import com.reely.utils.Constants;
import java.util.ArrayList;
import java.util.List;

/**
 * REELY — MovieGridAdapter
 * Grid adapter untuk All Movies screen (2 kolom).
 */
public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.GridViewHolder> {

    private final List<Movie> movies = new ArrayList<>();
    private final OnMovieClickListener listener;

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    public MovieGridAdapter(OnMovieClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMovieGridBinding binding = ItemMovieGridBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new GridViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() { return movies.size(); }

    /**
     * Set data awal (page 1).
     */
    public void setMovies(List<Movie> newMovies) {
        movies.clear();
        if (newMovies != null) movies.addAll(newMovies);
        notifyDataSetChanged();
    }

    /**
     * Tambah data berikutnya (page 2, 3, dst).
     */
    public void appendMovies(List<Movie> moreMovies) {
        if (moreMovies == null) return;
        int startPos = movies.size();
        movies.addAll(moreMovies);
        notifyItemRangeInserted(startPos, moreMovies.size());
    }

    class GridViewHolder extends RecyclerView.ViewHolder {
        private final ItemMovieGridBinding binding;

        GridViewHolder(ItemMovieGridBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Movie movie) {
            Context ctx = binding.getRoot().getContext();

            binding.tvGridTitle.setText(movie.getTitle());
            binding.tvGridYear.setText(movie.getReleaseYear());
            binding.tvGridRating.setText(movie.getFormattedRating());

            Glide.with(ctx)
                    .load(movie.getFullPosterUrl(Constants.IMAGE_SIZE_W342))
                    .transition(DrawableTransitionOptions.withCrossFade(200))
                    .placeholder(R.drawable.ic_placeholder_movie)
                    .error(R.drawable.ic_placeholder_movie)
                    .centerCrop()
                    .into(binding.ivGridPoster);

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onMovieClick(movie);
            });
        }
    }
}