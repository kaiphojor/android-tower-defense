package com.example.plaintowerdefense;

import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.plaintowerdefense.error_collect.BaseActivity;
import com.example.plaintowerdefense.id_setting.ImageResizeUtils;
import com.example.plaintowerdefense.user_info.UserInfoSingleton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class IdSettingActivity extends BaseActivity implements View.OnClickListener{

    EditText nicknameView;
    ImageView profileImageView;
    Button submitSettingView;
    ImageView cameraIconImageView;

    // 전 activity 공통 변수
    // 태그 = 현재 activity 이름
    private final String TAG = this.getClass().getSimpleName();
    // context = 현재 context
    private Context context = this;
    Intent intent;
    // 앨범 선택 코드
    private static final int PICK_FROM_ALBUM = 1;
    // 카메라 선택 코드
    private static final int PICK_FROM_CAMERA = 2;
    // view에 표시할 임시 이미지 파일
    private File tempFile;
    // 사진을 카메라로 찍었는지, 갤러리에서 가져왔는지 여부
    private Boolean isCamera = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_setting);
        // view binding
        nicknameView = findViewById(R.id.nickname_et_id_setting);
        profileImageView = findViewById(R.id.profile_image_iv_id_setting);
        submitSettingView = findViewById(R.id.submit_setting_bt_id_setting);
        cameraIconImageView = findViewById(R.id.camera_iv_id_setting);
        // click listener 등록
        submitSettingView.setOnClickListener(this);
        profileImageView.setOnClickListener(this);
        cameraIconImageView.setOnClickListener(this);

        // 권한 요청
        tedPermission();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch(id){
            case R.id.submit_setting_bt_id_setting:
                // 닉네임과 사진이 조건에 맞는지 확인 후 저장
                // 그리고 메인화면으로 전환
                // shared preference는 추후 client를 서버와 연동할 때 서버쪽으로 돌릴 계획이다.
                // 지금은 shared preference에 닉네임 저장됨
                //TODO : 공백 검사
                //TODO : 글자수 제한 검사, 특수문자 제거
                //TODO : shared preference에 json file 형식으로 저장(추후)
                String nickname = nicknameView.getText().toString();

                String imageUri = tempFile != null ? tempFile.getAbsolutePath() : "";
                // 검사 통과했을 때 정보 저장
                SharedPreferences sharedPreference = getSharedPreferences("sharedPreference", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreference.edit();
                editor.putString("nickname",nickname);
                editor.putString("profileImage",imageUri);
                // 유저 정보 등록
                registerUserInfo(nickname,imageUri);
                editor.apply();
                // activity stack을 다 비우고 main 만 남게 한다
                intent = new Intent(context,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            // 앨범에 접근하여 사진을 가져온다
            case R.id.profile_image_iv_id_setting :
                goToAlbum();
                break;
            // 카메라로 사진을 찍는다
            case R.id.camera_iv_id_setting :
                takePhoto();
                break;
        }
    }
    // 앨범에서 사진 선택
    private void goToAlbum() {
        isCamera = false;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 선택 도중 취소했을 경우
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            if(tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        Log.e(TAG, tempFile.getAbsolutePath() + " 삭제 성공");
                        tempFile = null;
                    }
                }
            }
            return;
        }
        Uri photoUri;
        // 선택에 성공했을 경우
        switch(requestCode){

            case PICK_FROM_ALBUM :
                // 사진 선택 후 결과
                photoUri = data.getData();
                Cursor cursor = null;

                try {
                    /*
                     *  Uri 스키마를
                     *  content:/// 에서 file:/// 로  변경한다.
                     *  절대경로를 받아오는 과정
                     */
                    String[] proj = {MediaStore.Images.Media.DATA};

                    assert photoUri != null;
                    cursor = getContentResolver().query(photoUri, proj, null, null, null);

                    assert cursor != null;
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                    cursor.moveToFirst();

                    tempFile = new File(cursor.getString(column_index));

                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                cropImage(photoUri);
//                setImage();
                break;
            // 카메라에서 찍기 선택
            case PICK_FROM_CAMERA :
                photoUri = Uri.fromFile(tempFile);
                cropImage(photoUri);
//                setImage();
                break;
            case Crop.REQUEST_CROP:
                setImage();
                // crop 한 파일 가져오기
                //File cropFile = new File(Crop.getOutput(data).getPath());

        }
    }
    // 가져온 image를 crop 해서 아이콘에 알맞게 변환한다
    private void cropImage(Uri photoUri) {

        Log.d(TAG, "tempFile : " + tempFile);

        /**
         *  갤러리에서 선택한 경우에는 tempFile 이 없으므로 새로 생성해줍니다.
         */
        if(tempFile == null) {
            try {
                tempFile = createImageFile();
            } catch (IOException e) {
                Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                finish();
                e.printStackTrace();
            }
        }

        //크롭 후 저장할 Uri
        Uri savingUri = Uri.fromFile(tempFile);
        // crop 하는 method. 정사각형 모양으로 crop한다
        Crop.of(photoUri, savingUri).asSquare().start(this);
    }
    // 이미지를 view에 세팅
    private void setImage() {
        ImageView imageView = findViewById(R.id.profile_image_iv_id_setting);
        // resizing. 긴 부분을 1280 사이즈로 줄인다
//        ImageResizeUtils.resizeFile(tempFile, tempFile, 1280, isCamera);
        ImageResizeUtils.resizeFile(tempFile, tempFile, 1280, true );

        BitmapFactory.Options options = new BitmapFactory.Options();
        // 절대경로를 통해 가져온다
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        Log.d(TAG, "setImage : " + tempFile.getAbsolutePath());

        imageView.setImageBitmap(originalBm);
    }

    // 권한 요청을 쉽게 만들어주는 라이브러리 사용
    private void tedPermission(){
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(context, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        // 각종 설정 후 권한 확인
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use profile service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                .check();
    }
    // 카메라로 사진 찍기
    private void takePhoto() {
        isCamera = true;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            tempFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
//        if (tempFile != null) {
//
//            Uri photoUri = Uri.fromFile(tempFile);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//            startActivityForResult(intent, PICK_FROM_CAMERA);
//        }
        if (tempFile != null) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                Uri photoUri = FileProvider.getUriForFile(this,
                        "{package name}.provider", tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            } else {

                Uri photoUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            }
        }

    }
    // 이미지가 들어갈 파일을 만든다
    private File createImageFile() throws IOException {

        // 이미지 파일 이름 ( blackJin_{시간}_ )
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "blackJin_" + timeStamp + "_";

        // 이미지가 저장될 폴더 이름 ( blackJin )
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/blackJin/");
        if (!storageDir.exists()) storageDir.mkdirs();

        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;
    }

    // 사용자 정보를 등록한다
    public void registerUserInfo(String nickname,String imageUri){
        LoginSingleton.getInstance(context);
        LoginSingleton.loginOnCreate();
        FirebaseAuth mAuth = LoginSingleton.getmAuth();
        FirebaseUser user = mAuth.getCurrentUser();
        UserInfoSingleton userInfo = UserInfoSingleton.getInstance();
        userInfo.setUserInfo(context,user,nickname,imageUri);

    }
}
