package com.reely.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.reely.R;
import com.reely.databinding.ItemMovieCardBinding;
import com.reely.models.Movie;
import com.reely.utils.Constants;
import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movies = new ArrayList<>();
    private final OnMovieClickListener listener;

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    public MovieAdapter(OnMovieClickListener listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMovieCardBinding binding = ItemMovieCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MovieViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.bind(movies.get(position));
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
    public Movie getMovieAt(int position) {
        return movies.get(position);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        private final ItemMovieCardBinding binding;

        MovieViewHolder(ItemMovieCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Movie movie) {
            Context context = binding.getRoot().getContext();

            binding.tvMovieTitle.setText(movie.getTitle());

            binding.tvMovieRating.setText(movie.getFormattedRating());

            binding.tvMovieYear.setText(movie.getReleaseYear());

            String posterUrl = movie.getFullPosterUrl(Constants.IMAGE_SIZE_W342);

            Glide.with(context)
                    .load(posterUrl)
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .placeholder(R.drawable.ic_placeholder_movie)
                    .error(R.drawable.ic_placeholder_movie)
                    .centerCrop()
                    .into(binding.ivMoviePoster);

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMovieClick(movie);
                }
            });
        }
    }
}