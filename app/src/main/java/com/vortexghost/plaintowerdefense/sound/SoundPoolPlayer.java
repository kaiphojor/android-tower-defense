package com.vortexghost.plaintowerdefense.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.vortexghost.plaintowerdefense.R;

import java.util.HashMap;

public class SoundPoolPlayer {
    private SoundPool mShortPlayer= null;
    private HashMap mSounds = new HashMap();

    public SoundPoolPlayer(Context pContext)
    {
        // setup Soundpool
        this.mShortPlayer = new SoundPool(40, AudioManager.STREAM_MUSIC, 0);


        mSounds.put(R.raw.laser1, this.mShortPlayer.load(pContext, R.raw.laser1, 1));
        mSounds.put(R.raw.laser2, this.mShortPlayer.load(pContext, R.raw.laser2, 1));
        mSounds.put(R.raw.laser3, this.mShortPlayer.load(pContext, R.raw.laser3, 1));
        mSounds.put(R.raw.laser4, this.mShortPlayer.load(pContext, R.raw.laser4, 1));
        mSounds.put(R.raw.laser5, this.mShortPlayer.load(pContext, R.raw.laser5, 1));
        mSounds.put(R.raw.laser6, this.mShortPlayer.load(pContext, R.raw.laser6, 1));
        mSounds.put(R.raw.laser7, this.mShortPlayer.load(pContext, R.raw.laser7, 1));
        mSounds.put(R.raw.laser8, this.mShortPlayer.load(pContext, R.raw.laser8, 1));
        mSounds.put(R.raw.laser9, this.mShortPlayer.load(pContext, R.raw.laser9, 1));
        mSounds.put(R.raw.laser10, this.mShortPlayer.load(pContext, R.raw.laser10, 1));
        mSounds.put(R.raw.laser11, this.mShortPlayer.load(pContext, R.raw.laser11, 1));
        mSounds.put(R.raw.laser12, this.mShortPlayer.load(pContext, R.raw.laser12, 1));
        mSounds.put(R.raw.laser13, this.mShortPlayer.load(pContext, R.raw.laser13, 1));
    }

    public void playShortResource(int piResource,final float volume) {
        int iSoundId = (Integer) mSounds.get(piResource);
        this.mShortPlayer.play(iSoundId, volume, volume, 0, 0, 1);
    }

    // Cleanup
    public void release() {
        // Cleanup
        this.mShortPlayer.release();
        this.mShortPlayer = null;
    }
}