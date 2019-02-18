package com.example.user.limger;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 2018/6/29.
 */

public class addfriendAdapter extends RecyclerView.Adapter<addfriendAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<chat_room_member_firebase> mList;
    DatabaseReference FriendReguest_DataRef, Friends_DataRef, Member_DataRef, Notice_sender_DataRef, Notice_receiver_DataRef;
    FirebaseAuth firebaseAuth;
    String user_uid;
    String myself_account, myself_nick;


    //    long networkTime;
    SimpleDateFormat simpleDateFormat;
    String Date;
    Calendar calendar;
    int sum = 0;

    addfriendAdapter(Context context, ArrayList<chat_room_member_firebase> list) {
        mContext = context;
        mList = list;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.addfriend_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final chat_room_member_firebase chatmember = mList.get(position);

        final CircleImageView memberImg = holder.addmember_img;


        final RelativeLayout relativeLayout = holder.relativeLayout;
        final TextView txt_blockade = holder.txt_blockade;
        final CardView cardview = holder.cardview;
        final Button bt_addfriend = holder.bt_addfriend;
        final ConstraintLayout Con_addfriend = holder.Con_addfriend;
        final ConstraintLayout Con_friends = holder.Con_friends;
        final TextView txtaddfriend = holder.txtaddfriend;
        final ConstraintLayout Con_blockade = holder.Con_blockade;

        Con_addfriend.setVisibility(View.INVISIBLE);
        Con_friends.setVisibility(View.INVISIBLE);
        Con_blockade.setVisibility(View.INVISIBLE);

        Member_DataRef.child(chatmember.cm_member_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Picasso.get().load(dataSnapshot.child("m_head").getValue().toString()).into(memberImg);
                        Friends_DataRef.child(user_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //待確認
                                if (dataSnapshot.child(chatmember.cm_member_id).exists()) {
                                    if (dataSnapshot.child(chatmember.cm_member_id).child("f_bstatus").getValue().toString().equals("0")) {
                                        Con_friends.setVisibility(View.VISIBLE);
                                        Log.d("tagjjjjjj----","CONCONFI1");
                                        memberImg.setEnabled(false);

                                        System.out.println("加好友---已成為好友" + dataSnapshot.child(chatmember.cm_member_id).child("f_member_id2").getValue().toString());
                                    } else if (dataSnapshot.child(chatmember.cm_member_id).child("f_bstatus").getValue().toString().equals("1")) {
                                        Con_blockade.setVisibility(View.VISIBLE);
                                        Log.d("tagjjjjjj----","CONCONFI2");
                                        memberImg.setEnabled(false);
                                        System.out.println("加好友---好友封鎖" + dataSnapshot.child(chatmember.cm_member_id).child("f_member_id2").getValue().toString());
                                    } else {

                                    }
                                } else {
                                    Con_addfriend.setVisibility(View.VISIBLE);
                                    Log.d("tagjjjjjj----","CONCONFI3");
                                    bt_addfriend.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            memberImg.setEnabled(false);
                                            bt_addfriend.setEnabled(false);
                                            bt_addfriend.setBackgroundResource(R.drawable.verified);
                                            txtaddfriend.setText("已寄送邀請");
                                            Member_DataRef.child(user_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    myself_account = dataSnapshot.child("m_account").getValue().toString();
                                                    myself_nick = dataSnapshot.child("m_nick").getValue().toString();
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                }
                                            });


                                            FriendReguest_DataRef.child(chatmember.cm_id)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                                            //檢查資料表中欄位是否已存在(表示有被人加好友了)，如果是，就直接成為好友
                                                            if (dataSnapshot.child(user_uid).child(chatmember.cm_member_id).exists()) {
                                                                //Friend資料表同時新增兩筆資料(送出邀請者 及 被加者)
                                                                Friend_firebase friend_fir1 = new Friend_firebase(user_uid, chatmember.cm_member_id, "0");
                                                                Friends_DataRef.child(user_uid).child(chatmember.cm_member_id).setValue(friend_fir1);

                                                                Friend_firebase friend_fir2 = new Friend_firebase(chatmember.cm_member_id, user_uid, "0");
                                                                Friends_DataRef.child(chatmember.cm_member_id).child(user_uid).setValue(friend_fir2);


                                                                Member_DataRef.child(chatmember.cm_member_id)
                                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                String otherUser_account = dataSnapshot.child("m_account").getValue().toString();
                                                                                String otherUser_nickname = dataSnapshot.child("m_nick").getValue().toString();

                                                                                calendar = Calendar.getInstance();
                                                                                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                                                                                Date = simpleDateFormat.format(calendar.getTime());

                                                                                //寄交友通知給對方
                                                                                String not_id = Notice_sender_DataRef.push().getKey();
                                                                                Notice_sender_firebase not_sender = new Notice_sender_firebase(
                                                                                        not_id
                                                                                        , user_uid
                                                                                        , "趕快到Friend圈進行第一次私聊吧！"
                                                                                        , Date
                                                                                        , "恭喜您與" + myself_nick + "-" + myself_account + "配對成功"
                                                                                        , "1"
                                                                                        , null);
                                                                                Notice_sender_DataRef.child(not_id).setValue(not_sender);

                                                                                //寄交友通知給自己
                                                                                String not_id2 = Notice_sender_DataRef.push().getKey();
                                                                                Notice_sender_firebase not_sender2 = new Notice_sender_firebase(
                                                                                        not_id2
                                                                                        , chatmember.cm_member_id
                                                                                        , "趕快到Friend圈進行第一次私聊吧！"
                                                                                        , Date
                                                                                        , "恭喜您與" + otherUser_nickname + "-" + otherUser_account + "配對成功"
                                                                                        , "1"
                                                                                        , null);
                                                                                Notice_sender_DataRef.child(not_id2).setValue(not_sender2);

                                                                                //寄接收通知給對方
                                                                                Notice_receiver_firebase not_receiver = new Notice_receiver_firebase(
                                                                                        not_id,
                                                                                        not_id,
                                                                                        otherUser_account,
                                                                                        chatmember.cm_member_id
                                                                                );
                                                                                Notice_receiver_DataRef.child(not_id).setValue(not_receiver);

                                                                                //寄接收通知給自己
                                                                                Notice_receiver_firebase not_receiver2 = new Notice_receiver_firebase(
                                                                                        not_id2,
                                                                                        not_id2,
                                                                                        myself_account,
                                                                                        user_uid
                                                                                );
                                                                                Notice_receiver_DataRef.child(not_id2).setValue(not_receiver2);

                                                                            }

                                                                            @Override
                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                            }
                                                                        });


                                                            }

                                                            //如果沒有，就點選並加對方好友(送出請求)
                                                            else {


                                                                FriendRequest_firebase fr = new FriendRequest_firebase(
                                                                        user_uid,
                                                                        chatmember.cm_member_id,
                                                                        chatmember.cm_id
                                                                );
                                                                final FriendRequest_firebase fr2 = new FriendRequest_firebase(
                                                                        chatmember.cm_member_id,
                                                                        user_uid,
                                                                        chatmember.cm_id
                                                                );
                                                                FriendReguest_DataRef.child(chatmember.cm_id).child(user_uid)
                                                                        .child(chatmember.cm_member_id).setValue(fr);
                                                                FriendReguest_DataRef.child(chatmember.cm_id).child(chatmember.cm_member_id).child(user_uid).setValue(fr2);

                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                        }
                                                    });
                                        }
                                    });
                                    memberImg.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            memberImg.setEnabled(false);
                                            bt_addfriend.setEnabled(false);
                                            bt_addfriend.setBackgroundResource(R.drawable.verified);
                                            txtaddfriend.setText("已寄送邀請");
                                            Member_DataRef.child(user_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    myself_account = dataSnapshot.child("m_account").getValue().toString();
                                                    myself_nick = dataSnapshot.child("m_nick").getValue().toString();
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                }
                                            });


                                            FriendReguest_DataRef.child(chatmember.cm_id)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                                            //檢查資料表中欄位是否已存在(表示有被人加好友了)，如果是，就直接成為好友
                                                            if (dataSnapshot.child(user_uid).child(chatmember.cm_member_id).exists()) {

                                                                //Friend資料表同時新增兩筆資料(送出邀請者 及 被加者)
                                                                Friend_firebase friend_fir1 = new Friend_firebase(user_uid, chatmember.cm_member_id, "0");
                                                                Friends_DataRef.child(user_uid).child(chatmember.cm_member_id).setValue(friend_fir1);

                                                                Friend_firebase friend_fir2 = new Friend_firebase(chatmember.cm_member_id, user_uid, "0");
                                                                Friends_DataRef.child(chatmember.cm_member_id).child(user_uid).setValue(friend_fir2);


                                                                Member_DataRef.child(chatmember.cm_member_id)
                                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                String otherUser_account = dataSnapshot.child("m_account").getValue().toString();
                                                                                String otherUser_nickname = dataSnapshot.child("m_nick").getValue().toString();

                                                                                calendar = Calendar.getInstance();
                                                                                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                                                                                Date = simpleDateFormat.format(calendar.getTime());

                                                                                //寄交友通知給對方
                                                                                String not_id = Notice_sender_DataRef.push().getKey();
                                                                                Notice_sender_firebase not_sender = new Notice_sender_firebase(
                                                                                        not_id
                                                                                        , user_uid
                                                                                        , "趕快到Friend圈進行第一次私聊吧！"
                                                                                        , Date
                                                                                        , "恭喜您與" + myself_nick + "-" + myself_account + "配對成功"
                                                                                        , "1"
                                                                                        , null);
                                                                                Notice_sender_DataRef.child(not_id).setValue(not_sender);

                                                                                //寄交友通知給自己
                                                                                String not_id2 = Notice_sender_DataRef.push().getKey();
                                                                                Notice_sender_firebase not_sender2 = new Notice_sender_firebase(
                                                                                        not_id2
                                                                                        , chatmember.cm_member_id
                                                                                        , "趕快到Friend圈進行第一次私聊吧！"
                                                                                        , Date
                                                                                        , "恭喜您與" + otherUser_nickname + "-" + otherUser_account + "配對成功"
                                                                                        , "1"
                                                                                        , null);
                                                                                Notice_sender_DataRef.child(not_id2).setValue(not_sender2);

                                                                                //寄接收通知給對方
                                                                                Notice_receiver_firebase not_receiver = new Notice_receiver_firebase(
                                                                                        not_id,
                                                                                        not_id,
                                                                                        otherUser_account,
                                                                                        chatmember.cm_member_id
                                                                                );
                                                                                Notice_receiver_DataRef.child(not_id).setValue(not_receiver);

                                                                                //寄接收通知給自己
                                                                                Notice_receiver_firebase not_receiver2 = new Notice_receiver_firebase(
                                                                                        not_id2,
                                                                                        not_id2,
                                                                                        myself_account,
                                                                                        user_uid
                                                                                );
                                                                                Notice_receiver_DataRef.child(not_id2).setValue(not_receiver2);

                                                                            }

                                                                            @Override
                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                            }
                                                                        });


                                                            }

                                                            //如果沒有，就點選並加對方好友(送出請求)
                                                            else {
                                                                FriendRequest_firebase fr = new FriendRequest_firebase(
                                                                        user_uid,
                                                                        chatmember.cm_member_id,
                                                                        chatmember.cm_id
                                                                );
                                                                final FriendRequest_firebase fr2 = new FriendRequest_firebase(
                                                                        chatmember.cm_member_id,
                                                                        user_uid,
                                                                        chatmember.cm_id
                                                                );
                                                                FriendReguest_DataRef.child(chatmember.cm_id).child(user_uid)
                                                                        .child(chatmember.cm_member_id).setValue(fr);
                                                                FriendReguest_DataRef.child(chatmember.cm_id).child(chatmember.cm_member_id)
                                                                        .child(user_uid).setValue(fr2);

                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                        }
                                                    });

                                        }
                                    });


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

    private void intoAddFriend(final chat_room_member_firebase chatmember) {

    }

    public static class Utils {
        // 两次点击按钮之间的点击间隔不能少于500毫秒
        private static final int MIN_CLICK_DELAY_TIME = 500;
        private static long lastClickTime;

        public static boolean isFastClick() {
            boolean flag = false;
            long curClickTime = System.currentTimeMillis();
            if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
                flag = true;
            }
            lastClickTime = curClickTime;
            return flag;
        }

    }

    @Override
    public int getItemCount() {
        System.out.println("數量----" + mList.size());
        return mList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView addmember_img;

        RelativeLayout relativeLayout;
        TextView txt_blockade, txtaddfriend;
        CardView cardview;
        Button bt_addfriend;
        ConstraintLayout Con_addfriend, Con_friends, Con_blockade;

        public ViewHolder(View itemView) {
            super(itemView);
            FriendReguest_DataRef = FirebaseDatabase.getInstance().getReference().child("Friend_Reguest");
            Friends_DataRef = FirebaseDatabase.getInstance().getReference().child("Friend");
            Member_DataRef = FirebaseDatabase.getInstance().getReference().child("member");
            Notice_sender_DataRef = FirebaseDatabase.getInstance().getReference().child("Notice_message_sender");
            Notice_receiver_DataRef = FirebaseDatabase.getInstance().getReference().child("Notice_message_receiver");

            firebaseAuth = FirebaseAuth.getInstance();
            user_uid = firebaseAuth.getCurrentUser().getUid();
            addmember_img = itemView.findViewById(R.id.friend_img);

            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            txt_blockade = itemView.findViewById(R.id.txt_blockade);
            cardview = itemView.findViewById(R.id.cardview);
            bt_addfriend = itemView.findViewById(R.id.bt_addfriend);
            txtaddfriend = itemView.findViewById(R.id.txtaddfriend);
            Con_addfriend = itemView.findViewById(R.id.Con_addfriend);
            Con_friends = itemView.findViewById(R.id.Con_friends);
            Con_blockade = itemView.findViewById(R.id.Con_blockade);

        }
    }


}
