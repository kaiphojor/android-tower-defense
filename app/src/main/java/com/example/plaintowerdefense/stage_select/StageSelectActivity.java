package com.example.plaintowerdefense.stage_select;

import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.plaintowerdefense.error_collect.BaseActivity;
import com.example.plaintowerdefense.GameLoadingActivity;
import com.example.plaintowerdefense.LoginSingleton;
import com.example.plaintowerdefense.R;
import com.example.plaintowerdefense.user_info.UserInfoSingleton;

public class StageSelectActivity extends BaseActivity implements View.OnClickListener{
    // 로그인 용
    TextView idTextView;
    ImageView profileImageView;
    TextView gemNumberTextView;
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
    // 현재 user 정보
    UserInfoSingleton userInfo;
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
        gemNumberTextView = findViewById(R.id.gem_number_tv_stage_select);

        // 현재 user 정보 초기화
        userInfo = UserInfoSingleton.getInstance();
        userInfo.setGemUi(gemNumberTextView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 로그인한 계정에 따라 닉네임/프로필 세팅
        LoginSingleton.getInstance(context);
        LoginSingleton.loginOnStart(idTextView,profileImageView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
        게임에서의 진행상태 정보 초기화
         */
        // shared preference 에 pause 정보 저장 -> game surfaceview에서 처리하도록하기위해
        SharedPreferences sharedPreferences = getSharedPreferences("game", MODE_PRIVATE | MODE_WORLD_WRITEABLE);
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isPause",false);
            editor.putBoolean("isWaveStart",false);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
