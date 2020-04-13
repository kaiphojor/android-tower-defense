package com.vortexghost.plaintowerdefense.game;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class Stage {
    private String stageName;
    private int stageCode;
    private int[][] mapInfo;
    // 플레이어가 게임 종료후 받을 보상
    private int playerCredit;
    // 플레이어가 타워를 살 때 사용할 골드
    private int playerGold;
    // stage 별 할당된 player
    private int playerHealthPoint;
    private int initialPlayerHealthPoint;
    // 현재 wave
    private int currentWave;
    private int waveNumber;
    // 죽인 적 정보
    public HashMap<String,Integer> enemyKilled;
    // 게임에서의 총 단계별 정보
    public ArrayList<Wave> waveList;
    // 재도전 했는지 여부
    public boolean isRetried;
    // 적 생산 위치, 진행방향(서쪽에서 시작해서 시계 방향 순서로 0,1,2,3 할당)
    public int[] startingPoint;

    public Stage(int stageCode) {
        this.stageCode = stageCode;
        this.currentWave = 1;
        this.isRetried = false;
        this.waveList = new ArrayList<>();
        Wave wave1;
        Wave wave2;
        Wave wave3;
        Wave wave4;
        Wave wave5;
        switch(stageCode){
            case 1:
                stageName = "snake";
                mapInfo = new int[][]{
                    {0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0},
                    {0,1,1,1,1, 0,0,0,0,0, 0,0,0,0,0, 0},
                    {0,1,0,0,1, 0,1,1,1,0, 0,0,0,0,0, 0},
                    {2,1,0,0,1, 0,1,0,1,0, 0,1,1,1,3, 0},
                    {0,0,0,0,1, 0,1,0,1,1, 1,1,0,0,0, 0},
                    {0,0,0,0,1, 1,1,0,0,0, 0,0,0,0,0, 0},
                    {0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0},
                    {0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0},
                    {0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0}
                };
                for(int y=0; y<mapInfo.length; y++){
                    for(int x=0; x<mapInfo[0].length; x++){
                        if(mapInfo[y][x]==2){
                            startingPoint = new int[]{x,y,2};
                            break;
                        }
                    }
                }
                playerGold = 80;
                playerCredit =0;
                playerHealthPoint = 100;
                initialPlayerHealthPoint = playerHealthPoint;
                enemyKilled = new HashMap<>();
                // wave 초기화
                wave1 = new Wave();
                // wave에 적 정보 추가( 어떤 적을, 몇명이나 , 어느 시점에서 , 어느 간격으로 생산할지 결정)
                wave1.setEnemyInfo(new EnemyInfo(0,5,10,30));
                wave1.setEnemyInfo(new EnemyInfo(1,5,20,30));
                wave1.setEnemyInfo(new EnemyInfo(2,5,30,30));

                wave2 = new Wave();
                wave2.setEnemyInfo(new EnemyInfo(0,10,10,100));
                wave2.setEnemyInfo(new EnemyInfo(3,5,40,30));
                wave2.setEnemyInfo(new EnemyInfo(4,1,100,30));
                // 정보를 다 추가한 wave를 wavelist에 등록
                waveList.add(wave1);
                waveList.add(wave2);
                // 총 wave
                waveNumber = waveList.size();
                break;
            case 2:
                stageName = "wave";
                mapInfo = new int[][]{
                        {2,1,1,0,1, 1,1,0,1,1, 1,1,1,-1,-1, -1,-1},
                        {0,0,1,0,1, 0,1,0,1,0, 0,0,1,-1,-1, -1,-1},
                        {1,1,1,0,1, 0,1,0,1,0, 3,0,1,-1,-1, -1,-1},
                        {1,0,0,0,1, 0,1,0,1,0, 1,1,1,-1,-1, -1,-1},
                        {1,1,1,1,1, 0,1,0,1,0, -1,-1, -1,-1,-1, -1,-1},
                        {0,0,0,0,0, 0,1,0,1,0, -1,-1, -1,-1,-1, -1,-1},
                        {1,1,1,1,1, 1,1,0,1,0, -1,-1, -1,-1,-1, -1,-1},
                        {1,0,0,0,0, 0,0,0,1,0, -1,-1, -1,-1,-1, -1,-1},
                        {1,1,1,1,1, 1,1,1,1,0, -1,-1, -1,-1,-1, -1,-1}
                };
                for(int y=0; y<mapInfo.length; y++){
                    for(int x=0; x<mapInfo[0].length; x++){
                        if(mapInfo[y][x]==2){
                            startingPoint = new int[]{x,y,1};
                            break;
                        }
                    }
                }
                playerGold = 80;
                playerCredit =0;
                playerHealthPoint = 100;
                initialPlayerHealthPoint = playerHealthPoint;
                enemyKilled = new HashMap<>();
                // wave 초기화
                wave1 = new Wave();
                // wave에 적 정보 추가( 어떤 적을, 몇명이나 , 어느 시점에서 , 어느 간격으로 생산할지 결정)
                wave1.setEnemyInfo(new EnemyInfo(0,5,10,30));
                wave1.setEnemyInfo(new EnemyInfo(1,5,20,30));
                wave1.setEnemyInfo(new EnemyInfo(2,5,30,30));

                wave2 = new Wave();
                wave2.setEnemyInfo(new EnemyInfo(0,10,10,100));
                wave2.setEnemyInfo(new EnemyInfo(3,5,40,30));
                wave2.setEnemyInfo(new EnemyInfo(4,1,100,30));
                // 정보를 다 추가한 wave를 wavelist에 등록
                waveList.add(wave1);
                waveList.add(wave2);
                // 총 wave
                waveNumber = waveList.size();
                break;
            case 3:
                stageName = "vortex";
                mapInfo = new int[][]{
                        {1,1,1,1,1, 1,1,1,1,1, 1,1,1,1,1, -1},
                        {1,0,0,0,0, 0,0,0,0,0, 0,0,0,0,1, -1},
                        {1,0,1,1,1, 1,1,1,1,1, 1,1,1,0,1, -1},
                        {1,0,1,0,0, 0,0,0,0,0, 0,0,1,0,1, -1},
                        {1,0,1,0,1, 1,1,1,1,1, 3,0,1,0,1, -1},
                        {1,0,1,0,1, 0,0,0,0,0, 0,0,1,0,1, -1},
                        {1,0,1,0,1, 1,1,1,1,1, 1,1,1,0,1, -1},
                        {1,0,1,0,0, 0,0,0,0,0, 0,0,0,0,1, -1},
                        {2,0,1,1,1, 1,1,1,1,1, 1,1,1,1,1, -1}
                };
                for(int y=0; y<mapInfo.length; y++){
                    for(int x=0; x<mapInfo[0].length; x++){
                        if(mapInfo[y][x]==2){
                            startingPoint = new int[]{x,y,1};
                            break;
                        }
                    }
                }
                playerGold = 80;
                playerCredit =0;
                playerHealthPoint = 100;
                initialPlayerHealthPoint = playerHealthPoint;
                enemyKilled = new HashMap<>();
                // wave 초기화
                wave1 = new Wave();
                // wave에 적 정보 추가( 어떤 적을, 몇명이나 , 어느 시점에서 , 어느 간격으로 생산할지 결정)
                wave1.setEnemyInfo(new EnemyInfo(0,5,10,30));
                wave1.setEnemyInfo(new EnemyInfo(1,5,20,30));
                wave1.setEnemyInfo(new EnemyInfo(2,5,30,30));

                wave2 = new Wave();
                wave2.setEnemyInfo(new EnemyInfo(0,10,10,100));
                wave2.setEnemyInfo(new EnemyInfo(3,5,40,30));
                wave2.setEnemyInfo(new EnemyInfo(4,1,100,30));
                // 정보를 다 추가한 wave를 wavelist에 등록
                waveList.add(wave1);
                waveList.add(wave2);
                // 총 wave
                waveNumber = waveList.size();
                break;
            case 4:
                stageName = "cat face";
                mapInfo = new int[][]{
                        {-1,-1,1,1,1, -1,-1,-1,-1,-1, 1,1,1,-1,-1, -1},
                        {-1,1,1,0,1, 1,-1,-1,-1,1, 1,0,1,1,-1, -1},
                        {1,1,0,0,0, 1,1,-1,1,1, 0,0,0,1,1, -1},
                        {1,0,0,0,0, 0,1,2,3,0, 0,0,0,0,1, -1},
                        {1,0,0,0,0, 0,0,0,0,0, 0,0,0,0,1, -1},
                        {1,1,0,0,0, 0,0,0,0,0, 0,0,0,1,1, -1},
                        {-1,1,1,1,0, 0,0,0,0,0, 0,1,1,1,-1, -1},
                        {-1,-1,-1,1,1, 1,0,0,0,1, 1,1,-1,-1,-1, -1},
                        {-1,-1,-1,-1,-1, 1,1,1,1,1, -1,-1,-1,-1,-1, -1}
                };
                for(int y=0; y<mapInfo.length; y++){
                    for(int x=0; x<mapInfo[0].length; x++){
                        if(mapInfo[y][x]==2){
                            startingPoint = new int[]{x,y,0};
                            break;
                        }
                    }
                }
                playerGold = 80;
                playerCredit =0;
                playerHealthPoint = 100;
                initialPlayerHealthPoint = playerHealthPoint;
                enemyKilled = new HashMap<>();
                // wave 초기화
                wave1 = new Wave();
                // wave에 적 정보 추가( 어떤 적을, 몇명이나 , 어느 시점에서 , 어느 간격으로 생산할지 결정)
                wave1.setEnemyInfo(new EnemyInfo(0,5,10,30));
                wave1.setEnemyInfo(new EnemyInfo(1,5,20,30));
                wave1.setEnemyInfo(new EnemyInfo(2,5,30,30));

                wave2 = new Wave();
                wave2.setEnemyInfo(new EnemyInfo(0,10,10,100));
                wave2.setEnemyInfo(new EnemyInfo(3,5,40,30));
                wave2.setEnemyInfo(new EnemyInfo(4,1,100,30));
                // 정보를 다 추가한 wave를 wavelist에 등록
                waveList.add(wave1);
                waveList.add(wave2);
                // 총 wave
                waveNumber = waveList.size();
                break;
            case 5:
                stageName = "snail";
                mapInfo = new int[][]{
                        {-1,-1,-1,0,0, 0,0,0,3,1, 0,0,0,-1,-1,-1},
                        {-1,-1,-1,0,0, 1,1,1,0,1, 1,0,0,-1,-1,-1},
                        {-1,-1,-1,0,0, 1,0,1,1,0, 1,1,0,-1,-1,-1},
                        {-1,-1,-1,0,1, 1,0,0,1,1, 0,1,1,-1,-1,-1},
                        {-1,-1,-1,1,1, 0,0,2,0,1, 0,0,1,-1,-1,-1},
                        {-1,-1,-1,1,0, 0,0,1,1,1, 0,1,1,-1,-1,-1},
                        {-1,-1,-1,1,1, 0,0,0,0,0, 1,1,0,-1,-1,-1},
                        {-1,-1,-1,0,1, 1,0,0,0,1, 1,0,0,-1,-1,-1},
                        {-1,-1,-1,0,0, 1,1,1,1,1, 0,0,0,-1,-1,-1}

                };
                for(int y=0; y<mapInfo.length; y++){
                    for(int x=0; x<mapInfo[0].length; x++){
                        if(mapInfo[y][x]==2){
                            startingPoint = new int[]{x,y,3};
                            break;
                        }
                    }
                }
                playerGold = 80;
                playerCredit =0;
                playerHealthPoint = 100;
                initialPlayerHealthPoint = playerHealthPoint;
                enemyKilled = new HashMap<>();
                // wave 초기화
                wave1 = new Wave();
                // wave에 적 정보 추가( 어떤 적을, 몇명이나 , 어느 시점에서 , 어느 간격으로 생산할지 결정)
                wave1.setEnemyInfo(new EnemyInfo(0,5,10,30));
                wave1.setEnemyInfo(new EnemyInfo(1,5,20,30));
                wave1.setEnemyInfo(new EnemyInfo(2,5,30,30));

                wave2 = new Wave();
                wave2.setEnemyInfo(new EnemyInfo(0,10,10,100));
                wave2.setEnemyInfo(new EnemyInfo(3,5,40,30));
                wave2.setEnemyInfo(new EnemyInfo(4,1,100,30));
                // 정보를 다 추가한 wave를 wavelist에 등록
                waveList.add(wave1);
                waveList.add(wave2);
                // 총 wave
                waveNumber = waveList.size();
                break;
            case 6:
                mapInfo = new int[][]{

                        {0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0},
                        {0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0},
                        {0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0},
                        {0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0},
                        {0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0},
                        {0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0},
                        {0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0},
                        {0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0},
                        {0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0}
                };
                for(int y=0; y<mapInfo.length; y++){
                    for(int x=0; x<mapInfo[0].length; x++){
                        if(mapInfo[y][x]==2){
                            startingPoint = new int[]{x,y};
                            break;
                        }
                    }
                }
                playerGold = 80;
                playerCredit =0;
                playerHealthPoint = 100;
                initialPlayerHealthPoint = playerHealthPoint;
                enemyKilled = new HashMap<>();
                // wave 초기화
                wave1 = new Wave();
                // wave에 적 정보 추가( 어떤 적을, 몇명이나 , 어느 시점에서 , 어느 간격으로 생산할지 결정)
                wave1.setEnemyInfo(new EnemyInfo(0,5,10,30));
                wave1.setEnemyInfo(new EnemyInfo(1,5,20,30));
                wave1.setEnemyInfo(new EnemyInfo(2,5,30,30));

                wave2 = new Wave();
                wave2.setEnemyInfo(new EnemyInfo(0,10,10,100));
                wave2.setEnemyInfo(new EnemyInfo(3,5,40,30));
                wave2.setEnemyInfo(new EnemyInfo(4,1,100,30));
                // 정보를 다 추가한 wave를 wavelist에 등록
                waveList.add(wave1);
                waveList.add(wave2);
                // 총 wave
                waveNumber = waveList.size();
                break;

            default :
                Log.i("Stage","unregistered stage code error");
                break;
        }
    }
    // 적이 죽었을 때 호출
    public void addEnemyKilled(String name){
        // 없으면 1 설정, 있으면 현재 적 없앤 숫자 + 1
        if(enemyKilled.get(name) != null){
            int killedEnemyNumber = enemyKilled.get(name);
            enemyKilled.put(name,++killedEnemyNumber);
        }else{
            enemyKilled.put(name,1);
        }
    }
    // 적 죽은 숫자를 반환
    public int getEnemyKilled(String name){
        if(enemyKilled.get(name) == null){
            return 0;
        }else{
            return enemyKilled.get(name);
        }
    }
    // 적을 죽이고 보상을 얻는다
    public void earnRewardGold(int reward){
        this.playerGold += reward;
    }
    // 골드를 사용한다
    public boolean useGold(int gold){
        // 현재 골드가 골드를 사용하기에 충분하다면 사용하고 true, 아니면 false 반환
        if(playerGold - gold >= 0){
            playerGold -= gold;
            return true;
        }else{
            return false;
        }
    }
    // 다음 wave로 넘어갈때
    public void updateWaveEnd(){
        currentWave++;
    }

    public int getStageCode() {
        return stageCode;
    }

    public void setStageCode(int stageCode) {
        this.stageCode = stageCode;
    }

    public int[][] getMapInfo() {
        return mapInfo;
    }

    public void setMapInfo(int[][] mapInfo) {
        this.mapInfo = mapInfo;
    }

    public int getPlayerGold() {
        return playerGold;
    }

    public void setPlayerGold(int playerGold) {
        this.playerGold = playerGold;
    }

    public int getPlayerHealthPoint() {
        return playerHealthPoint;
    }

    public void setPlayerHealthPoint(int playerHealthPoint) {
        this.playerHealthPoint = playerHealthPoint;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public void setCurrentWave(int currentWave) {
        this.currentWave = currentWave;
    }

    public HashMap<String, Integer> getEnemyKilled() {
        return enemyKilled;
    }

    public void setEnemyKilled(HashMap<String, Integer> enemyKilled) {
        this.enemyKilled = enemyKilled;
    }

    public ArrayList<Wave> getWaveList() {
        return waveList;
    }

    public void setWaveList(ArrayList<Wave> waveList) {
        this.waveList = waveList;
    }

    public boolean isRetried() {
        return isRetried;
    }

    public void setRetried(boolean retried) {
        isRetried = retried;
    }

    public int getWaveNumber() {
        return waveNumber;
    }

    public void setWaveNumber(int waveNumber) {
        this.waveNumber = waveNumber;
    }

    public int getInitialPlayerHealthPoint() {
        return initialPlayerHealthPoint;
    }

    public void setInitialPlayerHealthPoint(int initialPlayerHealthPoint) {
        this.initialPlayerHealthPoint = initialPlayerHealthPoint;
    }

    public int[] getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(int[] startingPoint) {
        this.startingPoint = startingPoint;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }
}
