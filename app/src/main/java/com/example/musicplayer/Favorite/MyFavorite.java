package com.example.musicplayer.Favorite;

import static com.example.musicplayer.Database.DatabaseHelper.TABLE_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import com.example.musicplayer.Adaptors.ArtistsTracksAdapter;
import com.example.musicplayer.Database.DatabaseHelper;
import com.example.musicplayer.Model.ArtistsTrackModel;
import com.example.musicplayer.Model.TrackModel;
import com.example.musicplayer.R;
import com.example.musicplayer.databinding.ActivityMyFavoriteBinding;

import java.util.ArrayList;

public class MyFavorite extends AppCompatActivity {
    ActivityMyFavoriteBinding binding;
    SQLiteDatabase sqLiteDatabase;
    DatabaseHelper databaseHelper;
    ArrayList<ArtistsTrackModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityMyFavoriteBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        databaseHelper = new DatabaseHelper(this);
        list = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getDrawable(R.drawable.custom_divider));
        binding.favoriteTracksRecyclerview.setLayoutManager(linearLayoutManager);
        binding.favoriteTracksRecyclerview.setHasFixedSize(true);
        binding.favoriteTracksRecyclerview.addItemDecoration(itemDecoration);
        display();

        binding.backFromFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void display() {
        list.clear();
        sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_NAME + "", null);

        while (cursor.moveToNext()) {
            long albumId = cursor.getLong(0);
            long songId = cursor.getLong(1);
            String title = cursor.getString(2);
            String imageUri = cursor.getString(3);
            String artistName = cursor.getString(4);
            list.add(new ArtistsTrackModel(albumId, title, imageUri, songId,artistName));
        }
        cursor.close();

        for (int i = 0; i < list.size(); i++) {
//            System.out.println("FAVORITE------" + list.get(i).getSongsId());
        }
        FavoriteTrackAdapter artistsTracksAdapter = new FavoriteTrackAdapter(this, list, new FavoriteTrackAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ArtistsTrackModel item) {
                display();
            }
        });

        if (list.isEmpty()) {
            binding.youDontHaveTracksFavorite.setVisibility(View.VISIBLE);
        } else {
            binding.youDontHaveTracksFavorite.setVisibility(View.GONE);
        }

        binding.favoriteTracksRecyclerview.setAdapter(artistsTracksAdapter);

    }
}