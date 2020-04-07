package com.example.plaintowerdefense;

import androidx.annotation.NonNull;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.plaintowerdefense.error_collect.CrashLogActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity extends BaseActivity implements View.OnClickListener{

    Button crashLogButton;
    TextView gemNumberTextView;
    // 닉네임 표시용
    // 로그인 용
    TextView nicknameView;
    ImageView profileImageView;

    // 태그 = 현재 activity 이름
    private final String TAG = this.getClass().getSimpleName();
    // context = 현재 context
    private Context context = this;
    // intent
    Intent intent;

    // firebase 인증
    private FirebaseAuth mAuth;
    // google 로그인 클라이언트. 로그아웃하는데 필요
    private GoogleSignInClient mGoogleSignInClient;
    // 현재 user 정보
    UserInfoSingleton userInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // 로그인 상태 표시
        nicknameView = findViewById(R.id.username_tv_setting);
        profileImageView = findViewById(R.id.profile_iv_setting);
        gemNumberTextView = findViewById(R.id.gem_number_tv_setting);
        crashLogButton = findViewById(R.id.crash_log_bt_setting);
        crashLogButton.setOnClickListener(this);
        // click listener 등록
        findViewById(R.id.sign_out_bt_setting).setOnClickListener(this);
        // 현재 user 정보 초기화
        userInfo = UserInfoSingleton.getInstance();
        userInfo.setGemUi(gemNumberTextView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 로그인한 계정에 따라 닉네임/프로필 세팅
        LoginSingleton.getInstance(context);
        LoginSingleton.loginOnStart(nicknameView,profileImageView);
        mGoogleSignInClient = LoginSingleton.getmGoogleSignInClient();
        mAuth = LoginSingleton.getmAuth();
    }


    // firebase 인증, google 로그인 클라이언트에서 모두 로그아웃한다
    private void signOut() {
        // Firebase sign out
        mAuth.signOut();
        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // 부가 작업
                        // 검사 통과시 등록된 닉네임과 프로필 이미지를 모두 없앤다
                        SharedPreferences sharedPreference = getSharedPreferences("sharedPreference", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreference.edit();
                        editor.putString("nickname","");
                        editor.putString("profileImage","");
                        editor.apply();
                        backToLoginPage();
                    }
                });
    }

    private void backToLoginPage(){
        // 액티비티 스택을 다지우고, 로그인 페이지로 돌아간다
        intent = new Intent(context,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        // 로그아웃시 해당 메소드를 호출한다
        switch(id){
            case R.id.sign_out_bt_setting :
                signOut();

                break;
            case R.id.crash_log_bt_setting :
                intent = new Intent(context, CrashLogActivity.class);
                startActivity(intent);
                break;
        }
    }
}
