package com.vortexghost.plaintowerdefense.social;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.vortexghost.plaintowerdefense.error_collect.BaseActivity;
import com.vortexghost.plaintowerdefense.LoginSingleton;
import com.vortexghost.plaintowerdefense.R;
import com.vortexghost.plaintowerdefense.Singleton;

import java.util.ArrayList;

public class SocialActivity extends BaseActivity implements View.OnClickListener{

    Button addFriendButton;

    // recycler view 표현 위한 view, adapter, arrayList
    RecyclerView userRecyclerView = null;
    UserRecyclerViewAdapter adapter = null;
    ArrayList<User> userList = new ArrayList<User>();

    /*
    전체 activity 공통 변수
     */
    // 로그인 용
    TextView nicknameTextView;
    ImageView profileImageView;
    // 태그 = 현재 activity 이름
    private final String TAG = this.getClass().getSimpleName();
    // context = 현재 context
    private Context context = this;
    // intent
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);
        /*
        전체 activity 공통
         */
        // 로그인 상태 표시
        nicknameTextView = findViewById(R.id.nickname_tv_social);
        profileImageView = findViewById(R.id.profile_iv_social);

        /*
        recyclerview 준비
         */
        for(int i = 0 ; i<50 ; i++){
            userList.add(new User("교황","갓갓",R.drawable.emoji_chimpanzee));
            userList.add(new User("감비아외교부장관","ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ",R.drawable.emoji_baby_chick));
            userList.add(new User("싱하","고인물",R.drawable.emoji_bat));
            userList.add(new User("삼도수군통제사","갓갓갓",R.drawable.emoji_badger));
            userList.add(new User("개고기중독자","",R.drawable.emoji_bird));
            userList.add(new User("유치원급식도둑","",R.drawable.emoji_crocodile));
            userList.add(new User("똥빚는기계","",R.drawable.emoji_blossom));
            userList.add(new User("어쌔신크리드","중2",R.drawable.emoji_fox));
            userList.add(new User("삼다수통제조사","",R.drawable.emoji_bug));
            userList.add(new User("상투메프린시페","한상동맹",R.drawable.emoji_wolf));

            userList.add(new User("조광덕","한상동맹",R.drawable.emoji_deciduous_tree));
            userList.add(new User("ㅇㅇ","기본 닉네임",R.drawable.emoji_hamster));
            userList.add(new User("1학년4반강하은","ㅇㅇ",R.drawable.emoji_turtle));
            userList.add(new User("썬연료가좋더라","네임드",R.drawable.emoji_penguin));
            userList.add(new User("넓적부리황새","ㅇㅈ",R.drawable.emoji_blossom));
            userList.add(new User("아스피린소년","",R.drawable.emoji_cat_face));
            userList.add(new User("백수묵시록","중붕이",R.drawable.emoji_blow_fish));
            userList.add(new User("콰지모도","",R.drawable.emoji_ant));
            userList.add(new User("군필여고생","",R.drawable.emoji_bug));
            userList.add(new User("토드하워드","갓",R.drawable.emoji_cactus));
        }
        userRecyclerView = findViewById(R.id.rv_social);
        adapter = new UserRecyclerViewAdapter(userList);
        // TODO : 아이템 클릭 이벤트를 MainActivity에서 처리.
        adapter.setOnItemClickListener(new UserRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                int id = v.getId();
            }
        }) ;
        // 실제 구현은 activity에서 한다
        adapter.setOnButtonClickListener(new UserRecyclerViewAdapter.OnButtonClickListener() {
            // item 메모 수정
            @Override
            public void onEditClick(View v, int position) {
//                Singleton.toast(position + ": 수정", false);
                String memoString = userList.get(position).getMemo();
                editMemoDialog(position,memoString);
            }
            // item 삭제
            @Override
            public void onDeleteClick(View v, int position) {
//                Singleton.toast(position + ": 삭제", false);
                adapter.deleteItem(position);
            }
        });

        userRecyclerView.setAdapter(adapter);
        // 리사이클러뷰에 LinearLayoutManager 지정. (vertical)
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this)) ;

        /*
        view binding & click listener 등록
         */

        addFriendButton = findViewById(R.id.add_friend_bt_social);
        addFriendButton.setOnClickListener(this);
    }

    /*
    전체 activity 공통
     */
    @Override
    protected void onStart() {
        super.onStart();
        // 로그인한 계정에 따라 닉네임/프로필 세팅
        LoginSingleton.getInstance(context);
        LoginSingleton.loginOnStart(nicknameTextView,profileImageView);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            // 친구 추가 버튼
            case R.id.add_friend_bt_social:
                addFriendDialog();
                break;
            default:
                Singleton.log("default");
                break;
        }
    }
    // 친구 추가
    public void addFriendDialog(){
        // 2. 레이아웃 파일 edit_box.xml 을 불러와서 화면에 다이얼로그를 보여줍니다.

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_add_friend, null, false);
        builder.setView(view);
        // view binding
        final Button submitButton = view.findViewById(R.id.submit_bt_add_friend);
        final EditText nicknameEditText = view.findViewById(R.id.nickname_et_add_friend);
        final EditText memoEditText = view.findViewById(R.id.memo_et_add_friend);
//        submitButton.setText("삽입");

        final AlertDialog dialog = builder.create();

        // 3. 다이얼로그에 있는 삽입 버튼을 클릭하면
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 4. 사용자가 입력한 내용을 가져와서
                String nickname = nicknameEditText.getText().toString();
                String memo = memoEditText.getText().toString();

                // 5. ArrayList에 추가하고

                User user = new User(nickname, memo,R.drawable.emoji_panda);
                //TODO : 가나다 순? 순서를 정하는데 사용
                userList.add(0, user); //첫번째 줄에 삽입됨
                //mArrayList.add(dict); //마지막 줄에 삽입됨


                // 6. 어댑터에서 RecyclerView에 반영하도록 합니다.

                adapter.notifyItemInserted(0);
                adapter.notifyDataSetChanged();


                dialog.dismiss();
            }
        });
        dialog.show();
    }

    // 친구 추가
    public void editMemoDialog(final int position,String memo){
        // 2. 레이아웃 파일 edit_box.xml 을 불러와서 화면에 다이얼로그를 보여줍니다.

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_edit_memo, null, false);
        builder.setView(view);
        // view binding
        final Button submitButton = view.findViewById(R.id.submit_bt_edit_memo);
        final EditText memoEditText = view.findViewById(R.id.memo_et_edit_memo);
        // 이전의 메모를 가져온다
        memoEditText.setText(memo);

        final AlertDialog dialog = builder.create();

        // 3. 다이얼로그에 있는 삽입 버튼을 클릭하면
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 4. 사용자가 입력한 내용을 가져와서
                String memo = memoEditText.getText().toString();

                // 5. ArrayList에 추가하고

                User user = userList.get(position);
                user.setMemo(memo);
                //TODO : 가나다 순? 순서를 정하는데 사용
                userList.set(position,user);

                // 6. 어댑터에서 RecyclerView에 반영하도록 합니다.

                adapter.notifyItemChanged(position);
                adapter.notifyDataSetChanged();

                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
