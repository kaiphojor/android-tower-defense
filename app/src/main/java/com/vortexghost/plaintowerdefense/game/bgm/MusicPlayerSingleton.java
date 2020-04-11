package com.vortexghost.plaintowerdefense.game.bgm;

import android.media.MediaPlayer;

public class MusicPlayerSingleton {
    private static MediaPlayer player = null;

    public static MediaPlayer getInstance(){
        if(player == null){
            player = new MediaPlayer();
            return player;
        } else{
            return player;
        }
    }
}
