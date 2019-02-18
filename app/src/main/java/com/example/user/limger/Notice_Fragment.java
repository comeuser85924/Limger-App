package com.example.user.limger;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

/**
 * Created by User on 2018/7/13.
 */

public class Notice_Fragment extends Fragment {
    private FragmentManager manager;
    private android.support.v4.app.FragmentTransaction transaction;
    NoticeTypeFragment noticeTypeFragment;

    TextView txt_noNotice;
    ListView noticlistview;
    DatabaseReference member_dataRef, Notice_sender_dataRef, Notice_receiver_dataRef, Private_chat_dataRef, Private_chat_room_member_dataRef, Friend_dataRef;
    FirebaseAuth firebaseAuth;
    String myaccount, user_uid;
    int sum = 0;
    long millionSeconds, millionSeconds2;
    String f_bstatus_memberID;
    ProgressBar progressBar3;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragemt_noticelist, container, false);

        progressBar3 = view.findViewById(R.id.progressBar3);

        noticlistview = view.findViewById(R.id.notice_Listview);
        txt_noNotice = view.findViewById(R.id.txt_noNotice);
        member_dataRef = FirebaseDatabase.getInstance().getReference("member");
        Notice_sender_dataRef = FirebaseDatabase.getInstance().getReference("Notice_message_sender");
        Notice_receiver_dataRef = FirebaseDatabase.getInstance().getReference("Notice_message_receiver");
        Private_chat_dataRef = FirebaseDatabase.getInstance().getReference("private_chat_room");
        Private_chat_room_member_dataRef = FirebaseDatabase.getInstance().getReference("private_chat_room_member");
        Friend_dataRef = FirebaseDatabase.getInstance().getReference("Friend");
        firebaseAuth = FirebaseAuth.getInstance();
        user_uid = firebaseAuth.getCurrentUser().getUid();



        member_dataRef
                .child(user_uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        myaccount = dataSnapshot.child("m_account").getValue().toString();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        Notice_sender_dataRef
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        noticlistview.setVisibility(View.GONE);
                        progressBar3.setVisibility(View.VISIBLE);
                        final List<Notice_sender_firebase> Not_list= new ArrayList<>();
                        for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {

                            final Notice_sender_firebase notices = artistSnapshot.getValue(Notice_sender_firebase.class);
                            Notice_receiver_dataRef
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                                                final Notice_receiver_firebase notices_re = artistSnapshot.getValue(Notice_receiver_firebase.class);
                                                //先區分使用者
                                                if (notices_re.mes_receiver_member_id.equals(user_uid)) {
                                                    sum += 1;
                                                    //區分通知狀態
                                                    if (notices_re.mes_sender_id.equals(notices.mes_sender_id)) {

                                                        Friend_dataRef.child(user_uid).addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                for (DataSnapshot freSnapshot : dataSnapshot.getChildren()) {
                                                                    Friend_firebase friendData = freSnapshot.getValue(Friend_firebase.class);
                                                                    if (friendData.f_bstatus.equals("1")) {
                                                                        f_bstatus_memberID=friendData.f_member_id2;
                                                                        if(notices.mes_sender_member_id.equals(friendData.f_member_id2)){

                                                                            Not_list.remove(notices);
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });
                                                        if(notices.mes_sender_member_id.equals(f_bstatus_memberID)){
                                                            Not_list.remove(notices);
                                                        }else{
                                                            Not_list.add(notices);
                                                        }
                                                        if (getContext() != null) {
                                                            Collections.sort(Not_list, new Comparator<Notice_sender_firebase>() {
                                                                @Override
                                                                public int compare(Notice_sender_firebase o1, Notice_sender_firebase o2) {
                                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                                                    try {

                                                                        millionSeconds = sdf.parse(o1.mes_sender_time).getTime();
                                                                        millionSeconds2 = sdf.parse(o2.mes_sender_time).getTime();
                                                                    } catch (ParseException e) {
                                                                        // TODO Auto-generated catch block
                                                                        e.printStackTrace();
                                                                    }//毫秒
                                                                    if (millionSeconds > millionSeconds2) {
                                                                        return -1;
                                                                    } else {
                                                                        if (millionSeconds < millionSeconds2) {
                                                                            return 1;
                                                                        } else {
                                                                            return 0;
                                                                        }
                                                                    }
                                                                }
                                                            });


                                                            final NoticeList adapter = new NoticeList(getContext(), Not_list);
                                                            noticlistview.setAdapter(adapter);


                                                            noticlistview
                                                                    .setOnItemClickListener
                                                                            (new AdapterView.OnItemClickListener() {
                                                                                @Override
                                                                                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                                                                                    if (Not_list.get(position).mes_sender_type.equals("0") ||
                                                                                            Not_list.get(position).mes_sender_type.equals("1") ||
                                                                                            Not_list.get(position).mes_sender_type.equals("2") ||
                                                                                            Not_list.get(position).mes_sender_type.equals("3") ||
                                                                                            Not_list.get(position).mes_sender_type.equals("4") ||
                                                                                            Not_list.get(position).mes_sender_type.equals("5") ||
                                                                                            Not_list.get(position).mes_sender_type.equals("6") ||
                                                                                            Not_list.get(position).mes_sender_type.equals("7") ||
                                                                                            Not_list.get(position).mes_sender_type.equals("8") ||
                                                                                            Not_list.get(position).mes_sender_type.equals("9") ||
                                                                                            Not_list.get(position).mes_sender_type.equals("10")) {

                                                                                        manager = getActivity().getSupportFragmentManager();
                                                                                        transaction = manager.beginTransaction();
                                                                                        noticeTypeFragment = new NoticeTypeFragment();

                                                                                        transaction.replace(R.id.LinearLayout_chatlist, noticeTypeFragment, "noticeType");
                                                                                        Bundle bundle = new Bundle();
                                                                                        bundle.putString("NoticeDataID", Not_list.get(position).mes_sender_id);
                                                                                        noticeTypeFragment.setArguments(bundle);
                                                                                        transaction.commit();

                                                                                    }
//
                                                                                }
                                                                            });
                                                        }
                                                    }
                                                }
                                            }
                                            if (Not_list.size() == 0) {
                                                txt_noNotice.setVisibility(View.VISIBLE);
                                            }else{
                                                txt_noNotice.setVisibility(View.GONE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                        }
                        progressBar3.setVisibility(View.GONE);
                        noticlistview.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        return view;
    }

    class NoticeList extends ArrayAdapter<Notice_sender_firebase> {

        private List<Notice_sender_firebase> Notice_senderList;

        public NoticeList(@NonNull Context context, List<Notice_sender_firebase> Notice_senderList) {
            super(context, R.layout.layout_notice_item, Notice_senderList);
            this.Notice_senderList = Notice_senderList;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final View view;
            final ViewHolder viewHolder;


            if (convertView == null) {

                view = LayoutInflater.from(getContext()).inflate(R.layout.layout_notice_item, null);
                viewHolder = new ViewHolder();
                viewHolder.Type = view.findViewById(R.id.notice_type);
                viewHolder.Title = view.findViewById(R.id.notice_tit);
                viewHolder.Content = view.findViewById(R.id.notice_con);
                viewHolder.Time = view.findViewById(R.id.notice_time);

                view.setTag(viewHolder);

            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
                view.setTag(viewHolder);
            }
            final Notice_sender_firebase artist = Notice_senderList.get(position);

            Notice_sender_dataRef
                    .child(artist.mes_sender_id)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("mes_sender_type").getValue().toString().equals("0")) {
                                viewHolder.Type.setText("一般通知");
                                viewHolder.Title.setText(dataSnapshot.child("mes_sender_title").getValue().toString());
                                viewHolder.Content.setText(dataSnapshot.child("mes_sender_content").getValue().toString());
                                viewHolder.Time.setText(dataSnapshot.child("mes_sender_time").getValue().toString());
                            } else if (dataSnapshot.child("mes_sender_type").getValue().toString().equals("1")) {
                                viewHolder.Type.setText("配對通知");
                                viewHolder.Title.setText(dataSnapshot.child("mes_sender_title").getValue().toString());
                                viewHolder.Content.setText(dataSnapshot.child("mes_sender_content").getValue().toString());
                                viewHolder.Time.setText(dataSnapshot.child("mes_sender_time").getValue().toString());
                            } else if (dataSnapshot.child("mes_sender_type").getValue().toString().equals("2") ||
                                    dataSnapshot.child("mes_sender_type").getValue().toString().equals("7")) {
                                viewHolder.Type.setText("私聊通知");
                                viewHolder.Title.setText(dataSnapshot.child("mes_sender_title").getValue().toString());
                                viewHolder.Content.setText(dataSnapshot.child("mes_sender_content").getValue().toString());
                                viewHolder.Time.setText(dataSnapshot.child("mes_sender_time").getValue().toString());
                            } else if (dataSnapshot.child("mes_sender_type").getValue().toString().equals("3") ||
                                    dataSnapshot.child("mes_sender_type").getValue().toString().equals("8")) {
                                viewHolder.Type.setText("多人聊天");
                                viewHolder.Title.setText(dataSnapshot.child("mes_sender_title").getValue().toString());
                                viewHolder.Content.setText(dataSnapshot.child("mes_sender_content").getValue().toString());
                                viewHolder.Time.setText(dataSnapshot.child("mes_sender_time").getValue().toString());
                            } else if (dataSnapshot.child("mes_sender_type").getValue().toString().equals("4")) {
                                if(dataSnapshot.child("mes_sender_title").getValue().toString().equals("封鎖錯誤通知")){
                                    viewHolder.Type.setText("封鎖錯誤");
                                    viewHolder.Title.setText(dataSnapshot.child("mes_sender_title").getValue().toString());
                                    viewHolder.Content.setText(dataSnapshot.child("mes_sender_content").getValue().toString());
                                    viewHolder.Time.setText(dataSnapshot.child("mes_sender_time").getValue().toString());
                                }else{
                                    viewHolder.Type.setText("檢舉通知");
                                    viewHolder.Title.setText(dataSnapshot.child("mes_sender_title").getValue().toString());
                                    viewHolder.Content.setText(dataSnapshot.child("mes_sender_content").getValue().toString());
                                    viewHolder.Time.setText(dataSnapshot.child("mes_sender_time").getValue().toString());
                                }

                            } else if (dataSnapshot.child("mes_sender_type").getValue().toString().equals("5") ||
                                    dataSnapshot.child("mes_sender_type").getValue().toString().equals("9")) {
                                viewHolder.Type.setText("簽到通知");
                                viewHolder.Title.setText(dataSnapshot.child("mes_sender_title").getValue().toString());
                                viewHolder.Content.setText(dataSnapshot.child("mes_sender_content").getValue().toString());
                                viewHolder.Time.setText(dataSnapshot.child("mes_sender_time").getValue().toString());
                            } else if (dataSnapshot.child("mes_sender_type").getValue().toString().equals("6")) {
                                viewHolder.Type.setText("活動通知");
                                viewHolder.Title.setText(dataSnapshot.child("mes_sender_title").getValue().toString());
                                viewHolder.Content.setText(dataSnapshot.child("mes_sender_content").getValue().toString());
                                viewHolder.Time.setText(dataSnapshot.child("mes_sender_time").getValue().toString());
                            } else if (dataSnapshot.child("mes_sender_type").getValue().toString().equals("10")) {
                                viewHolder.Type.setText("強制關閉");
                                viewHolder.Title.setText(dataSnapshot.child("mes_sender_title").getValue().toString());
                                viewHolder.Content.setText(dataSnapshot.child("mes_sender_content").getValue().toString());
                                viewHolder.Time.setText(dataSnapshot.child("mes_sender_time").getValue().toString());
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            return view;
        }
    }

    static class ViewHolder {

        TextView Type, Title, Content, Time;

    }

}
