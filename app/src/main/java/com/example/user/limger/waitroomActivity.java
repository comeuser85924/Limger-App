package com.example.user.limger;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.user.limger.Util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cn.iwgang.countdownview.CountdownView;
import de.hdodenhof.circleimageview.CircleImageView;

public class waitroomActivity extends AppCompatActivity {
    ActionBar actionBar;


    GridView wr_gridview;
    List<chat_room_member_firebase> chat_room_memberlist;
    CountdownView countDownTimer;
    SimpleDateFormat simpleDateFormat, intoroomDateFormat;
    Calendar calendar;
    CircleImageView Img_self;
    TextView txt_account;

    DatabaseReference ct_databaseRef;
    DatabaseReference wrdataRef;
    DatabaseReference messagedataRef;
    DatabaseReference member_databaseRef;
    FirebaseAuth firebaseAuth;

    String createdate;  //創房時的日期->轉格式用的
    String Dates;   //輸入Limger機器人內容的時間置資料庫
    String ceate_keyid;//創房的key
    String caccount;
    String account;
    String strmember_keyid;//取得成員key
    String chatmember_status;//取得成員狀態(已離開:0、加入中:1)
    String cm_member_id;

    String c_status;//聊天室狀態
    String c_account;
    String c_id;

    String user_id;
    String m_account;
    String m_head;

    String key, ckey;
    String Chat_Main_memberkey, creatememberkey;

    Timer timer;

    int room_persum;  //房內人數
//    int countdown;  //取得資料庫倒數時間
    int i = 0;//判斷countDownTimer只會執行一次
    int a = 0, b = 0;
    long currentsum, millionSeconds = 0, currentmill;//從主頁到等待室過了幾毫秒,目前的時間毫秒,資料庫倒數時間毫秒
    long lastseconds;   //資料庫倒數毫秒-手機系統當下時間毫秒=剩餘的毫秒

    boolean start = false;  //用來防止計時器重複計算(似乎不用，待檢查)
    boolean pro_deal = false;//會員被處罰
    Boolean creater = false;
    //chat_room_member資料
    String cm_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitroom);

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
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);


        member_databaseRef = FirebaseDatabase.getInstance().getReference("member");
        wrdataRef = FirebaseDatabase.getInstance().getReference("chat_room_member");
        ct_databaseRef = FirebaseDatabase.getInstance().getReference("chat_room");
        messagedataRef = FirebaseDatabase.getInstance().getReference("chat_room_message");
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();


        chat_room_memberlist = new ArrayList<>();
        waitroom_defin();//定義


    }

    private void waitroom_defin() {
        countDownTimer = findViewById(R.id.countdownview);
        wr_gridview = findViewById(R.id.wr_gridview);

    }

    //顯示menu中的字樣
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        //從創房頁按創房鈕時傳過來的key值
        final Bundle mBan_createkey = this.getIntent().getExtras();
        ckey = mBan_createkey.getString("createkey");

        System.out.println("CID----"+c_id);
        System.out.println("CIDckey----"+ckey);
        if(ckey!=null){
            ct_databaseRef.child(ckey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    MenuInflater inflater = getMenuInflater();
                    inflater.inflate(R.menu.waitroom_startchat, menu);
                    creater = true;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        if (creater) {
            return true;
        } else {
            return true;
        }
    }

    //製作返回健和"開始聊天"功能
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //從創房頁按創房鈕時傳過來的key值
        final Bundle mBan_createkey = this.getIntent().getExtras();
        ckey = mBan_createkey.getString("createkey");
        //從創房頁按創房鈕時傳過來的member key值
        final Bundle mBan_memberkey = this.getIntent().getExtras();
        creatememberkey = mBan_memberkey.getString("create_memberkey");

        //從主頁點選Listview中房間的key值 item
        final Bundle mBan_accountkey = this.getIntent().getExtras();
        key = mBan_accountkey.getString("key");
        //從主頁點選Listview item的member key值
        Chat_Main_memberkey = mBan_accountkey.getString("Chat_Main_memberkey");

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        boolean flag;
        //noinspection SimplifiableIfStatement
        if (id == R.id.startchat) {
            start = true;
            calendar = Calendar.getInstance();
            ct_databaseRef.child(ckey).child("c_status").setValue("1");
            intoroomDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String intoroomtime = intoroomDateFormat.format(calendar.getTime());
            ct_databaseRef.child(ckey).child("c_intoroomtime").setValue(intoroomtime);

            simpleDateFormat = new SimpleDateFormat("HH:mm");
            Dates = simpleDateFormat.format(calendar.getTime());
            String messageid = wrdataRef.push().getKey();
            message_firebase mf = new message_firebase(messageid, ckey, "LimgerRobot", Dates,
                    "您好~歡迎使用Limger\n祝您有個愉快的回憶～", message_firebase.TYPE_RECEIVED, "O1n30ZUQSuaLQhAhNItGiOUSavm1");
            messagedataRef.child(messageid).setValue(mf);
            Bundle Ban_ckeyandaccount = new Bundle();
            Ban_ckeyandaccount.putString("ckey", ckey);
            Ban_ckeyandaccount.putString("caccount", caccount);
            Ban_ckeyandaccount.putString("cmember_id", creatememberkey);
            Intent intent = new Intent(waitroomActivity.this, chatActivity.class);
            intent.putExtras(Ban_ckeyandaccount);
            startActivity(intent);
            finish();

            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                if (ceate_keyid.equals(key)) {
                    new AlertDialog.Builder(waitroomActivity.this)
                            .setTitle("確認視窗")
                            .setMessage("確定要離開等待室嗎?")
                            .setIcon(R.drawable.warning1)
                            .setPositiveButton("確定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            start = true;
                                            //判斷是否從主頁面進房
                                            //是的話
                                            System.out.println("取得資料表member_id----"+strmember_keyid);
                                            System.out.println("取得傳過來的member_id----"+Chat_Main_memberkey);
                                            if (strmember_keyid.equals(Chat_Main_memberkey)) {
                                                wrdataRef.child(Chat_Main_memberkey).child("cm_status").setValue("0");
                                                ct_databaseRef.child(ceate_keyid).child("c_personnum").setValue(room_persum - 1);
                                                Toast.makeText(waitroomActivity.this,"退房成功",Toast.LENGTH_LONG).show();

                                            }
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
                } else if (ceate_keyid.equals(ckey)) {
                    new AlertDialog.Builder(waitroomActivity.this)
                            .setTitle("確認視窗")
                            .setMessage(" 確定要離開等待室嗎?")
                            .setIcon(R.drawable.warning1)
                            .setPositiveButton("確定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            start = true;
                                            wrdataRef.child(creatememberkey).child("cm_status").setValue("0");
                                            ct_databaseRef.child(ceate_keyid).child("c_personnum").setValue(room_persum - 1);
                                            ct_databaseRef.child(ceate_keyid).child("c_status").setValue("3");
                                            Toast.makeText(waitroomActivity.this,"退房成功",Toast.LENGTH_LONG).show();
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

                }
                flag = true;
                break;
            default:
                flag = super.onOptionsItemSelected(item);
                break;
        }
        return flag;


    }

    @Override
    protected void onStart() {
        super.onStart();

        //從創房頁按創房鈕時傳過來的key值
        final Bundle mBan_createkey = this.getIntent().getExtras();
        ckey = mBan_createkey.getString("createkey");
        //從創房頁按創房鈕時傳過來的member key值
        final Bundle mBan_memberkey = this.getIntent().getExtras();
        creatememberkey = mBan_memberkey.getString("create_memberkey");

        //從主頁點選Listview中房間的key值 item
        final Bundle mBan_accountkey = this.getIntent().getExtras();
        key = mBan_accountkey.getString("key");
        //從主頁點選Listview item的member key值
        Chat_Main_memberkey = mBan_accountkey.getString("Chat_Main_memberkey");


        member_databaseRef.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                m_account = dataSnapshot.child("m_account").getValue().toString();
                m_head = dataSnapshot.child("m_head").getValue().toString();
                Picasso.get().load(m_head).into(Img_self);
                txt_account.setText(m_account);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //取得房間人數、等待室倒數時間、房間id、房間狀態、創房時限制的時間
        ct_databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                    final create_firebase artist = artistSnapshot.getValue(create_firebase.class);

                    if (artist.c_id.equals(ckey)) {
                        room_persum = artist.c_personnum;
                        ceate_keyid = artist.c_id;
                        c_status = artist.c_status;
                        createdate = artist.c_stime;

                    } else if (artist.c_id.equals(key)) {
                        room_persum = artist.c_personnum;
                        ceate_keyid = artist.c_id;
                        c_status = artist.c_status;
                        createdate = artist.c_stime;
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //取member_id、cm_status
        wrdataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                    final chat_room_member_firebase artist1 = artistSnapshot.getValue(chat_room_member_firebase.class);
                    if (ceate_keyid.equals(ckey)) {
                        if (artist1.chat_room_member_id.equals(creatememberkey)) {
                            strmember_keyid = artist1.chat_room_member_id;
                            chatmember_status = artist1.cm_status;
                            cm_member_id = artist1.cm_member_id;
                        }
                    } else if (ceate_keyid.equals(key)) {
                        if (artist1.chat_room_member_id.equals(Chat_Main_memberkey)) {
                            strmember_keyid = artist1.chat_room_member_id;
                            chatmember_status = artist1.cm_status;
                            cm_member_id = artist1.cm_member_id;
                        }


                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //顯示等待室中的成員
        wrdataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chat_room_memberlist.clear();

                for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                    final chat_room_member_firebase artist = artistSnapshot.getValue(chat_room_member_firebase.class);


                    cm_status = artist.cm_status;
                    if (artist.chat_room_member_id.equals(creatememberkey)) {
                        member_databaseRef.child(artist.cm_member_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                caccount = dataSnapshot.child("m_account").getValue().toString();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    } else if (artist.chat_room_member_id.equals(Chat_Main_memberkey)) {
                        member_databaseRef.child(artist.cm_member_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                account = dataSnapshot.child("m_account").getValue().toString();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    if (c_status.equals("0")) {
                        //判斷是從chat_main的listview 過來的key還是創房時傳過來的key
                        if (artist.cm_id.equals(ckey)) {
                            if (artist.cm_status.equals("1")) {
                                chat_room_memberlist.add(artist);
                            } else {
                                chat_room_memberlist.remove(artist);
                            }
                        }
                        if (artist.cm_id.equals(key)) {
                            if (artist.cm_status.equals("1")) {
                                chat_room_memberlist.add(artist);
                            } else {
                                chat_room_memberlist.remove(artist);
                            }
                        }

                    }
                }
                Collections.reverse(chat_room_memberlist); //排序倒過來
                chat_room_memberList adapter = new chat_room_memberList(waitroomActivity.this, chat_room_memberlist);
                wr_gridview.setNumColumns(3);
                wr_gridview.setVerticalSpacing(50);
                wr_gridview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //處理時間及轉跳頁面
        ct_databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                    final create_firebase artist = artistSnapshot.getValue(create_firebase.class);

                    c_id = artist.c_id;
                    //房主，會負責扣除秒數 (房主不能再等待室退出
                    if (artist.getc_id().equals(ckey)) {
                        i = i + 1;
                        if (i == 1) {
                            countDownTimer.start(30000);
                        }
                        member_databaseRef.child(user_id)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.child("m_deal").getValue().toString().equals("1")) {
                                            a += 1;
                                            if (a == 1) {
                                                if (artist.c_status.equals("0")) {
                                                    if (chatmember_status.equals("1")) {
                                                        if (cm_member_id.equals(user_id)) {
                                                            if (artist.c_intoroomtime == null) {
                                                                pro_deal = true;
                                                                ct_databaseRef.child(c_id).child("c_personnum").setValue(artist.c_personnum - 1);
                                                                ct_databaseRef.child(ckey).child("c_status").setValue("3");
                                                                wrdataRef.child(creatememberkey).child("cm_status").setValue("0");
                                                                Toast.makeText(waitroomActivity.this, "此帳號已被封鎖一天,詳細請查看通知...", Toast.LENGTH_SHORT).show();
                                                                finish();
                                                            }
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

                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            public void run() {
                                if (start == false) {
                                    timer.cancel();
                                    start = true;
                                    calendar = Calendar.getInstance();

                                    ct_databaseRef.child(ckey).child("c_status").setValue("1");
                                    intoroomDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                    String intoroomtime = intoroomDateFormat.format(calendar.getTime());
                                    ct_databaseRef.child(ckey).child("c_intoroomtime").setValue(intoroomtime);

                                    simpleDateFormat = new SimpleDateFormat("HH:mm");
                                    Dates = simpleDateFormat.format(calendar.getTime());

                                    String messageid = wrdataRef.push().getKey();
                                    message_firebase mf = new message_firebase(messageid, ckey, "管理者", Dates, "您好~歡迎使用Limger\n祝您有個愉快的回憶～", message_firebase.TYPE_RECEIVED, "O1n30ZUQSuaLQhAhNItGiOUSavm1");
                                    messagedataRef.child(messageid).setValue(mf);
                                    Bundle Ban_ckeyandaccount = new Bundle();
                                    Ban_ckeyandaccount.putString("ckey", ckey);
                                    Ban_ckeyandaccount.putString("caccount", caccount);
                                    Ban_ckeyandaccount.putString("cmember_id", creatememberkey);
                                    Intent intent = new Intent(waitroomActivity.this, chatActivity.class);
                                    intent.putExtras(Ban_ckeyandaccount);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }, 30000, 1000);


                        if (artist.c_status.equals("3")) {
                            if (pro_deal == false) {
                                if (artist.c_intoroomtime == null) {
                                    if (chatmember_status.equals("1")) {
                                        timer.cancel();
                                        start = true;
                                        wrdataRef.child(creatememberkey).child("cm_status").setValue("0");
                                        Toast.makeText(waitroomActivity.this, "此房間已被強制關閉,詳細請看通知...", Toast.LENGTH_LONG).show();
                                        finish();

                                    }
                                }
                            }

                        }


                    }

                    //一般使用者
                    else if (artist.getc_id().equals(key)) {

                        //先抓到創房的時間後(ex.1970~20180519.14.56)轉成毫秒-掉當前使用者手機的時間(ex.1970~20180519.15.00)轉成毫秒=已經過幾毫秒
                        //取得artist.c_countdown的值-已經過幾毫秒=目前倒數計時的時間(創房者and一般者皆會一致)
                        member_databaseRef.child(user_id)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        System.out.println("次數a等待-----" + a);
                                        if (dataSnapshot.child("m_deal").getValue().toString().equals("1")) {
                                            a += 1;
                                            if (a == 1) {
                                                wrdataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot chatmemberartist) {
                                                        for (final DataSnapshot artistSnapshot : chatmemberartist.getChildren()) {
                                                            final chat_room_member_firebase chatmemberData = artistSnapshot.getValue(chat_room_member_firebase.class);
                                                            if (chatmemberData.cm_id.equals(key)) {
                                                                if (chatmemberData.cm_member_id.equals(user_id)) {
                                                                    if (chatmemberData.cm_status.equals("1")) {
                                                                        ct_databaseRef.child(c_id).child("c_personnum").setValue(artist.c_personnum - 1);
                                                                        wrdataRef.child(Chat_Main_memberkey).child("cm_status").setValue("0");
                                                                        Toast.makeText(waitroomActivity.this, "此帳號已被封鎖一天,詳細請查看通知...", Toast.LENGTH_SHORT).show();
                                                                        a = 0;
                                                                        finish();
                                                                    }
                                                                }
                                                            }

                                                        }
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
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                        try {
                            //創立房間時的總毫秒數
                            millionSeconds = sdf.parse(createdate).getTime();
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }//毫秒

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final long networkTime = Util.getCurrentNetworkTime();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        //從主頁到等待中已經過幾豪秒
                                        currentsum = System.currentTimeMillis() - millionSeconds;
                                        //倒數器執行的時間=資料庫倒數時間-從主頁到等待中已經過幾豪秒
                                        if(artist.c_mid.equals("O1n30ZUQSuaLQhAhNItGiOUSavm1")){
                                            lastseconds = 40000 - currentsum;
                                        }else{
                                            lastseconds = 30000 - currentsum;
                                        }
                                        countDownTimer.start(lastseconds);
                                    }
                                });
                            }
                        }).start();

                        if (artist.c_status.equals("3")) {
                            if (artist.c_intoroomtime == null) {
                                if (chatmember_status.equals("1")) {
                                    wrdataRef.child(Chat_Main_memberkey).child("cm_status").setValue("0");
                                    Toast.makeText(waitroomActivity.this, "房主中途退出或此房已被系統強制關閉", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        }

                        //判斷如果創房者直接開始聊天的話
                        if (artist.c_status.equals("1")) {
                            i = i + 1;
                            if (i == 1) {
                                if (chatmember_status.equals("1")) {
                                    Bundle Ban_keyandaccount = new Bundle();
                                    Ban_keyandaccount.putString("key", key);
                                    Ban_keyandaccount.putString("account", account);
                                    Ban_keyandaccount.putString("member_id", Chat_Main_memberkey);
                                    Intent intent = new Intent(waitroomActivity.this, chatActivity.class);
                                    intent.putExtras(Ban_keyandaccount);
                                    startActivity(intent);
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

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        //從主頁點選Listview item傳過來的房間key值
        final Bundle mBan_accountkey = this.getIntent().getExtras();
        final String key = mBan_accountkey.getString("key");
        //從創房頁按創房鈕時傳過來的房間key值
        final Bundle mBan_createkey = this.getIntent().getExtras();
        final String ckey = mBan_createkey.getString("createkey");


        //從主頁點選Listview item的member key值
        final Bundle mBan_Chat_Main_memberkey = this.getIntent().getExtras();
        final String Chat_Main_memberkey = mBan_Chat_Main_memberkey.getString("Chat_Main_memberkey");
        //從創房頁按創房鈕時傳過來的member key值
        final Bundle mBan_memberkey = this.getIntent().getExtras();
        final String creatememberkey = mBan_memberkey.getString("create_memberkey");
        if (ceate_keyid.equals(key)) {
            if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵

                new AlertDialog.Builder(waitroomActivity.this)
                        .setTitle("確認視窗")
                        .setMessage("確定要離開等待室嗎?")
                        .setIcon(R.drawable.warning1)
                        .setPositiveButton("確定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        start = true;
                                        //判斷是否從主頁面進房
                                        //是的話
                                        if (strmember_keyid.equals(Chat_Main_memberkey)) {
                                            wrdataRef.child(Chat_Main_memberkey).child("cm_status").setValue("0");
                                            ct_databaseRef.child(ceate_keyid).child("c_personnum").setValue(room_persum - 1);
                                            Toast.makeText(waitroomActivity.this,"退房成功",Toast.LENGTH_LONG).show();
                                        }
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
            }
        } else if (ceate_keyid.equals(ckey)) {
            new AlertDialog.Builder(waitroomActivity.this)
                    .setTitle("確認視窗")
                    .setMessage("確定要離開等待室嗎?")
                    .setIcon(R.drawable.warning1)
                    .setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    start = true;
                                    wrdataRef.child(creatememberkey).child("cm_status").setValue("0");
                                    ct_databaseRef.child(ceate_keyid).child("c_personnum").setValue(room_persum - 1);
                                    ct_databaseRef.child(ceate_keyid).child("c_status").setValue("3");
                                    Toast.makeText(waitroomActivity.this,"退房成功",Toast.LENGTH_LONG).show();
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

        }
        return true;
    }
    private void exitwaitroom(){

    }

}
