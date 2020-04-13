package com.vortexghost.plaintowerdefense.game;


import androidx.annotation.NonNull;

// stage 에서 다룰 적 정보
public class EnemyInfo implements Cloneable{
    public String name;
    // minion 수
    public int number;
    // 최초 생성
    public int generationTime;
    // 생성 간격
    public int generationGap;
    public EnemyInfo(int enemyCode,int number, int generationTime, int generationGap) {
        switch(enemyCode){
            case 0:
                name = "minion";
                break;
            case 1:
                name = "dichotomy";
                break;
            case 2:
                name = "purple";
                break;
            case 3:
                name = "noise";
                break;
            case 4:
                name = "sahaquiel";
                break;
        }
//        this.name = name;
        this.number = number;
        this.generationTime = generationTime;
        this.generationGap = generationGap;
    }

    public String getName() {
        return name;
    }


    // 적 생산 가능한지 판단하기
    public boolean canGenerate(int currentTime){
        // 숫자
        return number > 0 && generationTime == currentTime;
    }
    // 적을 생산할 때 수치 갱신 메소드
    public void postGenerationUpdate(int currentTime){
        number--;
        generationTime = currentTime + generationGap;
    }

    public void setName(String name) {
        this.name = name;
    }
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getGenerationTime() {
        return generationTime;
    }

    public void setGenerationTime(int generationTime) {
        this.generationTime = generationTime;
    }

    public int getGenerationGap() {
        return generationGap;
    }

    public void setGenerationGap(int generationGap) {
        this.generationGap = generationGap;
    }

    // deep copy 위한
    @NonNull
    @Override
    public EnemyInfo clone() throws CloneNotSupportedException {
        return (EnemyInfo)super.clone();
    }
}