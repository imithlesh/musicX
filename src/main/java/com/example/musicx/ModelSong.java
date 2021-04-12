package com.example.musicx;

import android.net.Uri;

public class ModelSong {
    String SongName;
       double duration;
        Uri audioUri;
    public ModelSong(String songName, double duration, Uri audioUri) {
        SongName = songName;
        this.duration = duration;
        this.audioUri = audioUri;
    }

    public Uri getAudioUri() {
        return audioUri;
    }

    public void setAudioUri(Uri audioUri) {
        this.audioUri = audioUri;
    }

    public String getSongName() {
        return SongName;
    }

    public void setSongName(String songName) {
        SongName = songName;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }
}
