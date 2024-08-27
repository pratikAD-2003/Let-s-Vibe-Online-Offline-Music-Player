package com.example.musicplayer.PlayTrack;

import static com.example.musicplayer.Urls.Urls.ALBUM_BY_ID;
import static com.example.musicplayer.Urls.Urls.TOKEN_DEEZER;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.musicplayer.Adaptors.ArtistsTracksAdapter;
import com.example.musicplayer.Downloaded.DownAdapter;
import com.example.musicplayer.Downloaded.DownTrackModel;
import com.example.musicplayer.Favorite.FavoriteTrackAdapter;
import com.example.musicplayer.Model.AlbumModel.AlbumModel;
import com.example.musicplayer.Model.ArtistsTrackModel;
import com.example.musicplayer.PlayTrack.adaptors.RelatedTracksAdapter;
import com.example.musicplayer.R;
import com.example.musicplayer.SingleTrack.SingleTrack;
import com.example.musicplayer.databinding.FragmentShowRelatedTracksDialogBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ShowRelatedTracksDialog extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    FragmentShowRelatedTracksDialogBinding binding;
    long albumId = 0;
    String type = "";
    ArrayList<DownTrackModel> list;

    public ShowRelatedTracksDialog() {
        // Required empty public constructor
    }

    public static ShowRelatedTracksDialog newInstance(String param1, String param2) {
        ShowRelatedTracksDialog fragment = new ShowRelatedTracksDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentShowRelatedTracksDialogBinding.inflate(inflater, container, false);
        list = new ArrayList<>();
        Gson gson = new Gson();
        // Inflate the layout for this fragment
        assert getArguments() != null;
        type = getArguments().getString("type");


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getActivity().getDrawable(R.drawable.custom_divider));
        binding.relatedTracksRecyclerviewSRTD.setLayoutManager(linearLayoutManager);
        binding.relatedTracksRecyclerviewSRTD.setHasFixedSize(true);
        binding.relatedTracksRecyclerviewSRTD.addItemDecoration(itemDecoration);
        if (Objects.equals(type, "downloaded")) {
            String myList = getArguments().getString("list");
            Type listType = new TypeToken<ArrayList<DownTrackModel>>() {
            }.getType();
            list = gson.fromJson(myList, listType);
            DownAdapter downAdapter = new DownAdapter(getContext(), list, new FavoriteTrackAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(ArtistsTrackModel item) {
                    dismiss();
                }
            });
            binding.relatedTracksRecyclerviewSRTD.setAdapter(downAdapter);
        } else if (Objects.equals(type, "online")) {
            albumId = getArguments().getLong("albumId");
            getAlbumData(albumId);
        }

        return binding.getRoot();
    }

    void getAlbumData(long albumId) {
        binding.relatedTracksProgressbarSRTD.setVisibility(View.VISIBLE);
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
                            binding.relatedTracksProgressbarSRTD.setVisibility(View.GONE);
                            for (int i = 0; i < albumModel.getTracks().getData().size(); i++) {
                                list.add(new ArtistsTrackModel(albumModel.getTracks().getData().get(i).getAlbum().getId(), albumModel.getTracks().getData().get(i).getTitle(), albumModel.getTracks().getData().get(i).getAlbum().getCoverMedium(), albumModel.getTracks().getData().get(i).getId(), albumModel.getTracks().getData().get(i).getArtist().getName()));
                            }
                            RelatedTracksAdapter artistsTracksAdapter = new RelatedTracksAdapter(getContext(), list, getActivity());
                            binding.relatedTracksRecyclerviewSRTD.setAdapter(artistsTracksAdapter);
                        } else {
                            binding.relatedTracksProgressbarSRTD.setVisibility(View.GONE);
                        }
                    } else {
                        binding.relatedTracksProgressbarSRTD.setVisibility(View.GONE);
                    }
                } catch (Exception e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.relatedTracksProgressbarSRTD.setVisibility(View.GONE);
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
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }
}