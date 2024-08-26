package com.example.musicplayer.Search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.Adaptors.ArtistsTracksAdapter;
import com.example.musicplayer.Model.ArtistsTrackModel;
import com.example.musicplayer.databinding.ArtistsTracksItemsBinding;

import java.util.ArrayList;

public class SearchTrackAdapter extends RecyclerView.Adapter<SearchTrackAdapter.SearchTrackHolder> {
    Context context;
    ArrayList<ArtistsTrackModel> list = new ArrayList<>();
    ArtistsTracksItemsBinding binding;

    public SearchTrackAdapter(Context context, ArrayList<ArtistsTrackModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SearchTrackHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ArtistsTracksItemsBinding.inflate(LayoutInflater.from(context), parent, false);
        return new SearchTrackHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchTrackHolder holder, int position) {
        Glide.with(context).load(list.get(position).getImgUri()).into(holder.binding.trackImgItems);
        holder.binding.trackTitleItems.setText(list.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class SearchTrackHolder extends RecyclerView.ViewHolder {
        ArtistsTracksItemsBinding binding;

        public SearchTrackHolder(@NonNull ArtistsTracksItemsBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
