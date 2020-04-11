package com.vortexghost.plaintowerdefense.user_info;

public class StageInfo{
    boolean isClear;
    int starNumber;
    public StageInfo(boolean isClear, int starNumber) {
        this.isClear = isClear;
        this.starNumber = starNumber;
    }
    public boolean isClear() {
        return isClear;
    }

    public void setClear(boolean clear) {
        isClear = clear;
    }

    public int getStarNumber() {
        return starNumber;
    }


    public void setStarNumber(int starNumber) {
        this.starNumber = starNumber;
    }
}