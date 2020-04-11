package com.vortexghost.plaintowerdefense.stage_select;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.vortexghost.plaintowerdefense.GameLoadingActivity;
import com.vortexghost.plaintowerdefense.R;
import com.vortexghost.plaintowerdefense.Singleton;
import com.vortexghost.plaintowerdefense.user_info.StageInfo;
import com.vortexghost.plaintowerdefense.user_info.UserInfoSingleton;

public class StageAdapter extends PagerAdapter {
    // layoutInflator 위한 context
    private Context context = null;

    TextView levelTextView;
    RatingBar starRatingBar;
    ImageView stageImageView;
    ImageView clearImageView;
    int levelNumber;
    int starNumber;
    StageInfo[] stageInfo;
    // activity의 context를 전달받아서 저장
    public StageAdapter(Context context) {
        super();
        this.context = context;
        stageInfo = UserInfoSingleton.getInstance().getStageInfo();
    }

    @Override
    public int getCount() {
        return 5;
    }

    // 페이지뷰 만드는 작업
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
//        return super.instantiateItem(container, position);
        View page = null;

        if(context != null){
            // inflater service 등록
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // xml -> view 변환
            page = inflater.inflate(R.layout.page_stage,container,false);
            // view binding
            levelTextView = page.findViewById(R.id.level_tv_stage);
            final String levelString = "Stage "+(position+1);
            final int level = position + 1;
            levelTextView.setText(levelString);
            starRatingBar = page.findViewById(R.id.rb_stage);
            stageImageView = page.findViewById(R.id.iv_stage);
            clearImageView = page.findViewById(R.id.clear_iv_stage);

            // 이미지 설정 -> 스테이지 별로 교체해야..
            stageImageView.setImageResource(R.drawable.seele_logo);
            boolean isClear = stageInfo[position].isClear();
            int starCount = stageInfo[position].getStarNumber();
            if(isClear){
                clearImageView.setVisibility(View.VISIBLE);
            }
            starRatingBar.setRating(starCount);
            // click listener
            page.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Singleton.getInstance(context);
                    Singleton.log(levelString);
                    Singleton.log(level+"");
                    // 선택한 stage level shared preference에 저장
                    SharedPreferences sharedPreferences = context.getSharedPreferences("game", context.MODE_PRIVATE | context.MODE_WORLD_WRITEABLE);
                    try {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("stageLevel", level);
                        editor.apply();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // loading activity로 이동. activity stack
                    Intent intent = new Intent(context, GameLoadingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    context.startActivity(intent);
                }
            });
        }
        // 뷰페이저에 추가.
        container.addView(page);

        // 만든 view를 반환
        return page;
    }

    // 아이템 위치를 반환
    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }
    // update
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // 뷰페이저에서 삭제.
        container.removeView((View) object);
    }
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (View)object) ;
    }
}
