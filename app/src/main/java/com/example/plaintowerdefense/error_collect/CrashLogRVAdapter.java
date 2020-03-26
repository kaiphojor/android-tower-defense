package com.example.plaintowerdefense.error_collect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plaintowerdefense.R;
import com.example.plaintowerdefense.Singleton;

import java.util.ArrayList;

public class CrashLogRVAdapter extends RecyclerView.Adapter<CrashLogRVAdapter.ViewHolder> {
    // view에 표현할 data
    private ArrayList<CrashLog> data = null;
    private Context context;
    // 인자로 받은 data를 참조
    public CrashLogRVAdapter(Context context,ArrayList<CrashLog> data) {
        this.context = context;
        this.data = data;
    }

    // item click listener
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
    public interface OnButtonClickListener {
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
    public CrashLogRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // context 이용 inflater 초기화
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflater로 xml -> view 변환하고 반환
        View view = inflater.inflate(R.layout.item_crash_log, parent, false);
        CrashLogRVAdapter.ViewHolder viewHolder = new CrashLogRVAdapter.ViewHolder(view);
        return viewHolder;
    }

    // 생성한 view holder에 data binding
    @Override
    public void onBindViewHolder(@NonNull CrashLogRVAdapter.ViewHolder holder, int position) {
        CrashLog item = data.get(position);
        // view 에 data를 집어넣는다
        holder.emailView.setText(item.getId()+""); //sdkfjlsdjflsdjflsdfjlsfjldkjflsjfdk
        holder.timeView.setText(item.getTime());
        holder.descriptionView.setText(item.getDescription());
        holder.stackTraceView.setText(item.getStackTrace());
    }

    @Override
    public int getItemCount() {
        if(data == null){
            Singleton.log("no data");
            return 0;
        }
        return data.size();
    }
    // 아이템 삭제
    public void deleteItem(int position){
        // 이미 activity 쪽에서 지웠으니 두번 지우지 않는다
        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, data.size());
    }

    // view를 담아두는 holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // 닉네임, 프로필 사진, 메모.(메모만 수정가능)
        TextView emailView;
        TextView timeView;
        TextView descriptionView;
        TextView stackTraceView;
        LinearLayout contentLayout;
        // 각종 버튼

        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // view binding
            emailView = itemView.findViewById(R.id.number_tv_crash_log);
            timeView = itemView.findViewById(R.id.time_tv_crash_log);
            descriptionView = itemView.findViewById(R.id.description_tv_crash_log);
            stackTraceView = itemView.findViewById(R.id.stack_trace_tv_crash_log);

            contentLayout = itemView.findViewById(R.id.content_ll_crash_log);

            deleteButton = itemView.findViewById(R.id.delete_bt_crash_log);
            // 버튼 별 click listener

            deleteButton.setOnClickListener(this);
            //
            contentLayout.setOnClickListener(this);

            // 아이템 클릭 이벤트 처리.
            //TODO : set pop up dialog
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
//                        Singleton.log(pos + ": 이름 클릭");
                        CrashLog item = data.get(pos);
                        // listener 객체의 메서드 호출 -> activity에서 처리
                        if (itemClickListener != null) {
                            itemClickListener.onItemClick(v,pos);
//                            notifyItemChanged(pos) ;
                            // TODO : process click event.
                        }
                    }
                }
            });
            // 레이아웃도 클릭이벤트 처리
            contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
//                        Singleton.log(pos + ": 이름 클릭");
                        CrashLog item = data.get(pos);
                        // listener 객체의 메서드 호출 -> activity에서 처리
                        if (itemClickListener != null) {
                            itemClickListener.onItemClick(v,pos);
//                            notifyItemChanged(pos) ;
                            // TODO : process click event.
                        }
                    }
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
                case R.id.delete_bt_crash_log:
//                    Singleton.log(pos + ": 삭제 클릭");
                    buttonClickListener.onDeleteClick(v,position);
                    break;
            }
        }
    }
}
