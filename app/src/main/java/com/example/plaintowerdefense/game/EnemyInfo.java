package com.example.plaintowerdefense.game;


// stage 에서 다룰 적 정보
public class EnemyInfo{
    public String name;
    // minion 수
    public int number;
    // 최초 생성
    public int generationTime;
    // 생성 간격
    public int generationGap;
    public EnemyInfo(String name, int number, int generationTime, int generationGap) {
        this.name = name;
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
}