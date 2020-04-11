package com.vortexghost.plaintowerdefense.social;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vortexghost.plaintowerdefense.R;
import com.vortexghost.plaintowerdefense.Singleton;

import java.util.ArrayList;

public class UserListViewAdapter extends BaseAdapter implements View.OnClickListener{
    // context, xml-> view 만들어주는 inflater, 유저 목록
    Context context = null;
    LayoutInflater inflater = null;
    ArrayList<User> data;



    public interface OnButtonClickListener {
        void onEditClick(View v, int position);
        void onDeleteClick(View v, int position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private UserListViewAdapter.OnButtonClickListener buttonClickListener = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnButtonClickListener(UserListViewAdapter.OnButtonClickListener listener) {
        this.buttonClickListener = listener;
    }
    public UserListViewAdapter(Context context, ArrayList<User> data) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }
    // 타워 목록 갯수 반환
    @Override
    public int getCount() {
        return data.size();
    }
    // 번호에 해당하는 타워 반환
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // position과 context를 저장.
        final int pos = position;
        final Context context = parent.getContext();

        // xml -> view
        View view = inflater.inflate(R.layout.item_user,null);
        // view binding - 요소를 담는 view holder 안쓰기 때문에 final을 씀(안그러면 click listener에서 접근못함)
        final TextView  nicknameView = view.findViewById(R.id.name_tv_user);
        final ImageView imageView = view.findViewById(R.id.profile_image_iv_user);
        final TextView memoView = view.findViewById(R.id.memo_tv_user);
        final ImageView favoriteView = view.findViewById(R.id.favorite_iv_user);
        Button editMemoButton = view.findViewById(R.id.modify_bt_user);
        final Button deleteButton = view.findViewById(R.id.delete_bt_user);


        // data setting -  recyclerview에서는 onbindviewholder 에서 담당
        final User item = data.get(position);
        // view 에 data를 집어넣는다
        nicknameView.setText(item.getNickname());
        // drawable을 받아야 함
        imageView.setImageResource(item.getImageSource());
        // 메모가 있으면 설정하고, 없으면 해당 레이아웃 자체를 숨김처리
        String memo = item.getMemo();
        if(memo.contentEquals("")){
            memoView.setVisibility(View.INVISIBLE);
        }else{
            memoView.setText(memo);
            if(memoView.getVisibility() == View.INVISIBLE){
                memoView.setVisibility(View.VISIBLE);
            }
        }
        int drawableRessource = item.isFavorite() ? R.drawable.favourite_filled : R.drawable.favourite_blank ;
        favoriteView.setImageResource(drawableRessource);

        // 버튼 별 click listener - 실제로는 activity에서 구현
        editMemoButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                buttonClickListener.onEditClick(v,pos);
            }
        });
        deleteButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                buttonClickListener.onDeleteClick(v,pos);
            }
        });
        favoriteView.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(item.isFavorite()){
                    Singleton.toast(pos +": 즐겨찾기 해제",false);
                    item.setFavorite(false);
                }else{
                    Singleton.toast(pos +": 즐겨찾기 추가",false);
                    item.setFavorite(true);
                }
                notifyDataSetChanged();
            }
        });


        return view;
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        Singleton.log(id+"");
        switch(id){
            case R.id.modify_bt_user:
                break;
        }
    }
    // 아이템 삭제
    public void deleteItem(int position){
        // 이미 activity 쪽에서 지웠으니 두번 지우지 않는다
        data.remove(position);
        notifyDataSetChanged();
    }
}
