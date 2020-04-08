package com.example.plaintowerdefense;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

import com.example.plaintowerdefense.user_info.UserInfoSingleton;

import org.json.JSONObject;

public class SoundSingleton {


//    boolean isBgmMute = false;
//    int bgmVolume = 100;
//    boolean isSfxMute = false;
//    int sfxVolume = 100;
    static boolean isBgmMute;
    static int bgmVolume;
    static boolean isSfxMute;
    static int sfxVolume;

    static SoundSingleton instance;
    public SoundSingleton() {
    }

    /*
    처음 호출할 때에만 instance를 생성한다
     */
    public static SoundSingleton getInstance() {
        if (instance == null) {
            instance = new SoundSingleton();
        }
        return instance;
    }
    // singleton을 초기화한다
    public static void initSoundSingleton(Context context){
        SharedPreferences soundPreference = context.getSharedPreferences("setting",context.MODE_PRIVATE);
        try{
            String soundString = soundPreference.getString("sound","");
            // 없으면 새로 만들기
            if(soundString.contentEquals("")){
                JSONObject soundJsonObject = new JSONObject();
                // bgm 음소거 여부, 음량 조절 . 적용시 0.01f 에 곱해서 적용
                JSONObject bgmJsonObject = new JSONObject();
                bgmJsonObject.put("isMute",false);
                bgmJsonObject.put("volume",100);
                soundJsonObject.put("bgm",bgmJsonObject);
                // 효과음 음소거 여부, 음량 조절 . 적용시 0.01f 에 곱해서 적용
                JSONObject sfxJsonObject = new JSONObject();
                sfxJsonObject.put("isMute",false);
                sfxJsonObject.put("volume",100);
                soundJsonObject.put("sfx",sfxJsonObject);
                // shared preference 에 저장
                SharedPreferences.Editor editor = soundPreference.edit();
                editor.putString("sound",soundJsonObject.toString());
                editor.apply();
                // 값 초기화
                isBgmMute = false;
                bgmVolume = 100;
                isSfxMute = false;
                sfxVolume = 100;
            }else{
                //있으면 불러온다
                JSONObject soundJsonObject = new JSONObject(soundString);
                // bgm 음소거 여부, 음량 조절 . 적용시 0.01f 에 곱해서 적용
                String bgmString = soundJsonObject.getString("bgm");
                JSONObject bgmJsonObject = new JSONObject(bgmString);
                isBgmMute = bgmJsonObject.getBoolean("isMute");
                bgmVolume = bgmJsonObject.getInt("volume");
                // 효과음 음소거 여부, 음량 조절 . 적용시 0.01f 에 곱해서 적용
                String sfxString = soundJsonObject.getString("sfx");
                JSONObject sfxJsonObject = new JSONObject(sfxString);
                isSfxMute = sfxJsonObject.getBoolean("isMute");
                sfxVolume = sfxJsonObject.getInt("volume");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    // 변경사항을 shared preference에 반영한다
    public void updateSoundSingleton(Context context){
        SharedPreferences soundPreference = context.getSharedPreferences("setting",context.MODE_PRIVATE);
        try{
            JSONObject soundJsonObject = new JSONObject();
            // bgm 음소거 여부, 음량 조절 . 적용시 0.01f 에 곱해서 적용
            JSONObject bgmJsonObject = new JSONObject();
            bgmJsonObject.put("isMute",isBgmMute);
            bgmJsonObject.put("volume",bgmVolume);
            soundJsonObject.put("bgm",bgmJsonObject);
            // 효과음 음소거 여부, 음량 조절 . 적용시 0.01f 에 곱해서 적용
            JSONObject sfxJsonObject = new JSONObject();
            sfxJsonObject.put("isMute",isSfxMute);
            sfxJsonObject.put("volume",sfxVolume);
            soundJsonObject.put("sfx",sfxJsonObject);
            // shared preference 에 저장
            SharedPreferences.Editor editor = soundPreference.edit();
            editor.putString("sound",soundJsonObject.toString());
            editor.apply();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static boolean isIsBgmMute() {
        return isBgmMute;
    }

    public static void setIsBgmMute(boolean isBgmMute) {
        SoundSingleton.isBgmMute = isBgmMute;
    }

    public static int getBgmVolume() {
        return bgmVolume;
    }

    public static void setBgmVolume(int bgmVolume) {
        SoundSingleton.bgmVolume = bgmVolume;
    }

    public static boolean isIsSfxMute() {
        return isSfxMute;
    }

    public static void setIsSfxMute(boolean isSfxMute) {
        SoundSingleton.isSfxMute = isSfxMute;
    }

    public static int getSfxVolume() {
        return sfxVolume;
    }

    public static void setSfxVolume(int sfxVolume) {
        SoundSingleton.sfxVolume = sfxVolume;
    }
}
