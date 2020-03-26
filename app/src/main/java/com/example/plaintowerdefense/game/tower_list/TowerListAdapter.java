package com.example.plaintowerdefense.game.tower_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.plaintowerdefense.R;

import java.util.ArrayList;

// tower list를 보여주는 adapter
public class TowerListAdapter extends BaseAdapter {
    // context, xml-> view 만들어주는 inflater, 타워 목록
    Context context = null;
    LayoutInflater inflater = null;
    ArrayList<Tower> towerList;

    // 리스너 객체 참조를 저장하는 변수
    private TowerListAdapter.OnTowerClickListener towerClickListener = null;
    // tower interface
    public interface OnTowerClickListener {
        void onTowerClick(View v, int position);
//        void onDeleteClick(View v, int position);
    }
    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnTowerClickListener(TowerListAdapter.OnTowerClickListener listener) {
        this.towerClickListener = listener;
    }

    // adapter 초기화
    public TowerListAdapter(Context context, ArrayList<Tower> towerList) {
        this.context = context;
        this.towerList = towerList;
        inflater = LayoutInflater.from(context);
    }

    // 타워 목록 갯수 반환
    @Override
    public int getCount() {
        return towerList.size();
    }
    // 번호에 해당하는 타워 반환
    @Override
    public Tower getItem(int position) {
        return towerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    //
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // xml -> view
        View view = inflater.inflate(R.layout.item_tower,null);
        // view의 요소 binding
        ImageView towerImage = view.findViewById(R.id.iv_tower);
        TextView towerName = view.findViewById(R.id.name_tv_tower);
        TextView towerPrice = view.findViewById(R.id.price_tv_tower);
        ImageView coinImage = view.findViewById(R.id.coin_iv_tower);
        // 요소에 값을 넣어준다
        Tower tower = towerList.get(position);
        towerImage.setImageResource(tower.getImageResource());
        towerName.setText(tower.getName());
        towerPrice.setText(tower.getPrice()+"");
        coinImage.setImageResource(R.drawable.coin);
        // 타워 생성 click listener
        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                towerClickListener.onTowerClick(v,position);
            }
        });
        return view;
    }
}
