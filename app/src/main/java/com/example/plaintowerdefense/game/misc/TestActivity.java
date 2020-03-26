package com.example.plaintowerdefense.game.misc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.example.plaintowerdefense.R;

public class TestActivity extends AppCompatActivity {
    int value=5;
        TextView mText;
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_test);
            mText=(TextView)findViewById(R.id.text);
            mHandler.sendEmptyMessage(0); // 앱 시작과 동시에 핸들러에 메세지 전달
    }
    Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            value--;
            mText.setText("Value = " + value);
            if(value > 0){
                // 메세지를 처리하고 또다시 핸들러에 메세지 전달 (1000ms 지연)
                mHandler.sendEmptyMessageDelayed(0,1000);
            }
        }
    };
}
