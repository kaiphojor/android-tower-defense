package com.example.plaintowerdefense;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.plaintowerdefense.game.GameActivity;

public class GameLoadingActivity extends AppCompatActivity {

    TextView loadingTextView;
    ProgressBar loadingProgress;
    int loadingPercentage;
    String[] loadingMessage = {
            "길 다지는 중......",
            "타워 건설중......",
            "타워에 포탄 채워넣는 중......",
            "적들 줄 세우는 중......"
    };
    LoadingProgressTask loadingTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_loading);
        // view binding
        loadingTextView = findViewById(R.id.tv_game_loading);
        loadingProgress = findViewById(R.id.pb_game_loading);
        // progress bar 두께 두껍게 함
        loadingProgress.setScaleY(8f);
        // async task 초기화
        loadingTask = new LoadingProgressTask();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        task.execute(100);
        loadingTask.execute();
    }

    // Task 정의
    // <void , string, void> ->
    class LoadingProgressTask extends AsyncTask<Void, Integer, Void> {
        // 초기화 과정
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingPercentage = 0;
            loadingProgress.setProgress(loadingPercentage);
        }

        // background 계산 과정
        @Override
        protected Void doInBackground(Void ... voids) {
            while (isCancelled() == false) {
                loadingPercentage++;
                // progress 최대 100
                if (loadingPercentage >= 100) {
                    break;
                } else {
                    // 퍼센트에 해당하는 메시지 출력
                    publishProgress(loadingPercentage);
                }
                // delay를 준다
                try{
                    Thread.sleep(50);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }
        // publishProgress 했을 때 호출. text와 progress를 바꾼다
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            loadingProgress.setProgress(values[0]);
            String message = loadingMessage[values[0] / 25] + values[0] + "/100";
            loadingTextView.setText(message);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // 다 끝났을 때 현재 로딩 acitivity를 stack에 포함 안한 채로 다른 activity로 전환
            Intent intent = new Intent(GameLoadingActivity.this, GameActivity.class);
            startActivity(intent);
        }

        // 취소 되었을 때
        @Override
        protected void onCancelled() {
            super.onCancelled();
            onBackPressed();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        loadingTask.cancel(true);
    }
}
