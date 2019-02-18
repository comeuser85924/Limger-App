package com.example.user.limger;

import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.limger.Util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cn.iwgang.countdownview.CountdownView;
import de.hdodenhof.circleimageview.CircleImageView;

public class chatActivity extends AppCompatActivity {
    ActionBar actionBar;
    ListView chatlistview;
    EditText chat_et_message;
    Button chat_bt_send;
    DatabaseReference createdataRef, messagedataRef, wtdataRef, memberdataRef;
    ChildEventListener addmemberListener;
    FirebaseAuth firebaseAuth;
    SimpleDateFormat simpleDateFormat;
    String Date;
    Calendar calendar;
    List<message_firebase> msgList;
    List<chat_room_member_firebase> memberList;
    CountdownView chat_countDownTimer;
    ImageButton chat_imgbts;
    CircleImageView Img_self;
    TextView textviewTitle;
    TextView txt_account;

    //取得chat_room_message中的資料
    String msgID = null;
    String msgroomid = null;
    String msgaccount = null;
    String msgtime = null;
    String msgMESSAGE = null;
    String msgTYPE = null;
    String msgMemberID = null;
    String msgMemberHead = null;

    Timer timer;
    boolean roomcountdown = false;//計時器啟動和關閉

    String c_id;                //房間id
    String c_intoroomtime;      //從主頁進入聊天室的當下時間
    String c_status;            //房間狀態
    int c_personnum;            //房間內總人數


    String cm_id;              //房間id
    String memberaccount;      //成員帳號
    String cm_status;          //成員狀態
    String memberkey;//成員id
    String cm_memeber_id;

    long roomtime;              //設定的聊天時間*1000
    long millionSeconds = 0, currentmill;//目前的時間毫秒,資料庫倒數時間毫秒
    long lasttime;              //主頁到聊天室中過了多久
    long c_time;                //從資料庫取得創房時的時間
    Double dc_time = 0.1;
    int i = 0, x = 0;
    int Prevent_rerun = 0;
    final int[] sum = {0};      //幣值

    String user_uid;
    String m_head;
    final int[] m_coin = {0};
    String roomid;
    Boolean bl_close = false , bl_createclose = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //title字體顏色設定
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFA38473")));
        View viewActionBar = getLayoutInflater().inflate(R.layout.title, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        LinearLayout myself_linearLayout = viewActionBar.findViewById(R.id.myself_linearLayout);
        myself_linearLayout.setVisibility(View.VISIBLE);
        Img_self = viewActionBar.findViewById(R.id.Img_self);
        txt_account = viewActionBar.findViewById(R.id.txt_account);
        textviewTitle.setText("Limger");

        actionBar.setCustomView(viewActionBar, params);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);


        createdataRef = FirebaseDatabase.getInstance().getReference("chat_room");
        messagedataRef = FirebaseDatabase.getInstance().getReference("chat_room_message");
        wtdataRef = FirebaseDatabase.getInstance().getReference("chat_room_member");
        memberdataRef = FirebaseDatabase.getInstance().getReference("member");
        firebaseAuth = FirebaseAuth.getInstance();
        user_uid = firebaseAuth.getCurrentUser().getUid();


        msgList = new ArrayList<>();
        memberList = new ArrayList<>();
        chatmsg_defin();//定義

        chaymsg_onstart();//讀取各種資料及執行倒數
        chatmsg_send();//傳送訊息
        chatmsg_sendview();//顯示訊息(左右邊)
        addmemberchat();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean flag;
        switch (item.getItemId()) {
            case android.R.id.home:
                exitchatroom();
                flag = true;
                break;
            default:
                flag = super.onOptionsItemSelected(item);
                break;
        }
        return flag;
    }

    //取得物件
    public void chatmsg_defin() {
        chat_imgbts = findViewById(R.id.chat_imgbt);
        chat_et_message = findViewById(R.id.private_chat_et_message);
        chat_bt_send = findViewById(R.id.chat_bt_send);
        chatlistview = findViewById(R.id.private_chatlistview);
        chat_countDownTimer = findViewById(R.id.chat_countdownview);

    }

    //顯示訊息(左右邊)
    private void chatmsg_sendview() {
        //從等待室中取得創房者房間key和創房者帳號
        final Bundle Ban_ckeyandcaccount = this.getIntent().getExtras();
        final String ckey = Ban_ckeyandcaccount.getString("ckey");
        final String caccount = Ban_ckeyandcaccount.getString("caccount");
        //從等待室中取得一般使用者房間key和一般使用者帳號
        final Bundle Ban_keyandcaccount = this.getIntent().getExtras();
        final String key = Ban_keyandcaccount.getString("key");
        final String account = Ban_keyandcaccount.getString("account");
        //有開放中途加入，直接從主頁進聊天室
        final Bundle Ban_joinkeyandaccount = this.getIntent().getExtras();
        final String jkey = Ban_joinkeyandaccount.getString("jkey");
        final String jmember_account = Ban_joinkeyandaccount.getString("member_account");
        messagedataRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                msgID = String.valueOf((dataSnapshot.child("message_id").getValue()));
                msgroomid = String.valueOf((dataSnapshot.child("cmes_id").getValue()));
                msgaccount = String.valueOf((dataSnapshot.child("cmes_member").getValue()));
                msgtime = String.valueOf((dataSnapshot.child("cmes_time").getValue()));
                msgMESSAGE = String.valueOf((dataSnapshot.child("cmes_message").getValue()));
                msgTYPE = String.valueOf(dataSnapshot.child("cmes_msgtype").getValue());
                msgMemberID = String.valueOf(dataSnapshot.child("cmes_member_id").getValue());


                //區分不同房(key)，以免聊天訊息互通
                if (msgroomid.equals(ckey) || msgroomid.equals(key) || msgroomid.equals(jkey)) {
                    if (msgTYPE == message_firebase.TYPE_SEND) {
                        message_firebase datamsg = dataSnapshot.getValue(message_firebase.class);
                        msgList.add(datamsg);
                    } else {
                        message_firebase datamsg = new message_firebase(msgID, msgroomid, msgaccount, msgtime, msgMESSAGE, message_firebase.TYPE_RECEIVED, msgMemberID);
                        msgList.add(datamsg);


                    }
                    messageList adapter = new messageList(chatActivity.this, msgList);
                    chatlistview.setAdapter(adapter);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void addmemberchat() {
        //從等待室中取得創房者房間key和創房者帳號
        final Bundle Ban_ckeyandcaccount = this.getIntent().getExtras();
        final String ckey = Ban_ckeyandcaccount.getString("ckey");
        //從等待室中取得一般使用者房間key和一般使用者帳號
        final Bundle Ban_keyandcaccount = this.getIntent().getExtras();
        final String key = Ban_keyandcaccount.getString("key");
        //有開放中途加入，直接從主頁進聊天室
        final Bundle Ban_joinkeyandaccount = this.getIntent().getExtras();
        final String jkey = Ban_joinkeyandaccount.getString("jkey");
        if (ckey != null) {
            roomid = ckey;
        } else if (key != null) {
            roomid = key;
        } else if (jkey != null) {
            roomid = jkey;
        }
        addmemberListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                chat_room_member_firebase chatMemberData = dataSnapshot.getValue(chat_room_member_firebase.class);
                if (chatMemberData.cm_id.equals(roomid)) {
                    if (chatMemberData.cm_status.equals("1")) {
                        if (!chatMemberData.cm_member_id.equals(user_uid)) {
                            memberdataRef.child(chatMemberData.cm_member_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Toast.makeText(chatActivity.this, dataSnapshot.child("m_account").getValue().toString() + "已加入"
                                            , Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                chat_room_member_firebase chatMemberData = dataSnapshot.getValue(chat_room_member_firebase.class);
                if (chatMemberData.cm_id.equals(roomid)) {
                    if (chatMemberData.cm_status.equals("1")) {
                        if (!chatMemberData.cm_member_id.equals(user_uid)) {
                            memberdataRef.child(chatMemberData.cm_member_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Toast.makeText(chatActivity.this, dataSnapshot.child("m_account").getValue().toString() + "已加入"
                                            , Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }else{
                        if (!chatMemberData.cm_member_id.equals(user_uid)) {
                            memberdataRef.child(chatMemberData.cm_member_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!bl_close)
                                    Toast.makeText(chatActivity.this, dataSnapshot.child("m_account").getValue().toString() + "已退出"
                                            , Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        wtdataRef.addChildEventListener(addmemberListener);

        System.out.println("roomID----" + roomid);
    }

    //讀取各種資料及執行倒數
    private void chaymsg_onstart() {

        //從等待室中取得創房者房間key和創房者帳號
        final Bundle Ban_ckeyandcaccount = this.getIntent().getExtras();
        final String ckey = Ban_ckeyandcaccount.getString("ckey");
        final String caccount = Ban_ckeyandcaccount.getString("caccount");
        final String cmember_id = Ban_ckeyandcaccount.getString("cmember_id");
        //從等待室中取得一般使用者房間key和一般使用者帳號
        final Bundle Ban_keyandcaccount = this.getIntent().getExtras();
        final String key = Ban_keyandcaccount.getString("key");
        final String account = Ban_keyandcaccount.getString("account");
        final String member_id = Ban_keyandcaccount.getString("member_id");
        //有開放中途加入，直接從主頁進聊天室
        final Bundle Ban_joinkeyandaccount = this.getIntent().getExtras();
        final String jkey = Ban_joinkeyandaccount.getString("jkey");
        final String jmember_account = Ban_joinkeyandaccount.getString("member_account");
        final String jmember_id = Ban_joinkeyandaccount.getString("Chat_Main_memberkey");

        memberdataRef.child(user_uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                memberaccount = dataSnapshot.child("m_account").getValue().toString();

                m_head = dataSnapshot.child("m_head").getValue().toString();
                m_coin[0] = Integer.parseInt(dataSnapshot.child("m_coin").getValue().toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //取得member帳號(創房者、進等待室後的使用者、從主頁點選進來的使用者)
        wtdataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                    chat_room_member_firebase artist = artistSnapshot.getValue(chat_room_member_firebase.class);

                    if (artist.chat_room_member_id.equals(cmember_id)) {
                        if (artist.cm_member_id.equals(user_uid)) {
                            cm_id = artist.cm_id;
                            cm_status = artist.cm_status;
                            memberkey = artist.chat_room_member_id;
                            cm_memeber_id = artist.cm_member_id;
                        }
                    } else if (artist.chat_room_member_id.equals(member_id)) {
                        if (artist.cm_member_id.equals(user_uid)) {
                            cm_id = artist.cm_id;
                            cm_status = artist.cm_status;
                            memberkey = artist.chat_room_member_id;
                            cm_memeber_id = artist.cm_member_id;
                        }
                    } else if (artist.chat_room_member_id.equals(jmember_id)) {
                        if (artist.cm_member_id.equals(user_uid)) {
                            cm_id = artist.cm_id;
                            cm_status = artist.cm_status;
                            memberkey = artist.chat_room_member_id;
                            cm_memeber_id = artist.cm_member_id;
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //取得創房的訊息，房間id、從等待室到聊天室的時間、創房時限制的聊天時間
        createdataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                    final create_firebase cf = artistSnapshot.getValue(create_firebase.class);
                    //取得創房者資訊
                    if (cf.c_id.equals(ckey)) {
                        c_id = cf.c_id;
                        c_intoroomtime = cf.c_intoroomtime;
                        //前端創房-1
                        if (cf.c_time == 30) {
                            dc_time = 0.5;
                        }
                        //後端創房
                        else if (cf.c_time == 15) {
                            dc_time = 0.25;
                        }
                        //前端創房-2
                        else if (cf.c_time == 90){
                            dc_time = 1.5;
                        }else {
                            c_time = cf.c_time;
                        }
                        c_status = cf.c_status;
                        c_personnum = cf.c_personnum;
                        memberdataRef.child(user_uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.child("m_deal").getValue().toString().equals("1")) {
                                    roomcountdown = true;
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        if (c_status.equals("3")) {
                            if (cm_status.equals("1")) {
                                bl_close = true;
                                timer.cancel();
                                roomcountdown = true;
                                wtdataRef.child(memberkey).child("cm_status").setValue("0");
                                Toast.makeText(chatActivity.this, "此房間已被強制關閉,詳細請看通知...", Toast.LENGTH_SHORT).show();
                                finish();
                                break;

                            }
                        }
                    }
                    //取得一般使用者資訊及判斷房間狀態轉跳頁面
                    else if (cf.c_id.equals(key)) {
                        c_id = cf.c_id;
                        c_intoroomtime = cf.c_intoroomtime;
                        //前端創房-1
                        if (cf.c_time == 30) {
                            dc_time = 0.5;
                        }
                        //後端創房
                        else if (cf.c_time == 15) {
                            dc_time = 0.25;
                        }
                        //前端創房-2
                        else if (cf.c_time == 90){
                            dc_time = 1.5;
                        }else {
                            c_time = cf.c_time;
                        }
                        c_status = cf.c_status;
                        c_personnum = cf.c_personnum;

                        memberdataRef.child(user_uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child("m_deal").getValue().toString().equals("1")) {
                                    x += 1;
                                    if (x == 1) {
                                        roomcountdown = true;
                                        finish();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        if (c_status.equals("3")) {
                            if (cm_status.equals("1")) {
                                bl_close = true;
                                roomcountdown = true;
                                wtdataRef.child(memberkey).child("cm_status").setValue("0");
                                Toast.makeText(chatActivity.this, "房主中途退出或此房已被系統強制關閉", Toast.LENGTH_SHORT).show();
                                finish();
                                break;
                            }
                        } else if (c_status.equals("2")) {
                            if (cm_status.equals("1")) {
                                if (cf.c_mid.equals("O1n30ZUQSuaLQhAhNItGiOUSavm1")) {
                                    memberdataRef.child(user_uid).child("m_coin").setValue(m_coin[0] + 5)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Bundle Ban_keyandcaccount = new Bundle();
                                                        Ban_keyandcaccount.putString("key", key);
                                                        Ban_keyandcaccount.putString("account", account);
                                                        Ban_keyandcaccount.putString("member_id", memberkey);
                                                        Intent intent = new Intent(chatActivity.this, addfriendActivity.class);
                                                        intent.putExtras(Ban_keyandcaccount);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(chatActivity.this, "網路有延遲或系統有誤", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    memberdataRef.child(user_uid).child("m_coin").setValue(m_coin[0] - 1)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Bundle Ban_keyandcaccount = new Bundle();
                                                        Ban_keyandcaccount.putString("key", key);
                                                        Ban_keyandcaccount.putString("account", account);
                                                        Ban_keyandcaccount.putString("member_id", memberkey);
                                                        Intent intent = new Intent(chatActivity.this, addfriendActivity.class);
                                                        intent.putExtras(Ban_keyandcaccount);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(chatActivity.this, "網路有延遲或系統有誤", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }


                            }
                        }

                    }
                    //取得中途加入者資訊及判斷房間狀態轉跳頁面
                    else if (cf.c_id.equals(jkey)) {
                        c_id = cf.c_id;
                        c_intoroomtime = cf.c_intoroomtime;
                        //前端創房-1
                        if (cf.c_time == 30) {
                            dc_time = 0.5;
                        }
                        //後端創房
                        else if (cf.c_time == 15) {
                            dc_time = 0.25;
                        }
                        //前端創房-2
                        else if (cf.c_time == 90){
                            dc_time = 1.5;
                        }else {
                            c_time = cf.c_time;
                        }
                        c_status = cf.c_status;
                        c_personnum = cf.c_personnum;

                        memberdataRef.child(user_uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.child("m_deal").getValue().toString().equals("1")) {
                                    x += 1;
                                    if (x == 1) {
                                        if (c_status.equals("1")) {
                                            if (cm_status.equals("1")) {
                                                if (cm_memeber_id.equals(user_uid)) {
                                                    roomcountdown = true;
                                                    createdataRef.child(c_id).child("c_personnum").setValue(c_personnum - 1);
                                                    wtdataRef.child(memberkey).child("cm_status").setValue("0");
                                                    Toast.makeText(chatActivity.this, "此帳號已被封鎖一天,詳細請查看通知...", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }

                                            }
                                        }
                                    }


                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        if (c_status.equals("3")) {
                            if (cm_status.equals("1")) {
                                bl_close = true;
                                roomcountdown = true;
                                wtdataRef.child(memberkey).child("cm_status").setValue("0");
                                Toast.makeText(chatActivity.this, "房主中途退出或此房已被系統強制關閉", Toast.LENGTH_SHORT).show();
                                finish();
                                break;


                            }
                        } else if (c_status.equals("2")) {
                            if (cm_status.equals("1")) {
                                if (cf.c_mid.equals("O1n30ZUQSuaLQhAhNItGiOUSavm1")) {
                                    memberdataRef.child(user_uid).child("m_coin").setValue(m_coin[0] + 5).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Bundle Ban_keyandcaccount = new Bundle();
                                                Ban_keyandcaccount.putString("key", jkey);
                                                Ban_keyandcaccount.putString("account", account);
                                                Ban_keyandcaccount.putString("member_id", memberkey);
                                                Intent intent = new Intent(chatActivity.this, addfriendActivity.class);
                                                intent.putExtras(Ban_keyandcaccount);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(chatActivity.this, "網路有延遲或系統有誤", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    memberdataRef.child(user_uid).child("m_coin").setValue(m_coin[0] - 1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Bundle Ban_keyandcaccount = new Bundle();
                                                Ban_keyandcaccount.putString("key", jkey);
                                                Ban_keyandcaccount.putString("account", account);
                                                Ban_keyandcaccount.putString("member_id", memberkey);
                                                Intent intent = new Intent(chatActivity.this, addfriendActivity.class);
                                                intent.putExtras(Ban_keyandcaccount);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(chatActivity.this, "網路有延遲或系統有誤", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }


                            }
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //處理倒數時間
        createdataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("測試jkey-----" + jkey);
                for (final DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                    final create_firebase artist = artistSnapshot.getValue(create_firebase.class);
                    //創房者
                    if (artist.getc_id().equals(ckey)) {
                        System.out.println("帳號資料表---" + memberaccount);
                        System.out.println("帳號傳過來---" + caccount);
                        if (memberaccount.equals(caccount)) {
                            if (dc_time.equals(0.5)) {
                                roomtime = (long) (0.5 * 60 * 1000);
                            } else if (dc_time.equals(0.25)) {
                                roomtime = (long) (0.25 * 60 * 1000);
                            } else if (dc_time.equals(1.5)) {
                                roomtime = (long) (1.5 * 60 * 1000);
                            }
                            else {
                                roomtime = c_time * 60 * 1000;
                            }

                            chat_countDownTimer.start(roomtime);

                            timer = new Timer();
                            timer.schedule(new TimerTask() {
                                public void run() {
                                    if (roomcountdown == false) {

                                        roomcountdown = true;
                                        memberdataRef.child(user_uid).child("m_coin").setValue(m_coin[0] + 2);
                                        createdataRef.child(ckey).child("c_status").setValue("2");
//                                        wtdataRef.child(memberkey).child("cm_status").setValue("0");
                                        Bundle Ban_ckeyandaccount = new Bundle();
                                        Ban_ckeyandaccount.putString("ckey", ckey);
                                        Ban_ckeyandaccount.putString("caccount", caccount);
                                        Ban_ckeyandaccount.putString("cmember_id", memberkey);
                                        Intent intent = new Intent(chatActivity.this, addfriendActivity.class);
                                        intent.putExtras(Ban_ckeyandaccount);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }, roomtime, 1000);

                        }
                    }
                    //一般使用者(從等待室中一起進入聊天室的人)
                    else if (artist.getc_id().equals(key)) {
                        System.out.println("帳號資料表---" + memberaccount);
                        System.out.println("帳號傳過來---" + account);
                        if (memberaccount.equals(account)) {
                            if (dc_time.equals(0.5)) {
                                roomtime = (long) (0.5 * 60 * 1000);
                            } else if (dc_time.equals(0.25)) {
                                roomtime = (long) (0.25 * 60 * 1000);
                            }else if (dc_time.equals(1.5)) {
                                roomtime = (long) (1.5 * 60 * 1000);
                            }
                            else {
                                roomtime = c_time * 60 * 1000;
                            }
                            chat_countDownTimer.start(roomtime);

                        }
                    }


                    //從主頁近來聊天室使用者
                    else if (artist.getc_id().equals(jkey)) {
                        if (dc_time.equals(0.5)) {
                            roomtime = (long) (0.5 * 60 * 1000);
                        } else if (dc_time.equals(0.25)) {
                            roomtime = (long) (0.25 * 60 * 1000);
                        } else if (dc_time.equals(1.5)) {
                            roomtime = (long) (1.5 * 60 * 1000);
                        }
                        else {
                            roomtime = c_time * 60 * 1000;
                        }
                        //取得從"等待室進入聊天室"的時間點
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                        try {
                            //進入房間時的時間轉成總秒數
                            millionSeconds = sdf.parse(c_intoroomtime).getTime();
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }//毫秒

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //主頁到聊天室中過了多久=網路當下時間-從"等待室進入聊天室"的時間點
                                        lasttime = System.currentTimeMillis() - millionSeconds;
                                        chat_countDownTimer.start(roomtime - lasttime);
                                    }
                                });
                            }
                        }).start();


                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //傳送訊息
    private void chatmsg_send() {
        //從等待室中取得創房者房間key和創房者帳號
        final Bundle Ban_ckeyandcaccount = this.getIntent().getExtras();
        final String ckey = Ban_ckeyandcaccount.getString("ckey");
        final String caccount = Ban_ckeyandcaccount.getString("caccount");
        //從等待室中取得一般使用者房間key和一般使用者帳號
        final Bundle Ban_keyandcaccount = this.getIntent().getExtras();
        final String key = Ban_keyandcaccount.getString("key");
        final String account = Ban_keyandcaccount.getString("account");
        //有開放中途加入，直接從主頁進聊天室
        final Bundle Ban_joinkeyandaccount = this.getIntent().getExtras();
        final String jkey = Ban_joinkeyandaccount.getString("jkey");
        final String jmember_account = Ban_joinkeyandaccount.getString("member_account");
        chat_bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!chat_et_message.getText().toString().equals("")) {
                    if (memberaccount.equals(caccount)) {
                        String message = chat_et_message.getText().toString();
                        String msgkey = messagedataRef.push().getKey();
                        calendar = Calendar.getInstance();
                        simpleDateFormat = new SimpleDateFormat("HH:mm");
                        Date = simpleDateFormat.format(calendar.getTime());
                        message_firebase mf = new message_firebase(msgkey, ckey, caccount, Date, message, message_firebase.TYPE_SEND, user_uid);
                        messagedataRef.child(msgkey).setValue(mf);
                        chat_et_message.setText("");

                    } else if (memberaccount.equals(account)) {
                        String message = chat_et_message.getText().toString();
                        String msgkey = messagedataRef.push().getKey();
                        calendar = Calendar.getInstance();
                        simpleDateFormat = new SimpleDateFormat("HH:mm");
                        Date = simpleDateFormat.format(calendar.getTime());
                        message_firebase mf = new message_firebase(msgkey, key, account, Date, message, message_firebase.TYPE_SEND, user_uid);
                        messagedataRef.child(msgkey).setValue(mf);
                        chat_et_message.setText("");
                    } else if (memberaccount.equals(jmember_account)) {
                        String message = chat_et_message.getText().toString();
                        String msgkey = messagedataRef.push().getKey();
                        calendar = Calendar.getInstance();
                        simpleDateFormat = new SimpleDateFormat("HH:mm");
                        Date = simpleDateFormat.format(calendar.getTime());
                        message_firebase mf = new message_firebase(msgkey, jkey, jmember_account, Date, message, message_firebase.TYPE_SEND, user_uid);
                        messagedataRef.child(msgkey).setValue(mf);
                        chat_et_message.setText("");
                    }
                }


            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        memberdataRef.child(user_uid)
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


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitchatroom();
        }

        return true;
    }


    private void exitchatroom() {
        //從等待室中取得創房者房間key和創房者帳號
        final Bundle Ban_ckeyandcaccount = this.getIntent().getExtras();
        final String ckey = Ban_ckeyandcaccount.getString("ckey");
        final String caccount = Ban_ckeyandcaccount.getString("caccount");
        //從等待室中取得一般使用者房間key和一般使用者帳號
        final Bundle Ban_keyandcaccount = this.getIntent().getExtras();
        final String key = Ban_keyandcaccount.getString("key");
        final String account = Ban_keyandcaccount.getString("account");
        //有開放中途加入，直接從主頁進聊天室
        final Bundle Ban_joinkeyandaccount = this.getIntent().getExtras();
        final String jkey = Ban_joinkeyandaccount.getString("jkey");
        final String jmember_account = Ban_joinkeyandaccount.getString("member_account");
        if (memberaccount.equals(caccount)) {
            new AlertDialog.Builder(chatActivity.this)
                    .setTitle("房主")
                    .setMessage("房主若中途退出將會多扣5L幣，是否確定要離開聊天室嗎?")
                    .setIcon(R.drawable.warning1)
                    .setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    roomcountdown = true;
                                    memberdataRef.child(user_uid).child("m_coin").setValue(m_coin[0] - 5);
                                    wtdataRef.child(memberkey).child("cm_status").setValue("0");
                                    createdataRef.child(ckey).child("c_status").setValue("3");
                                    finish();
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub

                                }
                            }).show();
        } else {
            new AlertDialog.Builder(chatActivity.this)
                    .setTitle("使用者")
                    .setMessage("若中途退出將會多扣3L幣，是否確定要離開聊天室嗎?")
                    .setIcon(R.drawable.warning1)
                    .setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    if (memberaccount.equals(account)) {
                                        memberdataRef.child(user_uid).child("m_coin").setValue(m_coin[0] - 3);
                                        wtdataRef.child(memberkey).child("cm_status").setValue("0");
                                        createdataRef.child(key).child("c_personnum").setValue(c_personnum - 1);
                                        finish();
                                    } else if (memberaccount.equals(jmember_account)) {
                                        memberdataRef.child(user_uid).child("m_coin").setValue(m_coin[0] - 3);
                                        wtdataRef.child(memberkey).child("cm_status").setValue("0");
                                        createdataRef.child(jkey).child("c_personnum").setValue(c_personnum - 1);
                                        finish();
                                    }
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub

                                }
                            }).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wtdataRef.removeEventListener(addmemberListener);
    }

}
