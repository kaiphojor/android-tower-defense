package com.vortexghost.plaintowerdefense.gem_shop;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.vortexghost.plaintowerdefense.Singleton;
import com.vortexghost.plaintowerdefense.error_collect.BaseActivity;
import com.vortexghost.plaintowerdefense.LoginSingleton;
import com.vortexghost.plaintowerdefense.R;
import com.vortexghost.plaintowerdefense.social.UserRecyclerViewAdapter;
import com.vortexghost.plaintowerdefense.user_info.UserInfoSingleton;

import java.util.ArrayList;

public class BuyGemActivity extends BaseActivity {
    GemRecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<Gem> gemListData;

    /*
    전체 activity 공통 변수
     */
    // 로그인 용
    TextView idTextView;
    ImageView profileImageView;

    TextView gemNumberTextView;
    // 태그 = 현재 activity 이름
    private final String TAG = this.getClass().getSimpleName();
    // context = 현재 context
    private Context context = this;
    // intent
    Intent intent;

    // 현재 user 정보
    UserInfoSingleton userInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_gem);
        /*
        전체 activity 공통
         */
        // 로그인 상태 표시
        idTextView = findViewById(R.id.username_tv_buy_gem);
        profileImageView = findViewById(R.id.profile_iv_buy_gem);

        /*
        recycler view 표현
         */
        gemListData = new ArrayList<>();
        gemListData.add(new Gem("보석 1개",300,R.drawable.diamond,1));
        gemListData.add(new Gem("보석 5개",1500,R.drawable.diamond_5,5));
        gemListData.add(new Gem("보석 10개",3000,R.drawable.diamond_10,10));
        gemListData.add(new Gem("보석 20개",6000,R.drawable.diamond_20,20));
        gemListData.add(new Gem("보석 50개",12000,R.drawable.diamond_50,50));
        gemListData.add(new Gem("보석 100개",22000,R.drawable.diamond_100,100));
        gemListData.add(new Gem("보석 200개",40000,R.drawable.diamond_200,200));
        gemListData.add(new Gem("보석 500개",80000,R.drawable.diamond_500,500));
        // data를 adapter에 담고 recyclerview 표시
        adapter = new GemRecyclerViewAdapter(context);
        adapter.setData(gemListData);
        adapter.setOnItemClickListener(new GemRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                int id = v.getId();
                Gem gemData = gemListData.get(position);
                int price = gemData.getPrice();
                String gemString = gemData.getTitle();
                int amount = gemData.getAmount();
                Singleton.toast(gemString + " " + amount + "개 "+price + "원",false);
            }
        }) ;

        recyclerView = findViewById(R.id.gem_rv_buy_gem);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3)) ;

        gemNumberTextView = findViewById(R.id.gem_number_tv_buy_gem);

        // 현재 user 정보 초기화
        userInfo = UserInfoSingleton.getInstance();
        userInfo.setGemUi(gemNumberTextView);
    }
    /*
    전체 activity 공통 - 프로필 사진& 닉네임 설정
     */
    @Override
    protected void onStart() {
        super.onStart();
        // 로그인한 계정에 따라 닉네임/프로필 세팅
        LoginSingleton.getInstance(context);
        LoginSingleton.loginOnStart(idTextView,profileImageView);
    }
}
