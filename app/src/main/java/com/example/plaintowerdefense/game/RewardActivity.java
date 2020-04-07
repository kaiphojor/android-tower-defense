package com.example.plaintowerdefense.game;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.plaintowerdefense.error_collect.BaseActivity;
import com.example.plaintowerdefense.R;


public class RewardActivity extends BaseActivity implements View.OnClickListener {

    private Button changeActivityButton;

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    // 진동감지 관련한 view, 변수
    private int shakeCount;
    private final String shakeCountString = "흔든횟수 : ";
    private TextView shakeCountTextView;
    private Button shakeStartSButton;
    // 진동발생 관련 view, 변수
    private static TextView shakeTimeLeftTextView;
    private final int vibratileSecond = 10;
    private static int remainingSecond;
    private static boolean isShakeAvailable;
    private final String shakeTimeLeftString = "초";

    // 진동발생하는 vibrator
    Vibrator vibrator;

    private final static Handler mHandler = new Handler() {
        public void handleMessage(Message msg){
            msg.what -= 1;
//            remainingSecond--;
            shakeTimeLeftTextView.setText(msg.what+"초");
            if(msg.what > 0){
                // 메세지를 처리하고 또다시 핸들러에 메세지 전달 (1000ms 지연)
                mHandler.sendEmptyMessageDelayed(msg.what,1000);
            }else{
                isShakeAvailable = false;
            }
        }
    };
    /*
    0. 버튼을 누르면 시간 제한이 시작된다 - 서비스
    1. 흔든다
    2. 흔들림을 감지한다
        broadcast receiver
    3-1 흔들림 감지시 진동한다
        진동기능
    3-2 흔들림 감지시 숫자 증가(만보기 식)
     */

    // 전 activity 공통 변수
    // 태그 = 현재 activity 이름
    private final String TAG = this.getClass().getSimpleName();
    // context = 현재 context
    private Context context = this;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        shakeCount = 0;
        // view binding
        shakeCountTextView = findViewById(R.id.shaking_amount_tv_reward);
        shakeStartSButton = findViewById(R.id.shake_start_bt_reward);
        shakeTimeLeftTextView = findViewById(R.id.time_left_tv_reward);

        changeActivityButton = findViewById(R.id.temporary_bt_reward);

        // 진동기 초기화 - 시스템 서비스 호출
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // click listener 등록
        shakeStartSButton.setOnClickListener(this);

        changeActivityButton.setOnClickListener(this);

        remainingSecond = 5;

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
        shakeCountTextView.setText(shakeCountString + shakeCount);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"onStart called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG,"onResume called");
        // Add the following line to register the Session Manager Listener onResume
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

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"onStop called");
    }

    // click event
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            // shake 시작
            case R.id.shake_start_bt_reward:
                // 진동을 감지하고, 진동을 낼 수 있도록 만든다
                isShakeAvailable = true;
                // 시작할 때 버튼을 안보이게 만든다
                shakeStartSButton.setVisibility(View.GONE);
                // 앱 시작과 동시에 핸들러에 메세지 전달
                mHandler.sendEmptyMessage(vibratileSecond);
//                shakeStart();
                break;
            case R.id.temporary_bt_reward :
                intent = new Intent(context,GameActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


}
