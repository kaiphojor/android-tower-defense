package com.example.plaintowerdefense.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.plaintowerdefense.R;
import com.example.plaintowerdefense.Singleton;
import com.example.plaintowerdefense.game.tower_list.Tower;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class GameSurfaceView extends SurfaceView implements Runnable, SurfaceHolder.Callback{


//    Context context = this;

    int[][] tileMap
//                    = new int[7][15];
            = new int[][]{
            {0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0},
            {0,1,1,1,1, 0,0,0,0,0, 0,0,0,0,0, 0,0},
            {0,1,0,0,1, 0,1,1,1,0, 0,0,0,0,0, 0,0},
            {1,1,0,0,1, 0,1,0,1,0, 0,1,1,1,1, 0,0},
            {0,0,0,0,1, 0,1,0,1,1, 1,1,0,0,0, 0,0},
            {0,0,0,0,1, 1,1,0,0,0, 0,0,0,0,0, 0,0},
            {0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0},
            {0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0},
            {0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0}
    };
    int[] focusedTileCoordinate = null;

    int tileWidth = tileMap[0].length;
    int tileHeight = tileMap.length;

    private Point pImage;
    private Point pWindow;

    int baseX = 10;
    int baseY = 10;
    int tileGap = 2;
    Rect rect = new Rect();
    int tileLength = 100;
    int enemyScale = 40;
    Rect tileSize;
    // 타일, 적, focused image
    Bitmap tileImage;
    Bitmap enemyTileImage;
    Bitmap focusedTileImage;
    Bitmap enemyPelletImage;

    Bitmap tileImageResized;
    Bitmap enemyTileImageResized;

    // 빔 이미지
    Bitmap beamImage;
    Bitmap scaledBeamImage;
    // 게임 진행 제어하는 counter
    int counter = 0;

    private Thread thread = null;
    // surfaceview rendering 용
    Thread renderThread = null;
    SurfaceHolder holder;
    volatile boolean running = false;
    Canvas canvas;
    // 실험용 인자
    boolean testBoolean = false;
    // Game Activity Context
    Context context = (GameActivity)getContext();
    ArrayList<Tower> towerList;
    // 타워 이미지 모음
    Bitmap[] towerImageBitmap;
    // 적 생성 좌표
    int[] enemySpawnPoint = new int[]{0,3,2};
    // 적이 갈 좌표의 모음
    ArrayList<int[]> enemyTraversal;
    // 적을 저장한 목록
    ArrayList<Enemy> enemyList;
    HashMap<String,Integer> enemyPathMap;


    // 리스너 객체 참조를 저장하는 변수
    private static OnTowerClickListener towerClickListener = null;
    //    // tower interface
    public interface OnTowerClickListener {
        //        void onTowerClick(View v, int position);
        void onCreateClick(int position);
//        void onDeleteClick(View v, int position);
    }
    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public static void setOnTowerClickListener(OnTowerClickListener listener) {
        towerClickListener = listener;
    }
    public GameSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        getHolder().addCallback(this);
        holder = getHolder();
        setFocusable(true);
    }
    public GameSurfaceView(Context context) {
        super(context);
        holder = getHolder();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // window 크기
        pWindow = new Point();
        pWindow.x = getWidth();
        pWindow.y = getHeight()-50;
        // 타워 리스트 초기화
        towerList = new ArrayList<>();
        enemyList = new ArrayList<>();
        enemyPathMap = new HashMap<String,Integer>();
        // 적 경로 초기화
        setEnemyPath();

        // 이미지 위치
        pImage = new Point(0, 0);
        Resources res = getResources();
        tileImage = BitmapFactory.decodeResource(res, R.drawable.platform_tile_001);
        enemyTileImage =BitmapFactory.decodeResource(res, R.drawable.platform_tile_011);
        focusedTileImage =BitmapFactory.decodeResource(res, R.drawable.focused_pixel_500);
        enemyPelletImage = BitmapFactory.decodeResource(res,R.drawable.yellow_pellet);
        enemyPelletImage = BitmapFactory.decodeResource(res,R.drawable.yellow_pellet);
        beamImage = BitmapFactory.decodeResource(res,R.drawable.red_beam_horizontal);

        enemyPelletImage = Bitmap.createScaledBitmap(enemyPelletImage,enemyScale,enemyScale,true);
        tileImageResized  = Bitmap.createScaledBitmap(tileImage, tileLength, tileLength, true);
        enemyTileImageResized  = Bitmap.createScaledBitmap(enemyTileImage, tileLength, tileLength, true);
        focusedTileImage  = Bitmap.createScaledBitmap(focusedTileImage, tileLength, tileLength, true);

        scaledBeamImage = Bitmap.createScaledBitmap(beamImage, 300, 30, true);


        // tower image 초기화
        // 여기도 문제없음
        Bitmap bufferImage;
        Bitmap orangeBuffer;
        towerImageBitmap = new Bitmap[5];
        bufferImage = BitmapFactory.decodeResource(res, R.drawable.red_tile);
        towerImageBitmap[0] = Bitmap.createScaledBitmap(bufferImage, tileLength, tileLength, true);
        orangeBuffer = BitmapFactory.decodeResource(res, R.drawable.orange_tile);
        towerImageBitmap[1] = Bitmap.createScaledBitmap(orangeBuffer, tileLength, tileLength, true);
        bufferImage = BitmapFactory.decodeResource(res, R.drawable.yellow_tile);
        towerImageBitmap[2] = Bitmap.createScaledBitmap(bufferImage, tileLength, tileLength, true);
        bufferImage = BitmapFactory.decodeResource(res, R.drawable.light_green_tile);
        towerImageBitmap[3] = Bitmap.createScaledBitmap(bufferImage, tileLength, tileLength, true);
        bufferImage = BitmapFactory.decodeResource(res, R.drawable.green_tile);
        towerImageBitmap[4] = Bitmap.createScaledBitmap(bufferImage, tileLength, tileLength, true);
        Bitmap enemyTileImageResized;
        // 이미지 그리기
//        imgMove = Bitmap.createScaledBitmap(tempBitmap, imgWidth, imgHeight, true);
        setClickable(true);

        thread = new Thread(this);
        thread.start();

//        Canvas c = getHolder().lockCanvas();
//        doDraw(c);
//        getHolder().unlockCanvasAndPost(c);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Singleton.log("surface changed");


    }
    //
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            thread.interrupt();
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage());
        }
    }

    public void resume() {
        running = true;
        renderThread = new Thread(this);
        renderThread.start();
    }
    //        Chapter 4 ■ Android for Game Developers
    public void run() {
        Log.i("width : ",tileWidth+"");
        Log.i("height : ",tileHeight+"");
        doPrepare();

        while(!Thread.currentThread().isInterrupted()) {
            canvas = null;
            if(!holder.getSurface().isValid())
                continue;
            try{
                canvas = holder.lockCanvas();
                synchronized (holder){
                    doDraw(canvas);
                    Thread.sleep(10);
                }
                if(canvas != null ){
                    holder.unlockCanvasAndPost(canvas);
                }
            }catch(InterruptedException e){
                e.printStackTrace();
            }finally{
            }
            doProcess();
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
    public void doDraw(Canvas canvas){
        if(canvas != null){
            // Paint 표시, 그리기 개체
            Paint paint = new Paint();
            paint.setAntiAlias(true);

            canvas.drawRGB(0, 100, 0);
            // 맵에 해당하는 tile 그리기
            for(int i=0;i<tileWidth; i++){
                for(int j=0;j<tileHeight; j++){
                    // 전체 타일의 기준 좌표에 타일의 행 열 번호를 이용해서 위치를 정한다
                    rect.set(baseX+i*tileLength,baseY+j*tileLength,baseX+(i+1)*tileLength,baseY+(j+1)*tileLength);
                    // 적들이 지나가는 경로는 focus 갖지 못한다
                    if(tileMap[j][i] == 0){
                        canvas.drawBitmap(tileImageResized,null,rect,paint);
                    }else if(tileMap[j][i] == 1){
                        canvas.drawBitmap(enemyTileImageResized,null,rect,paint);
                    }
                }
            }
            // 생성된 타워들을 그림
            if(!towerList.isEmpty()){
                for(Tower tower : towerList){
//                    Singleton.log("towercode :" + tower.getTowerCode());
                    canvas.drawBitmap(towerImageBitmap[tower.getTowerCode()],baseX+tower.getX()*tileLength,baseY+tower.getY()*tileLength,paint);
                }
            }

            // 적을 생성
            if(!enemyList.isEmpty()){
                for(Enemy enemy : enemyList){
                    canvas.drawBitmap(enemyPelletImage,enemy.getX(),enemy.getY(),paint);
                }
            }
            // 적을 움직이게 해야함 경로대로
            // 현재 경로에서 map 경로만들기

            // TODO : 타워가 공격할 때 image
            //  사거리 내의 적을 탐지 - 우선순위 확인 ( 나중에 시간되면 바꿀 수 있는 것으로..)
            //  적을 공격 -> 적위치 타워위치 를 이용한 빔 이미지
//            Matrix matrix = new Matrix();
//            matrix.postRotate(40);
//            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBeamImage, 0, 0, scaledBeamImage.getWidth(), scaledBeamImage.getHeight(), matrix, true);
//            canvas.drawBitmap(rotatedBitmap,100,100,paint);
            // focus된 좌표의 타일
            // 좌표에 해당하는 곳에 테두리 밝은 표시
            if(focusedTileCoordinate != null){
                canvas.drawBitmap(focusedTileImage,baseX+focusedTileCoordinate[1]*tileLength,baseY+focusedTileCoordinate[0]*tileLength,paint);
            }
            if(testBoolean){
                testBoolean = false;
                ((GameActivity)getContext()).setMenuVisibility(false);
            }
        }
    }
    // 계산 과정을 담는 메소드. canvas에 그리는 doDraw와는 따로 처리한다.
    public void doProcess(){
        try{
            SharedPreferences sharedPreferences =  context.getSharedPreferences("game",Context.MODE_MULTI_PROCESS);
            // tower가 클릭 되었을 때에만 동작
            if(sharedPreferences.getBoolean("isTowerClick",false)){
                // focus tile이 있을 때에만 tower를 만든다
                if(focusedTileCoordinate != null){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String towerInfoJSONString = sharedPreferences.getString("towerInfo","error");
                    Singleton.log(towerInfoJSONString);
                    Tower tower = parseJSONTowerInfo(towerInfoJSONString);
                    // 좌표, 타워정보  리스트에 저장
                    tower.setX(focusedTileCoordinate[1]);
                    tower.setY(focusedTileCoordinate[0]);
                    towerList.add(tower);
                    // 다 사용한 shared preference 비워주기
                    editor.putString("towerInfo","");
                    editor.putBoolean("isTowerClick",false);
                    editor.apply();
//                Singleton.getInstance(context);
//                Singleton.log("COMMUNICATION_SUCCESS!!!");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        // 적 생성
        if(counter%100 == 0){
            Enemy enemy = new Enemy(0);
            int[] spawnCoordinate = new int[]{baseX+enemySpawnPoint[0]*tileLength + tileLength/2 - enemyScale/2,baseY+enemySpawnPoint[1]*tileLength + tileLength/2 - enemyScale/2,enemySpawnPoint[2]};
            enemy.setCoordinate(spawnCoordinate);
            enemyList.add(enemy);
        }
        // 적 위치 이동
        for(Enemy e : enemyList){
            // 좌표를 해시맵으로 해야 순회를 하지 않을 수 있다.
            int x = e.getX();
            int y = e.getY();
            int[] mapXY = getMappedCoordinate(x,y);
//            Singleton.log("mapXY : " + mapXY[0] + " "+mapXY[1]);
            int direction = getDirection(mapXY);
            // 방향이 같으면 그대로 간다
            if(e.getDirection() == direction){
                // 그대로 간다.

            // 방향이 다르다면 경로조정용 rect에 좌표가 포함 되는지 파악한다
            // 좌 상단 rect 와 우하단 rect에 모두 포함되는지
            // rect에 포함되었다 == 경로 조정한 대로 똑바로 갔다.
            }else {
                Rect[] adjustmentRect = getPathAdjustmentRect(mapXY[0],mapXY[1]);
                // rect에 포함이 되어야만 방향 조정한다
                if(adjustmentRect[0].intersect(x,y,x+enemyScale,y+enemyScale)
                && adjustmentRect[1].intersect(x,y,x+enemyScale,y+enemyScale)){
                    // 방향 전환을 한다.
                    e.setDirection(direction);
                }else{
                    // 방향전환을 하지 않는다
                    direction = e.getDirection();
                }
            }
            switch(direction){
                // 왼쪽 방향으로 간다.
                case 0:
                    e.setX(x-e.getSpeed());
                    break;
                // 위쪽 방향으로 간다.
                case 1:
                    e.setY(y-e.getSpeed());
                    break;
                // 오른쪽 방향으로 간다.
                case 2:
                    e.setX(x+e.getSpeed());
                    break;
                // 아래쪽 방향으로 간다.
                case 3:
                    e.setY(y+e.getSpeed());
                    break;
                // 마지막 경로이다. 움직일 필요없음
                case -1 :
                    break;
            }
            //TODO :  그 이전에 일단 중앙으로 가야한다.
            // left top right bottom
            // 중심부에 오는지 확인
//            Rect adjustmentRect = getPathAdjustmentRect(mapXY[0],mapXY[1]);
//            if(adjustmentRect.contains(x,y)){
//                // rect에 포함이 되어야만 방향 조정한다
//                e.setDirection(direction);
//                switch(direction){
//                    // 왼쪽 방향으로 간다.
//                    case 0:
//                        e.setX(x-e.getSpeed());
//                        break;
//                    // 위쪽 방향으로 간다.
//                    case 1:
//                        e.setY(y-e.getSpeed());
//                        break;
//                    // 오른쪽 방향으로 간다.
//                    case 2:
//                        e.setX(x+e.getSpeed());
//                        break;
//                    // 아래쪽 방향으로 간다.
//                    case 3:
//                        e.setY(y+e.getSpeed());
//                        break;
//                    // 마지막 경로이다. 움직일 필요없음
//                    case -1 :
//                        break;
//                }
//            }else{
//
//            }
        }
        counter++;

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int keyAction = event.getAction();
        // touch 되는 위치
        int x = (int)event.getX();
        int y = (int)event.getY();


        Log.i("TOUCH","EVENT executed");

        switch (keyAction){
//            case MotionEvent.ACTION_MOVE:
//                if (bMove){
//                    moveX = x;
//                    moveY = y;
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                bMove = false;
//                break;
                // 화면에 손가락이 닿았을 때
            case MotionEvent.ACTION_DOWN:
                Log.i("ACTION_DOWN","accessed");
                this.checkTileClick(x, y);
                break;
        }
        // 함수 override 해서 사용하게 되면  return  값이  super.onTouchEvent(event) 되므로
        // MOVE, UP 관련 이벤트가 연이어 발생하게 할려면 true 를 반환해주어야 한다.
        return true;
    }

    // 타일이 클릭되었는지를 확인
    public void checkTileClick(int x, int y){
        // tile 안이 touch 된 상태인지
        boolean isTileArea;

        // 타일안의 영역인지 구분
        if(x < baseX || x > baseX + tileLength * tileWidth || y < baseY || y > baseY + tileLength * tileHeight){
            isTileArea = false;
        }else{
            isTileArea = true;
        }
        // 타일 안의 영역일 경우
        if(isTileArea){
            x -= baseX;
            y -= baseY;
            // 좌표 mapping해서 tile 배열의 index로 접근 가능하도록 함
            int mappedX = x / tileLength;
            int mappedY = y / tileLength;
            // 해당하는 곳의 타일을 확인
            // [y 좌표 = 높이 = 행][x좌표 = 너비 = 열]
            if(tileMap[mappedY][mappedX] == 0){
                // 적 타일이 아니면 focus된 곳의 좌표를 저장
                // focused( int[2] - x,y 좌표 저장. focus 안되었을 때
                focusedTileCoordinate = new int[]{mappedY,mappedX};
                Log.i("FOCUS_COMPLETE","x : "+mappedX + " y : "+mappedY);
            }

            ((GameActivity)getContext()).setTextView("x : "+mappedX +" y : "+mappedY);
            // 왼쪽에 있던 메뉴가 드러나도록 한다.
            ((GameActivity)getContext()).setMenuVisibility(true);

        }else{
            // 타일 밖이면 focus를 잃는다
//            focusedTileCoordinate = null;
            //      만약 focus 가 있다면 lose focus
            ((GameActivity)getContext()).setMenuVisibility(false);
        }
    }

    public Tower parseJSONTowerInfo(String jsonString){
        try{
            JSONObject towerInfoJSONObject = new JSONObject(jsonString);
            int towerCode = towerInfoJSONObject.optInt("towerCode");
            String name = towerInfoJSONObject.optString("name");
            int price = towerInfoJSONObject.optInt("price");
            int image = towerInfoJSONObject.optInt("image");
            // 문제 발생
//            Singleton.log("tower code : " + towerCode);
            Tower tower = new Tower(towerCode,name,price,image);
            tower.initialSetting();
            return tower;
        }catch(Exception e){
            return null;
        }
    }
    public void doPrepare(){
        // 스레드 반복하기 이전에
    }
    // 적의 이동 경로 설정
    public void setEnemyPath(){
        Singleton.getInstance(context);
//        tileMap = null;
        // 현재 x,y 좌표
        int currentX = enemySpawnPoint[0];
        int currentY = enemySpawnPoint[1];
        // 경로 저장 변수 초기화
        enemyTraversal = new ArrayList<>();

        // tileMap에서 1있는 곳을 찾는다.
        // 이전 좌표와 일치하지 않는다면 등록한다.
        // 일치하는 좌표가 없다면 끝
        // 처음일 때
        // 중간일 때
        // 마지막일 때
        int[] prePos = new int[]{currentX, currentY,-1};
        int[] curPos = new int[]{currentX,currentY,-1};

        while(true){
            boolean isFindNextPath = false;
            // 다음 경로를 찾는다.
            currentX = prePos[0];
            currentY = prePos[1];

            int previousX = enemySpawnPoint[0];
            int previousY = enemySpawnPoint[1];
            // 이전 경로 위치 설정
            if(!enemyTraversal.isEmpty()){
                previousX = enemyTraversal.get(enemyTraversal.size()-1)[0];
                previousY = enemyTraversal.get(enemyTraversal.size()-1)[1];
            }

            for(int i=0; i<4; i++){
                int possibleXCoordinate = prePos[0];
                int possibleYCoordinate = prePos[1];
                // 검사 후 맵 왼쪽 끝이 아니라면 검사
                if(i == 0 && possibleXCoordinate != 0){
                    possibleXCoordinate -= 1;
                }
                // 검사 후 맵 위쪽 끝이 아니라면 검사
                if(i == 1 && possibleYCoordinate != 0){
                    possibleYCoordinate -= 1;
                }
                // 검사 후 맵 오른쪽 끝이 아니라면 검사
                if(i == 2 && possibleXCoordinate != tileWidth-1){
                    possibleXCoordinate += 1;
                }
                // 검사 후 맵 아랫쪽 끝이 아니라면 검사
                if(i == 3 && possibleYCoordinate != tileHeight-1){
                    possibleYCoordinate += 1;
                }
//                Singleton.log("x,y : " + possibleXCoordinate + ", " + possibleYCoordinate);
                if(tileMap[possibleYCoordinate][possibleXCoordinate] == 1){
//                    Singleton.log("found! - x,y : " + possibleXCoordinate + ", " + possibleYCoordinate);
                    // 같은 위치일 때 - pass
                    if(prePos[0] == possibleXCoordinate && prePos[1] == possibleYCoordinate){
//                        Singleton.log("같은위치! - x,y : " + possibleXCoordinate + ", " + possibleYCoordinate);
                        continue;
                    }
                    // 처음 값을 집어넣을 때
                    if(enemyTraversal.isEmpty()){
                        prePos[2] = i;
                        // 경로 추가 후
//                        enemyTraversal.add(new int[]{currentX,currentX,i});
                        enemyTraversal.add(prePos);
                        enemyPathMap.put(prePos[0]+","+prePos[1],prePos[2]);
//                        Singleton.log("1path ("+prePos[0]+","+prePos[1] +") -> " + prePos[2]);
                        curPos = new int[]{possibleXCoordinate,possibleYCoordinate,-1};
                        prePos = curPos;
                        isFindNextPath = true;
                        break;
                    // 이전에 방문했던 경로일 경우 - pass
                    }else if(previousX==possibleXCoordinate && previousY==possibleYCoordinate){
//                        Singleton.log("이전경로! - x,y : " + possibleXCoordinate + ", " + possibleYCoordinate);
                        continue;
                    }else{
                        prePos[2] = i;
                        // 경로 추가 후
//                        enemyTraversal.add(new int[]{currentX,currentX,i});
                        enemyTraversal.add(prePos);
                        enemyPathMap.put(prePos[0]+","+prePos[1],prePos[2]);
//                        Singleton.log("2path ("+prePos[0]+","+prePos[1] +") -> " + prePos[2]);
//                        Singleton.log("curpos ("+possibleXCoordinate+","+possibleYCoordinate +") -> " + -1);
                        curPos = new int[]{possibleXCoordinate,possibleYCoordinate,-1};
                        prePos = curPos;
                        isFindNextPath = true;
                        break;
                    }
                }else{
                    // 해당 타일이 적이 다니는 경로가 아닐 때
//                    Singleton.log("경로이탈! - x,y : " + possibleXCoordinate + ", " + possibleYCoordinate);
                }
                // loop 탈출
                if(i==3){
//                    break;
                }
            }
            // 마지막일 때
            if(!isFindNextPath){
                enemyTraversal.add(curPos);
                enemyPathMap.put(curPos[0]+","+curPos[1],curPos[2]);
                break;
            }
        }
        // 값이 잘 들어왔나 확인차 로그 출력
        Set<String> keyset = enemyPathMap.keySet();
        for(String key : keyset){
            Singleton.log("keyset " + key + " : " +enemyPathMap.get(key));
        }
        Singleton.log("array list size : " + enemyTraversal.size());
        Singleton.log("hashmap size : " + enemyPathMap.size());

//        for(int[] enemyPath : enemyTraversal){
//            Singleton.log("path ("+enemyPath[0]+","+enemyPath[1] +") -> " + enemyPath[2]);
//        }
    }
    // 현재 좌표(픽셀)의 타일 위치를 반환
    public int[] getMappedCoordinate(int x, int y){
        x -= baseX;
        y -= baseY;
        // 좌표 mapping해서 tile 배열의 index로 접근 가능하도록 함
        int mappedX = x / tileLength;
        int mappedY = y / tileLength;
        return new int[]{mappedX,mappedY};
    }
    // comma로 구분된 좌표 문자열을 integer로 변환
    public int[] getParsedCoordinate(String stringCoordinate){
        String[] halvedString = stringCoordinate.split(",");
        int[] parsedCoordinate = new int[2];
        parsedCoordinate[0] = Integer.parseInt(halvedString[0]);
        parsedCoordinate[1] = Integer.parseInt(halvedString[1]);
        return parsedCoordinate;
    }
    // x,y 좌표에서 진행할 방향을 return
    public int getDirection(int x,int y){
        // x, y는 tile 기준 좌표
        int pathDirection = (int)enemyPathMap.get(x+","+y);
        return pathDirection;
    }
    public int getDirection(int[] coordinate){
//        Singleton.log("coordinate key : " +coordinate[0]+","+coordinate[1]);
        // x, y는 tile 기준 좌표
        int pathDirection = (int)enemyPathMap.get(coordinate[0]+","+coordinate[1]);
        return pathDirection;
    }
    // 적 경로 조정하기 위한 Rect
    public Rect[] getPathAdjustmentRect(int x,int y){
        int rectLength = 2;
        int centerX = baseX + x*tileLength + tileLength/2;
        int centerY = baseY + y*tileLength + tileLength/2;
        // 좌상단 rect 좌표
        int left = centerX - enemyScale/2 - rectLength/2;
        int top = centerY - enemyScale/2 - rectLength/2;
        // 우하단 rect 좌표
        int subRectLeft = centerX + enemyScale/2 - rectLength/2;
        int subRectTop = centerY + enemyScale/2 - rectLength/2;
//        Rect adjustmentRect = new Rect(left,top,left+rectLength,top+rectLength);
//        Rect[] adjustmentRect =
        return new Rect[]{
                        new Rect(left,top,left+rectLength,top+rectLength)
                        ,new Rect(subRectLeft,subRectTop,subRectLeft+rectLength,subRectTop+rectLength)
        };
    }
}