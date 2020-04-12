package com.vortexghost.plaintowerdefense.game.game_result;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.vortexghost.plaintowerdefense.MainActivity;
import com.vortexghost.plaintowerdefense.R;
import com.vortexghost.plaintowerdefense.Singleton;

public class DefeatActivity extends Activity implements View.OnClickListener{
    Button gemRetryButton;
    Button advertisementRetryButton;
    static TextView timerTextView;
    static String timerString;
    static boolean canBackToMenu;
    // 태그 = 현재 activity 이름
    private final String TAG = this.getClass().getSimpleName();
    // context = 현재 context
    private Context context = this;
    Intent intent;
    // 보상형 광고 객체
    private RewardedAd rewardedAd;
    // 보상형광고 load 시 콜백
    RewardedAdLoadCallback adLoadCallback;
    private final static Handler mHandler = new Handler() {
        public void handleMessage(Message msg){
            timerString = msg.what/1000+"초 후 메뉴 이동";
            int dotNumber = 10 - (msg.what % 1000)/100;
            for(int i=0; i<dotNumber;i++){
                timerString += ".";
            }
            msg.what -= 100;
            timerTextView.setText(timerString);
            if(msg.what > 1000){
                // 메세지를 처리하고 또다시 핸들러에 메세지 전달 (1000ms 지연)
                mHandler.sendEmptyMessageDelayed(msg.what,100);
            }else{
//                canBackToMenu = true;
            }
        }
    };
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String timerText = timerTextView.getText().toString();
            if(timerText.contentEquals("1초 후 메뉴 이동.........")){
                intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 반투명 처리
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defeat);

        timerTextView = findViewById(R.id.timer_tv_defeat);
//        timerTextView;
        timerTextView.addTextChangedListener(textWatcher);
        advertisementRetryButton = findViewById(R.id.advertisement_retry_bt_defeat);
        gemRetryButton = findViewById(R.id.gem_retry_bt_defeat);
        advertisementRetryButton.setOnClickListener(this);
        gemRetryButton.setOnClickListener(this);

        // 모바일 광고 초기화
//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });
        // 보상형광고 객체 초기화 및 광고 load callback 초기화
        rewardedAd = new RewardedAd(this,
                "ca-app-pub-3940256099942544/5224354917");

        adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
                Singleton.getInstance(context);
                Singleton.toast("load complete",false);
                // 앱 시작과 동시에 핸들러에 메세지 전달
                mHandler.sendEmptyMessage(5000);
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
                // 앱 시작과 동시에 핸들러에 메세지 전달
                mHandler.sendEmptyMessage(5000);
            }
        };

        // 광고 로드
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
    }

    @Override
    public void onBackPressed() {
        // back button 에서 행동이 못나오게 한다
    }
    public void backToMenu(){

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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.advertisement_retry_bt_defeat :
                if (rewardedAd.isLoaded()) {
                    mHandler.removeCallbacksAndMessages(null);
                    RewardedAdCallback adCallback = new RewardedAdCallback() {
                        public void onRewardedAdOpened() {
                            // Ad opened.
                        }

                        public void onRewardedAdClosed() {
                            // Ad closed.
                            intent = new Intent(context, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }

                        public void onUserEarnedReward(@NonNull RewardItem reward) {
                            // User earned reward.
                            // 50 gold를 더 얻고 재시작. 재시작 표시 하기
                        }

                        public void onRewardedAdFailedToShow(int errorCode) {
                            // Ad failed to display
                            intent = new Intent(context, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    };
                    rewardedAd.show(((Activity)context) , adCallback);
                } else {
                    Log.d("TAG", "The rewarded ad wasn't loaded yet.");
                }
                break;
            case R.id.gem_retry_bt_defeat :
                mHandler.removeCallbacksAndMessages(null);
                break;
        }
    }
}
