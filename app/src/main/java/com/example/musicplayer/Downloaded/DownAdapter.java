package com.example.musicplayer.Downloaded;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.Favorite.FavoriteTrackAdapter;
import com.example.musicplayer.Model.ArtistsTrackModel;
import com.example.musicplayer.PlayTrack.PlayTrackScreen;
import com.example.musicplayer.R;
import com.example.musicplayer.databinding.ArtistsTracksItemsBinding;
import com.example.musicplayer.databinding.DownTracksItemsBinding;
import com.google.gson.Gson;

import java.util.ArrayList;

public class DownAdapter extends RecyclerView.Adapter<DownAdapter.DownHolder> {
    Context context;
    ArrayList<DownTrackModel> list = new ArrayList<>();
    DownTracksItemsBinding binding;
    FavoriteTrackAdapter.OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(ArtistsTrackModel item);
    }
    public DownAdapter(Context context, ArrayList<DownTrackModel> list) {
        this.context = context;
        this.list = list;
    }

    public DownAdapter(Context context, ArrayList<DownTrackModel> list, FavoriteTrackAdapter.OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DownHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = DownTracksItemsBinding.inflate(LayoutInflater.from(context), parent, false);
        return new DownHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DownHolder holder, int position) {
        if (list.get(position).getImage() != null) {
//            Glide.with(context).load(list.get(position).getImage()).into(holder.binding.trackImgItems);
            Glide.with(context).load(context.getDrawable(R.drawable.no_available)).into(holder.binding.trackImgItems);
        } else {
            Glide.with(context).load(context.getDrawable(R.drawable.no_available)).into(holder.binding.trackImgItems);
        }
        if (list.get(position).getTitle().length() > 35) {
            holder.binding.trackTitleItems.setText(list.get(position).getTitle().substring(0, 32) + "...");
        } else {
            holder.binding.trackTitleItems.setText(list.get(position).getTitle());
        }

        holder.binding.trackArtistNames.setText(list.get(position).getArtistName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlayTrackScreen.class);
                intent.putExtra("position",position);
                intent.putExtra("type","downloaded");
                Gson gson = new Gson();
                String myList = gson.toJson(list);
                intent.putExtra("list",myList);
                listener.onItemClick(new ArtistsTrackModel());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class DownHolder extends RecyclerView.ViewHolder {
        DownTracksItemsBinding binding;

        public DownHolder(@NonNull DownTracksItemsBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
