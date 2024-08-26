package com.example.musicplayer.PlayTrack.adaptors;

import static com.example.musicplayer.Database.DatabaseHelper.TABLE_NAME;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.Database.DatabaseHelper;
import com.example.musicplayer.Model.ArtistsTrackModel;
import com.example.musicplayer.PlayTrack.PlayTrackScreen;
import com.example.musicplayer.R;
import com.example.musicplayer.databinding.ArtistsTracksItemsBinding;
import com.google.gson.Gson;

import java.util.ArrayList;

public class RelatedTracksAdapter extends RecyclerView.Adapter<RelatedTracksAdapter.RelatedHolder> {
    Context context;
    ArrayList<ArtistsTrackModel> list = new ArrayList<>();
    ArtistsTracksItemsBinding binding;
    SQLiteDatabase sqLiteDatabase;
    DatabaseHelper databaseHelper;
    Activity activity;

    public RelatedTracksAdapter(Context context, ArrayList<ArtistsTrackModel> list, Activity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RelatedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ArtistsTracksItemsBinding.inflate(LayoutInflater.from(context), parent, false);
        return new RelatedHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RelatedHolder holder, int position) {
        Glide.with(context).load(list.get(position).getImgUri()).into(holder.binding.trackImgItems);
        if (list.get(position).getName().length() > 27) {
            holder.binding.trackTitleItems.setText(list.get(position).getName().substring(0, 24) + "...");
        } else {
            holder.binding.trackTitleItems.setText(list.get(position).getName());
        }
        holder.binding.trackArtistNames.setText(list.get(position).getArtistName());

        if (display(list.get(position).getSongsId())) {
            holder.binding.moreItemBtn.setImageDrawable(context.getDrawable(R.drawable.heart_like));
            holder.isLiked = true;
        } else {
            holder.binding.moreItemBtn.setImageDrawable(context.getDrawable(R.drawable.heart));
            holder.isLiked = false;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlayTrackScreen.class);
                intent.putExtra("type", "online");
                intent.putExtra("position", position);
                Gson gson = new Gson();
                String myList = gson.toJson(list);
                intent.putExtra("list", myList);
                context.startActivity(intent);
            }
        });

        holder.binding.moreItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.isLiked) {
                    if (deleteData(list.get(position).getSongsId())) {
                        holder.binding.moreItemBtn.setImageDrawable(context.getDrawable(R.drawable.heart));
                        holder.isLiked = false;
                    } else {
                        holder.binding.moreItemBtn.setImageDrawable(context.getDrawable(R.drawable.heart_like));
                        holder.isLiked = true;
                    }
                } else {
                    if (insertData(list.get(position).getId(), list.get(position).getSongsId(), list.get(position).getName(), list.get(position).getImgUri())) {
                        holder.binding.moreItemBtn.setImageDrawable(context.getDrawable(R.drawable.heart_like));
                        holder.isLiked = true;
                    } else {
                        holder.binding.moreItemBtn.setImageDrawable(context.getDrawable(R.drawable.heart));
                        holder.isLiked = false;
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class RelatedHolder extends RecyclerView.ViewHolder {
        ArtistsTracksItemsBinding binding;
        boolean isLiked = false;

        public RelatedHolder(@NonNull ArtistsTracksItemsBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    private boolean insertData(long albumId, long songId, String title, String imageUri) {
        databaseHelper = new DatabaseHelper(context);
        ContentValues cv = new ContentValues();
        cv.put("albumId", albumId);
        cv.put("songId", songId);
        cv.put("title", title);
        cv.put("imageUri", imageUri);

        sqLiteDatabase = databaseHelper.getWritableDatabase();
        Long checkInsert = sqLiteDatabase.insert(TABLE_NAME, null, cv);
        if (checkInsert != null) {
//            Toast.makeText(context, "Inserted!", Toast.LENGTH_SHORT).show();
//            Log.d("Inserted---", String.valueOf(albumId) + "    " + String.valueOf(songId) + "   " + title + "   " + imageUri);
            return true;
        } else {
            Toast.makeText(context, "Unexpected!", Toast.LENGTH_SHORT).show();
            Log.d("Error while inserting----", "Yes");
            return false;
        }
    }

    public boolean display(long songId2) {
        databaseHelper = new DatabaseHelper(context);
        sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_NAME + "", null);

        System.out.println(songId2);
        while (cursor.moveToNext()) {
            long songId = cursor.getLong(1);
            if (songId == songId2) {
                return true;
            }
        }
        cursor.close();
        return false;
    }

    private boolean deleteData(long songId) {
        databaseHelper = new DatabaseHelper(context);
        sqLiteDatabase = databaseHelper.getReadableDatabase();
        long delete = sqLiteDatabase.delete(TABLE_NAME, "songId=" + songId, null);
        if (delete != -1) {
//            Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(context, "Unexpected Error!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
