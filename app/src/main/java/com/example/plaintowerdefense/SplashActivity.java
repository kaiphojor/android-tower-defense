package com.example.plaintowerdefense;

import androidx.appcompat.app.AppCompatActivity;
//import androidx.room.Room;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.plaintowerdefense.game.tower_list.Tower;
import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener{

    /*
    login check
    if true :
         main
    else :
         login
     */

    // lottie animation view
    LottieAnimationView splashAnimationView;

    // firebase 인증 위한 변수
    private FirebaseAuth mAuth;
    // 태그 = 현재 activity 이름
    private final String TAG = this.getClass().getSimpleName();
    // context = 현재 context
    private Context context = this;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // click listener 등록
        findViewById(R.id.bt_splash).setOnClickListener(this);
        // animation view 설정
        splashAnimationView = findViewById(R.id.lav_splash);

//        splashAnimationView.setAnimation("loading_circle.json");
        splashAnimationView.setAnimation("loading_circle.json");
        // 반복 x
        splashAnimationView.loop(false);
        // 애니메이션 시작
        splashAnimationView.playAnimation();
        // animation view 생명주기 method
        splashAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            // animation  끝났을 때 동작
            @Override
            public void onAnimationEnd(Animator animation) {
                updateUI();
//                Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
//                startActivity(intent);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });


        // shared preference 초기화
        SharedPreferences sharedPreferences = getSharedPreferences("setting",MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("game",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try{
            // json object에 클릭한 타워정보 저장
            JSONObject towerInfoJSON = new JSONObject();
            towerInfoJSON.put("name","testName");
            towerInfoJSON.put("price",50);
            towerInfoJSON.put("image",6666);
            String towerInfoJSONString = towerInfoJSON.toString();

            // tower click 여부와 tower 정보 shared preference에 저장
//            JSONObject towerClickJSON = new JSONObject();
//            towerClickJSON.put("isTowerClick",false);
//            towerClickJSON.put("towerInfo",towerInfoJSONString);
            editor.putBoolean("isTowerClick", false);
            editor.putString("towerInfo",towerInfoJSONString.toString());
            editor.apply();
//            JSONObject towerJsonObject = towerJsonObject.optJSONObject("ObjectName");

        }catch(Exception e){
            e.printStackTrace();
        }

        // 계정 정보 초기화
//        SharedPreferences shared = getSharedPreferences("sharedPreference",MODE_PRIVATE);
//        SharedPreferences.Editor sharedEditor = shared.edit();
//        try{
//            sharedEditor.putString("jihoopark7666@gmail.com","");
//            sharedEditor.apply();
//        }catch(Exception e){
//            e.printStackTrace();
//        }

//        try{
//            // 저장한 shared preference 불러 오기
//            sharedPreferences = getSharedPreferences("game",MODE_WORLD_READABLE);
//            boolean isTowerClick = sharedPreferences.getBoolean("isTowerClick",false);
//            String towerInfo = sharedPreferences.getString("towerInfo","error");
//            JSONObject towerInfoJSONObject = new JSONObject(towerInfo);
//            String name = towerInfoJSONObject.optString("name");
//            int price = towerInfoJSONObject.optInt("price");
//            int image = towerInfoJSONObject.optInt("image");
//            Singleton.getInstance(context);
//            Singleton.log(isTowerClick+"");
////          Singleton.log(towerInfo);
//            Singleton.log("name : "+name);
//            Singleton.log("price : "+price);
//            Singleton.log("image : "+image);
//        }catch(Exception e){
//        }

        // hash key log로 얻는 code. -- 카카오 로그인
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.plaintowerdefense", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

//        try{
//            // android auth token 파일을 불러온다
//            FileInputStream serviceAccount =
//                    new FileInputStream("C:/Android_Projects/PlainTowerDefense/app/plain-tower-defense-firebase-adminsdk-pqjgh-4aa8e4f33c.json");
//            FirebaseOptions options = new FirebaseOptions.Builder()
//                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                    .setDatabaseUrl("https://plain-tower-defense.firebaseio.com")
//                    .build();
//            FirebaseApp.initializeApp(options);
//
//        }catch(Exception e){
//
//        }
        // shared preference 에 pause 정보 저장 -> game surfaceview에서 처리하도록하기위해
        SharedPreferences gamePauseSharedPreference = getSharedPreferences("game", MODE_PRIVATE | MODE_WORLD_WRITEABLE);
        try {
            SharedPreferences.Editor gamePreferenceEditor = sharedPreferences.edit();
            gamePreferenceEditor.putBoolean("isPause",false);
            gamePreferenceEditor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Tower parseJSONTowerInfo(String jsonString){
        try{
            JSONObject towerInfoJSONObject = new JSONObject(jsonString);
            String name = towerInfoJSONObject.optString("name");
            int price = towerInfoJSONObject.optInt("price");
            int image = towerInfoJSONObject.optInt("image");
            return new Tower(name,price,image);
        }catch(Exception e){
            return null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void updateUI(){
//        // firebase 인증 초기화
//        mAuth = FirebaseAuth.getInstance();
//        // 로그인 여부에 따라 다른 화면 전환
//        FirebaseUser user = mAuth.getCurrentUser();
//        // 로그인 여부 확인
//        if(user == null){
//            // 로그인하지 않았을 때 로그인 화면으로 전환
//            Log.i(TAG,"로그인 하지 않음");
////            Toast.makeText(this,"로그인 하지 않음",Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        // 이미 로그인 되었으면 메인화면 이동
//        }else{
//            Log.i(TAG,"로그인 함");
//            String loginMessage = "";
//            loginMessage += "email : " + user.getEmail();
//            loginMessage += "displayname : " + user.getDisplayName();
//            loginMessage += "uid : " + user.getUid();
//            loginMessage += "tostring : " + user.toString();
//            Log.i(TAG,loginMessage);
////            Toast.makeText(this,"로그인 함",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
//        }
    }

    @Override
    public void onClick(View v) {
        // 액티비티 이동
        if(v.getId() == R.id.bt_splash){
            Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
            startActivity(intent);
        }
    }
}
