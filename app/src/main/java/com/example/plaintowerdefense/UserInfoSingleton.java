package com.example.plaintowerdefense;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

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
    static int gem;
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
    // user가 가진 보석 숫자를 표시해준다
    public static void setGemUi(final TextView gemTextView){
        gemTextView.setText(String.valueOf(gem));
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
    // 이미 저장해놨던 유저 정보 불러와서 초기화
    public void initUserInfo(Context context,FirebaseAuth mAuth){
        // 대충 shared preference로 정보 불러와서 저장.
        SharedPreferences sharedPreference = context.getSharedPreferences("sharedPreference",context.MODE_PRIVATE);
        email = mAuth.getCurrentUser().getEmail();
        String userInfoString = sharedPreference.getString(email,"");
        if(!userInfoString.contentEquals("")){
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
        }else{
            Log.i("userinfoSingleton","user info load error");
        }

    }
    // 처음 로그인, id setting 할때 처리
    public void setUserInfo(Context context, FirebaseUser user,String nick, String uri){
        // 대충 shared preference로 정보 불러와서 저장.
        SharedPreferences sharedPreference = context.getSharedPreferences("sharedPreference",context.MODE_PRIVATE|context.MODE_WORLD_WRITEABLE);
        email = user.getEmail();
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
    // 업데이트한 정보 shared preference에 저장
    public void updateUserInfo(){
        // 대충 shared preference로 정보 불러와서 저장.
        SharedPreferences sharedPreference = context.getSharedPreferences("sharedPreference",context.MODE_PRIVATE|context.MODE_WORLD_WRITEABLE);
//        String email = user.getEmail();
//        String userInfoString = sharedPreference.getString(email,"");
        // 없으면 새로 만들기
//        if(userInfoString.contentEquals("")){
            stageNumber = 5;
            JSONObject jsonUserInfo = new JSONObject();
            try{

                jsonUserInfo.put("nickname",nickname);
                jsonUserInfo.put("imageUri",imageUri);
                jsonUserInfo.put("uid",uid);
                jsonUserInfo.put("displayName", displayName);
                jsonUserInfo.put("reward",reward);
                jsonUserInfo.put("gem",gem);
                jsonUserInfo.put("stageNumber",stageNumber);

                JSONArray stageArray = new JSONArray();
                for(int i=0; i<stageNumber; i++){
                    JSONObject jsonObjectStage = new JSONObject();
                    jsonObjectStage.put("isClear",stageInfo[i].isClear());
                    jsonObjectStage.put("starNumber",stageInfo[i].getStarNumber());
                    stageArray.put(jsonObjectStage);
                }
                jsonUserInfo.put("stageArray",stageArray);

                JSONArray achievementArray = new JSONArray();
                for(int i=0; i<5; i++){
                    achievementArray.put(achievementInfo[i]);
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
//
//        }else{
//            // 있으면 불러오기
//            try{
//                JSONObject jsonUserInfo = new JSONObject(userInfoString);
//                Log.i("json result after sign in",userInfoString);
//                nickname = jsonUserInfo.getString("nickname");
//                imageUri = jsonUserInfo.getString("imageUri");
//                uid = jsonUserInfo.getString("uid");
//                displayName = jsonUserInfo.getString("displayName");
//                reward = jsonUserInfo.getInt("reward");
//                gem = jsonUserInfo.getInt("gem");
//                stageNumber = jsonUserInfo.getInt("stageNumber");
//                String stageString = jsonUserInfo.getString("stageArray");
//                stageInfo = new StageInfo[stageNumber];
//                JSONArray jsonArrayStage = new JSONArray(stageString);
//                for(int i=0; i<jsonArrayStage.length(); i++){
//                    JSONObject jsonObjectStage = jsonArrayStage.getJSONObject(i);
//                    boolean isClear = jsonObjectStage.getBoolean("isClear");
//                    int starNumber = jsonObjectStage.getInt("starNumber");
//                    stageInfo[i] = new StageInfo(isClear,starNumber);
//                }
//                String achievementString = jsonUserInfo.getString("achievement");
//                JSONArray jsonArrayAchievement = new JSONArray(achievementString);
//                achievementInfo = new boolean[jsonArrayAchievement.length()];
//                for(int i=0; i<jsonArrayAchievement.length();i++){
//                    achievementInfo[i] = (boolean)jsonArrayAchievement.get(i);
//                    Log.i("json boolean from array",achievementInfo[i]+"");
//                }
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//        }
    }

    public static void setInstance(UserInfoSingleton instance) {
        UserInfoSingleton.instance = instance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public int getGem() {
        return gem;
    }

    public void setGem(int gem) {
        this.gem = gem;
    }

    public int getStageNumber() {
        return stageNumber;
    }

    public void setStageNumber(int stageNumber) {
        this.stageNumber = stageNumber;
    }

    public StageInfo[] getStageInfo() {
        return stageInfo;
    }

    public void setStageInfo(StageInfo[] stageInfo) {
        this.stageInfo = stageInfo;
    }

    public boolean[] getAchievementInfo() {
        return achievementInfo;
    }

    public void setAchievementInfo(boolean[] achievementInfo) {
        this.achievementInfo = achievementInfo;
    }
}
