package com.vortexghost.plaintowerdefense.error_collect;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

/*
전체 activity는 이 activity를 확장하여 사용한다
 */
public class BaseActivity extends AppCompatActivity {

    private Thread.UncaughtExceptionHandler handleAppCrash =
            new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    Long uid;
                    /*
                    email, crach 발생 시간
                     */
                    // 사용자 email
//                    String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    //TODO: 더미 데이터
                    String userEmail = "jihoopark7666@gmail.com";
//                    Log.i("error_report",userEmail);
                    // crash 발생 시간
                    SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String crashTime = timeFormat.format(new Date(System.currentTimeMillis()));
                    // exception 이름 및 설명
                    String description = ex.fillInStackTrace().getCause().toString();
                    // stack trace
                    String stackTrace = "";
                    for(StackTraceElement e :ex.getCause().getStackTrace()){
                        stackTrace += e.toString() + "\n";
                    }


                    // crash report 작성할 db helper 초기화
                    CrashReportDBHelper dbHelper = new CrashReportDBHelper(getApplicationContext(),"crash_report");
                    // 쓰기모드로 sqlite db 초기화
                    SQLiteDatabase crashReportdb = dbHelper.getWritableDatabase();
                    // db에 기록하기
                    ContentValues crashContentValues = new ContentValues();
                    crashContentValues.put(dbHelper.EMAIL,userEmail);
                    crashContentValues.put(dbHelper.CRASH_TIME,crashTime);
                    crashContentValues.put(dbHelper.CRASH_DESCRIPTION,description);
                    crashContentValues.put(dbHelper.STACK_TRACE, stackTrace);
                    long primaryKeyId = crashReportdb.insert(dbHelper.TABLE_NAME, null, crashContentValues);
                    Log.i("error_report","PRIMARY_KEY_ID : "+primaryKeyId);

                    /*
                    TODO : email 기능
                     */
                    //send email here
//                    Intent email = new Intent(Intent.ACTION_SEND);
//                    email.setType("plain/text");
//                    // email setting 배열로 해놔서 복수 발송 가능
//                    String[] address = {"jihoopark7666@gmail.com"};
//                    email.putExtra(Intent.EXTRA_EMAIL, address);

//                    email.putExtra(Intent.EXTRA_TEXT,"time : " + time +"\nactivity : "+activityName + "error message : " +ex.toString());
//                    startActivity(email);
                    // 에러 발생 후 먹통된 activity를 종료
                    finish();
                }
            };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(handleAppCrash);
        Log.i("error_report","BASEACTIVITY_ACTIVATED");
//        Singleton.log("sd");
        //                        Log.i("error_report",e.toString());
//                    Log.i("error_report",time);
//                    Log.i("error_report",ex.fillInStackTrace().getCause().toString());

//                    Log.w("error_report",stackTrace);
//                    String activityName = this.getClass().getSimpleName();

//                    Log.i("error_report","LOCAL : "+ex.getLocalizedMessage());
//                    Log.i("error_report","MESSAGE : "+ex.getMessage());
//                    Log.i("error_report","STRING : "+ex.toString());
//                    Log.i("error_report","START");
//                    Log.i("error_report",ex.fillInStackTrace().toString());
        //                        Log.i("error_report",e.getClassName());
//                        Log.i("error_report",e.getMethodName());
        // Activity.java
//                        Log.i("error_report",e.getFileName());
        // line
//                        Log.i("error_report",e.getLineNumber()+" ");
//                        Log.i("error_report",e.isNativeMethod() + " ");
//                    Log.i("error_report","END");
//                    email.putExtra(Intent.EXTRA_SUBJECT,"crash report");
//
    }

}