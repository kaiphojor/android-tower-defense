package com.vortexghost.plaintowerdefense.game;

public class Enemy {
    String name;
    int enemyCode;
    int healthPoint;
    int attackPoint;
    int speed;
    int x;
    int y;
    int[] centeredPixel;
    int direction;
    int rewardGold;

    // 적 번호에 따른 초기화
    public Enemy(int code){
        enemyCode = code;
        switch(code){
            case 0:
                name = "minion";
                healthPoint = 20; // 150 - 죽이기 어려움
                attackPoint = 10;
                speed = 5;
                rewardGold = 10;
                break;
            case 1:
                name = "dichotomy";
                healthPoint = 10;
                attackPoint = 5;
                speed = 20;
                rewardGold = 5;
                break;
            case 2:
                name = "purple";
                healthPoint = 25;
                attackPoint = 15;
                speed = 4;
                rewardGold = 30;
                break;
            case 3:
                name = "noise";
                healthPoint = 35;
                attackPoint = 25;
                speed = 2;
                rewardGold = 50;
                break;
            case 4:
                name = "sahaquiel";
                healthPoint = 100;
                attackPoint = 50;
                speed = 1;
                rewardGold = 100;
                break;
            default :
                break;
        }

    }
    // x,y, 방향 초기화
    public void setCoordinate(int[] coordinate){
        x = coordinate[0];
        y = coordinate[1];
        direction = coordinate[2];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHealthPoint() {
        return healthPoint;
    }

    public void setHealthPoint(int healthPoint) {
        this.healthPoint = healthPoint;
    }

    public int getAttackPoint() {
        return attackPoint;
    }

    public void setAttackPoint(int attackPoint) {
        this.attackPoint = attackPoint;
    }

    public int getEnemyCode() {
        return enemyCode;
    }

    public void setEnemyCode(int enemyCode) {
        this.enemyCode = enemyCode;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
    // 타일 중심부 픽셀 좌표 반환 - 타워 공격 이미지 출력 위한 정보
    public int[] getCenteredPixel() {
        return centeredPixel;
    }

    public void setCenteredPixel(int[] centeredPixel) {
        this.centeredPixel = centeredPixel;
    }

    // tower 피해 적용
    public void getDamage(int damage){
        healthPoint -= damage;
    }
    // 죽었는지 안죽었는지 확인
    public boolean isDead(){
        return healthPoint <= 0;
    }

    public int getRewardGold() {
        return rewardGold;
    }

    public void setRewardGold(int rewardGold) {
        this.rewardGold = rewardGold;
    }
}
