package com.vortexghost.plaintowerdefense.gem_shop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.vortexghost.plaintowerdefense.BuildConfig;
import com.vortexghost.plaintowerdefense.Singleton;
import com.vortexghost.plaintowerdefense.error_collect.BaseActivity;
import com.vortexghost.plaintowerdefense.LoginSingleton;
import com.vortexghost.plaintowerdefense.R;
import com.vortexghost.plaintowerdefense.social.UserRecyclerViewAdapter;
import com.vortexghost.plaintowerdefense.user_info.UserInfoSingleton;

import java.util.ArrayList;
import java.util.List;

public class BuyGemActivity extends BaseActivity implements BillingProcessor.IBillingHandler
 {
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
    // 결제 클라이언트
    private BillingProcessor billingProcessor;
    private BillingClient billingClient;
    Gem selectedItem;
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
//        gemListData.add(new Gem("보석 1개",300,R.drawable.diamond,1,"gem_1"));
//        gemListData.add(new Gem("보석 5개",1500,R.drawable.diamond_5,5,"gem_5"));
        gemListData.add(new Gem("보석 10개",3000,R.drawable.diamond_10,10,"gem_10"));
        gemListData.add(new Gem("보석 20개",6000,R.drawable.diamond_20,20,"gem_20"));
        gemListData.add(new Gem("보석 50개",12000,R.drawable.diamond_50,50,"gem_50"));
        gemListData.add(new Gem("보석 100개",22000,R.drawable.diamond_100,100,"gem_100"));
        gemListData.add(new Gem("보석 200개",40000,R.drawable.diamond_200,200,"gem_200"));
        gemListData.add(new Gem("보석 500개",80000,R.drawable.diamond_500,500,"gem_500"));
        // data를 adapter에 담고 recyclerview 표시
        adapter = new GemRecyclerViewAdapter(context);
        adapter.setData(gemListData);
        adapter.setOnItemClickListener(new GemRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                int id = v.getId();
                Gem gemData = gemListData.get(position);
                selectedItem = gemData;
                int price = gemData.getPrice();
                String gemString = gemData.getTitle();
                int amount = gemData.getAmount();
//                Singleton.toast(gemString + " " + amount + "개 "+price + "원",false);
//                connectBillingClient();
                buyItem(selectedItem);
//                출처: https://jizard.tistory.com/164 [GEUMSON]
            }
        }) ;

        recyclerView = findViewById(R.id.gem_rv_buy_gem);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3)) ;

        gemNumberTextView = findViewById(R.id.gem_number_tv_buy_gem);

        // 현재 user 정보 초기화
        userInfo = UserInfoSingleton.getInstance();
        userInfo.setGemUi(gemNumberTextView);
        // singleton 초기화
        Singleton.getInstance(context);
        // 결제 라이브러리 초기화 . api key는 gitignore에 추가한 파일에서 가져온다
        billingProcessor = new BillingProcessor(this, BuildConfig.GOOGLE_PLAY_LICENSE_KEY, this);
        billingProcessor.initialize();
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
     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if (!billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
             super.onActivityResult(requestCode, resultCode, data);
         }
     }
     @Override
     public void onDestroy() {
         if (billingProcessor != null) {
             billingProcessor.release();
         }
         super.onDestroy();
     }

//    public void connectBillingClient(){
//        billingClient = BillingClient.newBuilder(context).enablePendingPurchases().setListener(this).build();
//        billingClient.startConnection(new BillingClientStateListener() {
//            @Override
//            public void onBillingSetupFinished(BillingResult billingResult) {
//                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
//                    // The BillingClient is ready. You can query purchases here.
////                    showPurchaseDialog(billingClient);
//                }
//            }
//            @Override
//            public void onBillingServiceDisconnected() {
//                // Try to restart the connection on the next request to
//                // Google Play by calling the startConnection() method.
//            }
//        });
//    }
    // sku parameter 등록
    public SkuDetailsParams.Builder makeBillingParams(){
        List<String> skuList = new ArrayList<> ();
        skuList.add("premium_upgrade");
        skuList.add("gas");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        return params;
    }
    public void showPurchaseDialog(BillingClient billingClient){

//        // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
//        billingClient.querySkuDetailsAsync(makeBillingParams().build()) { billingResult, skuDetailsList ->
//            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
//                for (skuDetails in skuDetailsList) {
//                    val sku = skuDetails.sku
//                    val price = skuDetails.price
//                    if ("remove_ad" == sku) {
//                        val flowParams = BillingFlowParams.newBuilder()
//                                .setSkuDetails(skuDetails)
//                                .build()
//                        val responseCode = billingClient.launchBillingFlow(activity, flowParams)
//                    }
//                }
//            }
//        }
    }


    public void makeBillingParamete(){
        List<String> skuList = new ArrayList<> ();
        skuList.add("premium_upgrade");
        skuList.add("gas");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        // Process the result.
                    }
                });
    }

     @Override
     public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
         // * 구매 완료시 호출
         // productId: 구매한 sku (ex) no_ads)
         // details: 결제 관련 정보
         // 이 메소드는 구매 '성공'시에만 호출된다.
         if (productId.equals(selectedItem.getStockKeepingUnit())) {
             // TODO: 구매 해 주셔서 감사합니다! 메세지 보내기
//             Singleton.toast("구매 감사합니다!",true);
//             storage.setPurchasedRemoveAds(billingProcessor.isPurchased(selectedItem.getStockKeepingUnit()));

             // * 광고 제거는 1번 구매하면 영구적으로 사용하는 것이므로 consume하지 않지만,
             // 만약 게임 아이템 100개를 주는 것이라면 아래 메소드를 실행시켜 다음번에도 구매할 수 있도록 소비처리를 해줘야한다.
             // bp.consumePurchase(Config.Sku);
         }

     }

     @Override
     public void onPurchaseHistoryRestored() {
         // * 구매 정보가 복원되었을때 호출
         // bp.loadOwnedPurchasesFromGoogle() 하면 호출 가능
//         storage.setPurchasedRemoveAds(billingProcessor.isPurchased(selectedItem.getStockKeepingUnit()));

     }

     @Override
     public void onBillingError(int errorCode, @Nullable Throwable error) {
         // * 구매 오류시 호출
         // errorCode == Constants.BILLING_RESPONSE_RESULT_USER_CANCELED 일때는
         // 사용자가 단순히 구매 창을 닫은것임으로 이것 제외하고 핸들링하기.
          /*
        TODO: 이런식으로 구매 오류시 오류가 발생했다고 알려주는 것도 좋다.

        if (errorCode != Constants.BILLING_RESPONSE_RESULT_USER_CANCELED) {
            Snackbar.make(tvRemoveAds, R.string.unknown_error, Snackbar.LENGTH_SHORT).show();
        }
         */
     }

     @Override
     public void onBillingInitialized() {
         // * 처음에 초기화되었을때.
         // storage에 구매여부 저장
//         storage.setPurchasedRemoveAds(billingProcessor.isPurchased(selectedItem.getStockKeepingUnit()));

     }
     public void buyItem(Gem gem){
//        Singleton.toast(gem.getStockKeepingUnit(),false);
        billingProcessor.purchase(this, gem.getStockKeepingUnit());

     }



//     출처: https://jizard.tistory.com/164 [GEUMSON]
 }
