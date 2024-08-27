package com.example.musicplayer.Home;

import static android.os.Trace.isEnabled;
import static com.example.musicplayer.Database.DatabaseHelper.LAST_SESSION_TABLE;
import static com.example.musicplayer.Urls.Urls.ALBUM_BY_ID;
import static com.example.musicplayer.Urls.Urls.SEARCH_RESULT;
import static com.example.musicplayer.Urls.Urls.TOKEN;
import static com.example.musicplayer.Urls.Urls.TOKEN_DEEZER;
import static com.example.musicplayer.Urls.Urls.TRACKS_LIST;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
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
import com.example.musicplayer.Adaptors.HomeAlbumAdapter;
import com.example.musicplayer.Adaptors.HomeTopArtistsAdapter;
import com.example.musicplayer.Adaptors.HomeTrackAdapter;
import com.example.musicplayer.Database.DatabaseHelper;
import com.example.musicplayer.Downloaded.DownTrackModel;
import com.example.musicplayer.Downloaded.DownloadedTrackList;
import com.example.musicplayer.Favorite.MyFavorite;
import com.example.musicplayer.LastSession.LastSession;
import com.example.musicplayer.MediaPlayerManager;
import com.example.musicplayer.Model.AlbumModel.AlbumModel;
import com.example.musicplayer.Model.ArtistsTrackModel;
import com.example.musicplayer.Model.SearchModel.SearchResultModel;
import com.example.musicplayer.Model.TrackModel;
import com.example.musicplayer.PlayTrack.PlayTrackScreen;
import com.example.musicplayer.R;
import com.example.musicplayer.Search.SearchThings;
import com.example.musicplayer.databinding.ActivityHomeScreenBinding;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeScreen extends AppCompatActivity {
    ActivityHomeScreenBinding binding;
    ImageView openDrawer;
    RecyclerView trackRecyclerview, topArtistsRecyclerview, latestReleaseRecyclerview, popularRecyclerview, bestOf90sReyclerview, moodyMixedRecyclerview, englishReyclerview;
    ArrayList<AlbumModel> albumModelArrayList;
    EditText searchBox;
    boolean isClickBack = false;

    // setting shortcut btn----------
    TextView playingSongTitle, playingSongArtist;
    ShapeableImageView playingSongImage;
    ImageView playingBtn, nextBtn, previousBtn;
    MediaPlayerManager mediaPlayerManager;
    int currentPosition = 0;
    String type = "";
    ArrayList<DownTrackModel> downTrackModels;
    ArrayList<ArtistsTrackModel> artistsTrackModels;
    DatabaseHelper databaseHelper;
    SQLiteDatabase sqLiteDatabase;
    RelativeLayout bottomPlayingBar;
    Uri songUri;
    Handler handler = new Handler();
    boolean isPlaying = false;

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences("LIST", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        type = sharedPreferences.getString("type", "no_val");
        if (!Objects.equals(type, "no_val")) {
            Gson gson = new Gson();
            bottomPlayingBar.setVisibility(View.VISIBLE);
            currentPosition = sharedPreferences.getInt("position", 0);
            if (Objects.equals(type, "downloaded")) {
                String myList = sharedPreferences.getString("list", "");
                Type listType = new TypeToken<ArrayList<DownTrackModel>>() {
                }.getType();
                downTrackModels = gson.fromJson(myList, listType);
                if (currentPosition == downTrackModels.size() - 1) {
                    nextBtn.setVisibility(View.INVISIBLE);
                } else {
                    nextBtn.setVisibility(View.VISIBLE);
                }
                if (downTrackModels.size() == 1 || currentPosition == 0) {
                    previousBtn.setVisibility(View.INVISIBLE);
                } else {
                    previousBtn.setVisibility(View.VISIBLE);
                }
                if (downTrackModels.get(currentPosition).getTitle() != null) {
                    if (downTrackModels.get(currentPosition).getTitle().length() > 15) {
                        playingSongTitle.setText(downTrackModels.get(currentPosition).getTitle().substring(0, 12) + "...");
                    } else {
                        playingSongTitle.setText(downTrackModels.get(currentPosition).getTitle());
                    }
                }
                if (downTrackModels.get(currentPosition).getArtistName() != null) {
                    if (downTrackModels.get(currentPosition).getArtistName().length() > 15) {
                        playingSongArtist.setText(downTrackModels.get(currentPosition).getArtistName().substring(0, 12) + "...");
                    } else {
                        playingSongArtist.setText(downTrackModels.get(currentPosition).getArtistName());
                    }
                }
                if (downTrackModels.get(currentPosition).getImage() != null) {
//                    Glide.with(HomeScreen.this).load(downTrackModels.get(currentPosition).getImage()).into(playingSongImage);
                    Glide.with(HomeScreen.this).load(getDrawable(R.drawable.no_available)).into(playingSongImage);
                } else {
                    Glide.with(HomeScreen.this).load(getDrawable(R.drawable.no_available)).into(playingSongImage);
                }
            } else if (Objects.equals(type, "online")) {
                String myList = sharedPreferences.getString("list", "");
                Type listType = new TypeToken<ArrayList<ArtistsTrackModel>>() {
                }.getType();
                artistsTrackModels = gson.fromJson(myList, listType);
                if (currentPosition == artistsTrackModels.size() - 1) {
                    nextBtn.setVisibility(View.INVISIBLE);
                } else {
                    nextBtn.setVisibility(View.VISIBLE);
                }
                if (artistsTrackModels.size() == 1 || currentPosition == 0) {
                    previousBtn.setVisibility(View.INVISIBLE);
                } else {
                    previousBtn.setVisibility(View.VISIBLE);
                }
                if (artistsTrackModels.get(currentPosition).getName() != null) {
                    if (artistsTrackModels.get(currentPosition).getName().length() > 15) {
                        playingSongTitle.setText(artistsTrackModels.get(currentPosition).getName().substring(0, 12) + "...");
                    } else {
                        playingSongTitle.setText(artistsTrackModels.get(currentPosition).getName());
                    }
                }
                if (artistsTrackModels.get(currentPosition).getArtistName() != null) {
                    if (artistsTrackModels.get(currentPosition).getArtistName().length() > 15) {
                        playingSongArtist.setText(artistsTrackModels.get(currentPosition).getArtistName().substring(0, 12) + "...");
                    } else {
                        playingSongArtist.setText(artistsTrackModels.get(currentPosition).getArtistName());
                    }
                }
                if (artistsTrackModels.get(currentPosition).getImgUri() != null) {
                    Glide.with(HomeScreen.this).load(artistsTrackModels.get(currentPosition).getImgUri()).into(playingSongImage);
                } else {
                    Glide.with(HomeScreen.this).load(getDrawable(R.drawable.no_available)).into(playingSongImage);
                }
            }
        }

        mediaPlayerManager = MediaPlayerManager.getInstance();
        if (mediaPlayerManager.getMediaPlayer() != null && mediaPlayerManager.getMediaPlayer().isPlaying()) {
            playingBtn.setImageDrawable(getDrawable(R.drawable.pause));
        } else if (mediaPlayerManager.getMediaPlayer() != null && !mediaPlayerManager.getMediaPlayer().isPlaying()) {
            playingBtn.setImageDrawable(getDrawable(R.drawable.play_button));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityHomeScreenBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        albumModelArrayList = new ArrayList<>();
        downTrackModels = new ArrayList<>();
        artistsTrackModels = new ArrayList<>();
        mediaPlayerManager = MediaPlayerManager.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences("LIST", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        openDrawer = findViewById(R.id.openDrawerBtn);
        trackRecyclerview = findViewById(R.id.trendingTracksRecyclerview);
        topArtistsRecyclerview = findViewById(R.id.topArtistPlayListsRecyclerview);
        latestReleaseRecyclerview = findViewById(R.id.latestReleaseRecyclerview);
        popularRecyclerview = findViewById(R.id.popularAlbumsRecyclerview);
        bestOf90sReyclerview = findViewById(R.id.bestOf90sRecyclerview);
        moodyMixedRecyclerview = findViewById(R.id.mixedTracksRecyclerview);
        englishReyclerview = findViewById(R.id.englishTracksRecyclerview);
        searchBox = findViewById(R.id.searchBoxHomeScreen);

        playingSongTitle = findViewById(R.id.shortCutSongTitle);
        playingSongArtist = findViewById(R.id.shortCutSongArtist);
        playingSongImage = findViewById(R.id.shortCutSongImage);
        playingBtn = findViewById(R.id.shortCutSongPlayBtn);
        nextBtn = findViewById(R.id.shortCutSongNextBtn);
        previousBtn = findViewById(R.id.shortCutSongPreviousBtn);
        bottomPlayingBar = findViewById(R.id.playSongLayoutShortcut);


        setUpOnBackPressed();

        openDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        binding.navigationViewDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.mySongs) {
                    startActivity(new Intent(HomeScreen.this, DownloadedTrackList.class));
                } else if (id == R.id.favorite) {
                    startActivity(new Intent(HomeScreen.this, MyFavorite.class));
                } else if (id == R.id.lastSession) {
                    startActivity(new Intent(HomeScreen.this, LastSession.class));
                } else if (id == R.id.settings_btn) {

                }
                binding.drawerLayout.close();
                return false;
            }
        });

        initializeRecyclerview();

        // Trending tracks------------------
        ArrayList<TrackModel> trendingTracks = getTrendingTracks();
        HomeTrackAdapter homeTrackAdapter = new HomeTrackAdapter(trendingTracks, HomeScreen.this);
        trackRecyclerview.setAdapter(homeTrackAdapter);

        // Artists--------------------
        HomeTopArtistsAdapter homeTopArtistsAdapter = new HomeTopArtistsAdapter(HomeScreen.this, getArtists());
        topArtistsRecyclerview.setAdapter(homeTopArtistsAdapter);

        // Albums---------------------
        HomeAlbumAdapter homeTrackAdapter2 = new HomeAlbumAdapter(HomeScreen.this, getAlbumID());
        popularRecyclerview.setAdapter(homeTrackAdapter2);

        // Latest Release------------
        HomeTrackAdapter homeTrackAdapter1 = new HomeTrackAdapter(getLatestRelease(), HomeScreen.this);
        latestReleaseRecyclerview.setAdapter(homeTrackAdapter1);

        // best of 90s
        HomeTrackAdapter homeTrackAdapter3 = new HomeTrackAdapter(get90sDhamaka(), HomeScreen.this);
        bestOf90sReyclerview.setAdapter(homeTrackAdapter3);

        // moody mixed
        HomeTrackAdapter homeTrackAdapter4 = new HomeTrackAdapter(getMixedTracks(), HomeScreen.this);
        moodyMixedRecyclerview.setAdapter(homeTrackAdapter4);

        // english
        HomeTrackAdapter homeTrackAdapter5 = new HomeTrackAdapter(getEnglishTracks(), HomeScreen.this);
        englishReyclerview.setAdapter(homeTrackAdapter5);

        searchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreen.this, SearchThings.class));
            }
        });

        playingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    selectedSong(currentPosition);
                    playingBtn.setImageDrawable(getDrawable(R.drawable.pause));
                    isPlaying = true;
                } else {
                    pauseTrack();
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition = currentPosition + 1;
                selectedSong(currentPosition);
                playingBtn.setImageDrawable(getDrawable(R.drawable.pause));
                editor.putInt("position", currentPosition);
                editor.apply();
                if (Objects.equals(type, "online")) {
                    if (!checkAlreadyInMyList(artistsTrackModels.get(currentPosition).getSongsId())) {
                        insertInMyLastSession(artistsTrackModels.get(currentPosition).getId(), artistsTrackModels.get(currentPosition).getSongsId(), artistsTrackModels.get(currentPosition).getName(), artistsTrackModels.get(currentPosition).getImgUri(), artistsTrackModels.get(currentPosition).getArtistName());
                    } else {
//                    Toast.makeText(HomeScreen.this, "Already in list!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition = currentPosition - 1;
                selectedSong(currentPosition);
                playingBtn.setImageDrawable(getDrawable(R.drawable.pause));
                editor.putInt("position", currentPosition);
                editor.apply();
                if (Objects.equals(type, "online")) {
                    if (!checkAlreadyInMyList(artistsTrackModels.get(currentPosition).getSongsId())) {
                        insertInMyLastSession(artistsTrackModels.get(currentPosition).getId(), artistsTrackModels.get(currentPosition).getSongsId(), artistsTrackModels.get(currentPosition).getName(), artistsTrackModels.get(currentPosition).getImgUri(), artistsTrackModels.get(currentPosition).getArtistName());
                    } else {
//                    Toast.makeText(HomeScreen.this, "Already in list!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        bottomPlayingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.equals(type, "downloaded")) {
                    Intent intent = new Intent(HomeScreen.this, PlayTrackScreen.class);
                    intent.putExtra("position", currentPosition);
                    intent.putExtra("type", "downloaded");
                    Gson gson = new Gson();
                    String myList = gson.toJson(downTrackModels);
                    intent.putExtra("list", myList);
                    startActivity(intent);
                } else if (Objects.equals(type, "online")) {
                    Intent intent = new Intent(HomeScreen.this, PlayTrackScreen.class);
                    intent.putExtra("type", "online");
                    intent.putExtra("position", currentPosition);
                    Gson gson = new Gson();
                    String myList = gson.toJson(artistsTrackModels);
                    intent.putExtra("list", myList);
                    startActivity(intent);
                }
            }
        });

    }

    private void selectedSong(int currentPosition) {
        if (Objects.equals(type, "downloaded")) {
//            if (currentPosition == downTrackModels.size() - 1) {
//                nextBtn.setVisibility(View.INVISIBLE);
//            } else if (currentPosition == 0) {
//                previousBtn.setVisibility(View.INVISIBLE);
//            } else {
//                nextBtn.setVisibility(View.VISIBLE);
//                previousBtn.setVisibility(View.VISIBLE);
//            }
            if (currentPosition == downTrackModels.size() - 1) {
                nextBtn.setVisibility(View.INVISIBLE);
            } else {
                nextBtn.setVisibility(View.VISIBLE);
            }
            if (downTrackModels.size() == 1 || currentPosition == 0) {
                previousBtn.setVisibility(View.INVISIBLE);
            } else {
                previousBtn.setVisibility(View.VISIBLE);
            }
            if (downTrackModels.get(currentPosition).getTitle() != null) {
                if (downTrackModels.get(currentPosition).getTitle().length() > 15) {
                    playingSongTitle.setText(downTrackModels.get(currentPosition).getTitle().substring(0, 12) + "...");
                } else {
                    playingSongTitle.setText(downTrackModels.get(currentPosition).getTitle());
                }
            }
            if (downTrackModels.get(currentPosition).getArtistName() != null) {
                if (downTrackModels.get(currentPosition).getArtistName().length() > 15) {
                    playingSongArtist.setText(downTrackModels.get(currentPosition).getArtistName().substring(0, 12) + "...");
                } else {
                    playingSongArtist.setText(downTrackModels.get(currentPosition).getArtistName());
                }
            }
            if (downTrackModels.get(currentPosition).getImage() != null) {
//                Glide.with(HomeScreen.this).load(downTrackModels.get(currentPosition).getImage()).into(playingSongImage);
                Glide.with(HomeScreen.this).load(getDrawable(R.drawable.no_available)).into(playingSongImage);
            } else {
                Glide.with(HomeScreen.this).load(getDrawable(R.drawable.no_available)).into(playingSongImage);
            }
            playSong(Uri.parse(downTrackModels.get(currentPosition).getUri()));
        } else if (Objects.equals(type, "online")) {
//            if (currentPosition == artistsTrackModels.size() - 1) {
//                nextBtn.setVisibility(View.INVISIBLE);
//            } else if (currentPosition == 0) {
//                previousBtn.setVisibility(View.INVISIBLE);
//            } else {
//                nextBtn.setVisibility(View.VISIBLE);
//                previousBtn.setVisibility(View.VISIBLE);
//            }
            if (currentPosition == artistsTrackModels.size() - 1) {
                nextBtn.setVisibility(View.INVISIBLE);
            } else {
                nextBtn.setVisibility(View.VISIBLE);
            }
            if (artistsTrackModels.size() == 1 || currentPosition == 0) {
                previousBtn.setVisibility(View.INVISIBLE);
            } else {
                previousBtn.setVisibility(View.VISIBLE);
            }
            if (artistsTrackModels.get(currentPosition).getName() != null) {
                if (artistsTrackModels.get(currentPosition).getName().length() > 15) {
                    playingSongTitle.setText(artistsTrackModels.get(currentPosition).getName().substring(0, 12) + "...");
                } else {
                    playingSongTitle.setText(artistsTrackModels.get(currentPosition).getName());
                }
            }
            if (artistsTrackModels.get(currentPosition).getArtistName() != null) {
                if (artistsTrackModels.get(currentPosition).getArtistName().length() > 15) {
                    playingSongArtist.setText(artistsTrackModels.get(currentPosition).getArtistName().substring(0, 12) + "...");
                } else {
                    playingSongArtist.setText(artistsTrackModels.get(currentPosition).getArtistName());
                }
            }
            if (artistsTrackModels.get(currentPosition).getImgUri() != null) {
                Glide.with(HomeScreen.this).load(artistsTrackModels.get(currentPosition).getImgUri()).into(playingSongImage);
            } else {
                Glide.with(HomeScreen.this).load(getDrawable(R.drawable.no_available)).into(playingSongImage);
            }
            getAlbumData(artistsTrackModels.get(currentPosition).getId(), artistsTrackModels.get(currentPosition).getSongsId());
        }
    }

    private boolean insertInMyLastSession(long albumId, long songId, String title, String imageUri, String artistName) {
        databaseHelper = new DatabaseHelper(HomeScreen.this);
        ContentValues cv = new ContentValues();
        cv.put("albumId", albumId);
        cv.put("songId", songId);
        cv.put("title", title);
        cv.put("imageUri", imageUri);
        cv.put("artistName", artistName);

        sqLiteDatabase = databaseHelper.getWritableDatabase();
        Long checkInsert = sqLiteDatabase.insert(LAST_SESSION_TABLE, null, cv);
        if (checkInsert != null) {
//            Toast.makeText(HomeScreen.this, "Inserted In My Session!", Toast.LENGTH_SHORT).show();
//            Log.d("Inserted---", String.valueOf(albumId) + "    " + String.valueOf(songId) + "   " + title + "   " + imageUri);
            return true;
        } else {
            Toast.makeText(HomeScreen.this, "Unexpected!", Toast.LENGTH_SHORT).show();
            Log.d("Error while inserting----", "Yes");
            return false;
        }
    }

    public boolean checkAlreadyInMyList(long songId2) {
        databaseHelper = new DatabaseHelper(HomeScreen.this);
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

    public void initializeRecyclerview() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        trackRecyclerview.setLayoutManager(linearLayoutManager);
        trackRecyclerview.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        topArtistsRecyclerview.setLayoutManager(linearLayoutManager2);
        topArtistsRecyclerview.setHasFixedSize(true);


        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(this);
        linearLayoutManager3.setOrientation(LinearLayoutManager.HORIZONTAL);
        latestReleaseRecyclerview.setLayoutManager(linearLayoutManager3);
        latestReleaseRecyclerview.setHasFixedSize(true);


        LinearLayoutManager linearLayoutManager4 = new LinearLayoutManager(this);
        linearLayoutManager4.setOrientation(LinearLayoutManager.HORIZONTAL);
        popularRecyclerview.setLayoutManager(linearLayoutManager4);
        popularRecyclerview.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager5 = new LinearLayoutManager(this);
        linearLayoutManager5.setOrientation(LinearLayoutManager.HORIZONTAL);
        bestOf90sReyclerview.setLayoutManager(linearLayoutManager5);
        bestOf90sReyclerview.setHasFixedSize(true);


        LinearLayoutManager linearLayoutManager6 = new LinearLayoutManager(this);
        linearLayoutManager6.setOrientation(LinearLayoutManager.HORIZONTAL);
        moodyMixedRecyclerview.setLayoutManager(linearLayoutManager6);
        moodyMixedRecyclerview.setHasFixedSize(true);


        LinearLayoutManager linearLayoutManager7 = new LinearLayoutManager(this);
        linearLayoutManager7.setOrientation(LinearLayoutManager.HORIZONTAL);
        englishReyclerview.setLayoutManager(linearLayoutManager7);
        englishReyclerview.setHasFixedSize(true);
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
                                    songUri = Uri.parse(albumModel.getTracks().getData().get(i).getPreview());
                                    playSong(songUri);
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


    private void playSong(Uri songResId) {
        mediaPlayerManager = MediaPlayerManager.getInstance();

        // Play the selected song
        mediaPlayerManager.play(this, songResId);

        // Set SeekBar maximum to the duration of the song
        if (mediaPlayerManager.getMediaPlayer() != null) {
//            binding.songSeekbarPTS.setMax(mediaPlayerManager.getMediaPlayer().getDuration());
        }

        // Start updating the SeekBar
        updateSeekBar();

        // Handle SeekBar changes
//        binding.songSeekbarPTS.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser && mediaPlayerManager.getMediaPlayer() != null) {
//                    mediaPlayerManager.getMediaPlayer().seekTo(progress);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                if (mediaPlayerManager.getMediaPlayer() != null) {
//                    mediaPlayerManager.getMediaPlayer().pause();
//                }
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                if (mediaPlayerManager.getMediaPlayer() != null) {
//                    mediaPlayerManager.getMediaPlayer().start();
//                }
//            }
//        });

        // Handle MediaPlayer completion
        if (mediaPlayerManager.getMediaPlayer() != null) {
            mediaPlayerManager.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
//                    binding.songSeekbarPTS.setProgress(0);
//                    binding.songCompletedDurPTS.setText("00:00");
                    playingBtn.setImageDrawable(getDrawable(R.drawable.play_button));
                    handler.removeCallbacks(updateSeekBarRunnable);
                    if (Objects.equals(type, "downloaded")) {
                        if (currentPosition != downTrackModels.size() - 1) {
                            selectedSong(currentPosition += 1);
                            playingBtn.setImageDrawable(getDrawable(R.drawable.pause));
                        }
                    } else if (Objects.equals(type, "online")) {
                        if (currentPosition != artistsTrackModels.size() - 1) {
                            selectedSong(currentPosition += 1);
                            playingBtn.setImageDrawable(getDrawable(R.drawable.pause));
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
//            binding.songSeekbarPTS.setProgress(mediaPlayerManager.getMediaPlayer().getCurrentPosition());
//            binding.songCompletedDurPTS.setText(convertMillisToTrackDuration(currentPosition));
            handler.postDelayed(updateSeekBarRunnable, 1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateSeekBarRunnable);
    }

    public void pauseTrack() {
        mediaPlayerManager = MediaPlayerManager.getInstance();
        if (mediaPlayerManager.getMediaPlayer() != null && mediaPlayerManager.getMediaPlayer().isPlaying()) {
            mediaPlayerManager.getMediaPlayer().pause();
            playingBtn.setImageDrawable(getDrawable(R.drawable.play_button));
        } else if (mediaPlayerManager.getMediaPlayer() != null && !mediaPlayerManager.getMediaPlayer().isPlaying()) {
            playingBtn.setImageDrawable(getDrawable(R.drawable.pause));
            mediaPlayerManager.getMediaPlayer().start();
        }
    }

    public ArrayList<TrackModel> getArtists() {

        // find songs by it's artist id
        ArrayList<TrackModel> list = new ArrayList<>();
        list.add(new TrackModel(3830821, "https://e-cdns-images.dzcdn.net/images/artist/3d9cd96f1b2ca8fa877299bbeb771814/500x500-000000-80-0-0.jpg", "Arijit Singh"));
        list.add(new TrackModel(98371, "https://e-cdns-images.dzcdn.net/images/artist/3bb832d37d10ff2affcfa9afdc7c68a0/500x500-000000-80-0-0.jpg", "Shreya Ghoshal"));
        list.add(new TrackModel(405736, "https://e-cdns-images.dzcdn.net/images/artist/0ea90444148fff9c11d77f06a344724e/500x500-000000-80-0-0.jpg", "Atif Aslam"));
        list.add(new TrackModel(70075, "https://e-cdns-images.dzcdn.net/images/artist/812220125c4f0db57050438b65afcf78/500x500-000000-80-0-0.jpg", "Sonu Nigam"));
        list.add(new TrackModel(5017623, "https://e-cdns-images.dzcdn.net/images/artist/3aacd4e00a34aefb6041d30e0cc5bc5e/500x500-000000-80-0-0.jpg", "Armaan Malik"));
        list.add(new TrackModel(8419888, "https://e-cdns-images.dzcdn.net/images/artist/5a59f47ed958b8e02440ce05b9486e39/500x500-000000-80-0-0.jpg", "Jubin Nautiyal"));
        list.add(new TrackModel(129846, "https://e-cdns-images.dzcdn.net/images/artist/37e57f35a2fc007eab0c0e8a03237d59/500x500-000000-80-0-0.jpg", "Neha Kakkar"));
        list.add(new TrackModel(1680753, "https://e-cdns-images.dzcdn.net/images/artist/69bf6a7e304020efd1828d8509f8ab0f/500x500-000000-80-0-0.jpg", "Palak Muchhal"));
        list.add(new TrackModel(76645, "https://e-cdns-images.dzcdn.net/images/artist/dbcb7117c18f3c519a6edd02eaf8313d/500x500-000000-80-0-0.jpg", "Alka Yagnik"));
        list.add(new TrackModel(70758, "https://e-cdns-images.dzcdn.net/images/artist/7ad58f1c03087a082e22a718acc3f1fc/500x500-000000-80-0-0.jpg", "Kumar Sanu"));
        list.add(new TrackModel(14779, "https://e-cdns-images.dzcdn.net/images/artist/3e9fc253577914fc523213581e70a525/500x500-000000-80-0-0.jpg", "Sunidhi Chauhan"));
        return list;
    }

    public ArrayList<TrackModel> getAlbumID() {
        // find songs by it's album id
        ArrayList<TrackModel> list = new ArrayList<>();
        list.add(new TrackModel(561617732, "https://e-cdns-images.dzcdn.net/images/cover/5b334d724b6444b133229a3dc11cf16f/500x500-000000-80-0-0.jpg", "Ek Tha Raja"));
        list.add(new TrackModel(540414702, "https://e-cdns-images.dzcdn.net/images/cover/c0430db2aad6aafb4d251bcf0b13c32d/500x500-000000-80-0-0.jpg", "Guntur Kaaram"));
        list.add(new TrackModel(481587265, "https://e-cdns-images.dzcdn.net/images/cover/0aa4f79f7bc5510549a45158f604d977/500x500-000000-80-0-0.jpg", "Zara Hatke Zara Bachke (Original Motion Picture Soundtrack)"));
        list.add(new TrackModel(465969495, "https://e-cdns-images.dzcdn.net/images/artist/faae2718459c9fc1f848d308e17c554f/500x500-000000-80-0-0.jpg", "Softly"));
        list.add(new TrackModel(591549692, "https://e-cdns-images.dzcdn.net/images/cover/65e4ad0c83ab5b0b6d2c557237a6c0ff/500x500-000000-80-0-0.jpg", "Mr. And Mrs. Mahi (Original Motion Picture Soundtrack)"));
        list.add(new TrackModel(198977472, "https://e-cdns-images.dzcdn.net/images/cover/d13f6436db8c36e409071a4a048efc40/500x500-000000-80-0-0.jpg", "Fanaa"));
        list.add(new TrackModel(476359135, "https://e-cdns-images.dzcdn.net/images/cover/baeb296464b3cb3aea5984089e2b7b91/500x500-000000-80-0-0.jpg", "Ghilli"));
        return list;
    }

    public ArrayList<TrackModel> getTrendingTracks() {
        // get tracks by album id
        ArrayList<TrackModel> list = new ArrayList<>();
        list.add(new TrackModel(608235792, "https://e-cdns-images.dzcdn.net/images/cover/ff6bb1420d9fcd2671cf6f86c2e49658/500x500-000000-80-0-0.jpg", "Tauba Tauba (From Bad Newz)"));
        list.add(new TrackModel(569505411, "https://e-cdns-images.dzcdn.net/images/cover/d399cc3fa873990a8f0a0d1973873139/500x500-000000-80-0-0.jpg", "Suniyan Suniyan"));
        list.add(new TrackModel(619741081, "https://e-cdns-images.dzcdn.net/images/cover/1f8faf6b803911ad2d33ea66cacb3033/500x500-000000-80-0-0.jpg", "Aaj Ki Raat (From Stree 2"));
        list.add(new TrackModel(465969495, "https://e-cdns-images.dzcdn.net/images/artist/faae2718459c9fc1f848d308e17c554f/500x500-000000-80-0-0.jpg", "Softly"));
        list.add(new TrackModel(576978811, "https://e-cdns-images.dzcdn.net/images/cover/6f88346b2818313ccadbde509a411832/500x500-000000-80-0-0.jpg", "Paon Ki Jutti"));
        list.add(new TrackModel(618935911, "https://e-cdns-images.dzcdn.net/images/cover/5f2bd432c6660f668d1935593cb28bc4/500x500-000000-80-0-0.jpg", "Masle"));
        list.add(new TrackModel(623504801, "https://e-cdns-images.dzcdn.net/images/cover/c85e4d98787aa04833e9682f90e56fb5/500x500-000000-80-0-0.jpg", "Aayi Nai (From Stree 2)"));
        list.add(new TrackModel(602641262, "https://e-cdns-images.dzcdn.net/images/cover/d54cd334589f55ac47125593186b000b/500x500-000000-80-0-0.jpg", "Dilemma (feat. Sidhu Moose Wala)"));
        list.add(new TrackModel(609452132, "https://e-cdns-images.dzcdn.net/images/cover/eff6c527a40aad2f28707e75bb6ed095/500x500-000000-80-0-0.jpg", "Be Mine"));
        return list;
    }

    public ArrayList<TrackModel> getLatestRelease() {
        // by album id
        ArrayList<TrackModel> list = new ArrayList<>();
        list.add(new TrackModel(610332272, "https://e-cdns-images.dzcdn.net/images/cover/2d00c5a1488deb77bc1faa958f355b54/500x500-000000-80-0-0.jpg", "Big Dawgs"));
        list.add(new TrackModel(481587265, "https://e-cdns-images.dzcdn.net/images/cover/0aa4f79f7bc5510549a45158f604d977/500x500-000000-80-0-0.jpg", "Tere Vaaste"));
        list.add(new TrackModel(630314211, "https://e-cdns-images.dzcdn.net/images/cover/cca162b940a3956aced78a1370b49873/500x500-000000-80-0-0.jpg", "Akhiyaan Gulaab"));
        list.add(new TrackModel(591549692, "https://e-cdns-images.dzcdn.net/images/cover/65e4ad0c83ab5b0b6d2c557237a6c0ff/500x500-000000-80-0-0.jpg", "Mr. And Mrs. Mahi (Original Motion Picture Soundtrack)"));
        list.add(new TrackModel(555748522, "https://e-cdns-images.dzcdn.net/images/cover/3be3972ed279f77e1b9462ba22862127/500x500-000000-80-0-0.jpg", "Naina (From Crew)"));
        list.add(new TrackModel(566067871, "https://e-cdns-images.dzcdn.net/images/cover/5b1ba6e2a0fd1d1cb3193f8ffcfc6f25/500x500-000000-80-0-0.jpg", "Illuminati (From Aavesham )"));
        list.add(new TrackModel(586613652, "https://e-cdns-images.dzcdn.net/images/cover/8844ea2e2799280b9cca3976cf31c44b/500x500-000000-80-0-0.jpg", "Dekhha Tenu (From Mr. And Mrs. Mahi)"));
        list.add(new TrackModel(166916482, "https://e-cdns-images.dzcdn.net/images/cover/4311652191db0967cd04d4a17149e3d6/500x500-000000-80-0-0.jpg", "Tum Se Hi (From Sadak 2)"));
        list.add(new TrackModel(448055355, "https://e-cdns-images.dzcdn.net/images/cover/6b06bbbf7c2d9c6bcb60763bccc0571d/500x500-000000-80-0-0.jpg", "Heeriye (feat. Arijit Singh)"));
        list.add(new TrackModel(615808032, "https://e-cdns-images.dzcdn.net/images/cover/c5e8663cade4a54f7cf84c6218bf97ef/500x500-000000-80-0-0.jpg", "Diamond Ni"));
        list.add(new TrackModel(593438252, "https://e-cdns-images.dzcdn.net/images/cover/81df66514ebf25289022e0f9bdc9a877/500x500-000000-80-0-0.jpg", "Zaroor"));
        list.add(new TrackModel(57606552, "https://e-cdns-images.dzcdn.net/images/cover/f34708b3e3d5475a5c862f1852c75bdd/500x500-000000-80-0-0.jpg", "Bandeya (From Dil Juunglee)"));
        return list;
    }

    public ArrayList<TrackModel> get90sDhamaka() {
        ArrayList<TrackModel> list = new ArrayList<>();
        list.add(new TrackModel(62304792, "https://e-cdns-images.dzcdn.net/images/cover/b5d6f97e9d2b11dd538b78aa772a5d6e/500x500-000000-80-0-0.jpg", "The Original Don Amitabh Bachchan"));
        list.add(new TrackModel(181962862, "https://e-cdns-images.dzcdn.net/images/cover/d6ba99adbf3ecd99f62c237f0008365f/500x500-000000-80-0-0.jpg", "China - Gate (Original Motion Picture Soundtrack)"));
        list.add(new TrackModel(13457555, "https://e-cdns-images.dzcdn.net/images/cover/97c28186a1ce8c4313170a9c71c5ffb8/500x500-000000-80-0-0.jpg", "Main Khiladi Tu Anari (Original Motion Picture Soundtrack)"));
        list.add(new TrackModel(180657652, "https://e-cdns-images.dzcdn.net/images/cover/fd1096693d649c1029b6760751087898/500x500-000000-80-0-0.jpg", "Khal Nayak (Original Motion Picture Soundtrack)"));
        list.add(new TrackModel(202910032, "https://e-cdns-images.dzcdn.net/images/cover/1ae91989e1c4b555782d9e31d5271a50/500x500-000000-80-0-0.jpg", "Bade Miyan Chote Miyan (Original Motion Picture Soundtrack)"));
        list.add(new TrackModel(250548, "https://e-cdns-images.dzcdn.net/images/cover/8ccc103d88059757aae63222a6a52f89/500x500-000000-80-0-0.jpg", "Devdas (Original Motion Picture Soundtrack)"));
        list.add(new TrackModel(64390822, "https://e-cdns-images.dzcdn.net/images/cover/09130af31a2829401e198a55d4de2acc/500x500-000000-80-0-0.jpg", "Vishwatma (Original Motion Picture Soundtrack)"));
        list.add(new TrackModel(13419143, "https://e-cdns-images.dzcdn.net/images/cover/a12f148dd48a5d6b40604da52929e591/500x500-000000-80-0-0.jpg", "Satya (Original Motion Picture Soundtrack)"));
        list.add(new TrackModel(187455742, "https://e-cdns-images.dzcdn.net/images/cover/0ea063b59febbb1daccef8ce05e2c8a5/500x500-000000-80-0-0.jpg", "Humse Hai Muqabala (Original Motion Picture Soundtrack)"));
        list.add(new TrackModel(180653982, "https://e-cdns-images.dzcdn.net/images/cover/7f0b8e27a39d44565e7ce057ac29005b/500x500-000000-80-0-0.jpg", "Taal (Original Motion Picture Soundtrack)"));
        return list;
    }

    public ArrayList<TrackModel> getMixedTracks() {
        ArrayList<TrackModel> list = new ArrayList<>();
        list.add(new TrackModel(13348983, "https://e-cdns-images.dzcdn.net/images/cover/8d7dd2bbf55dd87f3dd7268a94a254bc/500x500-000000-80-0-0.jpg", "Bollywood Dance Masti"));
        list.add(new TrackModel(125130852, "https://e-cdns-images.dzcdn.net/images/cover/ca063c1df7b5999a26d0062758236204/500x500-000000-80-0-0.jpg", "Bollywood Dance Mashup"));
        list.add(new TrackModel(243817762, "https://e-cdns-images.dzcdn.net/images/cover/e20a0a2943656eb2a91f89ab428cda14/500x500-000000-80-0-0.jpg", "Soulful Retro Romance"));
        list.add(new TrackModel(52623382, "https://e-cdns-images.dzcdn.net/images/cover/08f7be52e272daee3c6af5ca89f4dabe/500x500-000000-80-0-0.jpg", "90's Bollywood Sad Songs (With Jhankar Beats)"));
        list.add(new TrackModel(581478272, "https://e-cdns-images.dzcdn.net/images/cover/65a096f9ee2175967d9ec8f6a4100efc/500x500-000000-80-0-0.jpg", "Old Is Gold Vol.1"));
        list.add(new TrackModel(582032592, "https://e-cdns-images.dzcdn.net/images/cover/e305350501acbeb76b17914724261edb/500x500-000000-80-0-0.jpg", "Old Is Gold Vol.2"));
        list.add(new TrackModel(13356289, "https://e-cdns-images.dzcdn.net/images/cover/2eb69ad1fbebf9d7dbc154577a5e7f89/500x500-000000-80-0-0.jpg", "Bollywood Best of 90's"));
        list.add(new TrackModel(14347269, "https://e-cdns-images.dzcdn.net/images/cover/3b99e08348cf8fbc9d185171df447099/500x500-000000-80-0-0.jpg", "Bollywood Mashup Compilation"));
        list.add(new TrackModel(497974381, "https://e-cdns-images.dzcdn.net/images/cover/f78fd15dc4ba0a0d25641fde59cd1ef9/500x500-000000-80-0-0.jpg", "Heeriye x Chaleya x Tu Aake dek le"));
        list.add(new TrackModel(628847411, "https://e-cdns-images.dzcdn.net/images/cover/e55c1086a235d76946e97c5059fe8a96/500x500-000000-80-0-0.jpg", "Bhangra vs Bollywood"));
        return list;
    }

    public ArrayList<TrackModel> getEnglishTracks() {
        ArrayList<TrackModel> list = new ArrayList<>();
        list.add(new TrackModel(569381241, "https://e-cdns-images.dzcdn.net/images/cover/6c86575f01afb68836f47a1319b9930e/500x500-000000-80-0-0.jpg", "This Could Be Texas"));
        list.add(new TrackModel(114657, "https://e-cdns-images.dzcdn.net/images/cover/7c534c80262517f5d4fee6230dd6bfd4/500x500-000000-80-0-0.jpg", "Bad English"));
        list.add(new TrackModel(68804761, "https://e-cdns-images.dzcdn.net/images/cover/e2491c22fb19c154e46b449ff7aa7a62/500x500-000000-80-0-0.jpg", "Ten Summoner's Tales"));
        list.add(new TrackModel(434176887, "https://e-cdns-images.dzcdn.net/images/cover/75d14cfd16a366ae9659aebc983386f2/500x500-000000-80-0-0.jpg", "How Does It Feel"));
        list.add(new TrackModel(352412657, "https://e-cdns-images.dzcdn.net/images/cover/704fc1d3226cbe373dfd6c00345f16f9/500x500-000000-80-0-0.jpg", "Without You I'm Nothing"));
        list.add(new TrackModel(7989512, "https://e-cdns-images.dzcdn.net/images/cover/607edd6e04268585206f8f42ca760de0/500x500-000000-80-0-0.jpg", "5 Seconds Of Summer (Deluxe)"));
        list.add(new TrackModel(648758, "https://e-cdns-images.dzcdn.net/images/cover/e82d8ca32361f3d22946c9f314b35406/500x500-000000-80-0-0.jpg", "Greatest Hits"));
        list.add(new TrackModel(125767, "https://e-cdns-images.dzcdn.net/images/cover/29daf9a4798a4a4de0275446b77683c3/500x500-000000-80-0-0.jpg", "Stars: The Best Of The Cranberries 1992-2002"));
        list.add(new TrackModel(271710, "https://e-cdns-images.dzcdn.net/images/cover/a3dd155f29ff348e636066e38d066b5a/500x500-000000-80-0-0.jpg", "Luther Love Songs"));
        list.add(new TrackModel(326550227, "https://e-cdns-images.dzcdn.net/images/cover/d0e203a3c93216e011eb070d0a57c96c/500x500-000000-80-0-0.jpg", "The Tears of a Clown"));
        return list;
    }

    private void setUpOnBackPressed() {
        HomeScreen.this.getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isEnabled()) {
                    if (!isClickBack) {
                        Toast.makeText(HomeScreen.this, "Tap again to exit!", Toast.LENGTH_SHORT).show();
                        isClickBack = true;
                    } else {
                        finish();
                        setEnabled(false);
                    }
                }
            }
        });
    }
}