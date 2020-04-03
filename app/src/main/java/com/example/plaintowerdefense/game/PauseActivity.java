package com.example.plaintowerdefense.game;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.plaintowerdefense.MainActivity;
import com.example.plaintowerdefense.R;

public class PauseActivity extends Activity implements View.OnClickListener {
    Button homeButton;
    Button resumeButton;
    Button muteButton;

    Intent intent;

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

        homeButton = findViewById(R.id.home_bt_pause);
        resumeButton = findViewById(R.id.resume_bt_pause);
        muteButton = findViewById(R.id.mute_bt_pause);
        homeButton.setOnClickListener(this);
        resumeButton.setOnClickListener(this);
        muteButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.home_bt_pause :
                // main activity로 돌아감
                intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.resume_bt_pause :
                onBackPressed();
                break;
            case R.id.mute_bt_pause :
                // 음량 조절
                break;
        }

    }
}
