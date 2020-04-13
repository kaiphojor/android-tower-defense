package com.vortexghost.plaintowerdefense.game.game_result;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.vortexghost.plaintowerdefense.R;
import com.vortexghost.plaintowerdefense.game.ShakeDetector;
import com.vortexghost.plaintowerdefense.user_info.StageInfo;
import com.vortexghost.plaintowerdefense.user_info.UserInfoSingleton;
import com.vortexghost.plaintowerdefense.stage_select.StageSelectActivity;

public class VictoryActivity extends Activity implements View.OnClickListener {
    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    // 진동감지 관련한 view, 변수
    private static int shakeCount;
    private final String shakeCountString = "흔든횟수 : ";
    private TextView shakeCountTextView;
    private Button shakeStartSButton;
    // 진동발생 관련 view, 변수
    private static TextView shakeTimeLeftTextView;
    private final int vibratileSecond = 10;
    private static int remainingSecond;
    private static boolean isShakeAvailable;
    private final String shakeTimeLeftString = "초";

    // view
    RatingBar starRatingBar;
    static TextView rewardTextView;
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
    static int reward;
    // 진동발생하는 vibrator
    Vibrator vibrator;

    private final static Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            msg.what -= 1;
//            remainingSecond--;
            shakeTimeLeftTextView.setText(msg.what+"초");
            if(msg.what > 0){
                // 메세지를 처리하고 또다시 핸들러에 메세지 전달 (1000ms 지연)
                mHandler.sendEmptyMessageDelayed(msg.what,1000);
            }else{
                isShakeAvailable = false;
                if(shakeCount >= 15){
                    reward = reward *2;
                    setShakeResult(true);
                    startCountAnimation(rewardTextView,0,reward);
                }else{
                    setShakeResult(false);
                }


            }
        }
    };

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
        shakeButton.setOnClickListener(this);
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
        reward = 0;
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

        initShakeDetector();

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
        shakeTimeLeftTextView = findViewById(R.id.time_left_tv_victory);
        starRatingBar = findViewById(R.id.star_rb_victory);
        rewardTextView = findViewById(R.id.reward_tv_victory);
        shakeButton = findViewById(R.id.shake_bonus_bt_victory);
        shakeStartSButton = shakeButton;
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
                // 점수 저장
                UserInfoSingleton userInfoSingleton = UserInfoSingleton.getInstance();
                userInfoSingleton.setReward(reward);
                intent = new Intent(context, StageSelectActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.shake_bonus_bt_victory :
                // 진동을 감지하고, 진동을 낼 수 있도록 만든다
                isShakeAvailable = true;
                // 시작할 때 버튼을 안보이게 만든다
                shakeStartSButton.setVisibility(View.GONE);
                shakeTimeLeftTextView.setVisibility(View.VISIBLE);
                // 앱 시작과 동시에 핸들러에 메세지 전달
                mHandler.sendEmptyMessage(vibratileSecond);
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
    private static void startCountAnimation(final TextView textView,int start,int end) {
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
    public void initShakeDetector(){
        shakeCount = 0;
        remainingSecond = 5;
        // 진동기 초기화 - 시스템 서비스 호출
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                /*
                 * The following method, "handleShakeEvent(count):" is a stub //
                 * method you would use to setup whatever you want done once the
                 * device has been shook.
                 */
//                Log.i(TAG,"on shake called : " + isShakeAvailable);
                if (isShakeAvailable) {
                    handleShakeEvent(count);
                }
            }
        });
    }
    // 진동 발생 및 카운터 증가
    public void handleShakeEvent(int count) {
        shakeCount++;
        vibrator.vibrate(400); // 0.5초간 진동
//        Log.i(TAG, "shake event 확인");
//        Toast.makeText(context, "event : " + shakeCount, Toast.LENGTH_SHORT).show();
//        shakeCountTextView.setText(shakeCountString + shakeCount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 가속도계 센서를 등록한다.
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }
    // background 상황에서도 흔들림을 감지하고 적용할 필요는 없다
    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        // 가속도계 센서를 해지한다
        mSensorManager.unregisterListener(mShakeDetector);
        if (shakeCount != 0){

        }
        super.onPause();
        Log.i(TAG,"onPause called");
    }
    public static void setShakeResult(final boolean isShakeSuccess){
        if(isShakeSuccess){
            shakeTimeLeftTextView.setText(shakeCount+"회, 성공!");
        }else{
            shakeTimeLeftTextView.setText(shakeCount+"회, 실패!");
        }

    }
}
