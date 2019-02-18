package com.example.user.limger;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.jobdispatcher.Constraint;
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
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class myFriend_Fragment extends Fragment {
    GridView myFriendListview;
    Button button3, button2, button4;
    TextView txt_nofriend;
    DatabaseReference Friend_dataRef, member_dataRef, Notice_sender_dataRef, Notice_receiver_dataRef, Private_chat_dataRef, Private_chat_room_member_dataRef;
    FirebaseAuth firebaseAuth;
    String user_uid;

    Boolean check = false;
    ArrayList<String> peoplechatList = new ArrayList<>();


    AlertDialog dialogs;
    SimpleDateFormat simpleDateFormat;
    String Date;
    Calendar calendar;
    String myself_nick, myself_account;
    String muchchat_account;
    String not_id;

    int sum = 0;

    public myFriend_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_my_friend, container, false);
        myFriendListview = view.findViewById(R.id.myFriendList);

        button3 = view.findViewById(R.id.button3);
        button2 = view.findViewById(R.id.button2);
        txt_nofriend = view.findViewById(R.id.txt_nofriend);
        Friend_dataRef = FirebaseDatabase.getInstance().getReference("Friend");
        member_dataRef = FirebaseDatabase.getInstance().getReference("member");
        Notice_sender_dataRef = FirebaseDatabase.getInstance().getReference("Notice_message_sender");
        Notice_receiver_dataRef = FirebaseDatabase.getInstance().getReference("Notice_message_receiver");
        Private_chat_dataRef = FirebaseDatabase.getInstance().getReference("private_chat_room");
        Private_chat_room_member_dataRef = FirebaseDatabase.getInstance().getReference("private_chat_room_member");
        firebaseAuth = FirebaseAuth.getInstance();
        user_uid = firebaseAuth.getCurrentUser().getUid();

        final List<Friend_firebase> Friend_list;
        Friend_list = new ArrayList<>();
        final List<Friend_firebase> Friend_list2 = new ArrayList<>();
        member_dataRef.child(user_uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        myself_nick = dataSnapshot.child("m_nick").getValue().toString();
                        myself_account = dataSnapshot.child("m_account").getValue().toString();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check) {
                    check = false;
                    button2.setVisibility(View.GONE);
                    peoplechatList.clear();
                    button3.setText("多人聊天");
                    Friend_dataRef.child(firebaseAuth.getCurrentUser().getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Friend_list.clear();
                                    for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                                        Friend_firebase friends = artistSnapshot.getValue(Friend_firebase.class);
                                        if (friends.f_bstatus.equals("0")) {

                                            Friend_list.add(friends);
                                        } else if (friends.f_bstatus.equals("1")) {
                                            Friend_list.remove(friends);
                                        }
                                    }

                                    myFriendListview.setNumColumns(3);
                                    myFriendListview.setVerticalSpacing(50);
                                    Collections.reverse(Friend_list); //排序倒過來
                                    if (getContext() != null) {
                                        myFriendsList adapter = new myFriendsList(getContext(), Friend_list);
                                        myFriendListview.setAdapter(adapter);

                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                } else {
                    check = true;
                    button3.setText("取消");
                    Friend_dataRef.child(firebaseAuth.getCurrentUser().getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Friend_list.clear();
                                    for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                                        Friend_firebase friends = artistSnapshot.getValue(Friend_firebase.class);
                                        if (friends.f_bstatus.equals("0")) {

                                            Friend_list.add(friends);
                                        } else if (friends.f_bstatus.equals("1")) {
                                            Friend_list.remove(friends);
                                        }
                                    }

                                    myFriendListview.setNumColumns(3);
                                    myFriendListview.setVerticalSpacing(50);
                                    Collections.reverse(Friend_list); //排序倒過來
                                    if (getContext() != null) {
                                        myFriendsList2 adapter = new myFriendsList2(getContext(), Friend_list);
                                        myFriendListview.setAdapter(adapter);

                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                    button2.setVisibility(View.VISIBLE);
                    button2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (peoplechatList.size() == 0) {
                                Toast.makeText(getContext(), "請選擇多人聊天對象", Toast.LENGTH_SHORT).show();
                            } else {

                                dialogs = new AlertDialog.Builder(getContext())
                                        .setMessage("確定要多人聊天嗎？")
                                        .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialogs.dismiss();
                                            }
                                        })
                                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
//                                            final String not_id = Notice_sender_dataRef.push().getKey();
                                                final String privc_id = Private_chat_dataRef.push().getKey();

                                                calendar = Calendar.getInstance();
                                                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                                                Date = simpleDateFormat.format(calendar.getTime());
                                                not_id = Notice_sender_dataRef.push().getKey();
                                                //寄通知
                                                Notice_sender_firebase not_sender = new Notice_sender_firebase(
                                                        not_id
                                                        , user_uid
                                                        , myself_nick + "-" + myself_account + "想邀請你進行多人聊天" + "\n" + "趕快來看看有誰一同參與吧！"
                                                        , Date
                                                        , "聊天邀請來囉！"
                                                        , "3"
                                                        , privc_id
                                                );
                                                Notice_sender_dataRef.child(not_id).setValue(not_sender);

                                                for (int i = 0; i < peoplechatList.size(); i++) {
                                                    final int finalI = i;
                                                    member_dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            for (final DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                                                                final registe_firebase artist = artistSnapshot.getValue(registe_firebase.class);
                                                                if (artist.id.equals(peoplechatList.get(finalI))) {

//                                                                        muchchat_account = dataSnapshot.child("m_account").getValue().toString();
                                                                    System.out.println("多人聊天接收通知ID-----" + not_id);
                                                                    //接收通知
                                                                    String notre_id = Notice_receiver_dataRef.push().getKey();
                                                                    Notice_receiver_firebase not_receiver = new Notice_receiver_firebase(
                                                                            notre_id,
                                                                            not_id,
                                                                            artist.m_account,
                                                                            peoplechatList.get(finalI)
                                                                    );

                                                                    Notice_receiver_dataRef.child(notre_id).setValue(not_receiver);
                                                                }
                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                }
                                                //多人聊天房間
                                                Private_chat_room_firebase p_chatroom = new Private_chat_room_firebase(
                                                        privc_id,
                                                        user_uid,
                                                        "0"
                                                );
                                                Private_chat_dataRef.child(privc_id).setValue(p_chatroom);

                                                //多人聊天房間內成員
                                                Private_chat_room_member_firebase pcm = new Private_chat_room_member_firebase(
                                                        privc_id,
                                                        "1",
                                                        user_uid
                                                );
                                                Private_chat_room_member_dataRef.child(privc_id).child(user_uid).setValue(pcm);

                                                Friend_dataRef.child(firebaseAuth.getCurrentUser().getUid())
                                                        .addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                Friend_list.clear();
                                                                for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                                                                    Friend_firebase friends = artistSnapshot.getValue(Friend_firebase.class);
                                                                    if (friends.f_bstatus.equals("0")) {

                                                                        Friend_list.add(friends);
                                                                    } else if (friends.f_bstatus.equals("1")) {
                                                                        Friend_list.remove(friends);
                                                                    }
                                                                }

                                                                myFriendListview.setNumColumns(3);
                                                                myFriendListview.setVerticalSpacing(50);
                                                                Collections.reverse(Friend_list); //排序倒過來
                                                                if (getContext() != null) {
                                                                    myFriendsList adapter = new myFriendsList(getContext(), Friend_list);
                                                                    myFriendListview.setAdapter(adapter);

                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });
                                                button2.setVisibility(View.GONE);


                                                Intent intent = new Intent(getContext(), Private_waitroomActivity.class);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("pr_roomid", privc_id);
                                                bundle.putString("serder_id", not_id);
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                            }


                                        })
                                        .create();

                                dialogs.show();
                                dialogs.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
                                dialogs.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.GRAY);
                            }


                        }
                    });

                }
            }

        });

        if (check == false) {
            Friend_dataRef.child(firebaseAuth.getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Friend_list.clear();
                            for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                                Friend_firebase friends = artistSnapshot.getValue(Friend_firebase.class);

                                if (friends.f_bstatus.equals("0")) {
                                    Friend_list.add(friends);
                                } else if (friends.f_bstatus.equals("1")) {
                                    Friend_list.remove(friends);
                                    Friend_list2.add(friends);

                                }

                            }

                            myFriendListview.setNumColumns(3);
                            myFriendListview.setVerticalSpacing(50);
                            Collections.reverse(Friend_list); //排序倒過來
                            if (getContext() != null) {
                                if (Friend_list.size() == 0) {
                                    txt_nofriend.setVisibility(View.VISIBLE);
                                    button3.setVisibility(View.GONE);
                                }
                              else {
                                    txt_nofriend.setVisibility(View.GONE);
                                    button3.setVisibility(View.VISIBLE);
                                }
                                myFriendsList adapter = new myFriendsList(getContext(), Friend_list);
                                myFriendListview.setAdapter(adapter);

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

        }


        return view;
    }

    //一般好友名單
    class myFriendsList extends ArrayAdapter<Friend_firebase> {
        private List<Friend_firebase> freindsList;

        public myFriendsList(@NonNull Context context, List<Friend_firebase> freindsList) {
            super(context, R.layout.layout_freinds_item, freindsList);
            this.freindsList = freindsList;

        }


        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Friend_firebase frd = getItem(position);
            View view;
            final ViewHolder viewHolder;
            view = LayoutInflater.from(getContext()).inflate(R.layout.layout_freinds_item, null);

            if (convertView == null) {
                final DatabaseReference member_dataRef, friend_dataRef;
                final FirebaseAuth firebaseAuth;

                member_dataRef = FirebaseDatabase.getInstance().getReference("member");
                friend_dataRef = FirebaseDatabase.getInstance().getReference("Friend");
                firebaseAuth = FirebaseAuth.getInstance();

                viewHolder = new ViewHolder();

                viewHolder.friends_account = view.findViewById(R.id.Frinds_account);
                viewHolder.friendsImg = view.findViewById(R.id.Friends_img);
                viewHolder.Frienditem_LinearLayout = view.findViewById(R.id.Frienditem_LinearLayout);
                viewHolder.checkBox_invite = view.findViewById(R.id.checkBox_invite);

                final Friend_firebase artist = freindsList.get(position);

                member_dataRef.child(artist.f_member_id2)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {


                                viewHolder.friends_account.setText(dataSnapshot.child("m_account").getValue().toString());
                                Picasso.get().load(dataSnapshot.child("m_head").getValue().toString()).into(viewHolder.friendsImg);


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });
                viewHolder.Frienditem_LinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SharedPreferences roomid = getContext().getSharedPreferences("myFriendsData", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = roomid.edit();
                        editor.putString("othersuer_uid", artist.f_member_id2);
                        editor.commit();

                        myFriendsdata_dailog friend_dailog = new myFriendsdata_dailog();
                        friend_dailog.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "myFriend");


                    }
                });

//            if (BoolInvite == true) {
//                viewHolder.checkBox_invite.setVisibility(View.VISIBLE);
//
//            }

                view.setTag(viewHolder);

            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }


            return view;
        }

        class ViewHolder {
            CheckBox checkBox_invite;
            TextView friends_account;
            CircleImageView friendsImg;
            LinearLayout Frienditem_LinearLayout;
        }
    }

    //點選多人聊天
    class myFriendsList2 extends ArrayAdapter<Friend_firebase> {
        private List<Friend_firebase> freindsList;

        public myFriendsList2(@NonNull Context context, List<Friend_firebase> freindsList) {
            super(context, R.layout.layout_freinds_item, freindsList);
            this.freindsList = freindsList;

        }


        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final int[] flag = {0};
            Friend_firebase frd = getItem(position);
            View view;
            final ViewHolder viewHolder;
            view = LayoutInflater.from(getContext()).inflate(R.layout.layout_freinds_item, null);


            if (convertView == null) {
                final DatabaseReference member_dataRef, friend_dataRef;
                final FirebaseAuth firebaseAuth;

                member_dataRef = FirebaseDatabase.getInstance().getReference("member");
                friend_dataRef = FirebaseDatabase.getInstance().getReference("Friend");
                firebaseAuth = FirebaseAuth.getInstance();

                viewHolder = new ViewHolder();
                viewHolder.friends_account = view.findViewById(R.id.Frinds_account);
                viewHolder.friendsImg = view.findViewById(R.id.Friends_img);
                viewHolder.Frienditem_LinearLayout = view.findViewById(R.id.Frienditem_LinearLayout);
                viewHolder.checkBox_invite = view.findViewById(R.id.checkBox_invite);
                viewHolder.ConLatout = view.findViewById(R.id.ConLatout);
                viewHolder.checkBox_invite.setVisibility(View.VISIBLE);

                final Friend_firebase artist = freindsList.get(position);

                member_dataRef.child(artist.f_member_id2)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {


                                viewHolder.friends_account.setText(dataSnapshot.child("m_account").getValue().toString());
                                Picasso.get().load(dataSnapshot.child("m_head").getValue().toString()).into(viewHolder.friendsImg);


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });
                viewHolder.Frienditem_LinearLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });

                viewHolder.checkBox_invite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (flag[0] == 0) {
                            viewHolder.checkBox_invite.setChecked(true);
                            peoplechatList.add(artist.f_member_id2);
                            flag[0] = 1;
                        } else {
                            viewHolder.checkBox_invite.setChecked(false);
                            peoplechatList.remove(artist.f_member_id2);
                            flag[0] = 0;
                        }
                        System.out.println("選取多聊陣列checkBox---" + peoplechatList);
                    }
                });

                viewHolder.ConLatout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (flag[0] == 0) {
                            viewHolder.checkBox_invite.setChecked(true);
                            peoplechatList.add(artist.f_member_id2);
                            flag[0] = 1;
                        } else {
                            viewHolder.checkBox_invite.setChecked(false);
                            peoplechatList.remove(artist.f_member_id2);
                            flag[0] = 0;
                        }
                        System.out.println("選取多聊陣列ConLatout---" + peoplechatList);

                    }
                });

                view.setTag(viewHolder);

            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }


            return view;
        }


        class ViewHolder {
            CheckBox checkBox_invite;
            TextView friends_account;
            CircleImageView friendsImg;
            LinearLayout Frienditem_LinearLayout;
            ConstraintLayout ConLatout;
        }
    }


}
