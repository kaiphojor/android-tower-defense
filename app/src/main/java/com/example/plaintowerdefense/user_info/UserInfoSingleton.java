package com.example.plaintowerdefense.user_info;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

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
    int[] enemyKilled;

    public UserInfoSingleton() {
    }

    // user가 가진 보석 숫자를 표시해준다
    public static void setGemUi(final TextView gemTextView) {
        gemTextView.setText(String.valueOf(gem));
    }

    /*
    처음 호출할 때에만 instance를 생성한다
     */
    public static UserInfoSingleton getInstance() {
        if (instance == null) {
            instance = new UserInfoSingleton();
        }
        return instance;
    }

    // 이미 저장해놨던 유저 정보 불러와서 초기화
    public void initUserInfo(Context context, FirebaseAuth mAuth) {
        // 대충 shared preference로 정보 불러와서 저장.
        SharedPreferences sharedPreference = context.getSharedPreferences("sharedPreference", context.MODE_PRIVATE);
        email = mAuth.getCurrentUser().getEmail();
        String userInfoString = sharedPreference.getString(email, "");
        if (!userInfoString.contentEquals("")) {
            // json 정보 있으면 불러오기
            try {
                JSONObject jsonUserInfo = new JSONObject(userInfoString);
                Log.i("json result after sign in", userInfoString);
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
                for (int i = 0; i < jsonArrayStage.length(); i++) {
                    JSONObject jsonObjectStage = jsonArrayStage.getJSONObject(i);
                    boolean isClear = jsonObjectStage.getBoolean("isClear");
                    int starNumber = jsonObjectStage.getInt("starNumber");
                    stageInfo[i] = new StageInfo(isClear, starNumber);
                }
                String achievementString = jsonUserInfo.getString("achievement");
                JSONArray jsonArrayAchievement = new JSONArray(achievementString);
                achievementInfo = new boolean[jsonArrayAchievement.length()];
                for (int i = 0; i < jsonArrayAchievement.length(); i++) {
                    achievementInfo[i] = (boolean) jsonArrayAchievement.get(i);
                    Log.i("json boolean from array", achievementInfo[i] + "");
                }

                String enemyKilledString = jsonUserInfo.getString("enemyKilled");
                JSONArray jsonArrayEnemyKilled = new JSONArray(enemyKilledString);
                enemyKilled = new int[jsonArrayEnemyKilled.length()];
                for (int i = 0; i < jsonArrayEnemyKilled.length(); i++) {
                    enemyKilled[i] = (int) jsonArrayEnemyKilled.get(i);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.i("userinfoSingleton", "user info load error");
        }

    }

    // 처음 로그인, id setting 할때 처리
    public void setUserInfo(Context context, FirebaseUser user, String nick, String uri) {
        // 대충 shared preference로 정보 불러와서 저장.
        SharedPreferences sharedPreference = context.getSharedPreferences("sharedPreference", context.MODE_PRIVATE | context.MODE_WORLD_WRITEABLE);
        email = user.getEmail();
        String userInfoString = sharedPreference.getString(email, "");
        // 없으면 새로 만들기
        if (userInfoString.contentEquals("")) {
            stageNumber = 5;
            JSONObject jsonUserInfo = new JSONObject();
            try {
                jsonUserInfo.put("nickname", nick);
                jsonUserInfo.put("imageUri", uri);
                jsonUserInfo.put("uid", user.getUid());
                jsonUserInfo.put("displayName", user.getDisplayName());
                jsonUserInfo.put("reward", 0);
                jsonUserInfo.put("gem", 0);
                jsonUserInfo.put("stageNumber", stageNumber);

                JSONArray stageArray = new JSONArray();
                for (int i = 0; i < stageNumber; i++) {
                    JSONObject jsonObjectStage = new JSONObject();
                    jsonObjectStage.put("isClear", false);
                    jsonObjectStage.put("starNumber", 0);
                    stageArray.put(jsonObjectStage);
                }
                jsonUserInfo.put("stageArray", stageArray);

                JSONArray achievementArray = new JSONArray();
                for (int i = 0; i < 5; i++) {
                    achievementArray.put(false);
                }
                jsonUserInfo.put("achievement", achievementArray);

                JSONArray totalEnemyKilledArray = new JSONArray();
                for (int i = 0; i < 5; i++) {
                    totalEnemyKilledArray.put(0);
                }
                jsonUserInfo.put("enemyKilled", totalEnemyKilledArray);

                String userInfo = jsonUserInfo.toString();
                Log.i("json result", userInfo);
                // shared prference에 저장.
                SharedPreferences.Editor editor = sharedPreference.edit();
                editor.putString(email, userInfo);
                editor.apply();

            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            // 있으면 불러오기
            try {
                JSONObject jsonUserInfo = new JSONObject(userInfoString);
                Log.i("json result after sign in", userInfoString);
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
                for (int i = 0; i < jsonArrayStage.length(); i++) {
                    JSONObject jsonObjectStage = jsonArrayStage.getJSONObject(i);
                    boolean isClear = jsonObjectStage.getBoolean("isClear");
                    int starNumber = jsonObjectStage.getInt("starNumber");
                    stageInfo[i] = new StageInfo(isClear, starNumber);
                }

                String achievementString = jsonUserInfo.getString("achievement");
                JSONArray jsonArrayAchievement = new JSONArray(achievementString);
                achievementInfo = new boolean[jsonArrayAchievement.length()];
                for (int i = 0; i < jsonArrayAchievement.length(); i++) {
                    achievementInfo[i] = (boolean) jsonArrayAchievement.get(i);
                    Log.i("json boolean from array", achievementInfo[i] + "");
                }

                String enemyKilledString = jsonUserInfo.getString("enemyKilled");
                JSONArray jsonArrayEnemyKilled = new JSONArray(enemyKilledString);
                enemyKilled = new int[jsonArrayEnemyKilled.length()];
                for (int i = 0; i < jsonArrayEnemyKilled.length(); i++) {
                    enemyKilled[i] = (int) jsonArrayEnemyKilled.get(i);
//                    Log.i("json boolean from array",achievementInfo[i]+"");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 없으면 새로 만듬
    }

    // 업데이트한 정보 shared preference에 저장
    public void updateUserInfo(Context context) {
        // 대충 shared preference로 정보 불러와서 저장.
        SharedPreferences sharedPreference = context.getSharedPreferences("sharedPreference", context.MODE_PRIVATE | context.MODE_WORLD_WRITEABLE);
//        String email = user.getEmail();
//        String userInfoString = sharedPreference.getString(email,"");
        // 없으면 새로 만들기
//        if(userInfoString.contentEquals("")){
        stageNumber = 5;
        JSONObject jsonUserInfo = new JSONObject();
        try {

            jsonUserInfo.put("nickname", nickname);
            jsonUserInfo.put("imageUri", imageUri);
            jsonUserInfo.put("uid", uid);
            jsonUserInfo.put("displayName", displayName);
            int totalReward = 0;
            for (int i = 0; i < 5; i++) {
                // 적 종류 별 점수 차등 적용
                switch (i) {
                    case 0:
                        totalReward += enemyKilled[i];
                        break;
                    case 1:
                        totalReward += enemyKilled[i]*2;
                        break;
                    case 2:
                        totalReward += enemyKilled[i]*3;
                        break;
                    case 3:
                        totalReward += enemyKilled[i]*4;
                        break;
                    case 4:
                        totalReward += enemyKilled[i]*5;
                        break;
                }
            }
            jsonUserInfo.put("reward", totalReward);
            jsonUserInfo.put("gem", gem);
            jsonUserInfo.put("stageNumber", stageNumber);

            JSONArray stageArray = new JSONArray();
            for (int i = 0; i < stageNumber; i++) {
                JSONObject jsonObjectStage = new JSONObject();
                jsonObjectStage.put("isClear", stageInfo[i].isClear());
                jsonObjectStage.put("starNumber", stageInfo[i].getStarNumber());
                stageArray.put(jsonObjectStage);
            }
            jsonUserInfo.put("stageArray", stageArray);

            JSONArray achievementArray = new JSONArray();
            for (int i = 0; i < 5; i++) {
                achievementArray.put(achievementInfo[i]);
            }
            jsonUserInfo.put("achievement", achievementArray);

            JSONArray totalEnemyKilledArray = new JSONArray();
            for (int i = 0; i < 5; i++) {
                totalEnemyKilledArray.put(enemyKilled[i]);
            }
            jsonUserInfo.put("enemyKilled", totalEnemyKilledArray);

            String userInfo = jsonUserInfo.toString();
            Log.i("json result", userInfo);
            // shared prference에 저장.
            SharedPreferences.Editor editor = sharedPreference.edit();
            editor.putString(email, userInfo);
            editor.apply();

        } catch (Exception e) {
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

    // 스테이지 진행 결과 불러오기
    public StageInfo getStageInfo(int stageLevel) {
        return stageInfo[stageLevel - 1];
    }

    public StageInfo[] getStageInfo() {
        return stageInfo;
    }

    // 스테이지 진행 결과 저장하기
    public void setStageInfo(Context context, int stageLevel, StageInfo stageResult) {
        stageInfo[stageLevel - 1] = stageResult;
        // 저장후 shared preference에 변경한 내용을 반영
        updateUserInfo(context);
    }

    // 업적 전체 정보 불러오기
    public boolean[] getAchievementInfo() {
        return achievementInfo;
    }

    // 업적 정보 저장하기
    public void setAchievementInfo(Context context, int index, boolean isAttained) {
        this.achievementInfo[index] = isAttained;
        updateUserInfo(context);
    }

    public int getEnemyKilled(int enemyCode) {
        return enemyKilled[enemyCode];
    }

    // 기존 죽인 횟수에 추가
    public void setEnemyKilled(Context context,int[] killCount) {
        for(int i=0; i<enemyKilled.length ;i++){
            if(killCount[i] != -1){
                enemyKilled[i] += killCount[i];
            }
        }
        // 저장후 shared preference에 변경한 내용을 반영
        updateUserInfo(context);
    }
}
