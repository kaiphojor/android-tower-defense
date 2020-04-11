package com.vortexghost.plaintowerdefense.game;

import java.util.HashMap;

// 한 게임에서의 wave 정보
/*
    한 stage에는 여러 wave가 있다.
 */
public class Wave {
    public HashMap<String,EnemyInfo> enemyInfoHashMap;
    // 보스몹이 존재하는지 여부
    public boolean isBossExist;

    public Wave(HashMap<String, EnemyInfo> enemyInfoHashMap) {
        this.enemyInfoHashMap = enemyInfoHashMap;
    }

    public Wave() {
        this.enemyInfoHashMap = new HashMap<String,EnemyInfo>();
    }
    // 적 정보 추가
    public void setEnemyInfo(EnemyInfo info){
        this.enemyInfoHashMap.put(info.getName(),info);
    }
    // 적 정보 반환
    public EnemyInfo getEnemyInfo(String key){
        return enemyInfoHashMap.get(key);
    }

    public boolean isBossExist() {
        return isBossExist;
    }

    public void setBossExist(boolean bossExist) {
        isBossExist = bossExist;
    }
}