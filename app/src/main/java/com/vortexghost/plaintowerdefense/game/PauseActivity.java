package com.vortexghost.plaintowerdefense.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.vortexghost.plaintowerdefense.MainActivity;
import com.vortexghost.plaintowerdefense.R;
import com.vortexghost.plaintowerdefense.sound.SoundSingleton;

import pl.droidsonroids.gif.GifImageView;

public class PauseActivity extends Activity implements View.OnClickListener {
    GifImageView homeButton;
    GifImageView resumeButton;

    Intent intent;
    GifImageView bgmMuteView;

    // 태그 = 현재 activity 이름
    private final String TAG = this.getClass().getSimpleName();
    // context = 현재 context
    private Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 반투명 처리
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pause);

        homeButton = findViewById(R.id.home_gif_pause);
        resumeButton = findViewById(R.id.resume_gif_pause);
        bgmMuteView = findViewById(R.id.mute_gif_pause);
        homeButton.setOnClickListener(this);
        resumeButton.setOnClickListener(this);
        bgmMuteView.setOnClickListener(this);

        // sound 설정 가져오기
        SoundSingleton.initSoundSingleton(context);
        boolean isBgmMute = SoundSingleton.isBgmMute();
        // 음소거면 음소거 그림, 아니면 그냥 그림
        if(isBgmMute){
            bgmMuteView.setImageResource(R.drawable.audio_mute);
        }else{
            bgmMuteView.setImageResource(R.drawable.audio_play);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.home_gif_pause:
                // main activity로 돌아감
                intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.resume_gif_pause :
                onBackPressed();
                break;
            case R.id.mute_gif_pause :
                // 음량 조절
                boolean isMute = SoundSingleton.isBgmMute();
                if(isMute){
                    bgmMuteView.setImageResource(R.drawable.audio_play);
                }else{
                    bgmMuteView.setImageResource(R.drawable.audio_mute);
                }
                SoundSingleton.setBgmMute(!isMute);
                SoundSingleton.updateSoundSingleton(context);
                // 사진 교체
                break;
        }

    }
}
