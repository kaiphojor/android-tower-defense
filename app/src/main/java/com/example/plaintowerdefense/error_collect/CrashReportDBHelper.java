package com.example.plaintowerdefense.error_collect;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;

public class CrashReportDBHelper extends SQLiteOpenHelper {
    // 상수 string 들. 취사선택 해야.
    public static final String DATABASE_NAME = "userdb";

    public static final String TABLE_NAME = "crash_report";
    public static final String C_ID = "_id";
    public static final String EMAIL = "email";
    public static final String CRASH_TIME = "crash_time";
    public static final String CRASH_DESCRIPTION = "crash_description";
    public static final String STACK_TRACE = "stack_trace";

    public static final int DATABASE_VERSION = 2;

//    출처: https://link2me.tistory.com/1694 [소소한 일상 및 업무TIP 다루기]

    public CrashReportDBHelper(@Nullable Context context, @Nullable String name) {
        super(context, name, null, DATABASE_VERSION);
    }

    /* Inner class that defines the table contents */
    private final String SQL_CREATE_TABLE = "create table if not exists "
            + TABLE_NAME + " ( "
            + C_ID + " integer primary key autoincrement, "
            + EMAIL + " text, "
            + CRASH_TIME + " text, "
            + CRASH_DESCRIPTION + " text, "
            + STACK_TRACE + " text)";

    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    // 처음 만들어질 때 실행
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 명령문 하나 실행 - db 만들기
        db.execSQL(SQL_CREATE_TABLE);
    }
    // 테이블 삭제, 추가, 버전 업그레이드시 불러짐
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 테이블 전체 삭제 후 다시 만들기. online의 data를 caching 하기 때문에 갱신이 필요함
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_TABLE);
//        onCreate(db);
    }
    // downgrade 와  ungrade는 같은 명령 수행
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public Cursor LoadSQLiteDBCursor() {
        //TODO : fix it
//        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String userEmail = "jihoopark7666@gmail.com";

        // db를 불러오고, 상호작용을 시작
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        // Select All Query 모든 필드를 가져온다  -- 조건 추가 현재 user의 crash log만 가져온다
        String selectQuery = "SELECT _id,email,crash_time,crash_description,stack_trace FROM " + TABLE_NAME;
//                + " WHERE email is " + userEmail;
        // cursor 라는 DB Data interface 를 이용해서 query 문을 통해 데이터를 받아온다
        // 성공하면 결과는 cursor를 통해 저장된다
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(selectQuery, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return cursor;
    }



//// 나중에 변수로 바꿀때 필요
//    public String createDBQuery(){
//        return "";
//    }
}
