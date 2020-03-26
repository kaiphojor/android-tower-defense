package com.example.plaintowerdefense.game;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plaintowerdefense.BaseActivity;
import com.example.plaintowerdefense.LoginSingleton;
import com.example.plaintowerdefense.R;
import com.example.plaintowerdefense.Singleton;
import com.example.plaintowerdefense.game.tower_list.Tower;
import com.example.plaintowerdefense.game.tower_list.TowerListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import org.json.JSONObject;

import java.util.ArrayList;

public class GameActivity extends BaseActivity implements View.OnClickListener{
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_game);
        setContentView(R.layout.activity_game);
        Singleton.getInstance(context);
        // view 연결 & click listener 등록
        temporaryResultButton = findViewById(R.id.temporary_bt_game);
        temporaryResultButton.setOnClickListener(this);
        temporaryTextView = findViewById(R.id.test_tv_game);
        sideMenuLayout = findViewById(R.id.menu_ll_game);
        sideMenuButton = findViewById(R.id.menu_bt_game);
        sideMenuButton.setOnClickListener(this);
        towerListView = findViewById(R.id.menu_lv_game);
        gameView = findViewById(R.id.sv_game);
        // tower 목록 초기화
        towerList = new ArrayList<Tower>();
        towerList.add(new Tower(0,getString(R.string.tower_code_1),10,R.drawable.red_tile));
        towerList.add(new Tower(1,getString(R.string.tower_code_2),30,R.drawable.orange_tile));
        towerList.add(new Tower(2,getString(R.string.tower_code_3),40,R.drawable.yellow_tile));
        towerList.add(new Tower(3,getString(R.string.tower_code_4),50,R.drawable.light_green_tile));
        towerList.add(new Tower(4,getString(R.string.tower_code_5),60,R.drawable.green_tile));
        // adapter 초기화 및 view에 연결
        towerListAdapter = new TowerListAdapter(this,towerList);
        towerListAdapter.setOnTowerClickListener(new TowerListAdapter.OnTowerClickListener(){
            // 타워 목록에서 타워를 클릭 했을 때
            @Override
            public void onTowerClick(View v, int position){
                Toast.makeText(getApplicationContext(),
                        towerListAdapter.getItem(position).getName(),
                Toast.LENGTH_LONG).show();
                // 돈이 충분하다면 shared preference 에 저장 ( GameActivity와 GameSurfaceView 통신용)
                SharedPreferences sharedPreferences = getSharedPreferences("game",MODE_MULTI_PROCESS | MODE_WORLD_WRITEABLE);
                try{
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Tower tower = towerList.get(position);
                    // json object에 클릭한 타워정보 저장
                    JSONObject towerInfoJSON = new JSONObject();
                    towerInfoJSON.put("towerCode",tower.getTowerCode());
                    towerInfoJSON.put("name",tower.getName());
                    towerInfoJSON.put("price",tower.getPrice());
                    towerInfoJSON.put("image",tower.getImageResource());
                    String towerInfoJSONString = towerInfoJSON.toString();


                    // tower click 여부와 tower 정보 shared preference에 저장
//                    editor.putBoolean("isTowerClick", false);
                    editor.putBoolean("isTowerClick",true);
                    editor.putString("towerInfo",towerInfoJSONString.toString());
                    editor.apply();
                    // tower code는 제대로 전달됨
//                    Singleton.log("tower code : "+ tower.getTowerCode()+"");
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        towerListView.setAdapter(towerListAdapter);

        // 로그인 상태 표시
        idTextView = findViewById(R.id.username_tv_game);
        //임시 click listener
        idTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });
        profileImageView = findViewById(R.id.profile_iv_game);


    }

    @Override
    protected void onResume() {
        super.onResume();
        // 클릭시 이름을 출력

    }

    public void fuckfuck(final int position){
        // listener
//        towerClickListener.onCreateClick(position);

    }
    public void setTextView(final String text){
        GameActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                temporaryTextView.setText(text);
            }
        });
    }
    // side menu가 보이는지 여부를 결정. focus 획득 혹은 button click에 따라 수행됨
    public void setMenuVisibility(final boolean visibilityParameter){
        GameActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                int visibility = visibilityParameter ? View.VISIBLE : View.GONE;
                sideMenuLayout.setVisibility(visibility);
            }
        });
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
            case R.id.temporary_bt_game:
                 // 액티비티를 완전히 끝내버리지 않으면 surfaceview가 계속 rendering 해서 전환이 불가능하다
                ((Activity) context).finish();
                intent = new Intent(context,RewardActivity.class);
                startActivity(intent);
                break;
            // 사이드 메뉴가 보이는 것을 버튼 클릭으로 전환.
            case R.id.menu_bt_game:
                if(sideMenuLayout.getVisibility() == View.GONE){
//                    sideMenuLayout.setVisibility(View.VISIBLE);
                    setMenuVisibility(true);
                }else{
                    sideMenuLayout.setVisibility(View.GONE);
//                    setMenuVisibility(false);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ((Activity) context).finish();
    }
}
