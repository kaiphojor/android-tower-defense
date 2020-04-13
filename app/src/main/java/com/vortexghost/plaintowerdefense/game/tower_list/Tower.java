package com.vortexghost.plaintowerdefense.game.tower_list;

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
    // 타워 공격 딜레이
    private int towerDelay;
    // 현재 적용되고 있는 쿨다운 시간
    private int towerCoolDown;
    // 빔 이미지를 좀더 오래 표시할 수 있도록 하는 변수
    private int beamImageTime;
    private int beamImageCountDown;
    // 좌표
    private int x;
    private int y;
    // 중심 pixel 좌표
    private int[] centeredPixel;
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
            case 0:
                towerAttackPoint = 2;
                towerAttackSpeed = 50;
                towerRange = 300;
                break;
            case 1:
                towerAttackPoint = 3;
                towerAttackSpeed = 60;
                towerRange = 350;
                break;
            case 2:
                towerAttackPoint = 3;
                towerAttackSpeed = 100;
                towerRange = 400;
                break;
            case 3:
                towerAttackPoint = 5;
                towerAttackSpeed = 80;
                towerRange = 500;
                break;
            case 4:
                towerAttackPoint = 60;
                towerAttackSpeed = 8;
                towerRange = 1000;
                break;
            default :
                break;
        }
        // 공격 쿨다운 초기화
        towerDelay = 1000 / towerAttackSpeed;
        towerCoolDown = 0;
        // 빔 이미지 지속시간 초기화 - 좀더 오랫동안 보여주려고
        beamImageTime = 7;
        beamImageCountDown = 0;
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


    public int getTowerDelay() {
        return towerDelay;
    }

    public void setTowerDelay(int towerDelay) {
        this.towerDelay = towerDelay;
    }

    public int getTowerCoolDown() {
        return towerCoolDown;
    }

    public void setTowerCoolDown(int towerCoolDown) {
        this.towerCoolDown = towerCoolDown;
    }
    // 타일 중심부 픽셀 좌표 반환 - 타워 공격 이미지 출력 위한 정보
    public int[] getCenteredPixel() {
        return centeredPixel;
    }

    public void setCenteredPixel(int[] centeredPixel) {
        this.centeredPixel = centeredPixel;
    }
    // 공격 쿨타임 다 찼는지 여부
    public boolean isAttackEnabled(){
        return towerCoolDown <= 0;
    }
    // 쿨타임 감소
    public void reduceCoolDown(){
        towerCoolDown--;
    }
    // 쿨타임 초기화
     public void resetCoolDown(){
        towerCoolDown = towerDelay;
     }
     // 빔 이미지 시간 설정(0이상 일때만 보임)
     public void setBeamImageCountDown(){
        beamImageCountDown = beamImageTime;
     }
     // 빔 이미지 카운트 다운 - 1 이상일 동안 이미지 출력
     public void reduceBeamImageCountDown(){
         beamImageCountDown--;
     }
     // 빔을 화면에 표시할 수 있는지 여부
     public boolean isBeamDisplayable(){
        return beamImageCountDown > 0;
     }
}
