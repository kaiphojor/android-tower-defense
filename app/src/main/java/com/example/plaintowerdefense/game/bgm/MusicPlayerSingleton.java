package com.example.plaintowerdefense.game.bgm;

import android.media.MediaPlayer;

public class bgmPlayerSingleton {
    private static MediaPlayer mp = null;

    public static MediaPlayer getMusicPlayerIns(){
        if(mp == null){
            mp = new MediaPlayer();
            return mp;
        } else{
            return mp;
        }
    }
}
