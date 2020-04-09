package com.example.plaintowerdefense.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plaintowerdefense.SoundSingleton;
import com.example.plaintowerdefense.error_collect.BaseActivity;
import com.example.plaintowerdefense.LoginSingleton;
import com.example.plaintowerdefense.R;
import com.example.plaintowerdefense.Singleton;
import com.example.plaintowerdefense.game.game_result.DefeatActivity;
import com.example.plaintowerdefense.game.game_result.VictoryActivity;
import com.example.plaintowerdefense.user_info.UserInfoSingleton;
import com.example.plaintowerdefense.game.bgm.MusicPlayerSingleton;
import com.example.plaintowerdefense.game.tower_list.Tower;
import com.example.plaintowerdefense.game.tower_list.TowerListAdapter;

import org.json.JSONObject;

import java.util.ArrayList;

public class GameActivity extends BaseActivity implements View.OnClickListener, MediaPlayer.OnPreparedListener {
    // 상단에 표시하는 게임 진행 정보
    TextView healthPointView;
    TextView coinCountView;
    Button startButton;
    TextView waveTextView;

    TextView gemNumberTextView;
    // 임시
    Button temporaryResultButton;
    TextView temporaryTextView;
    LinearLayout sideMenuLayout;
    Button sideMenuButton;
    // 타워 리스트뷰 표현용
    ListView towerListView;
    ArrayList<Tower> towerList;
    // 로그인 용
    TextView idTextView;
    ImageView profileImageView;
    GameSurfaceView gameView;

    // 태그 = 현재 activity 이름
    private final String TAG = this.getClass().getSimpleName();
    // context = 현재 context
    private Context context = this;
    Intent intent;
    TowerListAdapter towerListAdapter;
    // 배경음악
//    BackgroundMusic backgroundMusic = new BackgroundMusic();
//    MediaPlayer musicPlayer;
    // mp3 player singleton
    MediaPlayer musicPlayer = MusicPlayerSingleton.getInstance();
    float bgmVolume;
    // 현재 user 정보
    UserInfoSingleton userInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_game);
        setContentView(R.layout.activity_game);
        Singleton.getInstance(context);
        // view 연결 & click listener 등록
        bindView();
        // tower 목록 초기화
        towerList = new ArrayList<Tower>();
        towerList.add(new Tower(0, getString(R.string.tower_code_1), 10, R.drawable.red_tile));
        towerList.add(new Tower(1, getString(R.string.tower_code_2), 30, R.drawable.orange_tile));
        towerList.add(new Tower(2, getString(R.string.tower_code_3), 40, R.drawable.yellow_tile));
        towerList.add(new Tower(3, getString(R.string.tower_code_4), 50, R.drawable.light_green_tile));
        towerList.add(new Tower(4, getString(R.string.tower_code_5), 60, R.drawable.green_tile));
        // adapter 초기화 및 view에 연결
        towerListAdapter = new TowerListAdapter(this, towerList);
        towerListAdapter.setOnTowerClickListener(new TowerListAdapter.OnTowerClickListener() {
            // 타워 목록에서 타워를 클릭 했을 때
            @Override
            public void onTowerClick(View v, int position) {
//            Toast.makeText(getApplicationContext(),towerListAdapter.getItem(position).getName(),Toast.LENGTH_LONG).show();
            // 돈이 충분하다면 shared preference 에 저장 ( GameActivity와 GameSurfaceView 통신용)
            SharedPreferences sharedPreferences = getSharedPreferences("game", MODE_MULTI_PROCESS | MODE_WORLD_WRITEABLE);
            try {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Tower tower = towerList.get(position);
                // json object에 클릭한 타워정보 저장
                JSONObject towerInfoJSON = new JSONObject();
                towerInfoJSON.put("towerCode", tower.getTowerCode());
                towerInfoJSON.put("name", tower.getName());
                towerInfoJSON.put("price", tower.getPrice());
                towerInfoJSON.put("image", tower.getImageResource());
                String towerInfoJSONString = towerInfoJSON.toString();

                // tower click 여부와 tower 정보 shared preference에 저장
//                    editor.putBoolean("isTowerClick", false);
                editor.putBoolean("isTowerClick", true);
                editor.putString("towerInfo", towerInfoJSONString.toString());
                editor.apply();
                // tower code는 제대로 전달됨
//                    Singleton.log("tower code : "+ tower.getTowerCode()+"");
            } catch (Exception e) {
                e.printStackTrace();
            }
            }
        });
        towerListView.setAdapter(towerListAdapter);

        // 로그인 상태 표시
        idTextView = findViewById(R.id.username_tv_game);
        //임시 click listener
        idTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // mp3 초기화
        musicPlayer = MediaPlayer.create(GameActivity.this, R.raw.skull_fire);

        musicPlayer.setLooping(true);
        // 준비되었을 때 시작하기 위한 listener
        musicPlayer.setOnPreparedListener(this);


        // 현재 user 정보 초기화
        userInfo = UserInfoSingleton.getInstance();
        userInfo.setGemUi(gemNumberTextView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // sound 설정 가져오기
        SoundSingleton.initSoundSingleton(context);
        boolean isBgmMute = SoundSingleton.isBgmMute();
        // 음소거 여부를 고려한 설정
        bgmVolume = isBgmMute ? 0f : 0.01f * SoundSingleton.getBgmVolume();
        musicPlayer.setVolume(bgmVolume,bgmVolume);


        if(!musicPlayer.isPlaying()){
            musicPlayer.start();
        }


        // shared preference 에 pause 해제 정보 저장
        SharedPreferences sharedPreferences = getSharedPreferences("game", MODE_MULTI_PROCESS | MODE_WORLD_WRITEABLE);
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isPause",false);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        musicPlayer.prepareAsync();
//            musicPlayer.start();
//            return null;
        // 클릭시 이름을 출력
        // music context에 맞는 음악을 재생한다.
//        backgroundMusic.execute(new MusicContext(context,R.raw.skull_fire,true));

    }

    public void fuckfuck(final int position) {
        // listener
//        towerClickListener.onCreateClick(position);

    }


    @Override
    protected void onStart() {
        super.onStart();
        // 로그인한 계정에 따라 닉네임/프로필 세팅
        LoginSingleton.getInstance(context);
        LoginSingleton.loginOnStart(idTextView, profileImageView);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.temporary_bt_game:
                // 액티비티를 완전히 끝내버리지 않으면 surfaceview가 계속 rendering 해서 전환이 불가능하다
                ((Activity) context).finish();
                intent = new Intent(context, RewardActivity.class);
                startActivity(intent);
                break;
            // 사이드 메뉴가 보이는 것을 버튼 클릭으로 전환.
            case R.id.menu_bt_game:
                if (sideMenuLayout.getVisibility() == View.GONE) {
//                    sideMenuLayout.setVisibility(View.VISIBLE);
                    setMenuVisibility(true);
                } else {
                    sideMenuLayout.setVisibility(View.GONE);
//                    setMenuVisibility(false);
                }
                break;
            case R.id.start_bt_game :
                // wave 시작을 shared preference로 저장
                SharedPreferences sharedPreferences = getSharedPreferences("game", MODE_PRIVATE | MODE_WORLD_WRITEABLE);
                try {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isWaveStart",true);
                    editor.apply();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        musicPlayer.pause();
//        if(!backgroundMusic.isCancelled()){
//            backgroundMusic.cancel(true);
//        }

        // shared preference 에 pause 정보 저장 -> game surfaceview에서 처리하도록하기위해
        SharedPreferences sharedPreferences = getSharedPreferences("game", MODE_PRIVATE | MODE_WORLD_WRITEABLE);
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isPause",true);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//         음악 중지
        musicPlayer.stop();
//        musicPlayer.reset();
    }

    @Override
    public void onBackPressed() {
        // 일시정지
        intent = new Intent(context, PauseActivity.class);
        startActivity(intent);
//        super.onBackPressed();

        // 음악 정지 - async
//        musicPlayer.stop();

//        if(!backgroundMusic.isCancelled()){
//            backgroundMusic.cancel(true);
//        }
//        ((Activity) context).finish();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        musicPlayer.start();
    }
    // 음악 실행 - async

    //    public class BackgroundMusic extends AsyncTask<MusicContext, Void, Void> {
//        // 실행할 activity와 음악
//        Context context;
//        private int resourceNumber;
//        MediaPlayer musicPlayer;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//        // 백그라운드에서 작업(다른 thread에서 처리)
//        @Override
//        protected Void doInBackground(MusicContext... musicContexts) {
//            MusicContext musicContext = musicContexts[0];
//            // 음악 세팅 및 실행
//            musicPlayer = MediaPlayer.create((Activity)(musicContext.getContext()),musicContext.getResourceId());
//            musicPlayer.setVolume(100,100);
//            musicPlayer.setLooping(musicContext.isLooping());
//            musicPlayer.start();
//            return null;
//        }
//
//        @Override
//        protected void onCancelled(Void aVoid) {
//            super.onCancelled(aVoid);
//            musicPlayer.reset();
////            musicPlayer.stop();
//            musicPlayer = null;
//        }
//    }
    /*
    GameSurfaceView에서 호출하는 메소드
     */
    public void setTextView(final String text) {
        GameActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                temporaryTextView.setText(text);
            }
        });
    }
    // coin 숫자 설정
    public void setCoinCountView(final String coinCount) {
        GameActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                coinCountView.setText(coinCount);
            }
        });
    }
    // 체력 숫자 설정
    public void setHealthPointView(final String healthPoint) {
        GameActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                healthPointView.setText(healthPoint);
            }
        });
    }
    // 스테이지 설정
    public void setWaveTextView(final String stageText) {
        GameActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                waveTextView.setText(stageText);
            }
        });
    }
    // 시작 버튼 설정
    public void setstartButton(final String buttonText) {
        GameActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                startButton.setText(buttonText);
            }
        });
    }

    // side menu가 보이는지 여부를 결정. focus 획득 혹은 button click에 따라 수행됨
    public void setMenuVisibility(final boolean visibilityParameter) {
        GameActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                int visibility = visibilityParameter ? View.VISIBLE : View.GONE;
                sideMenuLayout.setVisibility(visibility);
            }
        });
    }
    // 승리 activity 호출
    public void showVictoryActivity(final int killed,final int passed,final int[] killCount) {
        GameActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                intent = new Intent(context, VictoryActivity.class);
                intent.putExtra("killed",killed);
                intent.putExtra("passed",passed);
                intent.putExtra("enemy0",killCount[0]);
                intent.putExtra("enemy1",killCount[1]);
                intent.putExtra("enemy2",killCount[2]);
                intent.putExtra("enemy3",killCount[3]);
                intent.putExtra("enemy4",killCount[4]);
                startActivity(intent);
            }
        });
    }
    // 패배 activity 호출
    public void showDefeatActivity() {
        GameActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                intent = new Intent(context, DefeatActivity.class);
                startActivity(intent);
            }
        });
    }
    // gold 부족 toast
    public void showInsufficientGoldToast() {
        GameActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Singleton.toast("골드가 부족합니다",false);
            }
        });
    }
    // view 연결 & click listener 등록
    public void bindView(){
        startButton = findViewById(R.id.start_bt_game);
        healthPointView = findViewById(R.id.health_point_tv_game);
        coinCountView = findViewById(R.id.coin_tv_game);
        waveTextView = findViewById(R.id.wave_tv_game);
        gemNumberTextView = findViewById(R.id.gem_number_tv_game);

        temporaryResultButton = findViewById(R.id.temporary_bt_game);
        temporaryResultButton.setOnClickListener(this);
        temporaryTextView = findViewById(R.id.test_tv_game);
        sideMenuLayout = findViewById(R.id.menu_ll_game);
        sideMenuButton = findViewById(R.id.menu_bt_game);
        sideMenuButton.setOnClickListener(this);
        towerListView = findViewById(R.id.menu_lv_game);
        gameView = findViewById(R.id.sv_game);
        profileImageView = findViewById(R.id.profile_iv_game);
        startButton.setOnClickListener(this);
    }



}
