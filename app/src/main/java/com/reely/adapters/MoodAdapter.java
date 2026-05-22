package com.reely.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.reely.databinding.ItemMoodCardBinding;
import com.reely.models.MoodItem;
import java.util.ArrayList;
import java.util.List;

public class MoodAdapter extends RecyclerView.Adapter<MoodAdapter.MoodViewHolder> {

    private List<MoodItem> moods = new ArrayList<>();
    private final OnMoodClickListener listener;

    public interface OnMoodClickListener {
        void onMoodClick(MoodItem mood, int position);
    }

    public MoodAdapter(OnMoodClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMoodCardBinding binding = ItemMoodCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MoodViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MoodViewHolder holder, int position) {
        holder.bind(moods.get(position));
    }

    @Override
    public int getItemCount() {
        return moods.size();
    }

    public void setMoods(List<MoodItem> newMoods) {
        this.moods.clear();
        if (newMoods != null) {
            this.moods.addAll(newMoods);
        }
        notifyDataSetChanged();
    }

    class MoodViewHolder extends RecyclerView.ViewHolder {

        private final ItemMoodCardBinding binding;

        MoodViewHolder(ItemMoodCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(MoodItem mood) {
            Context context = binding.getRoot().getContext();

            binding.tvMoodEmoji.setText(mood.getEmoji());
            binding.tvMoodName.setText(mood.getDisplayName());

            binding.cardMood.setBackgroundTintList(
                    ColorStateList.valueOf(
                            ContextCompat.getColor(context, mood.getGradientStartRes())));

            if (mood.isSelected()) {
                binding.cardMood.setStrokeWidth(3);
                binding.cardMood.setStrokeColor(
                        ContextCompat.getColor(context, mood.getAccentColorRes()));
                binding.getRoot().setScaleX(1.05f);
                binding.getRoot().setScaleY(1.05f);
                binding.tvMoodName.setAlpha(1.0f);
                binding.tvMoodEmoji.setAlpha(1.0f);
            } else {
                binding.cardMood.setStrokeWidth(0);
                binding.getRoot().setScaleX(1.0f);
                binding.getRoot().setScaleY(1.0f);
                binding.tvMoodName.setAlpha(0.6f);
                binding.tvMoodEmoji.setAlpha(0.6f);
            }

            binding.getRoot().animate()
                    .scaleX(mood.isSelected() ? 1.05f : 1.0f)
                    .scaleY(mood.isSelected() ? 1.05f : 1.0f)
                    .setDuration(200)
                    .start();

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMoodClick(mood, getAdapterPosition());
                }
            });
        }
    }
}