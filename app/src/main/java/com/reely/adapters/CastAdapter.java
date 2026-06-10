package com.reely.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.reely.R;
import com.reely.databinding.ItemCastBinding;
import com.reely.models.CastItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter untuk cast/crew horizontal RecyclerView.
 */
public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private List<CastItem> cast = new ArrayList<>();

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCastBinding binding = ItemCastBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CastViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        holder.bind(cast.get(position));
    }

    @Override
    public int getItemCount() { return cast.size(); }

    public void setCast(List<CastItem> newCast) {
        cast.clear();
        if (newCast != null) cast.addAll(newCast);
        notifyDataSetChanged();
    }

    class CastViewHolder extends RecyclerView.ViewHolder {
        private final ItemCastBinding binding;

        CastViewHolder(ItemCastBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CastItem item) {
            Context ctx = binding.getRoot().getContext();
            binding.tvCastName.setText(item.getName());
            binding.tvCastRole.setText(item.getSubtitle());

            Glide.with(ctx)
                    .load(item.getProfileUrl())
                    .transition(DrawableTransitionOptions.withCrossFade(200))
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .circleCrop()
                    .into(binding.ivCastPhoto);
        }
    }
}