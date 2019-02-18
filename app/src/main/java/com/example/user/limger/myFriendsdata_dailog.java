package com.example.user.limger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.utils.L;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.AlertDialog.THEME_HOLO_LIGHT;

/**
 * Created by User on 2018/7/12.
 */

public class myFriendsdata_dailog extends AppCompatDialogFragment {
    TextView MyFriendsname_account,MyFriendsname_nickname, MyFriends_school, MyFriends_subject, MyFriends_gender, MyFriends_birth, MyFriends_interset;
    CircleImageView MyFriendsImg;
    String othersuer_uid;
    DatabaseReference member_dataRef, friend_dataRef
            ,Notice_sender_dataRef, Notice_receiver_dataRef
            ,Private_chat_dataRef,Private_chat_room_member_dataRef;
    FirebaseAuth firebaseAuth;
    String user_uid,other_account;
    Context mContext;

    SimpleDateFormat simpleDateFormat;
    String Date;
    Calendar calendar;
    Boolean chatBoo;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext,THEME_HOLO_LIGHT);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.myfriends_dailog, null);
        if(chatBoo){
            SharedPreferences roomid = getContext().getSharedPreferences("myFriendsData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = roomid.edit();
            editor.putBoolean("prchatmember", false);
            editor.commit();

            builder.setView(view)
                    .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    })
                    .setNeutralButton("封鎖", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            AlertDialog dialogs = new AlertDialog.Builder(getActivity())

                                    .setMessage("是否確定要將"+other_account+"封鎖？封鎖後"+other_account+"就無法再和你成為好友！")
                                    .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dismiss();
                                        }
                                    })
                                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            friend_dataRef.child(firebaseAuth.getCurrentUser().getUid()).child(othersuer_uid).child("f_bstatus").setValue("1")
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(mContext,"封鎖成功",Toast.LENGTH_SHORT).show();

                                                }
                                            });

                                        }
                                    })
                                    .create();

                            dialogs.show();
                            dialogs.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
                            dialogs.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.GRAY);

                        }
                    });
        }else{
            builder.setView(view)
                    .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    })
                    .setNeutralButton("封鎖", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            AlertDialog dialogs = new AlertDialog.Builder(getActivity())

                                    .setMessage("是否確定要將"+other_account+"封鎖？封鎖後"+other_account+"就無法再和你成為好友！")
                                    .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dismiss();
                                        }
                                    })
                                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            friend_dataRef.child(firebaseAuth.getCurrentUser().getUid()).child(othersuer_uid).child("f_bstatus").setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(mContext,"封鎖成功",Toast.LENGTH_SHORT).show();

                                                }
                                            });

                                        }
                                    })
                                    .create();

                            dialogs.show();
                            dialogs.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
                            dialogs.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.GRAY);

                        }
                    })
                    .setPositiveButton("私聊", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            AlertDialog dialogs = new AlertDialog.Builder(mContext)
                                    .setMessage("確定邀請該好友私聊？")
                                    .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dismiss();
                                        }
                                    })
                                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            member_dataRef.child(user_uid)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            String myself_nick = dataSnapshot.child("m_nick").getValue().toString();
                                                            String myself_account = dataSnapshot.child("m_account").getValue().toString();
                                                            String not_id = Notice_sender_dataRef.push().getKey();
                                                            calendar = Calendar.getInstance();
                                                            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                                                            Date = simpleDateFormat.format(calendar.getTime());

                                                            //好友聊天
                                                            String privc_id = Private_chat_dataRef.push().getKey();
                                                            Private_chat_room_firebase p_chatroom = new Private_chat_room_firebase(
                                                                    privc_id,
                                                                    user_uid,
                                                                    "0"
                                                            );
                                                            Private_chat_dataRef.child(privc_id).setValue(p_chatroom);

                                                            Private_chat_room_member_firebase pcm = new Private_chat_room_member_firebase(
                                                                    privc_id,
                                                                    "1",
                                                                    user_uid
                                                            );
                                                            Private_chat_room_member_dataRef.child(privc_id).child(user_uid).setValue(pcm);

                                                            //寄收通知
                                                            Notice_sender_firebase not_sender = new Notice_sender_firebase(
                                                                    not_id
                                                                    ,user_uid
                                                                    ,myself_nick+"-"+myself_account+"想和您進行私聊～趕快看看要聊什麼吧～"
                                                                    ,Date
                                                                    ,"聊天邀請來囉！"
                                                                    ,"2"
                                                                    ,privc_id
                                                            );
                                                            Notice_sender_dataRef.child(not_id).setValue(not_sender);

                                                            Notice_receiver_firebase not_receiver =new Notice_receiver_firebase(
                                                                    not_id,
                                                                    not_id,
                                                                    other_account,
                                                                    othersuer_uid
                                                            );
                                                            Notice_receiver_dataRef.child(not_id).setValue(not_receiver);

//                                                Notice_receiver_firebase not_receiver =new Notice_receiver_firebase(
//                                                        not_id,
//                                                        other_account,
//                                                        myself_account
//
//                                                );
//                                                Notice_receiver_dataRef.child(othersuer_uid).child(not_id).setValue(not_receiver);



                                                            Intent intent = new Intent(mContext, Private_waitroomActivity.class);
                                                            Bundle bundle = new Bundle();
                                                            bundle.putString("pr_roomid",privc_id);
                                                            bundle.putString("serder_id",not_id);
                                                            intent.putExtras(bundle);
                                                            mContext.startActivity(intent);
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });


                                        }
                                    })
                                    .create();


                            dialogs.show();
                            dialogs.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
                            dialogs.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.GRAY);

                        }
                    });
        }

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.GRAY);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);

        MyFriendsname_account = view.findViewById(R.id.myfri_txt_account);
        MyFriendsname_nickname = view.findViewById(R.id.myfri_txt_nickname);
        MyFriends_school = view.findViewById(R.id.myfri_txt_school);
        MyFriends_subject = view.findViewById(R.id.myfri_txt_subject);
        MyFriends_gender = view.findViewById(R.id.myfri_txt_gender);
        MyFriends_birth = view.findViewById(R.id.myfri_txt_birth);
        MyFriends_interset = view.findViewById(R.id.myfri_txt_inter);
        MyFriendsImg = view.findViewById(R.id.myFriendsdata_img);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        member_dataRef = FirebaseDatabase.getInstance().getReference("member");
        friend_dataRef = FirebaseDatabase.getInstance().getReference("Friend");
        Notice_sender_dataRef = FirebaseDatabase.getInstance().getReference("Notice_message_sender");
        Notice_receiver_dataRef = FirebaseDatabase.getInstance().getReference("Notice_message_receiver");
        Private_chat_dataRef= FirebaseDatabase.getInstance().getReference("private_chat_room");
        Private_chat_room_member_dataRef =FirebaseDatabase.getInstance().getReference("private_chat_room_member");
        othersuer_uid = getContext().getSharedPreferences("myFriendsData", Context.MODE_PRIVATE).getString("othersuer_uid", "");
        chatBoo = getContext().getSharedPreferences("myFriendsData", Context.MODE_PRIVATE).getBoolean("prchatmember", false);
        firebaseAuth = FirebaseAuth.getInstance();
        user_uid = firebaseAuth.getCurrentUser().getUid();


    }

    @Override
    public void onStart() {
        super.onStart();
        mContext = getContext();
        System.out.println("test----"+othersuer_uid);
        member_dataRef.child(othersuer_uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        other_account = dataSnapshot.child("m_account").getValue().toString();
                        Picasso.get().load(dataSnapshot.child("m_head").getValue().toString()).into(MyFriendsImg);

                        MyFriendsname_account.setText(dataSnapshot.child("m_account").getValue().toString());
                        MyFriendsname_nickname.setText(dataSnapshot.child("m_nick").getValue().toString());
                        MyFriends_school.setText(dataSnapshot.child("m_school").getValue().toString());
                        MyFriends_subject.setText(dataSnapshot.child("m_subject").getValue().toString());
                        if ((dataSnapshot.child("m_gender").getValue().toString()).equals("0")) {
                            MyFriends_gender.setText("("+"女"+")");
                        } else if ((dataSnapshot.child("m_gender").getValue().toString()).equals("1")) {
                            MyFriends_gender.setText("("+"男"+")");
                        }

                        MyFriends_birth.setText(dataSnapshot.child("m_birth").getValue().toString());
                        MyFriends_interset.setText(dataSnapshot.child("m_interest").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }
}
