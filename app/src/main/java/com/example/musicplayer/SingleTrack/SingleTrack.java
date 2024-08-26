package com.example.musicplayer.SingleTrack;

import static com.example.musicplayer.Urls.Urls.ALBUM_BY_ID;
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
import com.example.musicplayer.Adaptors.HomeTrackAdapter;
import com.example.musicplayer.ArtistList.ArtistsSongs;
import com.example.musicplayer.Model.AlbumModel.AlbumModel;
import com.example.musicplayer.Model.ArtistsTrackModel;
import com.example.musicplayer.Model.SearchModel.SearchResultModel;
import com.example.musicplayer.Model.TrackModel;
import com.example.musicplayer.R;
import com.example.musicplayer.databinding.ActivitySingleTrackBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SingleTrack extends AppCompatActivity {
    ActivitySingleTrackBinding binding;
    long id;
    String title, imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivitySingleTrackBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        id = getIntent().getLongExtra("id", 0);
        title = getIntent().getStringExtra("title");
        imageUri = getIntent().getStringExtra("image");

        Glide.with(this).load(imageUri).into(binding.trackImgSingleTrack);
        binding.titleSingleTrack.setText(title);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getDrawable(R.drawable.custom_divider));
        binding.tracksRecyclerviewSingleTracks.setLayoutManager(linearLayoutManager);
        binding.tracksRecyclerviewSingleTracks.setHasFixedSize(true);
        binding.tracksRecyclerviewSingleTracks.addItemDecoration(itemDecoration);
        getAlbumData(id);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.moreTracksByArtistRecyclerview.setLayoutManager(linearLayoutManager1);
        binding.moreTracksByArtistRecyclerview.setHasFixedSize(true);


        binding.backFromSingleTracks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void getAlbumData(long albumId) {
        binding.tracksLoadingProgressbarSingleTracks.setVisibility(View.VISIBLE);
        System.out.println("ALBUM_ID" + albumId);
        ArrayList<ArtistsTrackModel> list = new ArrayList<>();
        String url = ALBUM_BY_ID + albumId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                System.out.println(response);
                Gson gson = new GsonBuilder().setLenient().create();
                AlbumModel albumModel = gson.fromJson(response, AlbumModel.class);
                try {
                    if (albumModel != null) {
                        if (albumModel.getTracks().getData() != null) {
                            binding.tracksLoadingProgressbarSingleTracks.setVisibility(View.GONE);
                            for (int i = 0; i < albumModel.getTracks().getData().size(); i++) {
                                if (i == 0) {
                                    getSearchData(albumModel.getArtist().getName());
                                }
//                                list.add(new ArtistsTrackModel(albumModel.getTracks().getData().get(i).getAlbum().getId(), albumModel.getTracks().getData().get(i).getTitle(), albumModel.getCoverMedium()));
                                list.add(new ArtistsTrackModel(albumModel.getTracks().getData().get(i).getAlbum().getId(), albumModel.getTracks().getData().get(i).getTitle(), albumModel.getTracks().getData().get(i).getAlbum().getCoverMedium(),albumModel.getTracks().getData().get(i).getId(),albumModel.getTracks().getData().get(i).getArtist().getName()));
                            }
                            ArtistsTracksAdapter artistsTracksAdapter = new ArtistsTracksAdapter(SingleTrack.this, list);
                            binding.tracksRecyclerviewSingleTracks.setAdapter(artistsTracksAdapter);
                        }else{
                            binding.tracksLoadingProgressbarSingleTracks.setVisibility(View.GONE);
                        }
                    }else{
                        binding.tracksLoadingProgressbarSingleTracks.setVisibility(View.GONE);
                    }
                } catch (Exception e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.tracksLoadingProgressbarSingleTracks.setVisibility(View.GONE);
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

    void getSearchData(String artisName) {
        binding.recommendedProgressbarSingleTracks.setVisibility(View.VISIBLE);
        ArrayList<TrackModel> list = new ArrayList<>();
        String url = SEARCH_RESULT + artisName;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                Gson gson = new GsonBuilder().setLenient().create();
                SearchResultModel searchResultModel = gson.fromJson(response, SearchResultModel.class);
                if (searchResultModel != null) {
                    binding.recommendedProgressbarSingleTracks.setVisibility(View.GONE);
                    for (int i = 1; i < searchResultModel.getData().size(); i++) {
                        list.add(new TrackModel(searchResultModel.getData().get(i).getAlbum().getId(), searchResultModel.getData().get(i).getAlbum().getCoverMedium().toString(), searchResultModel.getData().get(i).getTitleShort()));
                    }

                    HomeTrackAdapter trackAdapter = new HomeTrackAdapter(list, SingleTrack.this);
                    binding.moreTracksByArtistRecyclerview.setAdapter(trackAdapter);
                }else{
                    binding.recommendedProgressbarSingleTracks.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR " + error.toString());
                binding.recommendedProgressbarSingleTracks.setVisibility(View.GONE);
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