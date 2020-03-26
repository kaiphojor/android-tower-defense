package com.example.plaintowerdefense.game.tower_list;

import android.content.Context;

import com.example.plaintowerdefense.R;

/*
타워 클래스
이름
설명 -> 나중에 추가
가격
이미지
 */
public class Tower {
    private int towerCode;
    private int towerLevel;
    private int towerAttackPoint;
    private int towerAttackSpeed;
    private int towerRange;
    // 좌표
    private int x;
    private int y;


    private String name;
//    private String description;
    private int price;
    private int imageResource;
    // 이름, 가격, 이미지 uri를 포함하는 생성자
    public Tower(String name, int price, int imageResource) {
        this.name = name;
        this.price = price;
        this.imageResource = imageResource;
    }
    public Tower(int towerCode, String name, int price, int imageResource) {
        this.towerCode = towerCode;
        this.name = name;
        this.price = price;
        this.imageResource = imageResource;
    }
    // tower code 로 초기 세팅 -
    public void initialSetting(){
        towerLevel = 1;
        switch(towerCode){
            case 1:
                towerAttackPoint = 10;
                towerAttackSpeed = 10;
                towerRange = 10;
                break;
            case 2:
                towerAttackPoint = 20;
                towerAttackSpeed = 10;
                towerRange = 20;
                break;
            case 3:
                towerAttackPoint = 30;
                towerAttackSpeed = 10;
                towerRange = 20;
                break;
            case 4:
                towerAttackPoint = 40;
                towerAttackSpeed = 10;
                towerRange = 20;
                break;
            case 5:
                towerAttackPoint = 50;
                towerAttackSpeed = 10;
                towerRange = 20;
                break;
            default :
                break;
        }
    }

    // getter setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public int getTowerCode() {
        return towerCode;
    }

    public void setTowerCode(int towerCode) {
        this.towerCode = towerCode;
    }

    public int getTowerLevel() {
        return towerLevel;
    }

    public void setTowerLevel(int towerLevel) {
        this.towerLevel = towerLevel;
    }

    public int getTowerAttackPoint() {
        return towerAttackPoint;
    }

    public void setTowerAttackPoint(int towerAttackPoint) {
        this.towerAttackPoint = towerAttackPoint;
    }

    public int getTowerAttackSpeed() {
        return towerAttackSpeed;
    }

    public void setTowerAttackSpeed(int towerAttackSpeed) {
        this.towerAttackSpeed = towerAttackSpeed;
    }

    public int getTowerRange() {
        return towerRange;
    }

    public void setTowerRange(int towerRange) {
        this.towerRange = towerRange;
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
}
