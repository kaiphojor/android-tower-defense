package com.vortexghost.plaintowerdefense.gem_shop;

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
import com.vortexghost.plaintowerdefense.social.UserRecyclerViewAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class GemRecyclerViewAdapter extends RecyclerView.Adapter<GemRecyclerViewAdapter.ViewHolder> {
    // 팔 품목 데이터
    ArrayList<Gem> data;
    // context;
    Context context;

    // item click listener
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener itemClickListener = null;
    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public GemRecyclerViewAdapter(Context context) {
        this.context = context;
    }
    public void setData(ArrayList<Gem> data) {
        if(this.data != data){
            this.data = data;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // view 생성 반환
        View view = LayoutInflater.from(context).inflate(R.layout.item_gem,parent,false);
        // 생성한 view가 담긴 view holder 반환
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,final int position) {
        if(getItemCount() > 0){
            if(position < getItemCount()){
                final ViewHolder viewHolder = holder;
                // view holder 에 값을 세팅한다
                viewHolder.setData(data.get(position),context);
                // 구매 버튼 click event.
                //TODO : 필요시 activity 쪽으로 빼야.
                viewHolder.buyButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if (itemClickListener != null) {
                            itemClickListener.onItemClick(v,position);
                        }
                    }
                });
            }

        }
    }

    @Override
    public int getItemCount() {
        if(data == null){
            return 0;
        }
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // 이름, 홍보 설명, 홍보시간, 가격, 이미지
        public TextView titleTextView,promotionTimeTextView,promotionDescribeTextView,promotionTimeLeftStringTextView;
        public Button buyButton;
        public ImageView imageView;
        // 데이터 클래스 정의
        public Gem gemData;

        // view를 가져와서 엮는다
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.name_tv_gem);
            promotionDescribeTextView = itemView.findViewById(R.id.promote_describe_tv_gem);
            promotionTimeTextView = itemView.findViewById(R.id.promote_time_left_tv_gem);
            buyButton = itemView.findViewById(R.id.buy_bt_gem);
            imageView = itemView.findViewById(R.id.image_iv_gem);

            promotionTimeLeftStringTextView = itemView.findViewById(R.id.promote_time_tv_gem);

//            // 아이템 클릭 이벤트 처리.
//            buyButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int pos = getAdapterPosition();
//                    if (pos != RecyclerView.NO_POSITION) {
//                        // listener 객체의 메서드 호출 -> activity에서 처리
//                        if (itemClickListener != null) {
//                            itemClickListener.onItemClick(v,pos);
//                        }
//                    }
//                    // TODO : process click event.
//                }
//            });


        }
        // 값 설정
        public void setData(Gem gem,Context context){
            this.gemData = gem;
            this.titleTextView.setText(gem.getTitle());
            this.imageView.setImageResource(gem.getImageResource());
            // 행사 여부에 따라 view에 세팅 여부를 결정
            if(gemData.isPromotion()){
                promotionDescribeTextView.setText(gem.getPromoteString());
                promotionTimeTextView.setText(gem.getPromoteDate()+"");
            }else{
                promotionDescribeTextView.setVisibility(View.GONE);
                promotionTimeTextView.setVisibility(View.GONE);
                promotionTimeLeftStringTextView.setVisibility(View.GONE);
            }
            // 콤마를 세자리마다 넣어줌
//            DecimalFormat myFormatter = new DecimalFormat("###,###");
//            String formattedStringPrice = myFormatter.format(gem.getPrice());
            String formattedStringPrice = new DecimalFormat("###,###").format(gem.getPrice());
            buyButton.setText(formattedStringPrice+" KRW");


        }
    }
}
