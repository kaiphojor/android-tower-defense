package com.example.plaintowerdefense.game;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.plaintowerdefense.R;
import com.example.plaintowerdefense.user_info.StageInfo;
import com.example.plaintowerdefense.user_info.UserInfoSingleton;
import com.example.plaintowerdefense.stage_select.StageSelectActivity;

public class VictoryActivity extends Activity implements View.OnClickListener {
    // view
    RatingBar starRatingBar;
    TextView rewardTextView;
    Button shakeButton;
    Button advertisementButton;
    TextView pressNextTextView;

    int enemyKilled;
    int enemyPassed;
    int enemyTotal;
    int starCount = 3;
    int enemySlainPercentage;
    // 태그 = 현재 activity 이름
    private final String TAG = this.getClass().getSimpleName();
    // context = 현재 context
    private Context context = this;
    Intent intent;
    // 현재 user 정보
    UserInfoSingleton userInfo;
    int stageLevel;
    // 적 종류별 죽인 횟수
    int[] killCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 반투명 처리
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victory);

        // 현재 user 정보 초기화
        userInfo = UserInfoSingleton.getInstance();
        // view binding
        bindView();
        killCount = new int[5];

        // listener
        pressNextTextView.setOnClickListener(this);
        SharedPreferences gamePreference = getSharedPreferences("game",MODE_PRIVATE);
        SharedPreferences.Editor editor = gamePreference.edit();
        try{
            stageLevel = gamePreference.getInt("stageLevel",0);
            // stage 선택 shared preference reset
            editor.putInt("stageLevel",0);
            editor.apply();
        }catch(Exception e){
            e.printStackTrace();
        }
        // intent 받기
        intent = getIntent();
        killCount[0] = intent.getIntExtra("enemy0",-1);
        killCount[1] = intent.getIntExtra("enemy1",-1);
        killCount[2] = intent.getIntExtra("enemy2",-1);
        killCount[3] = intent.getIntExtra("enemy3",-1);
        killCount[4] = intent.getIntExtra("enemy4",-1);
        enemyKilled = intent.getIntExtra("killed",0);
        enemyPassed = intent.getIntExtra("passed",0);
        // 죽인 퍼센테이지에 따라서 별 등급 부여
        enemyTotal = enemyKilled + enemyPassed;
        if(enemyTotal != 0){
            enemySlainPercentage = 100 * enemyKilled / enemyTotal;
        }
        if (enemySlainPercentage < 50) {
            starCount = 0;
        } else if (enemySlainPercentage < 75) {
            starCount = 1;
        } else if (enemySlainPercentage < 100) {
            starCount = 2;
        } else {
            starCount = 3;
        }
        // 승리한 후 stage 정보 갱신
        if(stageLevel != 0){
            StageInfo stageuserInfo = userInfo.getStageInfo(stageLevel);
            stageuserInfo.setClear(true);
            // 별을 이전 보다 더 획득했다면 기록 갱신
            if(starCount > stageuserInfo.getStarNumber()){
                stageuserInfo.setStarNumber(starCount);
            }
            userInfo.setStageInfo(context,stageLevel,stageuserInfo);
        }
        // 죽인 적 점수 계산
        int reward = 0;
        userInfo.setEnemyKilled(context,killCount);
        for(int i=0; i<killCount.length; i++){
            if(killCount[i]!=-1){
                reward += killCount[i] * (i+1);
            }
        }
        // 애니메이션 설정 및 실행
        setUpFadeAnimation(pressNextTextView);
        startCountAnimation(rewardTextView,0,reward);
        startStarCountAnimation(starRatingBar);



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

    // click listener 구현 부분
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            // 스테이지 선택으로 돌아간다
            case R.id.finish_bt_victory :
                intent = new Intent(context, StageSelectActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            default :
                break;
        }
    }
    // fade in and out 애니메이션 효과
    private void setUpFadeAnimation(final TextView textView) {
        // Start from 0.1f if you desire 90% fade animation
        final Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        // fade in 시간
        fadeIn.setDuration(1000);
        // 시작시간
        fadeIn.setStartOffset(0000);
        // End to 0.1f if you desire 90% fade animation
        final Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(1000);
        fadeOut.setStartOffset(0000);

        fadeIn.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationEnd(Animation arg0) {
                // start fadeOut when fadeIn ends (continue)
                textView.startAnimation(fadeOut);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });

        fadeOut.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationEnd(Animation arg0) {
                // start fadeIn when fadeOut ends (repeat)
                textView.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });

        textView.startAnimation(fadeOut);
    }
    // number counter 효과
    private void startCountAnimation(final TextView textView,int start,int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end); //0 is min number, 600 is max number
        // 스르륵 흐르는 애니메이션 시간
        animator.setDuration(500); //Duration is in milliseconds
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                textView.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.start();
    }
    // star counter 효과
    private void startStarCountAnimation(final RatingBar ratingBar) {
        ValueAnimator animator = ValueAnimator.ofInt(0, 500); //0 is min number, 600 is max number
        // 스르륵 흐르는 애니메이션 시간
        animator.setDuration(1000); //Duration is in milliseconds
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                int number = Integer.parseInt(animation.getAnimatedValue().toString());
//                int starNumber = starCount * number / 500;
                ratingBar.setRating(starCount * number / 500);
//                ratingBar.setRating(starCount);
            }
        });
        animator.start();
    }

    @Override
    public void onBackPressed() {
        // back button 에서 행동이 못나오게 한다
    }
}
