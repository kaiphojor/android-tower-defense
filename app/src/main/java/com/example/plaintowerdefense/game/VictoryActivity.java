package com.example.plaintowerdefense.game;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.plaintowerdefense.R;

public class VictoryActivity extends Activity {
    // view
    RatingBar starRatingBar;
    TextView rewardTextView;
    Button shakeButton;
    Button advertisementButton;
    TextView pressNextTextView;


    // 태그 = 현재 activity 이름
    private final String TAG = this.getClass().getSimpleName();
    // context = 현재 context
    private Context context = this;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 반투명 처리
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victory);

        bindView();
//        LottieAnimationView video = findViewById(R.id.video_anim_victory);
//        video.setAnimation("video_anim.json");
//        video.playAnimation();
//        video.addAnimatorListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
    }
    // view binding
    public void bindView(){
        starRatingBar = findViewById(R.id.star_rb_victory);
        rewardTextView = findViewById(R.id.reward_tv_victory);
        shakeButton = findViewById(R.id.shake_bonus_bt_victory);
        advertisementButton = findViewById(R.id.advertisement_bonus_bt_victory);
        pressNextTextView = findViewById(R.id.finish_bt_victory);
    }
}
