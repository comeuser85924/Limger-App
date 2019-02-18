package com.example.user.limger;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cn.iwgang.countdownview.CountdownView;
import de.hdodenhof.circleimageview.CircleImageView;

public class addfriendActivity extends AppCompatActivity {
    ActionBar actionBar;

    static DatabaseReference chatmember_dataRef,create_dataReaf,member_databaseRef;
    CircleImageView Img_self;

    TextView txt_account;
    CountdownView countDownTimer;
    RecyclerView addfrinedRecyler;
    ArrayList<chat_room_member_firebase> chatmList;
    FirebaseAuth firebaseAuth;

    int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend);

        //title字體顏色設定
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFA38473")));
        View viewActionBar = getLayoutInflater().inflate(R.layout.title, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        LinearLayout myself_linearLayout = viewActionBar.findViewById(R.id.myself_linearLayout);
        myself_linearLayout.setVisibility(View.VISIBLE);
        Img_self = viewActionBar.findViewById(R.id.Img_self);
        txt_account = viewActionBar.findViewById(R.id.txt_account);
        textviewTitle.setText("Limger");
        actionBar.setCustomView(viewActionBar, params);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
//      actionBar.setDisplayHomeAsUpEnabled(true);回上一頁
        actionBar.setHomeButtonEnabled(true);

        chatmList = new ArrayList<>();

        create_dataReaf=FirebaseDatabase.getInstance().getReference("chat_room");
        chatmember_dataRef = FirebaseDatabase.getInstance().getReference("chat_room_member");
        member_databaseRef= FirebaseDatabase.getInstance().getReference("member");
        firebaseAuth = FirebaseAuth.getInstance();

        countDownTimer = findViewById(R.id.countdownview2);
        addfrinedRecyler = findViewById(R.id.addfirendRecyclerView);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        RecyclerView.LayoutManager rvlayoutManager = layoutManager;
//        addfrinedRecyler.setLayoutManager(rvlayoutManager);




    }


    @Override
    protected void onStart() {
        super.onStart();

        //(創房者)從聊天室傳值過來
        final Bundle Ban_cmember_id = this.getIntent().getExtras();
        final String ckey = Ban_cmember_id.getString("ckey");
        final String cmember_id = Ban_cmember_id.getString("cmember_id");
        //(一般使用者)從聊天室傳值過來
        final Bundle Ban_member_id = this.getIntent().getExtras();
        final String key = Ban_member_id.getString("key");
        final String member_id = Ban_member_id.getString("member_id");
        //(中途加入者)從聊天室傳值過來
        final Bundle Ban_join = this.getIntent().getExtras();
        final String jkey = Ban_join.getString("jkey");
        final String jmember_id = Ban_join.getString("jmember_id");

        member_databaseRef.child(firebaseAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Picasso.get().load(dataSnapshot.child("m_head").getValue().toString()).into(Img_self);
                txt_account.setText(dataSnapshot.child("m_account").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        create_dataReaf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatmList.clear();
                for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                    final create_firebase c_artist = artistSnapshot.getValue(create_firebase.class);
                    i +=1;
                    if(i==1){
                        countDownTimer.start(15000);
                        final Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            public void run() {
                                    chatmember_dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                                                        chat_room_member_firebase cm_artist = artistSnapshot.getValue(chat_room_member_firebase.class);

                                                        if(cm_artist.cm_id.equals(ckey)) {
                                                            chatmember_dataRef.child(cm_artist.chat_room_member_id).child("cm_status").setValue("0");
                                                        }else if(cm_artist.cm_id.equals(key)){
                                                            chatmember_dataRef.child(cm_artist.chat_room_member_id).child("cm_status").setValue("0");
                                                        }else if(cm_artist.cm_id.equals(jkey)){
                                                            chatmember_dataRef.child(cm_artist.chat_room_member_id).child("cm_status").setValue("0");
                                                        }
                                                    }
                                                    finish();
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                            }
                        }, 15000, 1000);
                    }
                    //創房者
                    if(c_artist.c_id.equals(ckey)){
                        //搜尋創房資料表欄位cm_id中有ckey都取出來
                        Query ckeySearchQuery = chatmember_dataRef.orderByChild("cm_id").startAt(ckey).endAt(ckey + "\uf8ff");
                        ckeySearchQuery.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                                    final chat_room_member_firebase artist = artistSnapshot.getValue(chat_room_member_firebase.class);


                                    if(artist.cm_id.equals(ckey)){
                                        if(artist.chat_room_member_id.equals(cmember_id)){
                                            chatmList.remove(artist);
                                        }else{
                                            if(artist.cm_status.equals("1")) {
                                                chatmList.add(artist);
                                            }
                                        }
                                    }
                                }

                                addfriendAdapter adapter = new addfriendAdapter(addfriendActivity.this, chatmList);
                                addfrinedRecyler.setAdapter(adapter);
                                System.out.println("網格排列----"+adapter);
                                GridLayoutManager manager = new GridLayoutManager(addfriendActivity.this, 3, GridLayoutManager.VERTICAL, false);
                                addfrinedRecyler.setLayoutManager(manager);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    //一般使用者
                    else if(c_artist.c_id.equals(key)){
                        //搜尋創房資料表欄位cm_id中有key都取出來
                        Query keySearchQuery = chatmember_dataRef.orderByChild("cm_id").startAt(key).endAt(key + "\uf8ff");
                        System.out.println("檢查房間編號資料筆數-----"+keySearchQuery);
                        keySearchQuery.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                                    final chat_room_member_firebase artist = artistSnapshot.getValue(chat_room_member_firebase.class);
                                    System.out.println("userData------"+artist.chat_room_member_id);

                                    if(artist.cm_id.equals(key)){
                                        if(artist.chat_room_member_id.equals(member_id)){
                                            chatmList.remove(artist);
                                        }else{
                                            if(artist.cm_status.equals("1")) {
                                                chatmList.add(artist);
                                            }
                                        }

                                    }

                                }
                                addfriendAdapter adapter = new addfriendAdapter(addfriendActivity.this, chatmList);
                                addfrinedRecyler.setAdapter(adapter);
                                GridLayoutManager manager = new GridLayoutManager(addfriendActivity.this, 3, GridLayoutManager.VERTICAL, false);
                                addfrinedRecyler.setLayoutManager(manager);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    //中途加入者
                    else if(c_artist.c_id.equals(jkey)){
                        //搜尋創房資料表欄位cm_id中有jkey都取出來
                        Query jkeySearchQuery = chatmember_dataRef.orderByChild("cm_id").startAt(jkey).endAt(jkey + "\uf8ff");
                        System.out.println("檢查房間編號資料筆數-----"+jkeySearchQuery);

                        jkeySearchQuery.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                                    final chat_room_member_firebase artist = artistSnapshot.getValue(chat_room_member_firebase.class);
                                    if(artist.cm_id.equals(jkey)){
                                        if(artist.chat_room_member_id.equals(jmember_id)){
                                            chatmList.remove(artist);
                                        }else{
                                            if(artist.cm_status.equals("1")) {
                                                System.out.println("測試傳來的memberid------"+artist.chat_room_member_id);
                                                System.out.println("測試搜尋結果傳回來的狀態------"+artist.cm_status);
                                                chatmList.add(artist);
                                            }
                                        }

                                    }

                                }
                                addfriendAdapter adapter = new addfriendAdapter(addfriendActivity.this, chatmList);
                                addfrinedRecyler.setAdapter(adapter);
                                GridLayoutManager manager = new GridLayoutManager(addfriendActivity.this, 3, GridLayoutManager.VERTICAL, false);
                                addfrinedRecyler.setLayoutManager(manager);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
}
