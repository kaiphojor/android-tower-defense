package com.example.plaintowerdefense;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

// user 정보 저장된 singleton. 로그인시 초기화
public class UserInfoSingleton {

    //TAG
    static final String TAG = "LoginSingleton";
    private static Context context;
    static UserInfoSingleton instance;

    String email;
    String imageUri;
    String uid;
    String displayName;
    String nickname;
    int reward;
    int gem;
    int stageNumber;
    StageInfo[] stageInfo;
    boolean[] achievementInfo;
    public class StageInfo{
        boolean isClear;
        int starNumber;
        public StageInfo(boolean isClear, int starNumber) {
            this.isClear = isClear;
            this.starNumber = starNumber;
        }
        public boolean isClear() {
            return isClear;
        }

        public void setClear(boolean clear) {
            isClear = clear;
        }

        public int getStarNumber() {
            return starNumber;
        }

        public void setStarNumber(int starNumber) {
            this.starNumber = starNumber;
        }
    }
    public UserInfoSingleton() {
    }
    /*
    처음 호출할 때에만 instance를 생성한다
     */
    public static UserInfoSingleton getInstance(){
        if(instance == null){
            instance = new UserInfoSingleton();
        }
        return instance;
    }
    public void setUserInfo(Context context, FirebaseUser user,String nick, String uri){
        // 대충 shared preference로 정보 불러와서 저장.
        SharedPreferences sharedPreference = context.getSharedPreferences("sharedPreference",context.MODE_PRIVATE|context.MODE_WORLD_WRITEABLE);
        String email = user.getEmail();
        String userInfoString = sharedPreference.getString(email,"");
        // 없으면 새로 만들기
        if(userInfoString.contentEquals("")){
            stageNumber = 5;
            JSONObject jsonUserInfo = new JSONObject();
            try{

                jsonUserInfo.put("nickname",nick);
                jsonUserInfo.put("imageUri",uri);
                jsonUserInfo.put("uid",user.getUid());
                jsonUserInfo.put("displayName", user.getDisplayName());
                jsonUserInfo.put("reward",0);
                jsonUserInfo.put("gem",0);
                jsonUserInfo.put("stageNumber",stageNumber);

                JSONArray stageArray = new JSONArray();
                for(int i=0; i<stageNumber; i++){
                    JSONObject jsonObjectStage = new JSONObject();
                    jsonObjectStage.put("isClear",false);
                    jsonObjectStage.put("starNumber",0);
                    stageArray.put(jsonObjectStage);
                }
                jsonUserInfo.put("stageArray",stageArray);

                JSONArray achievementArray = new JSONArray();
                for(int i=0; i<5; i++){
                    achievementArray.put(false);
                }
                jsonUserInfo.put("achievement",achievementArray);

                String userInfo = jsonUserInfo.toString();
                Log.i("json result",userInfo);
                // shared prference에 저장.
                SharedPreferences.Editor editor = sharedPreference.edit();
                editor.putString(email,userInfo);
                editor.apply();

            }catch(Exception e){
                e.printStackTrace();
            }




        }else{
            // 있으면 불러오기
            try{
                JSONObject jsonUserInfo = new JSONObject(userInfoString);
                Log.i("json result after sign in",userInfoString);
                nickname = jsonUserInfo.getString("nickname");
                imageUri = jsonUserInfo.getString("imageUri");
                uid = jsonUserInfo.getString("uid");
                displayName = jsonUserInfo.getString("displayName");
                reward = jsonUserInfo.getInt("reward");
                gem = jsonUserInfo.getInt("gem");
                stageNumber = jsonUserInfo.getInt("stageNumber");
                String stageString = jsonUserInfo.getString("stageArray");
                stageInfo = new StageInfo[stageNumber];
                JSONArray jsonArrayStage = new JSONArray(stageString);
                for(int i=0; i<jsonArrayStage.length(); i++){
                    JSONObject jsonObjectStage = jsonArrayStage.getJSONObject(i);
                    boolean isClear = jsonObjectStage.getBoolean("isClear");
                    int starNumber = jsonObjectStage.getInt("starNumber");
                    stageInfo[i] = new StageInfo(isClear,starNumber);
                }
                String achievementString = jsonUserInfo.getString("achievement");
                JSONArray jsonArrayAchievement = new JSONArray(achievementString);
                achievementInfo = new boolean[jsonArrayAchievement.length()];
                for(int i=0; i<jsonArrayAchievement.length();i++){
                    achievementInfo[i] = (boolean)jsonArrayAchievement.get(i);
                    Log.i("json boolean from array",achievementInfo[i]+"");
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        // 없으면 새로 만듬

    }

}
