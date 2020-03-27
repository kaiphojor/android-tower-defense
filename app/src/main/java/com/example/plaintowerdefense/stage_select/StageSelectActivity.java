package com.example.plaintowerdefense.stage_select;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.plaintowerdefense.BaseActivity;
import com.example.plaintowerdefense.GameLoadingActivity;
import com.example.plaintowerdefense.LoginSingleton;
import com.example.plaintowerdefense.R;
import com.example.plaintowerdefense.game.GameActivity;
import com.example.plaintowerdefense.game.RewardActivity;
import com.google.firebase.auth.FirebaseAuth;

public class StageSelectActivity extends BaseActivity implements View.OnClickListener{
    // 로그인 용
    TextView idTextView;
    ImageView profileImageView;
    // 임시 버튼
    Button temporaryGameStartButton;
    private ViewPager stageViewPager;
    private StageAdapter stageAdapter;

    private ImageView previousStageImage;
    private ImageView nextStageImage;


    // 태그 = 현재 activity 이름
    private final String TAG = this.getClass().getSimpleName();
    // context = 현재 context
    private Context context = this;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_select);
        // binding & listener 등록
        temporaryGameStartButton = findViewById(R.id.temporary_start_bt_stage_select);
        temporaryGameStartButton.setOnClickListener(this);
        // view pager 초기화
        stageViewPager = findViewById(R.id.vp_stage_select);
        stageAdapter = new StageAdapter(this);
        stageViewPager.setAdapter(stageAdapter);
        // 슬라이드와 같은 기능을 제공하는 버튼
        previousStageImage = findViewById(R.id.previous_stage_iv_stage_select);
        previousStageImage.setOnClickListener(this);
        nextStageImage = findViewById(R.id.next_stage_iv_stage_select);
        nextStageImage.setOnClickListener(this);

        //login 상태 관련 view
        idTextView = findViewById(R.id.username_tv_stage_select);
        profileImageView = findViewById(R.id.profile_iv_stage_select);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // 로그인한 계정에 따라 닉네임/프로필 세팅
        LoginSingleton.getInstance(context);
        LoginSingleton.loginOnStart(idTextView,profileImageView);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.temporary_start_bt_stage_select:
                intent = new Intent(context, GameLoadingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                break;
            case R.id.previous_stage_iv_stage_select :
                stageViewPager.arrowScroll(View.FOCUS_LEFT);
                break;
            case R.id.next_stage_iv_stage_select :
                stageViewPager.arrowScroll(View.FOCUS_RIGHT);
                break;
        }
    }
}
