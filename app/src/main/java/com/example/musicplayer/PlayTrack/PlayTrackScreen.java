package com.example.musicplayer.PlayTrack;

import static com.example.musicplayer.Database.DatabaseHelper.LAST_SESSION_TABLE;
import static com.example.musicplayer.Database.DatabaseHelper.TABLE_NAME;
import static com.example.musicplayer.Urls.Urls.ALBUM_BY_ID;
import static com.example.musicplayer.Urls.Urls.TOKEN_DEEZER;

import static java.security.AccessController.getContext;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.musicplayer.Adaptors.ArtistsTracksAdapter;
import com.example.musicplayer.Database.DatabaseHelper;
import com.example.musicplayer.Downloaded.DownTrackModel;
import com.example.musicplayer.MediaPlayerManager;
import com.example.musicplayer.Model.AlbumModel.AlbumModel;
import com.example.musicplayer.Model.ArtistsTrackModel;
import com.example.musicplayer.R;
import com.example.musicplayer.SingleTrack.SingleTrack;
import com.example.musicplayer.databinding.ActivityPlayTrackScreenBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PlayTrackScreen extends AppCompatActivity {
    ActivityPlayTrackScreenBinding binding;
    String songUri = "";
    ArrayList<ArtistsTrackModel> list;
    ArrayList<DownTrackModel> list2;
    int currentPosition;
    String type = "";
    Handler handler = new Handler();
    MediaPlayerManager mediaPlayerManager;
    SQLiteDatabase sqLiteDatabase;
    DatabaseHelper databaseHelper;
    long albumId = 0;
    boolean isLiked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityPlayTrackScreenBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        list = new ArrayList<>();
        list2 = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);
        Gson gson = new Gson();

        SharedPreferences sharedPreferences = getSharedPreferences("LIST", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        mediaPlayerManager = MediaPlayerManager.getInstance();
        if (mediaPlayerManager.getMediaPlayer() != null && mediaPlayerManager.getMediaPlayer().isPlaying()) {
            binding.playSongBtnPTS.setImageDrawable(getDrawable(R.drawable.pause));
        } else if (mediaPlayerManager.getMediaPlayer() != null && !mediaPlayerManager.getMediaPlayer().isPlaying()) {
            binding.playSongBtnPTS.setImageDrawable(getDrawable(R.drawable.play_button));
        }

        type = getIntent().getStringExtra("type");
        if (Objects.equals(type, "downloaded")) {
            binding.showPlayListPTS.setVisibility(View.INVISIBLE);
            binding.likedThatTrack.setVisibility(View.INVISIBLE);
            currentPosition = getIntent().getIntExtra("position", 0);
            String myList = getIntent().getStringExtra("list");
            Type listType = new TypeToken<ArrayList<DownTrackModel>>() {
            }.getType();
            list2 = gson.fromJson(myList, listType);
        } else if (Objects.equals(type, "online")) {
            binding.showPlayListPTS.setVisibility(View.VISIBLE);
            binding.likedThatTrack.setVisibility(View.VISIBLE);
            currentPosition = getIntent().getIntExtra("position", 0);
            String myList = getIntent().getStringExtra("list");
            Type listType = new TypeToken<ArrayList<ArtistsTrackModel>>() {
            }.getType();
            list = gson.fromJson(myList, listType);
            assert list != null;
            albumId = list.get(0).getId();
        }
        if (Objects.equals(type, "downloaded")) {
            if (Objects.equals(list2.get(currentPosition).getId(), sharedPreferences.getLong("id", 0))) {
                AlreadyPlaying();
            } else {
                selectedSong(currentPosition);
            }
        } else if (Objects.equals(type, "online")) {
            if (Objects.equals(list.get(currentPosition).getSongsId(), sharedPreferences.getLong("id", 0))) {
                AlreadyPlaying();
            } else {
                selectedSong(currentPosition);
                if (!checkAlreadyInMyList(list.get(currentPosition).getSongsId())) {
                    insertInMyLastSession(list.get(currentPosition).getId(), list.get(currentPosition).getSongsId(), list.get(currentPosition).getName(), list.get(currentPosition).getImgUri(), list.get(currentPosition).getArtistName());
                } else {
                    Toast.makeText(this, "Already in list!", Toast.LENGTH_SHORT).show();
                }
            }

            if (display(list.get(currentPosition).getSongsId())) {
                binding.likedThatTrack.setImageDrawable(getDrawable(R.drawable.heart_like));
                isLiked = true;
            } else {
                binding.likedThatTrack.setImageDrawable(getDrawable(R.drawable.heart));
                isLiked = false;
            }
        }

        binding.backFromPTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.playSongBtnPTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTrack();
            }
        });

        binding.nextSongPTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.equals(type, "downloaded")) {
                    if (currentPosition != list2.size() - 1) {
                        selectedSong(currentPosition += 1);
                    }
                } else if (Objects.equals(type, "online")) {
                    if (currentPosition != list.size() - 1) {
                        selectedSong(currentPosition += 1);
                    }
                }
            }
        });

        binding.prevSongPTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPosition > 0) {
                    binding.prevSongPTS.setVisibility(View.VISIBLE);
                    selectedSong(currentPosition -= 1);
                } else {
                    binding.prevSongPTS.setVisibility(View.INVISIBLE);
                }
            }
        });

//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                binding.songSeekbarPTS.setProgress(0);
//                handler.removeCallbacks(updateSeekBarRunnable);
//            }
//        });

        binding.showPlayListPTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putLong("albumId", albumId);
                ShowRelatedTracksDialog showRelatedTracksDialog = new ShowRelatedTracksDialog();
                showRelatedTracksDialog.setArguments(bundle);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    showRelatedTracksDialog.show(getSupportFragmentManager(), getAttributionTag());
                }
            }
        });


        binding.likedThatTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLiked) {
                    if (deleteData(list.get(currentPosition).getSongsId())) {
                        binding.likedThatTrack.setImageDrawable(getDrawable(R.drawable.heart));
                        isLiked = false;
                    } else {
                        binding.likedThatTrack.setImageDrawable(getDrawable(R.drawable.heart_like));
                        isLiked = true;
                    }
                } else {
                    if (insertData(list.get(currentPosition).getId(), list.get(currentPosition).getSongsId(), list.get(currentPosition).getName(), list.get(currentPosition).getImgUri(), list.get(currentPosition).getArtistName())) {
                        binding.likedThatTrack.setImageDrawable(getDrawable(R.drawable.heart_like));
                        isLiked = true;
                    } else {
                        binding.likedThatTrack.setImageDrawable(getDrawable(R.drawable.heart));
                        isLiked = false;
                    }
                }
            }
        });
    }

    private boolean insertData(long albumId, long songId, String title, String imageUri, String artistName) {
        databaseHelper = new DatabaseHelper(PlayTrackScreen.this);
        ContentValues cv = new ContentValues();
        cv.put("albumId", albumId);
        cv.put("songId", songId);
        cv.put("title", title);
        cv.put("imageUri", imageUri);
        cv.put("artistName", artistName);

        sqLiteDatabase = databaseHelper.getWritableDatabase();
        Long checkInsert = sqLiteDatabase.insert(TABLE_NAME, null, cv);
        if (checkInsert != null) {
//            Toast.makeText(PlayTrackScreen.this, "Inserted!", Toast.LENGTH_SHORT).show();
//            Log.d("Inserted---", String.valueOf(albumId) + "    " + String.valueOf(songId) + "   " + title + "   " + imageUri);
            return true;
        } else {
            Toast.makeText(PlayTrackScreen.this, "Unexpected!", Toast.LENGTH_SHORT).show();
            Log.d("Error while inserting----", "Yes");
            return false;
        }
    }

    public boolean display(long songId2) {
        databaseHelper = new DatabaseHelper(PlayTrackScreen.this);
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
        databaseHelper = new DatabaseHelper(PlayTrackScreen.this);
        sqLiteDatabase = databaseHelper.getReadableDatabase();
        long delete = sqLiteDatabase.delete(TABLE_NAME, "songId=" + songId, null);
        if (delete != -1) {
//            Toast.makeText(PlayTrackScreen.this, "Deleted!", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(PlayTrackScreen.this, "Unexpected Error!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    // Insert in last session--------------------
    private boolean insertInMyLastSession(long albumId, long songId, String title, String imageUri, String artistName) {
        databaseHelper = new DatabaseHelper(PlayTrackScreen.this);
        ContentValues cv = new ContentValues();
        cv.put("albumId", albumId);
        cv.put("songId", songId);
        cv.put("title", title);
        cv.put("imageUri", imageUri);
        cv.put("artistName", artistName);

        sqLiteDatabase = databaseHelper.getWritableDatabase();
        Long checkInsert = sqLiteDatabase.insert(LAST_SESSION_TABLE, null, cv);
        if (checkInsert != null) {
//            Toast.makeText(PlayTrackScreen.this, "Inserted In My Session!", Toast.LENGTH_SHORT).show();
//            Log.d("Inserted---", String.valueOf(albumId) + "    " + String.valueOf(songId) + "   " + title + "   " + imageUri);
            return true;
        } else {
            Toast.makeText(PlayTrackScreen.this, "Unexpected!", Toast.LENGTH_SHORT).show();
            Log.d("Error while inserting----", "Yes");
            return false;
        }
    }

    public boolean checkAlreadyInMyList(long songId2) {
        databaseHelper = new DatabaseHelper(PlayTrackScreen.this);
        sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + LAST_SESSION_TABLE + "", null);

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

    private void selectedSong(int currentPosition) {
        if (Objects.equals(type, "downloaded")) {
            if (currentPosition == list2.size() - 1) {
                binding.nextSongPTS.setVisibility(View.INVISIBLE);
            } else {
                binding.nextSongPTS.setVisibility(View.VISIBLE);
            }
            if (list2.size() == 1 || currentPosition == 0) {
                binding.prevSongPTS.setVisibility(View.INVISIBLE);
            } else {
                binding.prevSongPTS.setVisibility(View.VISIBLE);
            }
            if (list2.get(currentPosition).getImage() == null) {
                Glide.with(this).load(getDrawable(R.drawable.no_available)).into(binding.trackImgPTS);
            } else {
                Glide.with(this).load(list2.get(currentPosition).getImage()).into(binding.trackImgPTS);
            }
            binding.trackNamePTS.setText(list2.get(currentPosition).getTitle());
            binding.trackArtistNamePTS.setText(list2.get(currentPosition).getArtistName());
            binding.songDurationPTS.setText(convertMillisToTrackDuration(Integer.parseInt(list2.get(currentPosition).getDuration())));
            playSong(Uri.parse(list2.get(currentPosition).getUri()));
        } else if (Objects.equals(type, "online")) {
            if (currentPosition == list.size() - 1) {
                binding.nextSongPTS.setVisibility(View.INVISIBLE);
            } else {
                binding.nextSongPTS.setVisibility(View.VISIBLE);
            }
            if (list.size() == 1 || currentPosition == 0) {
                binding.prevSongPTS.setVisibility(View.INVISIBLE);
            } else {
                binding.prevSongPTS.setVisibility(View.VISIBLE);
            }
            Glide.with(this).load(list.get(currentPosition).getImgUri()).into(binding.trackImgPTS);
            binding.trackNamePTS.setText(list.get(currentPosition).getName());
            binding.trackArtistNamePTS.setText(list.get(currentPosition).getArtistName());
            binding.songDurationPTS.setText(convertMillisToTrackDuration((int) list.get(currentPosition).getDuration()));
            getAlbumData(list.get(currentPosition).getId(), list.get(currentPosition).getSongsId());
        }
    }

    private void playSong(Uri songResId) {
        sendData();
        mediaPlayerManager = MediaPlayerManager.getInstance();

        // Play the selected song
        mediaPlayerManager.play(this, songResId);

        // Set SeekBar maximum to the duration of the song
        if (mediaPlayerManager.getMediaPlayer() != null) {
            binding.songSeekbarPTS.setMax(mediaPlayerManager.getMediaPlayer().getDuration());
        }

        // Start updating the SeekBar
        updateSeekBar();

        // Handle SeekBar changes
        binding.songSeekbarPTS.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayerManager.getMediaPlayer() != null) {
                    mediaPlayerManager.getMediaPlayer().seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mediaPlayerManager.getMediaPlayer() != null) {
                    mediaPlayerManager.getMediaPlayer().pause();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayerManager.getMediaPlayer() != null) {
                    mediaPlayerManager.getMediaPlayer().start();
                }
            }
        });

        // Handle MediaPlayer completion
        if (mediaPlayerManager.getMediaPlayer() != null) {
            mediaPlayerManager.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    binding.songSeekbarPTS.setProgress(0);
                    binding.songCompletedDurPTS.setText("00:00");
                    binding.playSongBtnPTS.setImageDrawable(getDrawable(R.drawable.play_button));
                    handler.removeCallbacks(updateSeekBarRunnable);

                    if (Objects.equals(type, "downloaded")) {
                        if (currentPosition != list2.size() - 1) {
                            selectedSong(currentPosition += 1);
                            binding.playSongBtnPTS.setImageDrawable(getDrawable(R.drawable.pause));
                        }
                    } else if (Objects.equals(type, "online")) {
                        if (currentPosition != list.size() - 1) {
                            selectedSong(currentPosition += 1);
                            binding.playSongBtnPTS.setImageDrawable(getDrawable(R.drawable.pause));
                        }
                    }
                }
            });
        }
    }

    // Global Runnable to be used for updating the SeekBar
    private final Runnable updateSeekBarRunnable = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
        }
    };

    // Method to update the SeekBar
    private void updateSeekBar() {
        if (mediaPlayerManager.getMediaPlayer() != null) {
            int currentPosition = mediaPlayerManager.getMediaPlayer().getCurrentPosition();
            binding.songSeekbarPTS.setProgress(mediaPlayerManager.getMediaPlayer().getCurrentPosition());
            binding.songCompletedDurPTS.setText(convertMillisToTrackDuration(currentPosition));
            handler.postDelayed(updateSeekBarRunnable, 1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateSeekBarRunnable);
    }

    public void pauseTrack() {
        if (mediaPlayerManager.getMediaPlayer() != null && mediaPlayerManager.getMediaPlayer().isPlaying()) {
            mediaPlayerManager.getMediaPlayer().pause();
            binding.playSongBtnPTS.setImageDrawable(getDrawable(R.drawable.play_button));
        } else if (mediaPlayerManager.getMediaPlayer() != null && !mediaPlayerManager.getMediaPlayer().isPlaying()) {
            binding.playSongBtnPTS.setImageDrawable(getDrawable(R.drawable.pause));
            mediaPlayerManager.getMediaPlayer().start();
        }
    }

    void getAlbumData(long albumId, long songId) {
        System.out.println("ALBUM_ID" + albumId);
        String url = ALBUM_BY_ID + albumId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new GsonBuilder().setLenient().create();
                AlbumModel albumModel = gson.fromJson(response, AlbumModel.class);
                try {
                    if (albumModel != null) {
                        if (albumModel.getTracks().getData() != null) {
                            for (int i = 0; i < albumModel.getTracks().getData().size(); i++) {
                                if (albumModel.getTracks().getData().get(i).getId() == songId) {
                                    songUri = albumModel.getTracks().getData().get(i).getPreview();
                                    binding.songDurationPTS.setText(getTotalDuration(albumModel.getTracks().getData().get(i).getDuration()));
//                                    playTrackByUri(PlayTrackScreen.this, Uri.parse(songUri));
                                    playSong(Uri.parse(songUri));
                                }
                            }
                        } else {
                        }
                    } else {
                    }
                } catch (Exception e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR " + error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("x-rapidapi-key", TOKEN_DEEZER);
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return super.getBody();
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        };

        int timeOutPeriod = 60000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(timeOutPeriod, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void AlreadyPlaying() {
        if (Objects.equals(type, "downloaded")) {
            binding.showPlayListPTS.setVisibility(View.INVISIBLE);
            binding.likedThatTrack.setVisibility(View.INVISIBLE);
            if (currentPosition == list2.size() - 1) {
                binding.nextSongPTS.setVisibility(View.INVISIBLE);
            } else if (currentPosition == 0) {
                binding.prevSongPTS.setVisibility(View.INVISIBLE);
            } else {
                binding.nextSongPTS.setVisibility(View.VISIBLE);
                binding.prevSongPTS.setVisibility(View.VISIBLE);
            }
            if (list2.get(currentPosition).getImage() == null) {
                Glide.with(this).load(getDrawable(R.drawable.no_available)).into(binding.trackImgPTS);
            } else {
                Glide.with(this).load(list2.get(currentPosition).getImage()).into(binding.trackImgPTS);
            }
            binding.trackNamePTS.setText(list2.get(currentPosition).getTitle());
            binding.trackArtistNamePTS.setText(list2.get(currentPosition).getArtistName());
            binding.songDurationPTS.setText(convertMillisToTrackDuration(Integer.parseInt(list2.get(currentPosition).getDuration())));
        } else if (Objects.equals(type, "online")) {
            binding.showPlayListPTS.setVisibility(View.VISIBLE);
            binding.likedThatTrack.setVisibility(View.VISIBLE);
            if (currentPosition == list.size() - 1) {
                binding.nextSongPTS.setVisibility(View.INVISIBLE);
            } else if (currentPosition == 0) {
                binding.prevSongPTS.setVisibility(View.INVISIBLE);
            } else {
                binding.nextSongPTS.setVisibility(View.VISIBLE);
                binding.prevSongPTS.setVisibility(View.VISIBLE);
            }
            Glide.with(this).load(list.get(currentPosition).getImgUri()).into(binding.trackImgPTS);
            binding.trackNamePTS.setText(list.get(currentPosition).getName());
            binding.trackArtistNamePTS.setText(list.get(currentPosition).getArtistName());
        }

        mediaPlayerManager = MediaPlayerManager.getInstance();
        // Set SeekBar maximum to the duration of the s00ong
        if (mediaPlayerManager.getMediaPlayer() != null) {
            binding.songSeekbarPTS.setMax(mediaPlayerManager.getMediaPlayer().getDuration());
        }

        // Start updating the SeekBar
        updateSeekBar();

        // Handle SeekBar changes
        binding.songSeekbarPTS.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayerManager.getMediaPlayer() != null) {
                    mediaPlayerManager.getMediaPlayer().seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mediaPlayerManager.getMediaPlayer() != null) {
                    mediaPlayerManager.getMediaPlayer().pause();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayerManager.getMediaPlayer() != null) {
                    mediaPlayerManager.getMediaPlayer().start();
                }
            }
        });

        // Handle MediaPlayer completion
        if (mediaPlayerManager.getMediaPlayer() != null) {
            mediaPlayerManager.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    binding.songSeekbarPTS.setProgress(0);
                    binding.songCompletedDurPTS.setText("00:00");
                    handler.removeCallbacks(updateSeekBarRunnable);
                }
            });
        }
    }

    public String convertMillisToTrackDuration(int milliseconds) {
        int totalSeconds = (int) Math.ceil(milliseconds / 1000.0);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    public String getTotalDuration(int secondsInput) {
        int minutes = secondsInput / 60;
        int seconds = secondsInput % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void sendData() {
        SharedPreferences sharedPreferences = getSharedPreferences("LIST", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!type.isEmpty()) {
            if (Objects.equals(type, "downloaded")) {
                Gson gson = new Gson();
                String myList = gson.toJson(list2);
                editor.putString("type", "downloaded");
                editor.putInt("position", currentPosition);
                editor.putString("list", myList);
                editor.putLong("id", list2.get(currentPosition).getId());
                editor.apply();
            } else if (Objects.equals(type, "online")) {
                Gson gson = new Gson();
                String myList = gson.toJson(list);
                editor.putString("type", "online");
                editor.putInt("position", currentPosition);
                editor.putLong("id", list.get(currentPosition).getSongsId());
                editor.putString("list", myList);
                editor.apply();
            }
        }
    }
}