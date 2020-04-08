package com.example.plaintowerdefense.game;

import android.graphics.Point;

public class FadingImage {
    int x;
    int y;
    float alpha;

    public FadingImage(int x, int y) {
        this.x = x;
        this.y = y;
//        this.alpha = alpha;
        // alpha 값 1.0 (opaque) 0.0 (transparent)
        alpha = 1.0f;
    }

    public void setXY(int[] coordinate){
        x = coordinate[0];
        y = coordinate[1];
    }
    public int[] getXY(){
        return new int[]{x,y};
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

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
    public void move(){
        y -= 1;
        alpha -= 0.01f;
    }
    public boolean canErase(){
        return alpha <= 0;
    }
}
