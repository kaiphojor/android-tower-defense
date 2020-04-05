package com.example.plaintowerdefense.game;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.example.plaintowerdefense.MainActivity;
import com.example.plaintowerdefense.R;

public class DefeatActivity extends Activity {

    static TextView timerTextView;
    static String timerString;
    static boolean canBackToMenu;
    // 태그 = 현재 activity 이름
    private final String TAG = this.getClass().getSimpleName();
    // context = 현재 context
    private Context context = this;
    Intent intent;

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
        // 앱 시작과 동시에 핸들러에 메세지 전달
        mHandler.sendEmptyMessage(5000);

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
}
