package com.example.plaintowerdefense.game;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import com.example.plaintowerdefense.R;

public class BackgroundMusic extends AsyncTask<Void, Void, Void> {
    // 실행할 activity와 음악
    Context context;
    private int resourceNumber;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        MediaPlayer backgroundmusic = MediaPlayer.create(YourActivity.this, );
        backgroundmusic.setVolume(100,100);
        backgroundmusic.setLooping(true);
        backgroundmusic.start();

        return null;
    }

}