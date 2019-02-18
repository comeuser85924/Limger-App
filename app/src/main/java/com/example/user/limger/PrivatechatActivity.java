package com.example.user.limger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PrivatechatActivity extends AppCompatActivity {
    ActionBar actionBar;
    DatabaseReference member_dataRef, Notice_sender_dataRef, Notice_receiver_dataRef,
            Private_chat_dataRef, Private_chat_room_member_dataRef, Private_chat_room_message_dataRef;
    FirebaseAuth firebaseAuth;
    ChildEventListener postListener;
    ValueEventListener chatListener;
    String user_uid;

    SimpleDateFormat simpleDateFormat;
    String Date;
    Calendar calendar;

    ListView pr_chatList;
    Button pr_btsend;
    EditText pr_etmessage;
    List<Private_chat_room_message_firebase> msgList;
    CircleImageView Img_self;
    TextView txt_account;

    String pcm_status;
    String pcmes_id, pcmes_member_id, pcmes_time, pcmes_message_id, pcmes_message, pcmes_type;
    String selfaccount, mynick;

    int a = 0 , i=0;
    ArrayList<String> manyperonChatList = new ArrayList<>();
    String Sender_type = "";
    Boolean bl_close = false , bl_invclose = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privatechat);

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
        txt_account = viewActionBar.findViewById(R.id.txt_account);
        textviewTitle.setText("Limger");
        actionBar.setCustomView(viewActionBar, params);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        Img_self = viewActionBar.findViewById(R.id.Img_self);

        firebaseAuth = FirebaseAuth.getInstance();
        user_uid = firebaseAuth.getCurrentUser().getUid();

        member_dataRef = FirebaseDatabase.getInstance().getReference("member");
        Notice_sender_dataRef = FirebaseDatabase.getInstance().getReference("Notice_message_sender");
        Notice_receiver_dataRef = FirebaseDatabase.getInstance().getReference("Notice_message_receiver");
        Private_chat_dataRef = FirebaseDatabase.getInstance().getReference("private_chat_room");
        Private_chat_room_member_dataRef = FirebaseDatabase.getInstance().getReference("private_chat_room_member");
        Private_chat_room_message_dataRef = FirebaseDatabase.getInstance().getReference("private_chat_room_message");
        pr_chatList = findViewById(R.id.private_chatlistview);
        pr_btsend = findViewById(R.id.private_chat_bt_send);
        pr_etmessage = findViewById(R.id.private_chat_et_message);

        msgList = new ArrayList<>();
        pr_getchat_room_member();
        pr_chatmsg_send();
        pr_chatmsg_sendview();
    }

    @Override
    protected void onStart() {
        super.onStart();
        member_dataRef.child(user_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Picasso.get().load(dataSnapshot.child("m_head").getValue().toString()).into(Img_self);
                txt_account.setText(dataSnapshot.child("m_account").getValue().toString());
                selfaccount = dataSnapshot.child("m_account").getValue().toString();
                mynick = dataSnapshot.child("m_nick").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void pr_getchat_room_member() {
        final Bundle mBun_pr_roomid = this.getIntent().getExtras();
        final String pr_roomid = mBun_pr_roomid.getString("pr_roomid");

        postListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Private_chat_room_member_firebase artist = dataSnapshot.getValue(Private_chat_room_member_firebase.class);
                if (artist.pcm_status.equals("1")) {
                    if (!artist.pcm_member_id.equals(user_uid)) {
                        member_dataRef.child(artist.pcm_member_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Toast.makeText(PrivatechatActivity.this, dataSnapshot.child("m_account").getValue().toString() + "已加入"
                                        , Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }


                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final Private_chat_room_member_firebase artist = dataSnapshot.getValue(Private_chat_room_member_firebase.class);
                if (artist.pcm_status.equals("1")) {
                    if (!artist.pcm_member_id.equals(user_uid)) {
                        member_dataRef.child(artist.pcm_member_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Toast.makeText(PrivatechatActivity.this, dataSnapshot.child("m_account").getValue().toString() + "已加入"
                                        , Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    if (!artist.pcm_member_id.equals(user_uid)) {
                        member_dataRef.child(artist.pcm_member_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!bl_close) {
                                    Toast.makeText(PrivatechatActivity.this, dataSnapshot.child("m_account").getValue().toString() + "已退出"
                                            , Toast.LENGTH_SHORT).show();
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
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        Private_chat_room_member_dataRef.child(pr_roomid).addChildEventListener(postListener);


        Private_chat_room_member_dataRef.child(pr_roomid).child(user_uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        pcm_status = dataSnapshot.child("pcm_status").getValue().toString();


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        chatListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("pc_status").getValue().toString().equals("2")) {
                    Private_chat_room_member_dataRef.child(pr_roomid)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (final DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                                        i+=1;
                                        final Private_chat_room_member_firebase artist = artistSnapshot.getValue(Private_chat_room_member_firebase.class);
                                        Private_chat_room_member_dataRef.child(pr_roomid).child(artist.pcm_member_id).child("pcm_status").setValue("0");
                                        if(!bl_invclose){
                                            if(i==1)
                                            Toast.makeText(PrivatechatActivity.this, "邀請者已結束聊天", Toast.LENGTH_SHORT).show();
                                        }
                                        bl_close = true;

                                    }
                                    finish();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        Private_chat_dataRef.child(pr_roomid).addValueEventListener(chatListener);
    }

    //傳送訊息
    private void pr_chatmsg_send() {
        final Bundle mBun_pr_roomid = this.getIntent().getExtras();
        final String pr_roomid = mBun_pr_roomid.getString("pr_roomid");
        pr_btsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = pr_etmessage.getText().toString();
                String msgkey = Private_chat_room_message_dataRef.push().getKey();
                calendar = Calendar.getInstance();
                simpleDateFormat = new SimpleDateFormat("HH:mm");
                Date = simpleDateFormat.format(calendar.getTime());

                Private_chat_room_message_firebase pmes =
                        new Private_chat_room_message_firebase(
                                pr_roomid, user_uid, Date,
                                msgkey, message,
                                Private_chat_room_message_firebase.TYPE_SEND);

                Private_chat_room_message_dataRef.child(pr_roomid).child(msgkey).setValue(pmes);
                pr_etmessage.setText("");

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        final Bundle mBun_pr_roomid = this.getIntent().getExtras();
        final String pr_roomid = mBun_pr_roomid.getString("pr_roomid");

        Private_chat_room_member_dataRef.child(pr_roomid).removeEventListener(postListener);
        Private_chat_dataRef.child(pr_roomid).removeEventListener(chatListener);
    }

    //讀取訊息及分辨訊息加入至哪個陣列
    private void pr_chatmsg_sendview() {
        final Bundle mBun_pr_roomid = this.getIntent().getExtras();
        final String pr_roomid = mBun_pr_roomid.getString("pr_roomid");

        Private_chat_room_message_dataRef.child(pr_roomid).
                addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        pcmes_member_id = dataSnapshot.child("pcmes_member_id").getValue().toString();
                        pcmes_time = dataSnapshot.child("pcmes_time").getValue().toString();
                        pcmes_message_id = dataSnapshot.child("pcmes_message_id").getValue().toString();
                        pcmes_message = dataSnapshot.child("pcmes_message").getValue().toString();
                        pcmes_type = dataSnapshot.child("pcmes_type").getValue().toString();


                        if (pcmes_type.equals(Private_chat_room_message_firebase.TYPE_SEND)) {
                            if (user_uid.equals(pcmes_member_id)) {
                                Private_chat_room_message_firebase datamsg = dataSnapshot.getValue(Private_chat_room_message_firebase.class);
                                msgList.add(datamsg);
                                System.out.println("SEND查看List-----" + msgList);
                            } else {
                                Private_chat_room_message_firebase datamsg =
                                        new Private_chat_room_message_firebase(
                                                pr_roomid, pcmes_member_id, pcmes_time, pcmes_message_id, pcmes_message,
                                                Private_chat_room_message_firebase.TYPE_RECEIVED);
                                msgList.add(datamsg);
                                System.out.println("RECEIVED查看List-----" + msgList);
                            }
                        }

                        Pivate_MesList adapter = new Pivate_MesList(PrivatechatActivity.this, msgList);
                        pr_chatList.setAdapter(adapter);

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

    //區別顯示左右邊訊息
    class Pivate_MesList extends ArrayAdapter<Private_chat_room_message_firebase> {

        private List<Private_chat_room_message_firebase> Pivate_mesList;

        public Pivate_MesList(@NonNull Context context, List<Private_chat_room_message_firebase> Pivate_mesList) {
            super(context, R.layout.layout_message, Pivate_mesList);
            this.Pivate_mesList = Pivate_mesList;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Private_chat_room_message_firebase msgs = Pivate_mesList.get(position);
            final Private_chat_room_message_firebase msg = getItem(position);
            final View view;
            final ViewHolder viewHolder;


            if (convertView == null) {

                view = LayoutInflater.from(getContext()).inflate(R.layout.layout_message, null);
                viewHolder = new ViewHolder();
                viewHolder.leftLayout = (LinearLayout) view.findViewById(R.id.left_layout);
                viewHolder.rightLayout = (LinearLayout) view.findViewById(R.id.right_layout);
                viewHolder.leftMsg = (TextView) view.findViewById(R.id.left_msg);
                viewHolder.rightMsg = (TextView) view.findViewById(R.id.right_msg);
                viewHolder.lefttime = view.findViewById(R.id.left_txtmsgtime);
                viewHolder.righttime = view.findViewById(R.id.right_txtmsgtime);
                viewHolder.left_img = view.findViewById(R.id.left_img);
                viewHolder.leftaccount = view.findViewById(R.id.left_account);
                view.setTag(viewHolder);

            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }


            if (msg.getpcmes_type().equals(Private_chat_room_message_firebase.TYPE_RECEIVED)) {

                member_dataRef.child(msg.getpcmes_member_id())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                viewHolder.leftaccount.setText(dataSnapshot.child("m_account").getValue().toString());
                                Picasso.get().load(dataSnapshot.child("m_head").getValue().toString()).into(viewHolder.left_img);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                viewHolder.left_img.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        SharedPreferences roomid = getContext().getSharedPreferences("myFriendsData", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = roomid.edit();
                        editor.putString("othersuer_uid", msg.getpcmes_member_id());
                        editor.putBoolean("prchatmember", true);
                        editor.commit();

                        myFriendsdata_dailog friend_dailog = new myFriendsdata_dailog();
                        friend_dailog.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "myFriend");
                        return false;
                    }
                });


                viewHolder.rightLayout.setVisibility(View.GONE);
                viewHolder.leftLayout.setVisibility(View.VISIBLE);
                viewHolder.leftMsg.setText(msg.getpcmes_message());
                viewHolder.lefttime.setText(msg.getpcmes_time());

            } else if (msg.getpcmes_type().equals(Private_chat_room_message_firebase.TYPE_SEND)) {
                viewHolder.leftLayout.setVisibility(View.GONE);
                viewHolder.rightLayout.setVisibility(View.VISIBLE);
                viewHolder.rightMsg.setText(msg.getpcmes_message());
                viewHolder.righttime.setText(msg.getpcmes_time());


            }
            return view;
        }
    }

    static class ViewHolder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        TextView lefttime;
        TextView righttime;
        CircleImageView left_img;
        TextView leftaccount;
    }


    //製作返回健
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        boolean flag;

        switch (item.getItemId()) {
            case android.R.id.home:
                exitPrivatechatroom();

                flag = true;
                break;
            default:
                flag = super.onOptionsItemSelected(item);
                break;
        }
        return flag;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitPrivatechatroom();


        }
        return true;
    }

    private void exitPrivatechatroom() {
        final Bundle mBun_pr_roomid = this.getIntent().getExtras();
        final String pr_roomid = mBun_pr_roomid.getString("pr_roomid");
        final String serder_id = mBun_pr_roomid.getString("serder_id");
        Private_chat_room_member_dataRef.child(pr_roomid).child(user_uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Private_chat_dataRef.child(pr_roomid)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.child("pc_member_id").getValue().toString().equals(user_uid)) {
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
//                                                                                                    Toast.makeText(PrivatechatActivity.this, "已存在帳號:" + receiverData.mes_receiver_account, Toast.LENGTH_LONG).show();

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

                                            new AlertDialog.Builder(PrivatechatActivity.this)
                                                    .setTitle("確認視窗")
                                                    .setMessage("你是邀請者" + "\n" + "若離開聊天室" + "\n" + "所有人將會離開聊天室！")
                                                    .setIcon(R.drawable.warning1)
                                                    .setPositiveButton("確定",
                                                            new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog,
                                                                                    int which) {
                                                                    bl_invclose =true;
                                                                    Private_chat_dataRef.child(pr_roomid).child("pc_status").setValue("2");

                                                                    Notice_sender_dataRef.child(serder_id)
                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(final DataSnapshot SenderdataSnapshot) {

                                                                                    if (SenderdataSnapshot.child("mes_sender_type").getValue().toString().equals("2")) {
                                                                                        Sender_type = "7";
                                                                                        Notice_sender_dataRef.child(serder_id).child("mes_sender_type").setValue("7");
                                                                                    } else if (SenderdataSnapshot.child("mes_sender_type").getValue().toString().equals("3")) {
                                                                                        Sender_type = "8";
                                                                                        Notice_sender_dataRef.child(serder_id).child("mes_sender_type").setValue("8");
                                                                                    }

                                                                                    Notice_receiver_dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                            for (final DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                                                                                                final Notice_receiver_firebase receiverData = artistSnapshot.getValue(Notice_receiver_firebase.class);

                                                                                                if (receiverData.mes_sender_id.equals(serder_id)) {
                                                                                                    a += 1;
                                                                                                    System.out.println("陣列---" + manyperonChatList.size());
                                                                                                    calendar = Calendar.getInstance();
                                                                                                    simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                                                                                                    Date = simpleDateFormat.format(calendar.getTime());
                                                                                                    if (manyperonChatList.size() >= 1) {
                                                                                                        if (a == 1) {
                                                                                                            String not_id = Notice_sender_dataRef.push().getKey();
                                                                                                            Notice_sender_firebase not_sender = new Notice_sender_firebase(
                                                                                                                    not_id
                                                                                                                    , user_uid
                                                                                                                    , "親愛的會員您已錯過" + mynick + "於" + SenderdataSnapshot.child("mes_sender_time").getValue().toString() + "邀請您進行的聊天樓！"
                                                                                                                    , Date
                                                                                                                    , "聊天已結束囉！"
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

                                                                                @Override
                                                                                public void onCancelled(DatabaseError databaseError) {

                                                                                }
                                                                            });


                                                                    finish();
                                                                    Toast.makeText(PrivatechatActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
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
                                            new AlertDialog.Builder(PrivatechatActivity.this)
                                                    .setTitle("確認視窗")
                                                    .setMessage("確定離開好友聊天嗎？")
                                                    .setIcon(R.drawable.warning1)
                                                    .setPositiveButton("確定",
                                                            new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog,
                                                                                    int which) {
                                                                    Private_chat_room_member_dataRef.child(pr_roomid)
                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                    Private_chat_room_member_dataRef.child(pr_roomid).child(user_uid).child("pcm_status").setValue("0");
                                                                                    finish();
                                                                                    Toast.makeText(PrivatechatActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(DatabaseError databaseError) {

                                                                                }
                                                                            });
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

}
