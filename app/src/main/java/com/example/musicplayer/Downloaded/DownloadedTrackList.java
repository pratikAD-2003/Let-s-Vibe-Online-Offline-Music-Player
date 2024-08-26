package com.example.musicplayer.Downloaded;

import static android.os.Build.VERSION.SDK_INT;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.example.musicplayer.Adaptors.ArtistsTracksAdapter;
import com.example.musicplayer.Home.HomeScreen;
import com.example.musicplayer.MediaPlayerManager;
import com.example.musicplayer.Model.ArtistsTrackModel;
import com.example.musicplayer.R;
import com.example.musicplayer.databinding.ActivityDownloadedTrackListBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DownloadedTrackList extends AppCompatActivity {
    ActivityDownloadedTrackListBinding binding;

    ArrayList<DownTrackModel> list;
    MediaPlayerManager mediaPlayerManager;
    int currentPosition = 0;
    ArrayList<DownTrackModel> downTrackModels;
    ArrayList<ArtistsTrackModel> artistsTrackModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityDownloadedTrackListBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        list = new ArrayList<>();

        requestPermissions();

        binding.backFromDownloaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getDrawable(R.drawable.custom_divider));
        binding.downloadedTrackRecyclerview.setLayoutManager(linearLayoutManager);
        binding.downloadedTrackRecyclerview.setHasFixedSize(true);
        binding.downloadedTrackRecyclerview.addItemDecoration(itemDecoration);
    }


    private void requestPermissions() {
        List<String> permissions = new ArrayList<>();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            // For Android versions below 33
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            // For Android 13 (API 33) and above
            permissions.add(Manifest.permission.READ_MEDIA_AUDIO);
        }

        Dexter.withContext(this)
                .withPermissions(permissions)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // Check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
//                            Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                            try {
                                getAllDownloadedSongs();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        // Check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // Redirect to app settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();  // Continue requesting the permission
                    }
                })
                .withErrorListener(error -> Toast.makeText(getApplicationContext(), "Error occurred! " + error.toString(), Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        Toast.makeText(this, "You have permanently denied some permissions. Please enable them in app settings.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    // getting category from Bottom sheet-----------------------------
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("type");
            Log.d("TYPE_HKFSDHUJSF",data);
            Gson gson = new Gson();
            binding.playSongLayoutShortcut.setVisibility(View.VISIBLE);
            currentPosition = intent.getIntExtra("position", 0);
            if (Objects.equals(data, "downloaded")) {
                String myList = getIntent().getStringExtra("list");
                Type listType = new TypeToken<ArrayList<DownTrackModel>>() {
                }.getType();
                downTrackModels = gson.fromJson(myList, listType);
                Glide.with(DownloadedTrackList.this).load(downTrackModels.get(currentPosition).getImage()).into(binding.shortCutSongImage);
            } else if (Objects.equals(data, "online")) {
                String myList = getIntent().getStringExtra("list");
                Type listType = new TypeToken<ArrayList<ArtistsTrackModel>>() {
                }.getType();
                artistsTrackModels = gson.fromJson(myList, listType);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter("Categories"));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    public void getAllDownloadedSongs() throws IOException {
//        temList.clear();
//        list.clear();
//        list2.clear();
//        String[] projection = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DATE_MODIFIED, MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.BUCKET_DISPLAY_NAME, MediaStore.Audio.Media.BUCKET_ID};
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
        };
        String selection = null;
        String selectionArg[] = null;
        String orderBy = MediaStore.Audio.Media.DATE_MODIFIED + " DESC";

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = this.getContentResolver().query(uri, projection, selection, selectionArg, orderBy);

        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToPosition(0);
//            cursor.moveToFirst();
            while (true) {
//                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
//                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
//                String size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
//                String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
//                String date = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED));
//                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
//                String folderName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.BUCKET_DISPLAY_NAME));
//                String folderId = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.BUCKET_ID));

                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

//                Log.d("FETCH_DATA", album + "\n" + artist + "\n" + title + "\n" + path + "\n" + duration + "\n" + String.valueOf(getAlbumArt(path)));
                try {
                    File file = new File(path);
                    Uri uri1 = Uri.fromFile(file);
                    DownTrackModel model = new DownTrackModel(title, String.valueOf(uri1), getAlbumArt(path),artist,duration,albumId);
                    list.add(model);
                } catch (Exception e) {

                }
                if (!cursor.isLast()) {
                    cursor.moveToNext();
                } else {
                    cursor.close();
                    break;
                }

                DownAdapter artistsTracksAdapter = new DownAdapter(this, list);
                binding.downloadedTrackRecyclerview.setAdapter(artistsTracksAdapter);
            }
        }
    }

    private Bitmap getAlbumArt(String audioFilePath) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(audioFilePath);

        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();

        if (art != null) {
            return BitmapFactory.decodeByteArray(art, 0, art.length);
        } else {
            return null;
        }
    }
}