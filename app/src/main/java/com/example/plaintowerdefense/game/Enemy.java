package com.example.plaintowerdefense.game;

public class Enemy {
    int enemyCode;
    int healthPoint;
    int attackPoint;
    int speed;
    int x;
    int y;
    int[] centeredPixel;
    int direction;

    // 적 번호에 따른 초기화
    public Enemy(int code) {
        enemyCode = code;
        switch(code){
            case 0:
                healthPoint = 150;
                attackPoint =1;
                speed = 5;
                break;
            case 1:
                healthPoint = 20;
                attackPoint =1;
                speed = 2;
                break;
            case 2:
                healthPoint = 10;
                attackPoint = 4;
                speed = 1;
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
}
