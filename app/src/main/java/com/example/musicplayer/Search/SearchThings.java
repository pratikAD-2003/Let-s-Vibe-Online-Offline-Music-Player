package com.example.musicplayer.Search;

import static com.example.musicplayer.Urls.Urls.SEARCH_RESULT;
import static com.example.musicplayer.Urls.Urls.TOKEN_DEEZER;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.musicplayer.Adaptors.ArtistsTracksAdapter;
import com.example.musicplayer.Adaptors.HomeTrackAdapter;
import com.example.musicplayer.Model.ArtistsTrackModel;
import com.example.musicplayer.Model.SearchModel.SearchResultModel;
import com.example.musicplayer.Model.TrackModel;
import com.example.musicplayer.R;
import com.example.musicplayer.SingleTrack.SingleTrack;
import com.example.musicplayer.databinding.ActivitySearchThingsBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchThings extends AppCompatActivity {
    ActivitySearchThingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivitySearchThingsBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        initializeRecyclerview();

        binding.searchBoxSearchThings.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    getSearchData(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.backFromSearchThings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initializeRecyclerview() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getDrawable(R.drawable.custom_divider));
        binding.searchItemsRecyclerview.setLayoutManager(linearLayoutManager);
        binding.searchItemsRecyclerview.setHasFixedSize(true);
        binding.searchItemsRecyclerview.addItemDecoration(itemDecoration);
    }


    void getSearchData(String artisName) {
        binding.searchItemsProgressbarSearchThings.setVisibility(View.VISIBLE);
        binding.notItemFoundSearchThings.setVisibility(View.GONE);
        binding.searchItemsCountSearchThings.setVisibility(View.VISIBLE);
        ArrayList<ArtistsTrackModel> list = new ArrayList<>();
        String url = SEARCH_RESULT + artisName;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                Gson gson = new GsonBuilder().setLenient().create();
                SearchResultModel searchResultModel = gson.fromJson(response, SearchResultModel.class);
                if (searchResultModel != null) {
                    binding.searchItemsProgressbarSearchThings.setVisibility(View.GONE);
                    binding.notItemFoundSearchThings.setVisibility(View.GONE);
                    binding.searchItemsCountSearchThings.setVisibility(View.VISIBLE);
                    for (int i = 0; i < searchResultModel.getData().size(); i++) {
                        list.add(new ArtistsTrackModel(searchResultModel.getData().get(i).getAlbum().getId(), searchResultModel.getData().get(i).getTitleShort(), searchResultModel.getData().get(i).getAlbum().getCoverBig().toString(),searchResultModel.getData().get(i).getId(),searchResultModel.getData().get(i).getArtist().getName()));
                    }
                    binding.searchItemsCountSearchThings.setText("Searched items found " + list.size());
                    ArtistsTracksAdapter artistsTracksAdapter = new ArtistsTracksAdapter(SearchThings.this, list);
                    binding.searchItemsRecyclerview.setAdapter(artistsTracksAdapter);
                } else {
                    binding.searchItemsProgressbarSearchThings.setVisibility(View.GONE);
                    binding.notItemFoundSearchThings.setVisibility(View.VISIBLE);
                    binding.searchItemsCountSearchThings.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR " + error.toString());
                binding.searchItemsProgressbarSearchThings.setVisibility(View.GONE);
                binding.notItemFoundSearchThings.setVisibility(View.VISIBLE);
                binding.searchItemsCountSearchThings.setVisibility(View.GONE);
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