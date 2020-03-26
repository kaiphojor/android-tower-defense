package com.example.plaintowerdefense;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Singleton {
    // 인스턴스 저장용 변수. private으로 외부호출 통제
    private static Singleton uniqueInstance;
    // 각 activity에서 사용할 context
    private static Context context;
    // 생성자 또한 private으로 외부호출 통제
    private Singleton(){

    }

    // 인스턴스 반환 메소드
    public static Singleton getInstance(Context currentContext){
        // 인스턴스 존재하지 않으면 한번만 생성
        if(uniqueInstance == null){
            uniqueInstance = new Singleton();
        }
        context = currentContext;
        return uniqueInstance;
    }
    // 로그
    public static void log(String message){
        Log.i(context.getClass().getSimpleName(),message);
    }
    // 토스트 출력
    public static void toast(String text,boolean isLong){
        int duration = isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        Toast.makeText(context, text, duration).show();
    }
}
