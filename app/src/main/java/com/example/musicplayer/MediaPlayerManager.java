package com.example.musicplayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public class MediaPlayerManager {

    private static MediaPlayerManager instance;
    private MediaPlayer mediaPlayer;
    private Uri currentSongResId;

    private MediaPlayerManager() {
        // Private constructor to prevent direct instantiation
    }

    public static MediaPlayerManager getInstance() {
        if (instance == null) {
            instance = new MediaPlayerManager();
        }
        return instance;
    }

    public void play(Context context, Uri uri) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying() && currentSongResId == uri) {
                // If the same song is playing, do nothing
                return;
            } else {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }

        mediaPlayer = MediaPlayer.create(context, uri);
        currentSongResId = uri;
        mediaPlayer.start();
    }

    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            currentSongResId = null;
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public Uri getCurrentSongResId() {
        return currentSongResId;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}

