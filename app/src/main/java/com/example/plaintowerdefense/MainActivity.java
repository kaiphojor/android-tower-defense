package com.example.plaintowerdefense;

import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.example.plaintowerdefense.game.DefeatActivity;
import com.example.plaintowerdefense.gem_shop.BuyGemActivity;
import com.example.plaintowerdefense.social.SocialActivity;
import com.example.plaintowerdefense.social.SocialListviewActivity;
import com.example.plaintowerdefense.stage_select.StageSelectActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
//import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends BaseActivity {

    // 로그인 용
    TextView nicknameTextView;
    ImageView profileImageView;
    // 메뉴 이동용 textview
    TextView startTextView;
    TextView settingTextView;
    TextView multiPlayTextView;
    TextView gemShopTextView;
    TextView socialTextView;
    TextView loginTextView;
    TextView listviewView;
    TextView gemNumberTextView;

    Intent intent;

    // 태그 = 현재 activity 이름
    private final String TAG = this.getClass().getSimpleName();
    // context = 현재 context
    private Context context = this;

    private static final int RC_SIGN_IN = 9001;
    // firebase 인증
    private FirebaseAuth mAuth;
    // google 로그인
    private GoogleSignInClient mGoogleSignInClient;
    // 현재 user 정보
    UserInfoSingleton userInfo;

    SharedPreferences sharedPreference;
    // 이미지 갈아끼워주는 역할
    private static ViewSwitcher imageSwitcher;

    private static ImageView tv;

//    final AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //view binding
        nicknameTextView = findViewById(R.id.nickname_tv_main);
        profileImageView = findViewById(R.id.profile_iv_main);
        gemNumberTextView = findViewById(R.id.gem_number_tv_main);
        // 로그인 변수 초기화
        LoginSingleton login = LoginSingleton.getInstance(context);
        login.loginOnCreate();
        // 현재 user 정보 초기화
        userInfo = UserInfoSingleton.getInstance();
        userInfo.initUserInfo(context,login.getmAuth());
        userInfo.setGemUi(gemNumberTextView);

        // 클릭 이벤트 따로 분리
        buttonClick();

        // 싱글톤 인스턴스 가져오기.context만 갈아끼워 사용한다.
        Singleton.getInstance(this);

        // 체크박스 클릭여부, 체크박스 마지막 클릭 시간을 판단해서 팝업을 내보낸다
        // 체크 박스 클릭시 다음날에 팝업이 뜨게된다
        sharedPreference = getSharedPreferences("setting",MODE_PRIVATE | MODE_WORLD_WRITEABLE);
        // shared preference를 불러온다. 구분자 $ 의 왼쪽은 pop up를 띄우는지 여부, 오른쪽은 마지막 pop up checkbox 클릭 시간
        String popUpStatusString = sharedPreference.getString("popUpStatus", "true&none");
        String[] popUpStatus = popUpStatusString.split("&");
        // 팝업 뜰 때 - 체크박스 클릭 안되었을 때, 클릭되었으나 하루가 지났을 때
        if (popUpStatus[0].contentEquals("true")) {
            showAdvertisement();
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            String dateString = simpleDateFormat.format(new Date(System.currentTimeMillis()));
            Singleton.log(dateString);
            if (popUpStatus[1].contentEquals(dateString)) {
                // 하루 동안 보지 않기 클릭됨 & 아직 하루 안지남
                Singleton.log("하루동안보지않기,하루안지남 : " + dateString);
                // 테스트 용
//                SharedPreferences.Editor editor = sharedPreference.edit();
////                editor.remove("popUpStatus");
//                editor.putString("popUpStatus","true&"+dateString);
//                editor.apply();
            } else {
                // 하루 동안 보지 않기 클릭됨 & 하루 지남
                Singleton.log("하루동안보지않기,하루지남 : " + dateString);
                SharedPreferences.Editor editor = sharedPreference.edit();
//                editor.remove("popUpStatus");
                editor.putString("popUpStatus", "true&" + dateString);
                editor.apply();
                showAdvertisement();
            }
        }
        ImageView demoImage = (ImageView) findViewById(R.id.test_image_iv_main);
        int imagesToShow[] = {R.drawable.background_blue_aqua_turquoise, R.drawable.background_blur, R.drawable.background_free_pic, R.drawable.background_milky_way};

//        animate(demoImage, imagesToShow, 0,true);
        fadeAnimation(demoImage, true, 0);



//        LottieAnimationView animationView = (LottieAnimationView) findViewById(R.id.anim_main);
//        animationView.setAnimation("video_anim.json");
//        animationView.loop(true);
//        animationView.playAnimation();
    }

    @Override
    public void onStart() {
        super.onStart();
        // 로그인한 계정에 따라 닉네임/프로필 세팅
        LoginSingleton.getInstance(context);
        LoginSingleton.loginOnStart(nicknameTextView, profileImageView);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        Singleton.toast("싱글톤 클래스 테스트", true);
        //
//        imageSwitchHandler.sendEmptyMessage(-1);
    }

    // 따로 구분한 클릭 처리 이벤트
    public void buttonClick() {
        // 게임시작 페이지 이동
        startTextView = findViewById(R.id.stage_select_tv_main);
        startTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, StageSelectActivity.class);
                startActivity(intent);
            }
        });
        // 환경설정 페이지 이동
        settingTextView = findViewById(R.id.setting_tv_main);
        settingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
        // 멀티플레이 페이지 이동
        multiPlayTextView = findViewById(R.id.multi_play_tv_main);
        multiPlayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, DrawerTestActivity.class);
                startActivity(intent);
            }
        });
        // 보석상점 페이지 이동
        gemShopTextView = findViewById(R.id.buy_gem_tv_main);
        gemShopTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, BuyGemActivity.class);
                startActivity(intent);
            }
        });
        // 친구 페이지 이동
        socialTextView = findViewById(R.id.friend_tv_main);
        socialTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, SocialActivity.class);
                startActivity(intent);
            }
        });
        // 테스트 페이지 이동
        listviewView = findViewById(R.id.listview_tv_main);
        listviewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, SocialListviewActivity.class);
                startActivity(intent);
            }
        });
        // 테스트 페이지 이동
        findViewById(R.id.test_bt_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, DefeatActivity.class);
                startActivity(intent);
            }
        });
        // id 인증 이동 - 현재 안쓰임
        //TODO : 나중에도 안쓰면 지워야
        nicknameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }


    private void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            LoginSingleton.loginOnActivityResult(data);

        }
    }

    // 팝업을 띄워주는 기능
    public void showAdvertisement() {
        // xml -> view 한뒤 view binding
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_advertisement, null);
        final CheckBox todayCheckBox = dialogView.findViewById(R.id.cb_advertisement);
        final ImageView advertisementImageView = dialogView.findViewById(R.id.iv_advertisement);

        // builder 초기화 및 전체 layout binding
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        // 닫기 버튼
        builder.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int pos) {
                Singleton.log("팝업 닫기 눌림");
            }
        });
        // dialog 초기화 및 띄우기
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // 광고 클릭시 해당 액티비티 이동
        advertisementImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, BuyGemActivity.class);
                startActivity(intent);
                // dialog는 메인화면에 다시 진입했을 때 보이지 않도록 한다
                alertDialog.dismiss();
            }
        });
        // 하루동안 보이지 않기 체크박스
        todayCheckBox.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    // 팝업 보여주기 : false , 마지막 checkbox 클릭시간 : 현재 날짜 로 저장
                    SharedPreferences sharedPreferences = getSharedPreferences("setting", MODE_WORLD_WRITEABLE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                    String dateString = simpleDateFormat.format(new Date(System.currentTimeMillis()));
                    Singleton.toast(dateString, true);
                    editor.putString("popUpStatus", "false&" + dateString);
                    editor.apply();
                    // checkbox클릭시 바로 dialog를 닫는다
                    alertDialog.dismiss();
                } else {
                    Singleton.log("CheckBox is unchecked.");
                }
            }
        });
    }

    // handler 이용한 fade in fade out animation
    public void fadeAnimation(final ImageView imageView, final boolean isfadeOut, final int index) {
        final int imagesToShow[] = {R.drawable.background_blue_aqua_turquoise, R.drawable.background_blur, R.drawable.background_free_pic, R.drawable.background_milky_way};

        final Animation animationFade;
        // fade in & out animation 초기화
        final Animation fadein;
        final Animation fadeout;
        fadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);
        // 사진 세팅
        imageView.setImageResource(imagesToShow[index]);
        // 그림이 화면에 보이는 시간은 5초, 빈화면은 1초간 뜨게한다
        int delay;
        if(!isfadeOut){
            // fade in 일때는 투명 -> 불투명이어야한다.
            // 안그러면 뚝뚝 끊겨서 보임
            imageView.setAlpha(0f);
            delay = 1000;
        }else{
            delay = 5000;
        }
        // animation 설정
        if (isfadeOut) {
            animationFade = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        } else {
            animationFade = AnimationUtils.loadAnimation(this, R.anim.fadein);
        }
        // animation listener - 끝날 때에만 필요하다.
        animationFade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            // 재귀 호출 부분
            @Override
            public void onAnimationEnd(Animation animation) {
                if (isfadeOut) {
                    // fade out 시 다른 사진으로 전환
                    if(index < 3){
                        fadeAnimation(imageView,false,index+1);
                    }else{
                        fadeAnimation(imageView,false,0);
                    }
                }else {
                    // fade in 일 때는 fade out 으로 한다
                    fadeAnimation(imageView,true,index);
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        // 핸들러 자체는 무한루프가 아니다. 하지만 method에서 계속 자신을 호출하기 때문에 계속 돌게 된다
        Handler imageSwitchHandler = new Handler();
        imageSwitchHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageView.setAlpha(1f);
                imageView.startAnimation(animationFade);
            }
        }, delay);

    }

    // 원래 잘되는 예제
    private void animate(final ImageView imageView, final int images[], final int imageIndex, final boolean forever) {

        //imageView <-- The View which displays the images
        //images[] <-- Holds R references to the images to display
        //imageIndex <-- index of the first image to show in images[]
        //forever <-- If equals true then after the last image it starts all over again with the first image resulting in an infinite loop. You have been warned.

        int fadeInDuration = 1000; // Configure time values here
        int timeBetween = 3000;
        int fadeOutDuration = 1000;

        imageView.setVisibility(View.INVISIBLE);    //Visible or invisible by default - this will apply when the animation ends
        imageView.setImageResource(images[imageIndex]);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
        fadeIn.setDuration(fadeInDuration);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); // and this
        fadeOut.setStartOffset(fadeInDuration + timeBetween);
        fadeOut.setDuration(fadeOutDuration);

        AnimationSet animation = new AnimationSet(false); // change to false
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        animation.setRepeatCount(1);
        imageView.setAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                if (images.length - 1 > imageIndex) {
                    animate(imageView, images, imageIndex + 1, forever); //Calls itself until it gets to the end of the array
                } else {
                    if (forever) {
                        animate(imageView, images, 0, forever);  //Calls itself to start the animation all over again in a loop if forever = true
                    }
                }
            }

            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }
        });
    }

    // image switcher view - 사진 2개만 허용
    private final static Handler imageSwitchHandler = new Handler() {
        public void handleMessage(Message msg) {
            /*
            사진 전환
            0일 때 -> next . 1
            1 next 2
            2 next 3
            3 next 4
            4 0 -> 1
             */
            if (msg.what == -1) {
                imageSwitcher.setDisplayedChild(0);
                msg.what = 1;
            } else if (msg.what < 4) {
                imageSwitcher.showNext();
                msg.what += 1;
            } else {
                imageSwitcher.setDisplayedChild(0);
                msg.what = 1;
            }
            imageSwitchHandler.sendEmptyMessageDelayed(msg.what, 5000);
            //            msg.what += 1;
//            remainingSecond--;
            // 처음 시작 할 때 세팅

//            shakeTimeLeftTextView.setText(msg.what+"초");
//            if(msg.what > 0){
            // 메세지를 처리하고 또다시 핸들러에 메세지 전달 (1000ms 지연)
//            }else{
//                isShakeAvailable = false;
//            }
        }
    };
    // user가 가진 보석 숫자를 표시해준다
    public void setGemUi(final TextView gemTextView){
        int gemCount = userInfo.getGem();
        gemTextView.setText(String.valueOf(gemCount));
    }
}
