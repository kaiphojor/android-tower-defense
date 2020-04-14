package com.vortexghost.plaintowerdefense;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONObject;

public class LoginSingleton {
    // firebase 인증
    private static FirebaseAuth mAuth;
    // google 로그인
    private static GoogleSignInClient mGoogleSignInClient;
    //TAG
    static final String TAG = "LoginSingleton";
    private static Context context;
    static LoginSingleton instance;
    public LoginSingleton() {
    }
    /*
    처음 호출할 때에만 instance를 생성한다
    이후 호출은 각 Activity의 context를 갈아끼울 뿐이다.
     */
    public static LoginSingleton getInstance(Context currentContext){
        if(instance == null){
            instance = new LoginSingleton();
        }
        context = currentContext;
        return instance;
    }
    // onCreate 에서 호출 - 처음 로그인하는 Activity에서만 호출한다. 이후에는 호출할 필요없다.
    public static void loginOnCreate(){
        // 로그인에 필요한 정보(id,password)를 요청하는 옵션추가
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getResources().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // 추가한 옵션 바탕으로 google 로그인 변수 초기화
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        // firebase 인증 초기화
        mAuth = FirebaseAuth.getInstance();
    }
    // onStart 에서 호출 - 로그인 상태를 표현해야하는 모든 activity에서 필수
    public static final void loginOnStart(final TextView nicknameTextView, final ImageView profileImageView){
        // 로그인 여부 확인
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
//            Toast.makeText(context, "부적절한 접근", Toast.LENGTH_LONG).show();
        } else {
            // 메인화면 이후에서는 로그인 된 것으로 가정 - 로그인을 서비스 이요 조건으로 삼음
            // 로그인이 되었다면 nickname을 상단에 세팅.
            // 프로필 사진 또한 설정한다
            SharedPreferences sharedPreference = context.getSharedPreferences("sharedPreference",context.MODE_PRIVATE);
//            String nickname = sharedPreference.getString("nickname","");
//            String imageUri = sharedPreference.getString("profileImage","");
            String nickname="";
            String imageUri="";
            String userInfoString = sharedPreference.getString(currentUser.getEmail(),"");
            try{
                JSONObject userInfoObject = new JSONObject(userInfoString);
                nickname = userInfoObject.getString("nickname");
                imageUri = userInfoObject.getString("imageUri");
            }catch(Exception e){

            }

            // 이미지 선택 없을 시 기본 이미지, 선택 했을 경우 해당 이미지로 설정
            Bitmap originalBm = imageUri.contentEquals("") ?
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.team_nova)
                    : BitmapFactory.decodeFile(imageUri, new BitmapFactory.Options());
//            Log.d(TAG, "setImage : " + tempFile.getAbsolutePath());

            profileImageView.setImageBitmap(originalBm);
            nicknameTextView.setText(nickname);
        }
    }

    // 파이어베이스 - 구글 연동 인증 -> 닉네임 세팅
    public static void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity)context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // 성공시 닉네임 세팅
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        // 실패시 가짜 닉네임 세팅
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(context, "Authentication Failed.", Toast.LENGTH_SHORT).show();
//                            String idString = "user0";
//                            nicknameTextView.setText(idString);
//                            Log.i(TAG, idString);
                            updateUI(null);
                        }
                    }
                });
    }
    // onActivityResult 호출시
    public static void loginOnActivityResult(Intent data){
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e);
            // ...
        }
    }


    // UI 갱신
    private static void updateUI(FirebaseUser user) {
        TextView idTextView = ((Activity) context).findViewById(R.id.nickname_tv_main);
        if (user != null) {
            String idString = user.getEmail();
//            idString += ", " + user.getUid();
            idTextView.setText(idString);
            Log.i(TAG, idString);

//            findViewById(R.id.signInButton).setVisibility(View.GONE);
//            findViewById(R.id.signOutAndDisconnect).setVisibility(View.VISIBLE);
        } else {
            String idString = "user0";
            idTextView.setText(idString);
            Log.i(TAG, idString);

//            findViewById(R.id.signInButton).setVisibility(View.VISIBLE);
//            findViewById(R.id.signOutAndDisconnect).setVisibility(View.GONE);
        }
    }

    public static GoogleSignInClient getmGoogleSignInClient() {
        return mGoogleSignInClient;
    }
    public static FirebaseAuth getmAuth(){
        return mAuth;
    }
}
