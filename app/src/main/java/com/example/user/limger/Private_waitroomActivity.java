package com.example.user.limger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Private_waitroomActivity extends AppCompatActivity {

    ActionBar actionBar;
    CircleImageView Img_self;
    GridView private_waitgridview;
    ConstraintLayout constraintLayout;
    List<Private_chat_room_member_firebase> Pivate_waitList;
    DatabaseReference member_dataRef, Notice_sender_dataRef, Notice_receiver_dataRef, Private_chat_dataRef, Private_chat_room_member_dataRef;
    ValueEventListener waitmemberListener;
    FirebaseAuth firebaseAuth;
    String user_uid, myaccount,mynick;
    TextView txt_account;
    int i = 0;
    SimpleDateFormat simpleDateFormat;
    String Date;
    Calendar calendar;
    int a = 0;
    String Sender_type = "";
    ArrayList<String> manyperonChatList = new ArrayList<>();
    Boolean Inviter = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_waitroom);

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

        textviewTitle.setText("Limger");
        actionBar.setCustomView(viewActionBar, params);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        Img_self = viewActionBar.findViewById(R.id.Img_self);
        txt_account = viewActionBar.findViewById(R.id.txt_account);


        private_waitgridview = findViewById(R.id.private_waitgridview);
        constraintLayout = findViewById(R.id.constraintLayout);
        firebaseAuth = FirebaseAuth.getInstance();
        user_uid = firebaseAuth.getCurrentUser().getUid();
        Pivate_waitList = new ArrayList<>();
        member_dataRef = FirebaseDatabase.getInstance().getReference("member");
        Notice_sender_dataRef = FirebaseDatabase.getInstance().getReference("Notice_message_sender");
        Notice_receiver_dataRef = FirebaseDatabase.getInstance().getReference("Notice_message_receiver");
        Private_chat_dataRef = FirebaseDatabase.getInstance().getReference("private_chat_room");
        Private_chat_room_member_dataRef = FirebaseDatabase.getInstance().getReference("private_chat_room_member");

        pr_waitroom();

    }

    private void pr_waitroom() {
        final Bundle mBun_pr_roomid = this.getIntent().getExtras();
        final String pr_roomid = mBun_pr_roomid.getString("pr_roomid");
        final String serder_id = mBun_pr_roomid.getString("serder_id");

        Private_chat_room_member_dataRef.child(pr_roomid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Pivate_waitList.clear();
                        for (final DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                            final Private_chat_room_member_firebase artist = artistSnapshot.getValue(Private_chat_room_member_firebase.class);
                            if (artist.pcm_member_id.equals(user_uid)) {
                                Pivate_waitList.remove(artist);
                            } else {
                                if (artist.pcm_status.equals("1")) {
                                    Pivate_waitList.add(artist);
                                } else {
                                    Pivate_waitList.remove(artist);
                                }

                            }

                        }
                        Collections.reverse(Pivate_waitList); //排序倒過來
                        Pivate_waitList adapter = new Pivate_waitList(Private_waitroomActivity.this, Pivate_waitList);
                        private_waitgridview.setNumColumns(3);
                        private_waitgridview.setVerticalSpacing(50);
                        private_waitgridview.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        waitmemberListener= new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot chat_dataSnapshot) {
                Private_chat_room_member_dataRef.child(pr_roomid).child(user_uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child("pcm_status").getValue().toString().equals("1")) {
                                    if (chat_dataSnapshot.child("pc_status").getValue().toString().equals("2")) {

                                        Private_chat_room_member_dataRef.child(pr_roomid)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        for (final DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                                                            final Private_chat_room_member_firebase artist = artistSnapshot.getValue(Private_chat_room_member_firebase.class);
                                                            Private_chat_room_member_dataRef.child(pr_roomid).child(artist.pcm_member_id).child("pcm_status").setValue("0");

                                                        }
                                                        Toast.makeText(Private_waitroomActivity.this, "邀請者已結束聊天", Toast.LENGTH_SHORT).show();
                                                        Private_chat_dataRef.child(pr_roomid).child("pc_population").setValue(0);
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                    } else if (chat_dataSnapshot.child("pc_status").getValue().toString().equals("1")) {
                                        i += 1;
                                        if (i == 1) {
                                            Intent intent = new Intent(Private_waitroomActivity.this, PrivatechatActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("pr_roomid", pr_roomid);
                                            bundle.putString("serder_id", serder_id);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                            finish();
                                        }

                                    } else if (chat_dataSnapshot.child("pc_status").getValue().toString().equals("0")) {

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        Private_chat_dataRef.child(pr_roomid).addValueEventListener(waitmemberListener);

    }


    @Override
    protected void onStart() {
        super.onStart();
        member_dataRef.child(user_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Picasso.get().load(dataSnapshot.child("m_head").getValue().toString()).into(Img_self);
                txt_account.setText(dataSnapshot.child("m_account").getValue().toString());
                myaccount = dataSnapshot.child("m_account").getValue().toString();
                mynick = dataSnapshot.child("m_nick").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        final Bundle mBun_pr_roomid = this.getIntent().getExtras();
        final String pr_roomid = mBun_pr_roomid.getString("pr_roomid");
        final String serder_id = mBun_pr_roomid.getString("serder_id");
        Private_chat_dataRef.child(pr_roomid).removeEventListener(waitmemberListener);
    }

    //顯示menu中的字樣
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        final Bundle mBun_pr_roomid = this.getIntent().getExtras();
        final String pr_roomid = mBun_pr_roomid.getString("pr_roomid");
        final String serder_id = mBun_pr_roomid.getString("serder_id");
        Private_chat_room_member_dataRef.child(pr_roomid).child(user_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot pm_dataSnapshot) {
                Notice_sender_dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                            Notice_sender_firebase senderData = artistSnapshot.getValue(Notice_sender_firebase.class);
                            if(senderData.mes_sender_id.equals(serder_id)){
                                if(pm_dataSnapshot.child("pcm_member_id").getValue().toString().equals(senderData.mes_sender_member_id)){
                                    Inviter=true;
                                    MenuInflater inflater = getMenuInflater();
                                    inflater.inflate(R.menu.waitroom_startchat, menu);
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
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(Inviter){
            return true;
        }else{
            return true;
        }

    }

    //製作返回健和"開始聊天"功能
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Bundle mBun_pr_roomid = this.getIntent().getExtras();
        final String pr_roomid = mBun_pr_roomid.getString("pr_roomid");
        final String serder_id = mBun_pr_roomid.getString("serder_id");
        int id = item.getItemId();
        boolean flag;

        if (id == R.id.startchat) {
            Private_chat_dataRef.child(pr_roomid).child("pc_status").setValue("1");

            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                exitPrivatewaitroom();

                flag = true;
                break;
            default:
                flag = super.onOptionsItemSelected(item);
                break;
        }
        return flag;
    }

    private void exitPrivatewaitroom() {
        final Bundle mBun_pr_roomid = this.getIntent().getExtras();
        final String pr_roomid = mBun_pr_roomid.getString("pr_roomid");
        final String serder_id = mBun_pr_roomid.getString("serder_id");
        manyperonChatList.clear();
        Private_chat_room_member_dataRef.child(pr_roomid).child(user_uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Private_chat_dataRef.child(pr_roomid)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot privatechatData) {
                                        if (privatechatData.child("pc_member_id").getValue().toString().equals(user_uid)) {
                                            Notice_sender_dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for (final DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                                                        final Notice_sender_firebase senderData = artistSnapshot.getValue(Notice_sender_firebase.class);
                                                        if (senderData.mes_pc_id != null) {
                                                            if (senderData.mes_pc_id.equals(pr_roomid)) {
                                                                //檢查接收通知當中的人 誰不再私多聊聊天室當中
                                                                Notice_receiver_dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        for (final DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                                                                            final Notice_receiver_firebase receiverData = artistSnapshot.getValue(Notice_receiver_firebase.class);
                                                                            if (receiverData.mes_sender_id.equals(senderData.mes_sender_id)) {
                                                                                Private_chat_room_member_dataRef.child(pr_roomid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                        //一對一發通知成功(私聊)
                                                                                        //需要分辨私多聊的情況而會有所不同的寄通知
                                                                                        if (dataSnapshot.child(receiverData.mes_receiver_member_id).exists()) {
//                                                                                            Toast.makeText(Private_waitroomActivity.this, "已存在帳號:" + receiverData.mes_receiver_account, Toast.LENGTH_LONG).show();

                                                                                        } else {
                                                                                            manyperonChatList.add(receiverData.mes_receiver_member_id);
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
                                                            }
                                                        }


                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                            new AlertDialog.Builder(Private_waitroomActivity.this)
                                                    .setTitle("確認視窗")
                                                    .setMessage("你是邀請者" + "\n" + "若離開等待室" + "\n" + "則房間狀態將會直接結束！")
                                                    .setIcon(R.drawable.warning1)
                                                    .setPositiveButton("確定",
                                                            new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog,
                                                                                    int which) {

                                                                    //去比對私多聊成員ID及接收通知的使用者ID，查看接收通知成員中有誰不在私聊成員裡
                                                                    //將不在的私多聊成員裡的成員ID(沒有回覆聊天的人)拉出來丟入陣列，使用for迴圈個別傳送聊天已結束的通知

                                                                    Private_chat_dataRef.child(pr_roomid).child("pc_status").setValue("2");

                                                                    Notice_sender_dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                            for (final DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                                                                                final Notice_sender_firebase senderData = artistSnapshot.getValue(Notice_sender_firebase.class);
                                                                                if (senderData.mes_pc_id != null) {
                                                                                    if (senderData.mes_pc_id.equals(pr_roomid)) {
                                                                                        if (senderData.mes_sender_id.equals(serder_id)) {
                                                                                            if (senderData.mes_sender_type.equals("2")) {
                                                                                                Sender_type = "7";
                                                                                                Notice_sender_dataRef.child(serder_id).child("mes_sender_type").setValue("7");
                                                                                            } else if (senderData.mes_sender_type.equals("3")) {
                                                                                                Sender_type = "8";
                                                                                                Notice_sender_dataRef.child(serder_id).child("mes_sender_type").setValue("8");
                                                                                            }
                                                                                        }

                                                                                        Notice_receiver_dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                            @Override
                                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                for (final DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                                                                                                    final Notice_receiver_firebase receiverData = artistSnapshot.getValue(Notice_receiver_firebase.class);

                                                                                                    if (receiverData.mes_sender_id.equals(senderData.mes_sender_id)) {
                                                                                                        a+=1;
                                                                                                        System.out.println("陣列---" + manyperonChatList.size());
                                                                                                        calendar = Calendar.getInstance();
                                                                                                        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                                                                                                        Date = simpleDateFormat.format(calendar.getTime());
                                                                                                        if(manyperonChatList.size()>=1){
                                                                                                            if(a==1){

                                                                                                                String not_id = Notice_sender_dataRef.push().getKey();
                                                                                                                Notice_sender_firebase not_sender = new Notice_sender_firebase(
                                                                                                                        not_id
                                                                                                                        , user_uid
                                                                                                                        , "親愛的會員您已錯過"+mynick+"於"+senderData.mes_sender_time+"邀請進行的聊天樓！"
                                                                                                                        , Date
                                                                                                                        , "聊天結束通知！"
                                                                                                                        , Sender_type
                                                                                                                        , pr_roomid
                                                                                                                );
                                                                                                                Notice_sender_dataRef.child(not_id).setValue(not_sender);

                                                                                                                for (int i = 0; i < manyperonChatList.size(); i++) {

                                                                                                                    String notre_id = Notice_receiver_dataRef.push().getKey();
                                                                                                                    Notice_receiver_firebase not_receiver = new Notice_receiver_firebase(
                                                                                                                            notre_id,
                                                                                                                            not_id,
                                                                                                                            receiverData.mes_receiver_account,
                                                                                                                            manyperonChatList.get(i)
                                                                                                                    );
                                                                                                                    Notice_receiver_dataRef.child(notre_id).setValue(not_receiver);
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
                                                                                }


                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                        }
                                                                    });
                                                                    finish();
                                                                    Toast.makeText(Private_waitroomActivity.this,"退出成功",Toast.LENGTH_SHORT).show();
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
                                            new AlertDialog.Builder(Private_waitroomActivity.this)
                                                    .setTitle("確認視窗")
                                                    .setMessage("確定離開等待室嗎？")
                                                    .setIcon(R.drawable.warning1)
                                                    .setPositiveButton("確定",
                                                            new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog,
                                                                                    int which) {
                                                                    Private_chat_room_member_dataRef.child(pr_roomid).child(user_uid).child("pcm_status").setValue("0");
                                                                    finish();
                                                                    Toast.makeText(Private_waitroomActivity.this,"退出成功",Toast.LENGTH_SHORT).show();
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
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitPrivatewaitroom();

        }
        return true;
    }

    class Pivate_waitList extends ArrayAdapter<Private_chat_room_member_firebase> {

        private List<Private_chat_room_member_firebase> Pivate_waitmemberList;

        public Pivate_waitList(@NonNull Context context, List<Private_chat_room_member_firebase> Pivate_waitmemberList) {
            super(context, R.layout.layout_waitroom_member, Pivate_waitmemberList);
            this.Pivate_waitmemberList = Pivate_waitmemberList;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final View view;
            final ViewHolder viewHolder;


            if (convertView == null) {

                view = LayoutInflater.from(getContext()).inflate(R.layout.layout_waitroom_member, null);
                viewHolder = new ViewHolder();
                viewHolder.pr_img = view.findViewById(R.id.wr_img);
                viewHolder.pr_txtaccount = view.findViewById(R.id.wr_txtaccount);

                final Private_chat_room_member_firebase artist = Pivate_waitmemberList.get(position);

                member_dataRef
                        .child(artist.pcm_member_id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Picasso.get().load(dataSnapshot.child("m_head").getValue().toString()).into(viewHolder.pr_img);
                                viewHolder.pr_txtaccount.setText(dataSnapshot.child("m_account").getValue().toString());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


            } else {
                view = convertView;
            }


            return view;
        }
    }

    static class ViewHolder {
        CircleImageView pr_img;
        TextView pr_txtaccount;
    }

}
