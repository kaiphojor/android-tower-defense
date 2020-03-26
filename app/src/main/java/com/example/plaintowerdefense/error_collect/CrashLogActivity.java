package com.example.plaintowerdefense.error_collect;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.plaintowerdefense.LoginSingleton;
import com.example.plaintowerdefense.MainActivity;
import com.example.plaintowerdefense.R;
import com.example.plaintowerdefense.Singleton;
import com.example.plaintowerdefense.gem_shop.BuyGemActivity;
import com.example.plaintowerdefense.social.UserRecyclerViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CrashLogActivity extends AppCompatActivity {

    ArrayList<CrashLog> crashLogList;
    SQLiteDatabase db;
    CrashReportDBHelper dbHelper;
    CrashLogRVAdapter logAdapter;
    /*
    전체 activity 공통 변수
     */
    // 로그인 용
    TextView nicknameTextView;
    ImageView profileImageView;
    // 태그 = 현재 activity 이름
    private final String TAG = this.getClass().getSimpleName();
    // context = 현재 context
    private Context context = this;
    // intent
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_log);
        /*
            전체 activity 공통
         */
        // 로그인 상태 표시
        nicknameTextView = findViewById(R.id.nickname_tv_crash_log);
        profileImageView = findViewById(R.id.profile_iv_crash_log);

        // DB helper 초기화
        crashLogList = new ArrayList<>();
        dbHelper = new CrashReportDBHelper(this, "crash_report");
        db = dbHelper.getReadableDatabase();
        db.beginTransaction();

        // cursor를 얻어온다
        Cursor cursor = dbHelper.LoadSQLiteDBCursor();
        try {
            cursor.moveToFirst();
            System.out.println("SQLiteDB 개수 = " + cursor.getCount());
            // db에 있는 모든 record를 순회하면서 list에 추가한다.
            while (!cursor.isAfterLast()) {
                addGroupItem(cursor.getLong(0), cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4));
                cursor.moveToNext();
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                db.endTransaction();
            }
        }
        // 삭제를 위한 dummy data
        crashLogList.add(new CrashLog(9999,"surfaceview@naver.com", "20200430", "dummy data", "dummy stack trace"));
        //recycleView 초기화
        final RecyclerView crashLogRV = findViewById(R.id.rv_crash_log);
        crashLogRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        logAdapter = new CrashLogRVAdapter(this, crashLogList);
        // 삭제 버튼 click listener
        logAdapter.setOnButtonClickListener(new CrashLogRVAdapter.OnButtonClickListener() {
            // item 삭제
            @Override
            public void onDeleteClick(View v, int position) {
//                Singleton.toast(position + ": 삭제", false);
                logAdapter.deleteItem(position);
            }
        });
        // dialog 출력 버튼
        logAdapter.setOnItemClickListener(new CrashLogRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
//                Singleton.toast(position + ": 클릭", false);
                showDetailCrashLog(position);
            }
        });
        crashLogRV.setAdapter(logAdapter);
    }

    /*
    전체 activity 공통
     */
    @Override
    protected void onStart() {
        super.onStart();
        // 로그인한 계정에 따라 닉네임/프로필 세팅
        LoginSingleton.getInstance(context);
        LoginSingleton.loginOnStart(nicknameTextView, profileImageView);
    }

    public void addGroupItem(Long uid, String email, String time, String description, String stackTrace) {
        CrashLog item = new CrashLog(uid, email, time, description, stackTrace);
        item.setId(uid);
        item.setTime(time);
        item.setDescription(description);
        item.setStackTrace(stackTrace);

        crashLogList.add(item);
    }

    // 팝업을 띄워주는 기능
    public void showDetailCrashLog(int position){

        // xml -> view 한뒤 view binding
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_crash_detail, null);

        final TextView numberView = dialogView.findViewById(R.id.number_tv_crash_detail);
        final TextView timeView = dialogView.findViewById(R.id.time_tv_crash_detail);
        final TextView exceptionView = dialogView.findViewById(R.id.exception_tv_crash_detail);
        final TextView stackTraceView = dialogView.findViewById(R.id.stack_trace_tv_crash_detail);
        // 해당 위치의 로그를 가져와서 view에 값을 설정
        CrashLog log = crashLogList.get(position);
        numberView.setText(log.getId()+"");
        timeView.setText(log.getTime());
        exceptionView.setText(log.getDescription());
        stackTraceView.setText(log.getStackTrace());

        // builder 초기화 및 전체 layout binding
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        // 닫기 버튼
        builder.setPositiveButton("닫기", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int pos)
            {
                Singleton.log("팝업 닫기 눌림");
            }
        });
        // dialog 초기화 및 띄우기
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}
