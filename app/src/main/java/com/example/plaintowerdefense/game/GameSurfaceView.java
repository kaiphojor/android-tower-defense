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
    // new int[9][17];
    int[][] tileMap
            = new int[][]{
            {0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0},
            {0,1,1,1,1, 0,0,0,0,0, 0,0,0,0,0, 0},
            {0,1,0,0,1, 0,1,1,1,0, 0,0,0,0,0, 0},
            {1,1,0,0,1, 0,1,0,1,0, 0,1,1,1,1, 0},
            {0,0,0,0,1, 0,1,0,1,1, 1,1,0,0,0, 0},
            {0,0,0,0,1, 1,1,0,0,0, 0,0,0,0,0, 0},
            {0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0},
            {0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0},
            {0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0}
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
    // starting, end point
    Bitmap startPointImage;
    Bitmap endPointImage;
    // 빔 이미지
    Bitmap focusedTileImage;
    // resized image
    Bitmap[] beamImage;
    Bitmap enemyPelletImage;
    Bitmap tileImageResized;
    Bitmap enemyTileImageResized;
    Bitmap scaledBeamImage;

    // 게임 진행 제어하는 counter
    int counter = 0;
    int enemySpawnGap = 200;
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
    // 스테이지 객체
    Stage stage;
    int stageLevel;
    // wave의 list
    ArrayList waveList;
    int waveNumber;
    // 현재 wave에서 생성할 적 정보
    EnemyInfo[] waveEnemyInfo;
    static final int MINION = 0;
    static final int BOSS = 1;
    /*
    상태 전이 변수
     */
    int state = -1;
    int previousState = -1;
    static final int READY = -1;
    static final int FIGHT = 0;
    static final int PAUSE = 1;
    static final int VICTORY = 2;
    static final int DEFEAT = 3;
    static final int FINISH = 4;

    static boolean isPause;
    static SharedPreferences pausePreference;
    boolean isAllEnemyGenerated = false;
    // 별 계산 용
    /*
    0% 0star 50% 1star 75% 2star 100% 3star
     */
    int enemyKilled = 0;
    int enemyPassed = 0;


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

    /*
    surfaceview 기본 메소드
     */
    // surface 만들어진 직후 호출 - image load, 위치, 스레드 초기화
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
        // 빔 이미지 load
        beamImage = new Bitmap[6];
        beamImage[0] = BitmapFactory.decodeResource(res,R.drawable.red_beam_horizontal);
        beamImage[1] = BitmapFactory.decodeResource(res,R.drawable.yellow_beam);
        beamImage[2] = BitmapFactory.decodeResource(res,R.drawable.yellow_circle_beam);
        beamImage[3] = BitmapFactory.decodeResource(res,R.drawable.green_beam);
        beamImage[4] = BitmapFactory.decodeResource(res,R.drawable.sky_blue_beam);
        beamImage[5] = BitmapFactory.decodeResource(res,R.drawable.sky_blue_circle_beam);
        startPointImage = BitmapFactory.decodeResource(res,R.drawable.watertex0);
        endPointImage = BitmapFactory.decodeResource(res,R.drawable.deathtex3);
        startPointImage = Bitmap.createScaledBitmap(startPointImage, tileLength, tileLength, true);
        endPointImage = Bitmap.createScaledBitmap(endPointImage, tileLength, tileLength, true);

        enemyPelletImage = Bitmap.createScaledBitmap(enemyPelletImage,enemyScale,enemyScale,true);
        tileImageResized  = Bitmap.createScaledBitmap(tileImage, tileLength, tileLength, true);
        enemyTileImageResized  = Bitmap.createScaledBitmap(enemyTileImage, tileLength, tileLength, true);
        focusedTileImage  = Bitmap.createScaledBitmap(focusedTileImage, tileLength, tileLength, true);

//        scaledBeamImage = Bitmap.createScaledBitmap(beamImage, 300, 30, true);


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
    // 화면 크기 바뀔 때 호출
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Singleton.log("surface changed");


    }
    // surface 삭제 전 호출, 스레드를 안전하게 종료
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            thread.interrupt();
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage());
        }
    }

    // surfaceview가 동작하는 thread 부분
    public void run() {
//        Log.i("width : ",tileWidth+"");
//        Log.i("height : ",tileHeight+"");
        // 준비과정 - onCreate와 비슷한 기능
        doPrepare();

        while(!Thread.currentThread().isInterrupted()) {
            canvas = null;
            // 유효한 surface 일때만 적용
            if(!holder.getSurface().isValid())
                continue;
            // surfaceview의 pixel 수정. 수정이 끝난 canvas가 최종적으로 surface에 그려짐
            try{
                canvas = holder.lockCanvas();

                // 그림 그리는 부분
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
            // 객체 처리 부분
            doProcess();
        }
    }
    // rendering 사전 준비
    // 스레드 반복이전 완료되어야 할 객체 초기화
    public void doPrepare(){
        // 선택한 level 불러오기
        SharedPreferences sharedPreferences =  context.getSharedPreferences("game",Context.MODE_MULTI_PROCESS);
        stageLevel = sharedPreferences.getInt("stageLevel",1);
        Singleton.log("stage level : " + stageLevel);
        // stage 초기화
        stage = new Stage(stageLevel);
        waveEnemyInfo = new EnemyInfo[5];
        // 맵 정보를 가져온다
        tileMap = stage.getMapInfo();
        // 골드, 체력 세팅
        ((GameActivity)getContext()).setCoinCountView(stage.getPlayerGold()+"");
        ((GameActivity)getContext()).setHealthPointView(stage.getPlayerHealthPoint()+"");

        waveList = stage.getWaveList();
        // 현재 wave 반환 및 적 생성 정보 저장
        Wave wave = (Wave)waveList.get(stage.getCurrentWave()-1);
        // wave 정보 탑재
        waveEnemyInfo[MINION] = wave.getEnemyInfo("minion");
        waveEnemyInfo[BOSS] = wave.getEnemyInfo("boss");
    }

    // rendering - 여러 그림들을 canvas에 그린다.
    public void doDraw(Canvas canvas){
        if(canvas != null){
            // Paint 표시, 그리기 개체
            Paint paint = new Paint();
            paint.setAntiAlias(true);

            canvas.drawRGB(0, 100, 0);
            // 맵 그리기
            drawMapTile(canvas,paint);
            // 타워 그리기
            drawTower(canvas,paint);
            // 적 그리기
            drawEnemy(canvas,paint);
            // 적을 움직이게 해야함 경로대로
            // 현재 경로에서 map 경로만들기

            // 타워가 공격하는 그림 그리기
            drawTowerAttack(canvas,paint);

            // focus 얻음 표시 그리기
            drawFocus(canvas,paint);
        }
    }
    // game 진행에서 필요한 객체처리 메소드.
    public void doProcess(){
        // 타워 생성
        towerInit();
        // 적 생성 및 이동
        enemyInit();
        // 게임 중일 때에만 정보 갱신
        if(state == FIGHT){
            // 갱신 후 남은 적의 이동
            enemyUpdate();
            enemyMove();
            // 타워 쿨타임 감소
            towerAttack();
            towerUpdate();
        }
        stateProcess();
    }
    // touch 시 focus 얻는 이벤트
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

    /*
    객체 처리 메소드 - doProcess에서 호출
     */
    // 적 객체 생성
    public void enemyInit(){
        // 적 생성
        // minion 생성
        if(waveEnemyInfo[MINION] != null){
            // 생성 가능한지 확인 (남은 적 수, 시간)
            if(waveEnemyInfo[MINION].canGenerate(counter)){
                // 적 정보 갱신
                waveEnemyInfo[MINION].postGenerationUpdate(counter);
                // 적 생성
                Enemy enemy = new Enemy(0);
                int[] spawnCoordinate = new int[]{baseX+enemySpawnPoint[0]*tileLength + tileLength/2 - enemyScale/2,baseY+enemySpawnPoint[1]*tileLength + tileLength/2 - enemyScale/2,enemySpawnPoint[2]};
                enemy.setCoordinate(spawnCoordinate);
                // 적 중앙 좌표 설정
                int[] centeredPixel = new int[]{enemy.getX()+enemyScale/2,enemy.getY()+enemyScale/2};
                enemy.setCenteredPixel(centeredPixel);
                enemyList.add(enemy);
                // 갱신
            }
        }
        // boss 생성
        if(waveEnemyInfo[BOSS] != null){
            // 생성 가능한지 확인 (남은 적 수, 시간)
            if(waveEnemyInfo[BOSS].canGenerate(counter)){
                // 적 정보 갱신
                waveEnemyInfo[BOSS].postGenerationUpdate(counter);
                // 적 생성
                Enemy enemy = new Enemy(1);
                int[] spawnCoordinate = new int[]{baseX+enemySpawnPoint[0]*tileLength + tileLength/2 - enemyScale/2,baseY+enemySpawnPoint[1]*tileLength + tileLength/2 - enemyScale/2,enemySpawnPoint[2]};
                enemy.setCoordinate(spawnCoordinate);
                // 적 중앙 좌표 설정
                int[] centeredPixel = new int[]{enemy.getX()+enemyScale/2,enemy.getY()+enemyScale/2};
                enemy.setCenteredPixel(centeredPixel);
                enemyList.add(enemy);
                // 갱신
            }
        }
//        if(counter%enemySpawnGap == 0){
//            Enemy enemy = new Enemy(0);
//            int[] spawnCoordinate = new int[]{baseX+enemySpawnPoint[0]*tileLength + tileLength/2 - enemyScale/2,baseY+enemySpawnPoint[1]*tileLength + tileLength/2 - enemyScale/2,enemySpawnPoint[2]};
//            enemy.setCoordinate(spawnCoordinate);
//            // 적 중앙 좌표 설정
//            int[] centeredPixel = new int[]{enemy.getX()+enemyScale/2,enemy.getY()+enemyScale/2};
//            enemy.setCenteredPixel(centeredPixel);
//            enemyList.add(enemy);
//        }
    }
    // enemy 상태 갱신
    public void enemyUpdate(){
        // iterator 쓰는 건 for each 에서는 현재 element를 제거할 수 없기 때문
        for (Iterator<Enemy> iterator = enemyList.iterator(); iterator.hasNext();) {
            Enemy enemy = iterator.next();
            // 적이 죽었을 경우 목록에서 제거한다
            if (enemy.isDead()) {
                // 적이 죽었을 때 보상을 얻고 화면을 업데이트한다.
                stage.earnRewardGold(enemy.getRewardGold());
                ((GameActivity)getContext()).setCoinCountView(stage.getPlayerGold()+"");
                // Remove the current element from the iterator and the list.
                iterator.remove();
                // 죽인 적 추가
                enemyKilled++;
            }
        }
    }
    // 적 이동 및 경로 끝에 도달했을 때 상태 갱신
    public void enemyMove(){

        //TODO :  그 이전에 일단 중앙으로 가야한다.
        // left top right bottom
        // 중심부에 오는지 확인
        // 적 위치 이동
        for(Iterator<Enemy> iterator = enemyList.iterator(); iterator.hasNext();) {
            Enemy e = iterator.next();
            boolean isEnemyReached= false;
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
                    // 적이 끝에 도달해서 플레이어에 데미지를 입힘
                    isEnemyReached = true;
                    break;
            }
            // 적이 끝까지 도달했다면 플레이어에게 피해를 주고 사라져야한다
            if(isEnemyReached){
                int remainingHealthPoint = stage.getPlayerHealthPoint()-e.getAttackPoint();
                // 3항 연산자가 더 빠른지, Math.max가 더 빠른지 - 동일한 코드이다. 알기 쉽게 3항씀
                remainingHealthPoint = remainingHealthPoint >= 0 ? remainingHealthPoint: 0;
                // 피해받고 남은 플레이어 체력을 UI에 표시
                ((GameActivity)getContext()).setHealthPointView(remainingHealthPoint+"");
                // surfaceview에서 갖고 있는 객체에도 반영
                stage.setPlayerHealthPoint(remainingHealthPoint);
                // 적 삭제
                iterator.remove();
                // 지나간 적 추가
                enemyPassed++;
            // 적이 진행중이라면 이동한 적의 중앙 좌표를 설정한다
            }else {
                // 적 중앙 좌표 설정
                int[] centeredPixel = new int[]{e.getX()+enemyScale/2,e.getY()+enemyScale/2};
                e.setCenteredPixel(centeredPixel);
            }
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
    }
    // tower 객체 생성
    public void towerInit(){
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
                    // 중심 픽셀 좌표 등록
                    int[] centeredCoordinate = getCenteredCoordinate(new int[]{focusedTileCoordinate[1],focusedTileCoordinate[0]});
                    tower.setCenteredPixel(centeredCoordinate);
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
    }
    // tower 상태 갱신
    public void towerUpdate(){
        // 쿨타임 감소
        for(Tower tower : towerList){
            tower.reduceCoolDown();
            tower.reduceBeamImageCountDown();
        }
    }
    // tower 공격
    public void towerAttack(){
        // 공격 모션 그리기
        // 적 찾기
        for(Tower tower : towerList){
            if(tower.isAttackEnabled()){
                // 공격 가능
                for(Enemy enemy : enemyList){
                    // 타워 - 적 간 거리 측정
                    int distance = getDistance(tower,enemy);
                    // 사거리 이내에 들어왔을 때 공격
                    if(distance < tower.getTowerRange()){
                        Singleton.log("tower-enemy distance : " + distance);
                        // 공격
                        // 공격 및 피해 적용
                        enemy.getDamage(tower.getTowerAttackPoint());
                        tower.resetCoolDown();
                        tower.setBeamImageCountDown();
                        // damage 입히기
                        break;
                    }else{
                        // 공격하지 않음
                    }
                }
            }else{
                // 공격 불가
//                Singleton.log("can not attack");
            }

        }
    }

    /*
    그림 그리는 메소드 - doDraw에서 호출
     */
    // 맵에 해당하는 타일 그리기
    public void drawMapTile(Canvas canvas,Paint paint){
        // 맵에 해당하는 tile 그리기
        for(int i=0;i<tileWidth; i++){
            for(int j=0;j<tileHeight; j++){
                // 전체 타일의 기준 좌표에 타일의 행 열 번호를 이용해서 위치를 정한다
                rect.set(baseX+i*tileLength,baseY+j*tileLength,baseX+(i+1)*tileLength,baseY+(j+1)*tileLength);
                // 적들이 지나가는 경로는 focus 갖지 못한다
                if(tileMap[j][i] == 0){
                    canvas.drawBitmap(tileImageResized,null,rect,paint);
                }else if(tileMap[j][i] >= 1){
                    canvas.drawBitmap(enemyTileImageResized,null,rect,paint);
                    // 시작 지점 , 종료지점은 따로 처리한다
                    if(tileMap[j][i] == 2){
                        canvas.drawBitmap(startPointImage,null,rect,paint);
                    }else if(tileMap[j][i] == 3){
                        canvas.drawBitmap(endPointImage,null,rect,paint);
                    }
                }
            }
        }
    }
    // 타워 그리기
    public void drawTower(Canvas canvas,Paint paint){
        // 생성된 타워들을 그림
        if(!towerList.isEmpty()){
            for(Tower tower : towerList){
//                    Singleton.log("towercode :" + tower.getTowerCode());
                canvas.drawBitmap(towerImageBitmap[tower.getTowerCode()],baseX+tower.getX()*tileLength,baseY+tower.getY()*tileLength,paint);
            }
        }
    }
    // 적 그리기
    public void drawEnemy(Canvas canvas,Paint paint){
        // 적을 그린다
        if(!enemyList.isEmpty()){
            for(Enemy enemy : enemyList){
                canvas.drawBitmap(enemyPelletImage,enemy.getX(),enemy.getY(),paint);
            }
        }
    }
    // focus 얻음 표시 그리기
    public void drawFocus(Canvas canvas,Paint paint){
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
    // 타워 공격 이미지 그리기(공격할 때 뿐만 아니라 일정시간 동안 보여줌)
    public void drawTowerAttack(Canvas canvas,Paint paint){
        // 공격 모션 그리기
        // 적 찾기
        for(Tower tower : towerList){
            // 빔을 보여줄 수 있을 때
            if(tower.isBeamDisplayable()){
                // 공격 가능
                for(Enemy enemy : enemyList){
                    // 타워 - 적 간 거리 측정
                    int distance = getDistance(tower,enemy);
                    // 사거리 이내에 들어왔을 때 공격
                    if(distance < tower.getTowerRange()){
                        Singleton.log("tower-enemy distance : " + distance);
//                        Singleton.log("tower range : " + tower.getTowerRange());
//                        Singleton.log("angle : " + angle);
                        // 빔의 회전 각도값 계산
                        float angle = getAngle(tower,enemy);
                        Matrix matrix = new Matrix();
                        matrix.postRotate(angle);
                        int[] centeredPixelCoordinate = tower.getCenteredPixel();
                        // 빔 길이를 타워 - 적 간 거리에 따라 조정
                        scaledBeamImage = Bitmap.createScaledBitmap(beamImage[tower.getTowerCode()], distance, 20, true);
                        // 각도 값에 따라 빔 그림 회전
                        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBeamImage,0,0, scaledBeamImage.getWidth(), scaledBeamImage.getHeight(), matrix, true);
                    /*
                    android 좌표 사분면
                    3 | 4
                    ㅡㅡㅡ
                    2 | 1
                     */
                        // X좌표 보정 - 1사분면 4사분면 : 0, 2사분면 3사분면 : 너비값
                        int angleCorrectionX = angle <= 90 || angle >= 270 ? 0 : rotatedBitmap.getWidth();
                        // y좌표 보정 - 1사분면 2사분면 : 0 , 3사분면 4사분면 : 높이값
                        int angleCorrectionY = angle < 180 ? 0 : rotatedBitmap.getHeight();
                        // 좌표 보정한 회전된 빔 그림을 그린다
                        canvas.drawBitmap(rotatedBitmap,centeredPixelCoordinate[0]-angleCorrectionX,centeredPixelCoordinate[1]-angleCorrectionY,paint);
                        break;
                    }else{
                        // 공격하지 않음
//                        Singleton.log("can not attack - range");
                    }
                }
            }else{
                // 빔을 보여줄 수 없음
//                Singleton.log("can not show beam");
            }


        }
    }
//    // 타워가 공격하는 빔 그리기(공격 관련 계산도 한꺼번에 적용)
//    public void drawTowerAttack(Canvas canvas,Paint paint){
//        // 공격 모션 그리기
//        // 적 찾기
//        for(Tower tower : towerList){
//            if(tower.isAttackEnabled()){
//                // 공격 가능
//                for(Enemy enemy : enemyList){
//                    // 타워 - 적 간 거리 측정
//                    int distance = getDistance(tower,enemy);
//                    // 사거리 이내에 들어왔을 때 공격
//                    if(distance < tower.getTowerRange()){
//                        Singleton.log("tower-enemy distance : " + distance);
////                        Singleton.log("tower range : " + tower.getTowerRange());
////                        Singleton.log("angle : " + angle);
//                        // 빔의 회전 각도값 계산
//                        float angle = getAngle(tower,enemy);
//                        Matrix matrix = new Matrix();
//                        matrix.postRotate(angle);
//                        int[] centeredPixelCoordinate = tower.getCenteredPixel();
//                        // 빔 길이를 타워 - 적 간 거리에 따라 조정
//                        scaledBeamImage = Bitmap.createScaledBitmap(beamImage, distance, 20, true);
//                        // 각도 값에 따라 빔 그림 회전
//                        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBeamImage,0,0, scaledBeamImage.getWidth(), scaledBeamImage.getHeight(), matrix, true);
//                        /*
//                        android 좌표 사분면
//                        3 | 4
//                        ㅡㅡㅡ
//                        2 | 1
//                         */
//                        // X좌표 보정 - 1사분면 4사분면 : 0, 2사분면 3사분면 : 너비값
//                        int angleCorrectionX = angle <= 90 || angle >= 270 ? 0 : rotatedBitmap.getWidth();
//                        // y좌표 보정 - 1사분면 2사분면 : 0 , 3사분면 4사분면 : 높이값
//                        int angleCorrectionY = angle < 180 ? 0 : rotatedBitmap.getHeight();
//                        // 좌표 보정한 회전된 빔 그림을 그린다
//                        canvas.drawBitmap(rotatedBitmap,centeredPixelCoordinate[0]-angleCorrectionX,centeredPixelCoordinate[1]-angleCorrectionY,paint);
//                        // 공격
//                        // 공격 및 피해 적용
//                        enemy.getDamage(tower.getTowerAttackPoint());
//                        tower.resetCoolDown();
//                        // damage 입히기
//                        break;
//                    }else{
//                        // 공격하지 않음
//                    }
//                }
//            }else{
//                // 공격 불가
//                Singleton.log("can not attack");
//            }
//
//        }
//    }
    /*
    각종 좌표 계산
     */
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
    // json object로 된 tower 정보를 저장 -> 타워 생성에 이용
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
//            Singleton.log("parse - tower range : " + tower.getTowerRange());

            return tower;
        }catch(Exception e){
            return null;
        }
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
    public int getDirection(int[] coordinate){
        // x, y는 tile 기준 좌표
        int pathDirection = (int)enemyPathMap.get(coordinate[0]+","+coordinate[1]);
//        Singleton.log("coordinate key : " +coordinate[0]+","+coordinate[1]);
        return pathDirection;
    }
    public int getDirection(int x,int y){
        // x, y는 tile 기준 좌표
        int pathDirection = (int)enemyPathMap.get(x+","+y);
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
    // map 좌표 -> pixel 좌표 -> 중심좌표 조정
    public int[] getCenteredCoordinate(int[] mappedCoordinate){
        int centeredX = baseX + mappedCoordinate[0]*tileLength + tileLength/2;
        int centeredY = baseY + mappedCoordinate[1]*tileLength + tileLength/2;
        return new int[]{centeredX,centeredY};
    }
    // tower - enemy 간 거리 반환
    public int getDistance(Tower tower,Enemy enemy){
        int[] towerCoordinate = tower.getCenteredPixel();
        int[] enemyCoordinate = enemy.getCenteredPixel();
        int distance = (int)Math.sqrt(Math.pow(towerCoordinate[0]-enemyCoordinate[0],2)+Math.pow(towerCoordinate[1]-enemyCoordinate[1],2));
        return distance;
    }
    // tower - enemy 간 각도 계산
    public float getAngle(Tower tower,Enemy enemy){
        int[] towerCoordinate = tower.getCenteredPixel();
        int[] enemyCoordinate = enemy.getCenteredPixel();
        // (target y - source y, target x - source x) atan 값을 theta -> degree 로 변환
        // tangent 값을 y , x 로 나눠 받아서 각 사분면간 구분을 할 수 있도록 한다.
        float angle = (float)Math.toDegrees(Math.atan2(enemyCoordinate[1]-towerCoordinate[1],enemyCoordinate[0]-towerCoordinate[0]));
        // -pi ~ + pi 범위에서 0 ~ 2 pi 범위로 만들어서 음수 값이 나오지 않게 한다.
        if(angle < 0){
            angle += 360;
        }
        return angle;
    }
    // 상태 제어
    public void stateProcess(){
        Singleton.log("state : " + state);
        // 상태에 따라서 작업
        /*
            static final int READY = -1;
    static final int FIGHT = 0;
    static final int PAUSE = 1;
    static final int VICTORY = 2;
    static final int DEFEAT = 3;
    static final int FINISH = 4;
         */
        switch(state){
            // 준비 단계
            case READY :
                SharedPreferences sharedPreferences = context.getSharedPreferences("game", context.MODE_MULTI_PROCESS | context.MODE_WORLD_WRITEABLE);
                try {
                    // wave 시작버튼이 눌렸는지 확인하고 상태를 바꾼다
                    boolean isWaveStart = sharedPreferences.getBoolean("isWaveStart",false);
                    if(isWaveStart){
                        // 상태 전환 및 이전 상태 저장
                        previousState = state;
                        state = FIGHT;
                        // 값을 초기화
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isWaveStart",false);
                        editor.apply();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // pause가 true면 pause로 상태 전환
                checkPauseStatus();
                break;
            case FIGHT :
                // pause가 true면 pause로 상태 전환
                checkPauseStatus();
                // 적이 하나도 없으면 상태 ready로 전환.
                // 적 목록이 다 있는지 확인
                isAllEnemyGenerated = true;
                // enemy info null check 하면서 다 죽었는지 확인
                for(EnemyInfo enemyInfo : waveEnemyInfo){
                    if(enemyInfo != null){
                        if(enemyInfo.getNumber() != 0){
                            isAllEnemyGenerated = false;
                        }
                    }
                }
//                Singleton.log("all gen : "+isAllEnemyGenerated);
                if(isAllEnemyGenerated){
//                    Singleton.log("all generated");
                    // 적이 다 죽었는지 확인
                    if(enemyList.isEmpty()){
                        // counter 초기화
                        counter = 0;
//                        Singleton.log("all generated and dead");
                        // 다음 wave로 넘어감
                        stage.updateWaveEnd();
                        if(stage.getWaveNumber() < stage.getCurrentWave()){
                            // 총 wave를 넘어선 경우 = 다음 wave가 없는 경우 = 승리
                            // 상태 fight -> victory
                            previousState = state;
                            state = VICTORY;

                            // 여기서 조정 - 승리 문구
                            ((GameActivity)getContext()).setWaveTextView("Victory!");
                            ((GameActivity)getContext()).showVictoryActivity();

                        }else{ // 다음 wave로 넘어갔을 경우
                            // 상태 전환 및 이전 상태 저장
                            previousState = state;
                            state = READY;
                            // is wave start -> false
                            // wave start button을 다르게 처리한다
                            // 다음 wave의 enemy를 설정
                            Wave wave = (Wave)waveList.get(stage.getCurrentWave()-1);
                            // wave 정보 탑재
                            waveEnemyInfo[MINION] = wave.getEnemyInfo("minion");
                            waveEnemyInfo[BOSS] = wave.getEnemyInfo("boss");
                            // 상태 전환
                        }
                    }else{
                        // 다 안죽었다면
                    }
                }
                // 패배 조건
                if(stage.getPlayerHealthPoint() == 0){
                    previousState = state;
                    state = DEFEAT;
                    ((GameActivity)getContext()).showDefeatActivity();
                }


                counter++;
                break;
            case PAUSE :

                // pause가 false면 이전 상태로 전환
                pausePreference = context.getSharedPreferences("game", context.MODE_PRIVATE |context.MODE_WORLD_WRITEABLE);
                try {
                    isPause = pausePreference.getBoolean("isPause",false);
                    if(!isPause){
                        // 이전 상태로 복귀
                        state = previousState;
                        previousState = PAUSE;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case VICTORY :
                // 전환 할 때 승리에 해당하는 메소드 호출
                // 전환 하고 나서는 딱히 해야할 것이 업삳
                // 다른 activity를 열거나 함
                break;
            case DEFEAT :
                break;
            case FINISH :
                // pause가 true면 pause로 상태 전환
                checkPauseStatus();
                break;
        }

    }
    // 일시정지를 눌렀는지 확인
    public void checkPauseStatus(){
        // pause가 true면 pause로 상태 전환
        pausePreference = context.getSharedPreferences("game", context.MODE_PRIVATE |context.MODE_WORLD_WRITEABLE);
        isPause = pausePreference.getBoolean("isPause",false);
        if(isPause){
            previousState = state;
            state = PAUSE;
        }
    }
    // rendering thread 초기화 - surfaceview를 일반 view가 아닌 layout으로 취급했을 때 필요한 것...
    public void resume() {
    running = true;
    renderThread = new Thread(this);
    renderThread.start();
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