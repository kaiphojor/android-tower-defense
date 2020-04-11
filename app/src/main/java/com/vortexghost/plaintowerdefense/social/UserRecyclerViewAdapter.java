package com.vortexghost.plaintowerdefense.social;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vortexghost.plaintowerdefense.R;
import com.vortexghost.plaintowerdefense.Singleton;

import java.util.ArrayList;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder> {
    // view에 표현할 data
    private ArrayList<User> data = null;

    // 인자로 받은 data를 참조
    public UserRecyclerViewAdapter(ArrayList<User> data) {
        this.data = data;
    }

    // item click listener
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
    public interface OnButtonClickListener {
        void onEditClick(View v, int position);
        void onDeleteClick(View v, int position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener itemClickListener = null;
    private OnButtonClickListener buttonClickListener = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }
    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.buttonClickListener = listener;
    }

    // view holder 생성해서 반환
    @NonNull
    @Override
    public UserRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // context 이용 inflater 초기화
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflater로 xml -> view 변환하고 반환
        View view = inflater.inflate(R.layout.item_user, parent, false);
        UserRecyclerViewAdapter.ViewHolder viewHolder = new UserRecyclerViewAdapter.ViewHolder(view);
        return viewHolder;
    }

    // 생성한 view holder에 data binding
    @Override
    public void onBindViewHolder(@NonNull UserRecyclerViewAdapter.ViewHolder holder, int position) {
        User item = data.get(position);
        // view 에 data를 집어넣는다
        holder.nicknameView.setText(item.getNickname());
        // drawable을 받아야 함
//        holder.imageView.setImageDrawable(item.getImageSource());
        holder.imageView.setImageResource(item.getImageSource());
//        holder.memoView.setText(item.getMemo());
        // 메모가 있으면 설정하고, 없으면 해당 레이아웃 자체를 숨김처리
        String memo = item.getMemo();
        if(memo.contentEquals("")){
            holder.memoView.setVisibility(View.INVISIBLE);
        }else{
            holder.memoView.setText(memo);
            if(holder.memoView.getVisibility() == View.INVISIBLE){
                holder.memoView.setVisibility(View.VISIBLE);
            }
        }
        int drawableRessource = item.isFavorite() ? R.drawable.favourite_filled : R.drawable.favourite_blank ;
        holder.favoriteView.setImageResource(drawableRessource);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    // 아이템 삭제
    public void deleteItem(int position){
        // 이미 activity 쪽에서 지웠으니 두번 지우지 않는다
        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, data.size());
    }
    public void editItem(int position){
        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, data.size());
    }

    // view를 담아두는 holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // 닉네임, 프로필 사진, 메모.(메모만 수정가능)
        TextView nicknameView;
        ImageView imageView;
        TextView memoView;
        ImageView favoriteView;
        // 각종 버튼
        Button editMemoButton;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // view binding
            nicknameView = itemView.findViewById(R.id.name_tv_user);
            imageView = itemView.findViewById(R.id.profile_image_iv_user);
            memoView = itemView.findViewById(R.id.memo_tv_user);
            editMemoButton = itemView.findViewById(R.id.modify_bt_user);
            deleteButton = itemView.findViewById(R.id.delete_bt_user);
            favoriteView = itemView.findViewById(R.id.favorite_iv_user);
            // 버튼 별 click listener
            editMemoButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);
            favoriteView.setOnClickListener(this);

            // 아이템 클릭 이벤트 처리.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
//                        Singleton.log(pos + ": 이름 클릭");
                        User item = data.get(pos);
                        // listener 객체의 메서드 호출 -> activity에서 처리
                        if (itemClickListener != null) {
                            itemClickListener.onItemClick(v,pos);
                            notifyItemChanged(pos) ;
                        }
                    }

                    // TODO : process click event.
                }
            });
        }
        // onClick method
        @Override
        public void onClick(View v) {
            // view 구분 위한 id
            int id = v.getId();
            // list 내 선택한 item의 위치
            int position = getAdapterPosition();
            switch (id) {
                /*
                수정, 삭제. 실제 구현은 activity에서 한다.
                 */
                case R.id.modify_bt_user:
//                    Singleton.log(pos + ": 이름 클릭");
                    buttonClickListener.onEditClick(v,position);
                    break;
                case R.id.delete_bt_user:
//                    Singleton.log(pos + ": 삭제 클릭");
                    buttonClickListener.onDeleteClick(v,position);
                    break;
                // 즐겨찾기 추가/해제.
                case R.id.favorite_iv_user:
                    User item = data.get(position);
                    if(item.isFavorite()){
                        Singleton.toast(position +": 즐겨찾기 해제",false);
                        item.setFavorite(false);
                        notifyItemChanged(position);
                    }else{
                        Singleton.toast(position +": 즐겨찾기 추가",false);
                        item.setFavorite(true);
                        notifyItemChanged(position);
                    }
                    break;
            }
        }
    }
}
