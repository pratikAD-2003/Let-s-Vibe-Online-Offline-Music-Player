package com.example.musicplayer.LastSession;

import static com.example.musicplayer.Database.DatabaseHelper.LAST_SESSION_TABLE;
import static com.example.musicplayer.Database.DatabaseHelper.TABLE_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import com.example.musicplayer.Database.DatabaseHelper;
import com.example.musicplayer.Favorite.FavoriteTrackAdapter;
import com.example.musicplayer.Model.ArtistsTrackModel;
import com.example.musicplayer.R;
import com.example.musicplayer.databinding.ActivityLastSessionBinding;

import java.util.ArrayList;

public class LastSession extends AppCompatActivity {
    ActivityLastSessionBinding binding;
    SQLiteDatabase sqLiteDatabase;
    DatabaseHelper databaseHelper;
    ArrayList<ArtistsTrackModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityLastSessionBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        databaseHelper = new DatabaseHelper(this);
        list = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getDrawable(R.drawable.custom_divider));
        binding.mySessionTracksRecyclerview.setLayoutManager(linearLayoutManager);
        binding.mySessionTracksRecyclerview.setHasFixedSize(true);
        binding.mySessionTracksRecyclerview.addItemDecoration(itemDecoration);
        display();

        binding.backFromSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void display() {
        list.clear();
        sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + LAST_SESSION_TABLE + "", null);

        while (cursor.moveToNext()) {
            long albumId = cursor.getLong(0);
            long songId = cursor.getLong(1);
            String title = cursor.getString(2);
            String imageUri = cursor.getString(3);
            String artistName = cursor.getString(4);
            list.add(new ArtistsTrackModel(albumId, title, imageUri, songId,artistName));
        }
        cursor.close();

        LastSessionAdapter artistsTracksAdapter = new LastSessionAdapter(this, list, new LastSessionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ArtistsTrackModel item) {
                display();
            }
        });

        if (list.isEmpty()) {
            binding.youDontHaveTracksSession.setVisibility(View.VISIBLE);
        } else {
            binding.youDontHaveTracksSession.setVisibility(View.GONE);
        }

        binding.mySessionTracksRecyclerview.setAdapter(artistsTracksAdapter);

    }
}