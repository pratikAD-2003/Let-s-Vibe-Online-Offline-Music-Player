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

public class HomeTrackAdapter extends RecyclerView.Adapter<HomeTrackAdapter.HomeTrackHolder> {
    ArrayList<TrackModel> list;
    Context context;

    public HomeTrackAdapter(ArrayList<TrackModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    HomeTracksListItemsBinding binding;

    @NonNull
    @Override
    public HomeTrackHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = HomeTracksListItemsBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HomeTrackHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeTrackHolder holder, int position) {
        Glide.with(context).load(list.get(position).getImgUri()).into(holder.binding.trackItemImg);
        holder.binding.trackTitleItem.setText(list.get(position).getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SingleTrack.class);
                intent.putExtra("id",list.get(position).getId());
                intent.putExtra("title",list.get(position).getTitle());
                intent.putExtra("image",list.get(position).getImgUri());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class HomeTrackHolder extends RecyclerView.ViewHolder {
        HomeTracksListItemsBinding binding;
        public HomeTrackHolder(@NonNull HomeTracksListItemsBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
