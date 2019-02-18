package com.example.user.limger;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class NoticeTypeFragment extends Fragment {

    DatabaseReference member_dataRef, Notice_sender_dataRef, Notice_receiver_dataRef, Private_chat_dataRef, Private_chat_room_member_dataRef;
    FirebaseAuth firebaseAuth;
    Context mContext;

    String user_uid, selfaccount;
    int coin;
    long NoticeSendDataTime,NoticeSendDataTime2;
    SimpleDateFormat senddate,sendtime;

    AlertDialog dialogs;

    public NoticeTypeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_notice_type, container, false);

        member_dataRef = FirebaseDatabase.getInstance().getReference("member");
        Notice_sender_dataRef = FirebaseDatabase.getInstance().getReference("Notice_message_sender");
        Private_chat_dataRef = FirebaseDatabase.getInstance().getReference("private_chat_room");
        Private_chat_room_member_dataRef = FirebaseDatabase.getInstance().getReference("private_chat_room_member");
        firebaseAuth = FirebaseAuth.getInstance();
        user_uid = firebaseAuth.getCurrentUser().getUid();

        final TextView txt_sender = view.findViewById(R.id.txt_sender);
        final TextView txt_senddate = view.findViewById(R.id.txt_senddate);
        final TextView txt_sendtime = view.findViewById(R.id.txt_sendtime);
        final TextView txt_sendcontent = view.findViewById(R.id.txt_sendcontent);
        final Button but_function = view.findViewById(R.id.but_function);
        Bundle bundle = getArguments();
        if (bundle != null) {
            final String item = bundle.getString("NoticeDataID");


            member_dataRef.child(user_uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            selfaccount = dataSnapshot.child("m_account").getValue().toString();
                            coin = Integer.parseInt(dataSnapshot.child("m_coin").getValue().toString());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            Notice_sender_dataRef.child(item)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {

                            if(dataSnapshot.child("mes_sender_member_id").getValue().toString().equals("O1n30ZUQSuaLQhAhNItGiOUSavm1")){
                                txt_sender.setText("管理者");
                            }else{
                                member_dataRef.child(dataSnapshot.child("mes_sender_member_id").getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        txt_sender.setText(dataSnapshot.child("m_account").getValue().toString());
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
//                            txt_sender.setText(dataSnapshot.child("mes_sender_account").getValue().toString());



                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                            try {
                                //創立房間時的總毫秒數
                                NoticeSendDataTime = sdf.parse(dataSnapshot.child("mes_sender_time").getValue().toString()).getTime();
                            } catch (ParseException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }//毫秒


                            senddate = new SimpleDateFormat("yyyy-MM-dd");
                            sendtime = new SimpleDateFormat("HH:mm");
                            String txtsenddate = senddate.format(NoticeSendDataTime);
                            String txtsendtime = sendtime.format(NoticeSendDataTime);

                            txt_senddate.setText(txtsenddate);
                            txt_sendtime.setText(txtsendtime);

                            txt_sendcontent.setText(dataSnapshot.child("mes_sender_content").getValue().toString());


                            if (dataSnapshot.child("mes_sender_type").getValue().toString().equals("0") ||
                                    dataSnapshot.child("mes_sender_type").getValue().toString().equals("1")||
                                    dataSnapshot.child("mes_sender_type").getValue().toString().equals("6")||
                                    dataSnapshot.child("mes_sender_type").getValue().toString().equals("10")) {

                            } else if(dataSnapshot.child("mes_sender_type").getValue().toString().equals("4")){
                                txt_sendcontent.setText(dataSnapshot.child("mes_sender_content").getValue().toString().replace("<br>","\n"));
                            } else if (dataSnapshot.child("mes_sender_type").getValue().toString().equals("5")) {
                                but_function.setVisibility(View.VISIBLE);
                                SimpleDateFormat signinDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                try {
                                    Date date = signinDate.parse(dataSnapshot.child("mes_sender_time").getValue().toString());

                                    if(System.currentTimeMillis()>date.getTime()){
                                        if(date.getTime()+21600000>System.currentTimeMillis()){
                                            but_function.setText("簽到");
//                                            but_function.setBackgroundResource(R.drawable.button3);
//                                            but_function.setTextColor(Color.parseColor("#FFFFFFFF"));
                                            but_function.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    coin += 5;
                                                    member_dataRef.child(user_uid).child("m_coin").setValue(coin)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Notice_sender_dataRef.child(dataSnapshot.child("mes_sender_id").getValue().toString())
                                                                            .child("mes_sender_type").setValue("9");

                                                                    SharedPreferences first_pre = getContext().getSharedPreferences("FIRST", MODE_PRIVATE);
                                                                    first_pre.edit()
                                                                            .putString("firstUse", "SecondupUse")
                                                                            .commit();
                                                                    SharedPreferences pref = getContext().getSharedPreferences("SigninData", MODE_PRIVATE);
                                                                    pref.edit()
                                                                            .putLong("currenttime", System.currentTimeMillis())
                                                                            .putBoolean("sginBool",false)
                                                                            .commit();
//                                                                    dialogs = new AlertDialog.Builder(getContext())
//                                                                            .setTitle("簽到成功")
//                                                                            .setMessage("系統已經將簽到獎勵發給您囉~")
//
//                                                                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
//                                                                                @Override
//                                                                                public void onClick(DialogInterface dialog, int which) {
//
//                                                                                    dialogs.dismiss();
//                                                                                }
//                                                                            })
//                                                                            .create();
                                                                    Toast.makeText(getContext(),"簽到成功",Toast.LENGTH_LONG).show();
//                                                                    dialogs.show();
//                                                                    dialogs.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
//                                                                    dialogs.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.GRAY);

                                                                }
                                                            });
                                                }
                                            });
                                        }else{
                                            but_function.setBackgroundResource(android.R.drawable.btn_default);
                                            but_function.setText("已簽到");
                                            but_function.setEnabled(false);
                                        }
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else if (dataSnapshot.child("mes_sender_type").getValue().toString().equals("2")) {
                                but_function.setVisibility(View.VISIBLE);
                                but_function.setText("回覆私聊邀請");
//                                but_function.setBackgroundResource(R.drawable.button3);
//                                but_function.setTextColor(Color.parseColor("#FFFFFFFF"));
                                but_function.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Private_chat_dataRef.child(dataSnapshot.child("mes_pc_id").getValue().toString())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot chatdataSnapshot) {

//                                                        final String pc_member_id = chatdataSnapshot.child("pc_member_id").getValue().toString();
                                                        final String pc_id = chatdataSnapshot.child("pc_id").getValue().toString();
                                                        final String pc_status = chatdataSnapshot.child("pc_status").getValue().toString();
                                                        Private_chat_room_member_firebase pcm = new Private_chat_room_member_firebase(
                                                                pc_id,
                                                                "1",
                                                                firebaseAuth.getCurrentUser().getUid()
                                                        );
                                                        Private_chat_room_member_dataRef.child(pc_id)
                                                                .child(firebaseAuth.getCurrentUser().getUid())
                                                                .setValue(pcm);

                                                        Private_chat_room_member_dataRef.child(pc_id).child(user_uid)
                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot chatmemberdataSnapshot) {
                                                                if (chatmemberdataSnapshot.child("pcm_status").getValue().toString().equals("1")) {
                                                                    if (pc_status.equals("0")) {
//                                                                        if (pc_account.equals(dataSnapshot.child("mes_sender_account").getValue().toString())) {

                                                                            Intent intent = new Intent(mContext, Private_waitroomActivity.class);
                                                                            Bundle bundle = new Bundle();
                                                                            bundle.putString("pr_roomid", pc_id);
                                                                            bundle.putString("serder_id", item);
                                                                            intent.putExtras(bundle);
                                                                            mContext.startActivity(intent);
//                                                                        }
                                                                    } else if (pc_status.equals("1")) {
//                                                                        if (pc_account.equals(dataSnapshot.child("mes_sender_account").getValue().toString())) {

                                                                            Intent intent = new Intent(mContext, PrivatechatActivity.class);
                                                                            Bundle bundle = new Bundle();
                                                                            bundle.putString("pr_roomid", pc_id);
                                                                            bundle.putString("serder_id", item);
                                                                            intent.putExtras(bundle);
                                                                            mContext.startActivity(intent);
//                                                                        }
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
                                    }
                                });
                            } else if (dataSnapshot.child("mes_sender_type").getValue().toString().equals("3")) {
                                but_function.setVisibility(View.VISIBLE);
                                but_function.setText("回覆多人聊天邀請");
//                                but_function.setBackgroundResource(R.drawable.button3);
//                                but_function.setTextColor(Color.parseColor("#FFFFFFFF"));
                                but_function.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Private_chat_dataRef.child(dataSnapshot.child("mes_pc_id").getValue().toString())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot chatdataSnapshot) {

//                                                        final String pc_account = chatdataSnapshot.child("pc_account").getValue().toString();
                                                        final String pc_id = chatdataSnapshot.child("pc_id").getValue().toString();
                                                        final String pc_status = chatdataSnapshot.child("pc_status").getValue().toString();
                                                        Private_chat_room_member_firebase pcm = new Private_chat_room_member_firebase(
                                                                pc_id,
                                                                "1",
                                                                firebaseAuth.getCurrentUser().getUid()
                                                        );
                                                        Private_chat_room_member_dataRef.child(pc_id)
                                                                .child(firebaseAuth.getCurrentUser().getUid())
                                                                .setValue(pcm);

                                                        Private_chat_room_member_dataRef.child(pc_id).child(user_uid)
                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot chatmemberdataSnapshot) {
                                                                        if (chatmemberdataSnapshot.child("pcm_status").getValue().toString().equals("1")) {
                                                                            if (pc_status.equals("0")) {
//                                                                                if (pc_account.equals(dataSnapshot.child("mes_sender_account").getValue().toString())) {

                                                                                    Intent intent = new Intent(mContext, Private_waitroomActivity.class);
                                                                                    Bundle bundle = new Bundle();
                                                                                    bundle.putString("pr_roomid", pc_id);
                                                                                    bundle.putString("serder_id", item);
                                                                                    intent.putExtras(bundle);
                                                                                    mContext.startActivity(intent);
//                                                                                }
                                                                            } else if (pc_status.equals("1")) {
//                                                                                if (pc_account.equals(dataSnapshot.child("mes_sender_account").getValue().toString())) {

                                                                                    Intent intent = new Intent(mContext, PrivatechatActivity.class);
                                                                                    Bundle bundle = new Bundle();
                                                                                    bundle.putString("pr_roomid", pc_id);
                                                                                    bundle.putString("serder_id", item);
                                                                                    intent.putExtras(bundle);
                                                                                    mContext.startActivity(intent);
//                                                                                }
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
                                    }
                                });

                            } else if (dataSnapshot.child("mes_sender_type").getValue().toString().equals("7")) {
                                but_function.setVisibility(View.VISIBLE);
                                but_function.setText("私聊已結束");
                                but_function.setEnabled(false);
                            } else if (dataSnapshot.child("mes_sender_type").getValue().toString().equals("8")) {
                                but_function.setVisibility(View.VISIBLE);
                                but_function.setText("多人聊天已結束");
                                but_function.setEnabled(false);

                            }else if (dataSnapshot.child("mes_sender_type").getValue().toString().equals("9")) {
                                but_function.setVisibility(View.VISIBLE);
                                but_function.setText("已簽到");
                                but_function.setEnabled(false);

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }


        return view;
    }

}
