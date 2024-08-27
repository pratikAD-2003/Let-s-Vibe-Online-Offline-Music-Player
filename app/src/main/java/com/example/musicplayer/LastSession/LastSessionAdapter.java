package com.example.musicplayer.LastSession;

import static com.example.musicplayer.Database.DatabaseHelper.LAST_SESSION_TABLE;
import static com.example.musicplayer.Database.DatabaseHelper.TABLE_NAME;

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
import com.example.musicplayer.databinding.LastSessionAdapterBinding;
import com.google.gson.Gson;

import java.util.ArrayList;

public class LastSessionAdapter extends RecyclerView.Adapter<LastSessionAdapter.LastViewHolder> {
    Context context;
    ArrayList<ArtistsTrackModel> list = new ArrayList<>();

    LastSessionAdapterBinding binding;
    SQLiteDatabase sqLiteDatabase;
    DatabaseHelper databaseHelper;
    OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ArtistsTrackModel item);
    }

    public LastSessionAdapter(Context context, ArrayList<ArtistsTrackModel> list) {
        this.context = context;
        this.list = list;
    }

    public LastSessionAdapter(Context context, ArrayList<ArtistsTrackModel> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = LastSessionAdapterBinding.inflate(LayoutInflater.from(context), parent, false);
        return new LastViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LastViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getImgUri()).into(holder.binding.trackImgItems);
//        holder.binding.trackTitleItems.setText(list.get(position).getName());

        if (list.get(position).getName().length() > 23) {
            holder.binding.trackTitleItems.setText(list.get(position).getName().substring(0, 20) + "...");
        } else {
            holder.binding.trackTitleItems.setText(list.get(position).getName());
        }
        if (list.get(position).getArtistName().length() > 20) {
            holder.binding.trackArtistNames.setText(list.get(position).getArtistName().substring(0, 17) + "...");
        } else {
            holder.binding.trackArtistNames.setText(list.get(position).getArtistName());
        }

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
                        listener.onItemClick(new ArtistsTrackModel());
                    } else {
                        holder.binding.moreItemBtn.setImageDrawable(context.getDrawable(R.drawable.heart_like));
                        holder.isLiked = true;
                    }
                } else {
                    if (insertData(list.get(position).getId(), list.get(position).getSongsId(), list.get(position).getName(), list.get(position).getImgUri(),list.get(position).getArtistName())) {
                        holder.binding.moreItemBtn.setImageDrawable(context.getDrawable(R.drawable.heart_like));
                        holder.isLiked = true;
                    } else {
                        holder.binding.moreItemBtn.setImageDrawable(context.getDrawable(R.drawable.heart));
                        holder.isLiked = false;
                    }
                }
            }
        });

        holder.binding.removeFromLS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFromLS(list.get(position).getSongsId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class LastViewHolder extends RecyclerView.ViewHolder {
        LastSessionAdapterBinding binding;
        boolean isLiked = true;

        public LastViewHolder(@NonNull LastSessionAdapterBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    private boolean insertData(long albumId, long songId, String title, String imageUri,String artistName) {
        databaseHelper = new DatabaseHelper(context);
        ContentValues cv = new ContentValues();
        cv.put("albumId", albumId);
        cv.put("songId", songId);
        cv.put("title", title);
        cv.put("imageUri", imageUri);
        cv.put("artistName", artistName);

        sqLiteDatabase = databaseHelper.getWritableDatabase();
        Long checkInsert = sqLiteDatabase.insert(TABLE_NAME, null, cv);
        if (checkInsert != null) {
//            Toast.makeText(context, "Inserted!", Toast.LENGTH_SHORT).show();
            Log.d("Inserted---", String.valueOf(albumId) + "    " + String.valueOf(songId) + "   " + title + "   " + imageUri);
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

    private boolean deleteFromLS(long songId) {
        databaseHelper = new DatabaseHelper(context);
        sqLiteDatabase = databaseHelper.getReadableDatabase();
        long delete = sqLiteDatabase.delete(LAST_SESSION_TABLE, "songId=" + songId, null);
        if (delete != -1) {
            listener.onItemClick(new ArtistsTrackModel());
//            Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(context, "Unexpected Error!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
