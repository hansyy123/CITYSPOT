package com.example.cityspot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AttractionsAdapter extends RecyclerView.Adapter<AttractionsAdapter.ViewHolder> {

    private final List<Attraction> attractions;
    private final List<Attraction> filteredList;
    private final OnAttractionClickListener listener;

    public interface OnAttractionClickListener {
        void onAttractionClick(Attraction attraction);
        void onFavoriteClick(Attraction attraction);
    }

    public AttractionsAdapter(List<Attraction> attractions, OnAttractionClickListener listener) {
        this.attractions = attractions;
        this.filteredList = new ArrayList<>(attractions);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attraction_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Attraction attraction = filteredList.get(position);
        holder.txtName.setText(attraction.getName());
        holder.txtLocation.setText(attraction.getLocation());
        holder.txtDetails.setText(attraction.getDetails());
        holder.imgAttraction.setImageResource(attraction.getImageResId());

        holder.itemView.setOnClickListener(v -> listener.onAttractionClick(attraction));
        holder.btnFavorite.setOnClickListener(v -> listener.onFavoriteClick(attraction));
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(attractions);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Attraction item : attractions) {
                if (item.getName().toLowerCase().contains(lowerQuery) ||
                    item.getLocation().toLowerCase().contains(lowerQuery)) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView txtName, txtLocation, txtDetails;
        final ImageView imgAttraction;
        final ImageButton btnFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtAttractionName);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            txtDetails = itemView.findViewById(R.id.txtDetails);
            imgAttraction = itemView.findViewById(R.id.imgAttraction);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}