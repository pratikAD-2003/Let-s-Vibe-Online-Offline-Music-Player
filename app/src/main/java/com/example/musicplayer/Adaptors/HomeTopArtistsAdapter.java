package com.example.musicplayer.Adaptors;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.ArtistList.ArtistsSongs;
import com.example.musicplayer.Model.TrackModel;
import com.example.musicplayer.databinding.TopArtistsItemsBinding;

import java.util.ArrayList;

public class HomeTopArtistsAdapter extends RecyclerView.Adapter<HomeTopArtistsAdapter.HomeArtistHolder> {
    Context context;
    ArrayList<TrackModel> list = new ArrayList<>();

    public HomeTopArtistsAdapter(Context context, ArrayList<TrackModel> list) {
        this.context = context;
        this.list = list;
    }

    TopArtistsItemsBinding binding;

    @NonNull
    @Override
    public HomeArtistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = TopArtistsItemsBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HomeArtistHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeArtistHolder holder, int position) {
        Glide.with(context).load(list.get(position).getImgUri()).into(holder.binding.imageItems);
        holder.binding.nameItems.setText(list.get(position).getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ArtistsSongs.class);
                intent.putExtra("id", list.get(position).getId());
                intent.putExtra("artistName",list.get(position).getTitle());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class HomeArtistHolder extends RecyclerView.ViewHolder {
        TopArtistsItemsBinding binding;

        public HomeArtistHolder(@NonNull TopArtistsItemsBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
