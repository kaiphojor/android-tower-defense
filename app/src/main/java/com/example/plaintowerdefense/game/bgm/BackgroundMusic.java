package com.example.plaintowerdefense.game.bgm;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.plaintowerdefense.R;

public class BackgroundMusic extends AsyncTask<MusicContext, Void, Void> {
    // 실행할 activity와 음악
    Context context;
    private int resourceNumber;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    // 백그라운드에서 작업(다른 thread에서 처리)
    @Override
    protected Void doInBackground(MusicContext... musicContexts) {
        MediaPlayer musicPlayer = MusicPlayerSingleton.getInstance();
        MusicContext musicContext = musicContexts[0];
        // 음악 세팅 및 실행
//        musicPlayer.set
        musicPlayer.setAudioSessionId(R.raw.skull_fire);
        musicPlayer = MediaPlayer.create((Activity)(musicContext.getContext()),musicContext.getResourceId());
        musicPlayer.setVolume(100,100);
        musicPlayer.setLooping(musicContext.isLooping());
        musicPlayer.start();
        return null;
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
//        musicPlayer.reset();
//        musicPlayer.stop();
//        musicPlayer = null;
    }
}