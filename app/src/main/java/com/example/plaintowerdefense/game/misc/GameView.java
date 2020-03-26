package com.example.plaintowerdefense.game.misc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.plaintowerdefense.R;

public class GameView
        extends SurfaceView //surfaceView 확장
        implements SurfaceHolder.Callback {// surface holder의 callback method의 요구를 충족시킨다
    // context
    Context mContext;
    // surface
    SurfaceHolder mHolder;
    // 그림을 surface에 그려주는 rendering thread
    RenderingThread mRThread;
    // animation 용
    private int positionX = 0;
    public GameView(Context context) {
        super(context);
        mContext = context;
        // holder 초기화
        mHolder = getHolder();
        // callback method 등록
        mHolder.addCallback(this);
        // rendering Thread 초기화
        mRThread = new GameView.RenderingThread();
        setFocusable(true);

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // Surface가 만들어질 때 호출됨
        // rendering Thread를 시작함
        // --> 여기서 rendering 하기 때문에 다른 Thread에서는 surface 접근해서 그리면 안된다
        mRThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        // Surface가 변경될 때 호출됨
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        // Surface가 종료될 때 호출됨
        // rendering Thread가 종료될 때까지 return 하지 않음
        // --> surface destroy return 전까지 thread 정지를 보장함
        try {
            mRThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // rendering Thread. 그림 하나를 계속 그려준다
    class RenderingThread extends Thread {
        Bitmap img_android; // 비트맵 변수
        public RenderingThread() {
            Log.d("RenderingThread", "RenderingThread()");
            // 변수에 그림 등록 - bitmap 형태로
            img_android = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.diamond);
        }
        // 동작 부분
        public void run() {
            Log.d("RenderingThread", "run()");
            // 그릴 도화지인 캔버스
            Canvas canvas = null;

            while (true) {
                // surface holder에서 canvas의 pixel편집을 시작함
                canvas = mHolder.lockCanvas();
                try {
                    // 동기화를 설정한다
                    synchronized (mHolder) {
                        // 실제로 그리는 부분
                        canvas.drawBitmap(img_android,  positionX, 0, null);
                    }
                } finally {
                    // null이면 return
                    if (canvas == null) return;
                    // 캔버스의 픽셀 수정을  끝낸다. 끝낸후에 픽셀들이 화면에 보이게 된다.
                    //    Finish editing pixels in the surface.  After this call, the surface's
                    //    current pixels will be shown on the screen, but its content is lost,
                    //     in particular there is no guarantee that the content of the Surface
                    //     will remain unchanged when lockCanvas() is called again.
                    mHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    } // RenderingThread
}