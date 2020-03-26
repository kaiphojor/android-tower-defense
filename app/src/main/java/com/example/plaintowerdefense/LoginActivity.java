package com.example.plaintowerdefense;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import org.w3c.dom.Text;

public class LoginActivity extends BaseActivity {
    // 로그인 입력 view
    EditText loginIdView;
    EditText loginPasswordView;
    Button loginView;

    TextView nicknameView;
    ImageView profileView;

    // 회원가입 입력 view
    EditText signUpIdView;
    EditText signUpPasswordView;
    Button signUpView;

    // google login 위한 button
    Button googleSignInView;

    // firebase 인증 위한 변수
    private FirebaseAuth mAuth;
    // 태그 = 현재 activity 이름
    private final String TAG = this.getClass().getSimpleName();
    // context = 현재 context
    private Context context = this;
    Intent intent;

    // google 로그인
    private GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        profileView = findViewById(R.id.profile_iv_login);
        nicknameView = findViewById(R.id.nickname_tv_login);

        // firebase와 연동된 구글 로그인 초기화
        LoginSingleton.getInstance(context);
        LoginSingleton.loginOnCreate();

        // view binding
        googleSignInView = findViewById(R.id.google_sign_in_bt_login);

        // 클릭 이벤트 따로 분리
        buttonClick();

    }
    @Override
    public void onStart() {
        super.onStart();
        LoginSingleton.loginOnStart(nicknameView,profileView);

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void updateUI(FirebaseUser user){
        // 로그인 여부 확인
        if(user == null){
            Log.i(TAG,"로그인 하지 않음");
            Toast.makeText(this,"로그인 하지 않음",Toast.LENGTH_LONG).show();
        }else{
            Log.i(TAG,"로그인 함");
            String loginMessage = "";
            loginMessage += "email : " + user.getEmail();
            loginMessage += "displayname : " + user.getDisplayName();
            loginMessage += "uid : " + user.getUid();
            loginMessage += "tostring : " + user.toString();
            Log.i(TAG,loginMessage);
            Toast.makeText(this,"로그인 함",Toast.LENGTH_LONG).show();
        }
    }
    // 따로 구분한 클릭 처리 이벤트
    public void buttonClick() {
        // 입력한 email, password 이용 로그인 진행
        loginIdView = findViewById(R.id.sign_in_email_et_login);
        loginPasswordView = findViewById(R.id.sign_in_password_et_login);
        loginView = findViewById(R.id.sign_in_bt_login);
        loginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailString = loginIdView.getText().toString();
                String passwordString = loginPasswordView.getText().toString();
                loginProcess(emailString,passwordString);
            }
        });

        // 입력한 email, password 이용 회원가입 진행
        signUpIdView = findViewById(R.id.sign_up_email_et_login);
        signUpPasswordView = findViewById(R.id.sign_up_password_et_login);
        signUpView = findViewById(R.id.sign_up_bt_login);
        signUpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailString = signUpIdView.getText().toString();
                String passwordString = signUpPasswordView.getText().toString();
                signUpProcess(emailString,passwordString);

            }
        });

        // id 인증 이동
        googleSignInView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
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
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
//        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // [END_EXCLUDE]
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getInstance().verifyIdToken();
                            FirebaseUser user = mAuth.getCurrentUser();

                            updateUI(user);
                            // shared preference 로 닉네임이 있는지 확인
                            // 처음 가입 시 setting page로 가서 닉네임, 사진 설정,
                            // 이전에 가입했다면 main page 이동
                            SharedPreferences sharedPreference = getSharedPreferences("sharedPreference", MODE_PRIVATE);
                            String text = sharedPreference.getString("nickname","");
                            if(text.contentEquals("")){
                                intent = new Intent(context,IdSettingActivity.class);
                            }else{
                                intent = new Intent(context,MainActivity.class);
                            }
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(context, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
//                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }



    ///////////////////////////////////////////////// 씀레기


    // firebase 인증을 이용한 로그인 프로세스
    public void loginProcess(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // 인증이 되었을 경우
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
    // firebase 인증을 이용한 회원가입 프로세스
    public void signUpProcess(String email,String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
}
