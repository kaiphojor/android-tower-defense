package com.vortexghost.plaintowerdefense;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

public class SoundSingleton {


    static boolean bgmMute;
    static int bgmVolume;
    static boolean sfxMute;
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
                bgmMute = false;
                bgmVolume = 100;
                sfxMute = false;
                sfxVolume = 100;
            }else{
                //있으면 불러온다
                JSONObject soundJsonObject = new JSONObject(soundString);
                // bgm 음소거 여부, 음량 조절 . 적용시 0.01f 에 곱해서 적용
                String bgmString = soundJsonObject.getString("bgm");
                JSONObject bgmJsonObject = new JSONObject(bgmString);
                bgmMute = bgmJsonObject.getBoolean("isMute");
                bgmVolume = bgmJsonObject.getInt("volume");
                // 효과음 음소거 여부, 음량 조절 . 적용시 0.01f 에 곱해서 적용
                String sfxString = soundJsonObject.getString("sfx");
                JSONObject sfxJsonObject = new JSONObject(sfxString);
                sfxMute = sfxJsonObject.getBoolean("isMute");
                sfxVolume = sfxJsonObject.getInt("volume");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    // 변경사항을 shared preference에 반영한다
    public static void updateSoundSingleton(Context context){
        SharedPreferences soundPreference = context.getSharedPreferences("setting",context.MODE_PRIVATE);
        try{
            JSONObject soundJsonObject = new JSONObject();
            // bgm 음소거 여부, 음량 조절 . 적용시 0.01f 에 곱해서 적용
            JSONObject bgmJsonObject = new JSONObject();
            bgmJsonObject.put("isMute", bgmMute);
            bgmJsonObject.put("volume",bgmVolume);
            soundJsonObject.put("bgm",bgmJsonObject);
            // 효과음 음소거 여부, 음량 조절 . 적용시 0.01f 에 곱해서 적용
            JSONObject sfxJsonObject = new JSONObject();
            sfxJsonObject.put("isMute", sfxMute);
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

    public static boolean isBgmMute() {
        return bgmMute;
    }

    public static void setBgmMute(boolean bgmMute) {
        SoundSingleton.bgmMute = bgmMute;
    }

    public static int getBgmVolume() {
        return bgmVolume;
    }

    public static void setBgmVolume(int bgmVolume) {
        SoundSingleton.bgmVolume = bgmVolume;
    }

    public static boolean isSfxMute() {
        return sfxMute;
    }

    public static void setSfxMute(boolean sfxMute) {
        SoundSingleton.sfxMute = sfxMute;
    }

    public static int getSfxVolume() {
        return sfxVolume;
    }

    public static void setSfxVolume(int sfxVolume) {
        SoundSingleton.sfxVolume = sfxVolume;
    }
}
