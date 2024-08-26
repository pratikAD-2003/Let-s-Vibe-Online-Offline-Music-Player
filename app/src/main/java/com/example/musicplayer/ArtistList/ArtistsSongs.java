package com.example.musicplayer.ArtistList;

import static com.example.musicplayer.Urls.Urls.ALBUM_BY_ID;
import static com.example.musicplayer.Urls.Urls.ARTIST_BY_ID;
import static com.example.musicplayer.Urls.Urls.SEARCH_RESULT;
import static com.example.musicplayer.Urls.Urls.TOKEN_DEEZER;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

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
import com.example.musicplayer.Model.AlbumModel.AlbumModel;
import com.example.musicplayer.Model.ArtistModel.ArtistsModel;
import com.example.musicplayer.Model.ArtistsTrackModel;
import com.example.musicplayer.Model.SearchModel.SearchResultModel;
import com.example.musicplayer.Model.TrackModel;
import com.example.musicplayer.R;
import com.example.musicplayer.databinding.ActivityArtistsSongsBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ArtistsSongs extends AppCompatActivity {
    ActivityArtistsSongsBinding binding;
    String coverImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityArtistsSongsBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getDrawable(R.drawable.custom_divider));
        binding.artistsTracksRecylcerview.setLayoutManager(linearLayoutManager);
        binding.artistsTracksRecylcerview.addItemDecoration(itemDecoration);
        binding.artistsTracksRecylcerview.setHasFixedSize(true);
        long id = getIntent().getLongExtra("id", 0);
        String name = getIntent().getStringExtra("artistName");
        getSearchData(name);
        getArtistById(id);

        binding.backFromArtistSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void getSearchData(String artisName) {
        ArrayList<ArtistsTrackModel> list = new ArrayList<>();
        String url = SEARCH_RESULT + artisName;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                Gson gson = new GsonBuilder().setLenient().create();
                SearchResultModel searchResultModel = gson.fromJson(response, SearchResultModel.class);
                if (searchResultModel != null) {
                    for (int i = 0; i < searchResultModel.getData().size(); i++) {
                        if (i == 0) {
                            coverImage = searchResultModel.getData().get(0).getAlbum().getCoverMedium();
                            Glide.with(ArtistsSongs.this).load(coverImage).into(binding.bigPicItem);
                        }
                        list.add(new ArtistsTrackModel(searchResultModel.getData().get(i).getAlbum().getId(), searchResultModel.getData().get(i).getTitleShort(), searchResultModel.getData().get(i).getAlbum().getCoverMedium(),searchResultModel.getData().get(i).getId()));
                    }

                    ArtistsTracksAdapter artistsTracksAdapter = new ArtistsTracksAdapter(ArtistsSongs.this, list);
                    binding.artistsTracksRecylcerview.setAdapter(artistsTracksAdapter);
                }
                System.out.println("SEARCH_RESULT_DATA" + searchResultModel.getData().get(0).getTitleShort());
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

    void getArtistById(long id) {
        ArrayList<ArtistsTrackModel> list = new ArrayList<>();
        String url = ARTIST_BY_ID + id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                Gson gson = new GsonBuilder().setLenient().create();
                ArtistsModel artistsModel = gson.fromJson(response, ArtistsModel.class);
                if (artistsModel != null) {
                    binding.artisNameItem.setText(artistsModel.getName());
                    Glide.with(ArtistsSongs.this).load(artistsModel.getPictureMedium()).into(binding.artisImgItems);
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
}