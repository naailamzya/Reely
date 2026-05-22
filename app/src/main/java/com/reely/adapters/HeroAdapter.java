package com.reely.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.reely.R;
import com.reely.databinding.ItemHeroBannerBinding;
import com.reely.models.Movie;
import com.reely.utils.Constants;
import java.util.ArrayList;
import java.util.List;

public class HeroAdapter extends RecyclerView.Adapter<HeroAdapter.HeroViewHolder> {

    private static final int MAX_HERO_ITEMS = 5;

    private List<Movie> movies = new ArrayList<>();
    private final OnMovieClickListener listener;

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    public HeroAdapter(OnMovieClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public HeroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHeroBannerBinding binding = ItemHeroBannerBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new HeroViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HeroViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void setMovies(List<Movie> newMovies) {
        this.movies.clear();
        if (newMovies != null) {
            int count = Math.min(newMovies.size(), MAX_HERO_ITEMS);
            this.movies.addAll(newMovies.subList(0, count));
        }
        notifyDataSetChanged();
    }

    class HeroViewHolder extends RecyclerView.ViewHolder {

        private final ItemHeroBannerBinding binding;

        HeroViewHolder(ItemHeroBannerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Movie movie) {
            Context context = binding.getRoot().getContext();

            binding.tvHeroTitle.setText(movie.getTitle());

            binding.tvHeroRating.setText("★ " + movie.getFormattedRating());

            binding.tvHeroYear.setText(movie.getReleaseYear());

            String backdropUrl = movie.getFullBackdropUrl(Constants.IMAGE_SIZE_W780);

            Glide.with(context)
                    .load(backdropUrl)
                    .transition(DrawableTransitionOptions.withCrossFade(400))
                    .placeholder(R.drawable.ic_placeholder_movie)
                    .error(R.drawable.ic_placeholder_movie)
                    .centerCrop()
                    .into(binding.ivHeroBackdrop);

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMovieClick(movie);
                }
            });
        }
    }
}