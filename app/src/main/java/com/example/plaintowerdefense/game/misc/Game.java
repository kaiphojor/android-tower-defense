package com.example.plaintowerdefense.game.misc;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.example.plaintowerdefense.R;

public class Game extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    /**
     * Holds the surface frame
     */
    private SurfaceHolder holder;
    /**
     * Draw thread
     */
    private Thread drawThread;
    /**
     * True when the surface is ready to draw
     */
    private boolean surfaceReady = false;
    /**
     * Drawing thread flag
     */
    private boolean drawingActive = false;
    /**
     * Time per frame for 60 FPS
     */
    private static final int MAX_FRAME_TIME = (int) (1000.0 / 60.0);
    private static final String LOGTAG = "surface";

    public final int x = 100;//The reason for this being static will be shown when the game is runnable
    public int y;
    public int velY;

    private Bitmap PLAYER_BMP = BitmapFactory.decodeResource(getResources(),
            R.drawable.diamond);

    boolean up = false;

    public static int WIDTH, HEIGHT;
    Context context;

    /*
    * All the constructors are overridden to ensure functionality if one of the different
   constructors are used through an XML file or programmatically
    */
    public Game(Context context) {
        super(context);
        init(context);
    }

    public Game(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Game(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public Game(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void init(Context c) {
        //Initialize other stuff here later
        this.context = c;
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        setFocusable(true);

        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        WIDTH = size.x;
        HEIGHT = size.y;
        y = HEIGHT/ 2 - PLAYER_BMP.getHeight();

    }

    public void render(Canvas c) {
        //Game rendering here

        c.drawBitmap(PLAYER_BMP, x, y, null);
    }

    public void tick() {
        //Game logic here
        if (up) {
            velY -= 1;
        } else {
            velY += 1;
        }
        if (velY > 14) velY = 14;
        if (velY < -14) velY = -14;
        y += velY * 2;

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (width == 0 || height == 0) {
            return;
        }
        // resize your UI
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.holder = holder;
        if (drawThread != null) {
            Log.d(LOGTAG, "draw thread still active..");
            drawingActive = false;
            try {
                drawThread.join();
            } catch (InterruptedException e) {
            }
        }
        surfaceReady = true;
        startDrawThread();
        Log.d(LOGTAG, "Created");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface is not used anymore - stop the drawing thread
        stopDrawThread();
        // and release the surface
        holder.getSurface().release();
        this.holder = null;
        surfaceReady = false;
        Log.d(LOGTAG, "Destroyed");
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Handle touch events
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            up = true;
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            up = false;
        }


        return true;
    }

    /**
     * Stops the drawing thread
     */
    public void stopDrawThread() {
        if (drawThread == null) {
            Log.d(LOGTAG, "DrawThread is null");
            return;
        }
        drawingActive = false;
        while (true) {
            try {
                Log.d(LOGTAG, "Request last frame");
                drawThread.join(5000);
                break;
            } catch (Exception e) {
                Log.e(LOGTAG, "Could not join with draw thread");
            }
        }
        drawThread = null;
    }

    /**
     * Creates a new draw thread and starts it.
     */
    public void startDrawThread() {
        if (surfaceReady && drawThread == null) {
            drawThread = new Thread(this, "Draw thread");
            drawingActive = true;
            drawThread.start();
        }
    }

    @Override
    public void run() {
        Log.d(LOGTAG, "Draw thread started");
        long frameStartTime;
        long frameTime;
 /*
 * In order to work reliable on Nexus 7, we place ~500ms delay at the start of drawing
thread
 * (AOSP - Issue 58385)
 */
        if (android.os.Build.BRAND.equalsIgnoreCase("google") &&
                android.os.Build.MANUFACTURER.equalsIgnoreCase("asus") &&
                android.os.Build.MODEL.equalsIgnoreCase("Nexus 7")) {
            Log.w(LOGTAG, "Sleep 500ms (Device: Asus Nexus 7)");
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
        SurfaceHolder sf;
        sf = getHolder();
        while (drawThread != null) { // drawing
            if (sf == null) {
                return;
            }
            frameStartTime = System.nanoTime();
            Canvas canvas = sf.lockCanvas();
            if (canvas != null) {
                try {
                    synchronized (sf) {
                        tick();
                        render(canvas);
                    }
                } finally {
                    sf.unlockCanvasAndPost(canvas);
                }
            }
            // calculate the time required to draw the frame in ms
            frameTime = (System.nanoTime() - frameStartTime) / 1000000;
            if (frameTime < MAX_FRAME_TIME) {
                try {
                    Thread.sleep(MAX_FRAME_TIME - frameTime);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
        Log.d(LOGTAG, "Draw thread finished");
    }
}
