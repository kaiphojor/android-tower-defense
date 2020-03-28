package com.example.plaintowerdefense.game;

public class Enemy {
    int enemyCode;
    int health;
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
                health = 1;
                attackPoint =1;
                speed = 5;
                break;
            case 1:
                health = 2;
                attackPoint =1;
                speed = 2;
                break;
            case 2:
                health = 1;
                attackPoint =4;
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


    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
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
}
