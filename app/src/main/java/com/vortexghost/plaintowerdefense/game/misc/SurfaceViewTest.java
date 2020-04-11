package com.vortexghost.plaintowerdefense.game.misc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.vortexghost.plaintowerdefense.R;

public class SurfaceViewTest extends AppCompatActivity {
    FastRenderView renderView;
    Context context = this;

    int[][] tileMap
//                    = new int[7][15];
            = new int[][]{
            {0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0},
            {0,1,1,1,1, 0,0,0,0,0, 0,0,0,0,0},
            {0,1,0,0,1, 0,1,1,1,0, 0,0,0,0,0},
            {1,1,0,0,1, 0,1,0,1,0, 0,1,1,1,1},
            {0,0,0,0,1, 0,1,0,1,1, 1,1,0,0,0},
            {0,0,0,0,1, 1,1,0,0,0, 0,0,0,0,0},
            {0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0}
    };
    int tileWidth = tileMap[0].length;
    int tileHeight = tileMap.length;

    int baseX = 30;
    int baseY = 300;
    int tileGap = 2;
    Rect rect = new Rect();
    int tileLength = 100;
    Rect tileSize;
    Bitmap tileImage;
    Bitmap enemyTileImage;


    private boolean bMove = false;
    private int mouseX = 0;
    private int mouseY = 0;
    private int moveX = 0;
    private int moveY = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        renderView = new FastRenderView(this);
        setContentView(renderView);
    }
    protected void onResume() {
        super.onResume();
        renderView.resume();
    }
    protected void onPause() {
        super.onPause();
        renderView.pause();
    }
    class FastRenderView extends SurfaceView implements Runnable {


        Thread renderThread = null;
        SurfaceHolder holder;
        volatile boolean running = false;
        public FastRenderView(Context context) {
            super(context);
            holder = getHolder();
        }
        public void resume() {
            running = true;
            renderThread = new Thread(this);
            renderThread.start();
        }
//        Chapter 4 ■ Android for Game Developers
//154
        public void run() {
            Log.i("width : ",tileWidth+"");
            Log.i("height : ",tileHeight+"");
            tileImage =BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.platform_tile_001);
            enemyTileImage =BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.platform_tile_011);
            while(!Thread.currentThread().isInterrupted()) {
                Canvas canvas = null;
                if(!holder.getSurface().isValid())
                    continue;
                try{
                    canvas = holder.lockCanvas();
                    synchronized (holder){
                        doDraw(canvas);
                        Thread.sleep(50);
                    }
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    if(canvas != null ){
                        holder.unlockCanvasAndPost(canvas);
                    }
                }




            }
        }
        public void pause() {
            running = false;
            while(true) {
                try {
                    renderThread.join();
                    return;
                } catch (InterruptedException e) {
                    // retry
                }
            }
        }
    }
    public void doDraw(Canvas canvas){
        canvas.drawRGB(255, 0, 0);
        for(int i=0;i<tileWidth; i++){
            for(int j=0;j<tileHeight; j++){
                rect.set(baseX+i*tileLength+tileGap,baseY+j*tileLength+tileGap,baseX+(i+1)*tileLength,baseY+(j+1)*tileLength);
                if(tileMap[j][i] == 0){
                    canvas.drawBitmap(tileImage,null,rect,null);
                }else if(tileMap[j][i] == 1){
                    canvas.drawBitmap(enemyTileImage,null,rect,null);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int keyAction = event.getAction();
        int x = (int)event.getX();
        int y = (int)event.getY();
        mouseX = x;
        mouseY = y;
        Log.i("TOUCH","EVENT executed");
        switch (keyAction){
            case MotionEvent.ACTION_MOVE:
                if (bMove){
                    moveX = x;
                    moveY = y;
                }
                break;
            case MotionEvent.ACTION_UP:
                bMove = false;
                break;
            case MotionEvent.ACTION_DOWN:
//                this.checkImageMove(x, y);
                break;
        }
        // 함수 override 해서 사용하게 되면  return  값이  super.onTouchEvent(event) 되므로
        // MOVE, UP 관련 이벤트가 연이어 발생하게 할려면 true 를 반환해주어야 한다.
        return true;
    }
}