package com.example.plaintowerdefense.stage_select;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.plaintowerdefense.R;

import org.w3c.dom.Text;

public class StageAdapter extends PagerAdapter {
    // layoutInflator 위한 context
    private Context context = null;

    TextView levelTextView;
    RatingBar starRatingBar;
    ImageView stageImageView;
    int levelNumber;
    int starNumber;

    // activity의 context를 전달받아서 저장
    public StageAdapter(Context context) {
        super();
        this.context = context;
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
        View view = null;

        if(context != null){
            // inflater service 등록
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // xml -> view 변환
            view = inflater.inflate(R.layout.page_stage,container,false);

            // view binding
            levelTextView = view.findViewById(R.id.level_tv_stage);
            String levelString = "Level "+(position+1);
            levelTextView.setText(levelString);
            starRatingBar = view.findViewById(R.id.rb_stage);
            stageImageView = view.findViewById(R.id.iv_stage);
            stageImageView.setImageResource(R.drawable.seele_logo);
        }
        // 뷰페이저에 추가.
        container.addView(view);

        // 만든 view를 반환
        return view;
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
