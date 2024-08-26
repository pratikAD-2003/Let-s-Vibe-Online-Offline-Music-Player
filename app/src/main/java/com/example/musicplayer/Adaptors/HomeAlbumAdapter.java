package com.example.musicplayer.Adaptors;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.Model.TrackModel;
import com.example.musicplayer.SingleTrack.SingleTrack;
import com.example.musicplayer.databinding.HomeTracksListItemsBinding;

import java.util.ArrayList;

public class HomeAlbumAdapter extends RecyclerView.Adapter<HomeAlbumAdapter.HomeAlbumHolder> {
    Context context;
    ArrayList<TrackModel> list = new ArrayList<>();

    HomeTracksListItemsBinding binding;

    public HomeAlbumAdapter(Context context, ArrayList<TrackModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public HomeAlbumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = HomeTracksListItemsBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HomeAlbumHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAlbumHolder holder, int position) {
        Glide.with(context).load(list.get(position).getImgUri()).into(holder.binding.trackItemImg);
        holder.binding.trackTitleItem.setText(list.get(position).getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SingleTrack.class);
                intent.putExtra("id", list.get(position).getId());
                intent.putExtra("title", list.get(position).getTitle());
                intent.putExtra("image", list.get(position).getImgUri());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class HomeAlbumHolder extends RecyclerView.ViewHolder {
        HomeTracksListItemsBinding binding;

        public HomeAlbumHolder(@NonNull HomeTracksListItemsBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
