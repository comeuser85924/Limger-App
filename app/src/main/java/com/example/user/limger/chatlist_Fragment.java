package com.example.user.limger;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class chatlist_Fragment extends Fragment {


    String Title;
    Boolean checkSearch = false;
    String bt_rStatus;
    String query;
    int firstSearch;

    List<create_firebase> createlist;
    ListView chatmain_listview1;
    DatabaseReference total_ct_databaseRef, cM_databaseRef, member_databaseRef;
    FirebaseAuth firebaseAuth;
    Button bt_All, bt_wait, bt_process;

    String c_status;//房間狀態 (等待中:0、進行中:1、結束:2、被迫結束:3)
    String c_join;//是否中途加入(否:0、是:1)
    int persum; //房內人數
    int population; //總人數
    String room_gender;//創房性別限制
    String m_deal;//處罰狀態

    final int[] m_coin = {0};
    String user_uid, m_head, m_account, m_gender;
    int i = 0, x = 0;
    create_firebase ct;
    long millionSeconds, millionSeconds2;

    public chatlist_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        firstSearch = getContext().getSharedPreferences("RoomStatus", Context.MODE_PRIVATE).getInt("firstSearch", 1);
        System.out.println("firstSearch---" + firstSearch);
        if (firstSearch == 1) {
            firstSearch += 1;
            SharedPreferences roomid = getContext().getSharedPreferences("RoomStatus", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = roomid.edit();
            editor.putInt("firstSearch", firstSearch);
            editor.putString("bt_rStatus", "undefine");
            editor.commit();


        }

        View view = inflater.inflate(R.layout.fragment_chatlist, container, false);
        // Inflate the layout for this fragment
        chatmain_listview1 = view.findViewById(R.id.chatmain_listview1);
        bt_All = view.findViewById(R.id.chatlist_bt_All);
        bt_wait = view.findViewById(R.id.chatlist_bt_wait);
        bt_process = view.findViewById(R.id.chatlist_bt_process);


        total_ct_databaseRef = FirebaseDatabase.getInstance().getReference("chat_room");
        cM_databaseRef = FirebaseDatabase.getInstance().getReference("chat_room_member");
        member_databaseRef = FirebaseDatabase.getInstance().getReference("member");
        firebaseAuth = FirebaseAuth.getInstance();
        user_uid = firebaseAuth.getCurrentUser().getUid();
        createlist = new ArrayList<>();


        Bundle bundle = getArguments();
        if (bundle != null) {
            Title = bundle.getString("Title");
            query = bundle.getString("query");
            checkSearch = bundle.getBoolean("checkSearch");

            if (checkSearch == null) {
                checkSearch = false;
            }


        }


        member_databaseRef.child(user_uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        m_account = dataSnapshot.child("m_account").getValue().toString();
                        m_head = dataSnapshot.child("m_head").getValue().toString();
                        m_coin[0] = Integer.parseInt(dataSnapshot.child("m_coin").getValue().toString());
                        m_gender = dataSnapshot.child("m_gender").getValue().toString();
                        m_deal = dataSnapshot.child("m_deal").getValue().toString();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        bt_rStatus = getContext().getSharedPreferences("RoomStatus", Context.MODE_PRIVATE).getString("bt_rStatus", "undefine");
        if (bt_rStatus.equals("All")) {
            bt_All.setBackgroundColor(Color.parseColor("#FF906A54"));

            bt_wait.setBackgroundColor(Color.parseColor("#FFA38473"));
            bt_process.setBackgroundColor(Color.parseColor("#FFA38473"));
        } else if (bt_rStatus.equals("wait")) {
            bt_wait.setBackgroundColor(Color.parseColor("#FF906A54"));

            bt_All.setBackgroundColor(Color.parseColor("#FFA38473"));
            bt_process.setBackgroundColor(Color.parseColor("#FFA38473"));
        } else if (bt_rStatus.equals("process")) {
            bt_process.setBackgroundColor(Color.parseColor("#FF906A54"));

            bt_wait.setBackgroundColor(Color.parseColor("#FFA38473"));
            bt_All.setBackgroundColor(Color.parseColor("#FFA38473"));
        } else {
            bt_process.setBackgroundColor(Color.parseColor("#FFA38473"));
            bt_wait.setBackgroundColor(Color.parseColor("#FFA38473"));
            bt_All.setBackgroundColor(Color.parseColor("#FFA38473"));
        }

        bt_All.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences roomid = getContext().getSharedPreferences("RoomStatus", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = roomid.edit();
                editor.putString("bt_rStatus", "All");
                editor.commit();
                bt_All.setBackgroundColor(Color.parseColor("#FF906A54"));

                bt_wait.setBackgroundColor(Color.parseColor("#FFA38473"));
                bt_process.setBackgroundColor(Color.parseColor("#FFA38473"));
                Allroom();
            }
        });
        bt_wait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences roomid = getContext().getSharedPreferences("RoomStatus", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = roomid.edit();
                editor.putString("bt_rStatus", "wait");
                editor.commit();
                bt_wait.setBackgroundColor(Color.parseColor("#FF906A54"));

                bt_All.setBackgroundColor(Color.parseColor("#FFA38473"));
                bt_process.setBackgroundColor(Color.parseColor("#FFA38473"));
                Waitroon();
            }
        });
        bt_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences roomid = getContext().getSharedPreferences("RoomStatus", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = roomid.edit();
                editor.putString("bt_rStatus", "process");
                editor.commit();
                bt_process.setBackgroundColor(Color.parseColor("#FF906A54"));

                bt_wait.setBackgroundColor(Color.parseColor("#FFA38473"));
                bt_All.setBackgroundColor(Color.parseColor("#FFA38473"));
                Processroom();
            }
        });

        total_ct_databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                createlist.clear();
                for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                    create_firebase artist = artistSnapshot.getValue(create_firebase.class);
                    c_status = artist.c_status;
                    c_join = artist.c_join;
                    room_gender = artist.c_gender;
                    population = artist.getc_population();
                    String c_title = artist.c_title;


                    //表示沒有按搜尋鈕 或 按了搜尋叉叉鈕
                    if (checkSearch == false) {

                        //按了搜尋叉叉鈕
                        if (bt_rStatus != null) {
                            if (artist.c_status.equals("0") || artist.c_status.equals("1")) {
                                //如果已有選擇主題版搜尋
                                if (Title != null) {
                                    //只取出已選擇的主題房間(綜合房間的情況下)
                                    if (c_title.equals(Title)) {
                                        createlist.add(artist);
                                    }
                                } else {
                                    createlist.add(artist);
                                }
                            } else {
                                createlist.remove(artist);
                            }
                        }
                        //沒有按搜尋鈕(一般正常情況下)
                        else {
                            //綜合房間
                            if (artist.c_status.equals("0") || artist.c_status.equals("1")) {
                                //如果已有選擇主題版搜尋
                                if (Title != null) {
                                    //只取出已選擇的主題房間(綜合房間的情況下)
                                    if (c_title.equals(Title)) {
                                        createlist.add(artist);
                                    }
                                } else {
                                    createlist.add(artist);
                                }
                            } else {
                                createlist.remove(artist);
                            }
                        }

                    }
                    //表示已經按搜尋鈕 控制搜尋按鈕與前一次觸發房間狀態按鈕
                    else {

                        if (checkSearch == true) {
                            //區分全部、等待、進行
                            if (artist.c_roomname.contains(query)) {
                                if (bt_rStatus != null) {
                                    if (bt_rStatus.equals("All") || bt_rStatus.equals("undefine")) {
                                        if (artist.c_status.equals("0") || artist.c_status.equals("1")) {
                                            if (Title != null) {
                                                if (c_title.equals(Title)) {
                                                    createlist.add(artist);
                                                }
                                            } else {
                                                createlist.add(artist);
                                            }
                                        } else {
                                            createlist.remove(artist);
                                        }
                                    } else if (bt_rStatus.equals("wait")) {
                                        if (artist.c_status.equals("0")) {
                                            if (Title != null) {
                                                if (c_title.equals(Title)) {
                                                    createlist.add(artist);
                                                }
                                            } else {
                                                createlist.add(artist);
                                            }
                                        } else {
                                            createlist.remove(artist);
                                        }
                                    } else if (bt_rStatus.equals("process")) {
                                        if (artist.c_status.equals("1")) {
                                            if (Title != null) {
                                                if (c_title.equals(Title)) {
                                                    createlist.add(artist);
                                                }
                                            } else {
                                                createlist.add(artist);
                                            }
                                        } else {
                                            createlist.remove(artist);
                                        }
                                    }
                                }

                            }
                        }
                    }
                }

                if (getContext() != null) {
                    createListSort();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        chatmain_listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ct = createlist.get(position);

                System.out.println("會員狀態----" + m_deal);
                if (m_deal.equals("1")) {
                    Toast.makeText(getContext(), "處罰中...聊天功能已封鎖", Toast.LENGTH_SHORT).show();
                } else {
                    //房間人數
                    if (population > ct.c_personnum) {
                        //房間性別限制為無 =>2
                        if (ct.c_gender.equals("2")) {
                            //房間等待中
                            if (c_status.equals("0")) {
                                //L幣
                                if (m_coin[0] > 3) {
                                    if (Utils.isFastClick()) {
                                        final String id2 = cM_databaseRef.push().getKey();
                                        final chat_room_member_firebase cm = new chat_room_member_firebase(ct.c_id, id2, "1", user_uid);


                                        if (cm.chat_room_member_id.equals(id2)) {
                                            cM_databaseRef.child(cm.chat_room_member_id).setValue(cm);
                                        } else {
                                            cM_databaseRef.child(id2).setValue(cm);
                                        }

                                        persum = ct.c_personnum + 1;
                                        total_ct_databaseRef.child(ct.c_id).child("c_personnum").setValue(persum);

                                        //帳號跟房間key包起來傳waitroom
                                        Bundle mBan_accountkey = new Bundle();
                                        mBan_accountkey.putString("key", ct.c_id);
                                        mBan_accountkey.putString("Chat_Main_memberkey", id2);

                                        Intent intent = new Intent(getContext(), waitroomActivity.class);
                                        intent.putExtras(mBan_accountkey);
                                        startActivity(intent);
                                    }
                                } else {
                                    Toast.makeText(getContext(), "L幣不足，記得每日簽到賺L幣唷~", Toast.LENGTH_SHORT).show();
                                }

                                //房間進行中的時候
                            } else {
                                //房間設定可中途加入
                                if (c_join.equals("1")) {
                                    if (m_coin[0] > 3) {
                                        if (Utils.isFastClick()) {
                                            final String id2 = cM_databaseRef.push().getKey();
                                            final chat_room_member_firebase cm = new chat_room_member_firebase(ct.c_id, id2, "1", user_uid);
                                            if (cm.chat_room_member_id.equals(id2)) {
                                                cM_databaseRef.child(cm.chat_room_member_id).setValue(cm);
                                            } else {
                                                cM_databaseRef.child(id2).setValue(cm);
                                            }

                                            persum = ct.c_personnum + 1;
                                            total_ct_databaseRef.child(ct.c_id).child("c_personnum").setValue(persum);
                                            //帳號跟房間key包起來傳chatroom
                                            Bundle mBan_accountkey = new Bundle();
                                            mBan_accountkey.putString("jkey", ct.c_id); //取得房間keyid值
                                            mBan_accountkey.putString("Chat_Main_memberkey", id2);  //取得建立member的keyid值
                                            mBan_accountkey.putString("member_account", m_account);
                                            Intent intent = new Intent(getContext(), chatActivity.class); //直接進到聊天室
                                            intent.putExtras(mBan_accountkey);
                                            startActivity(intent);
                                        }
                                    } else {
                                        Toast.makeText(getContext(), "L幣不足，記得每日簽到賺L幣唷~", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(getContext(), "房間進行中，未開放中途加入", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            //房間性別設定為女性=>0
                            if (ct.c_gender.equals(m_gender)) {
                                //房間等待中
                                if (c_status.equals("0")) {
                                    //L幣
                                    if (m_coin[0] > 3) {
                                        if (Utils.isFastClick()) {
                                            final String id2 = cM_databaseRef.push().getKey();
                                            final chat_room_member_firebase cm = new chat_room_member_firebase(ct.c_id, id2, "1", user_uid);
                                            if (cm.chat_room_member_id.equals(id2)) {
                                                cM_databaseRef.child(cm.chat_room_member_id).setValue(cm);
                                            } else {
                                                cM_databaseRef.child(id2).setValue(cm);
                                            }

                                            persum = ct.c_personnum + 1;
                                            total_ct_databaseRef.child(ct.c_id).child("c_personnum").setValue(persum);
                                            //帳號跟房間key包起來傳waitroom
                                            Bundle mBan_accountkey = new Bundle();
                                            mBan_accountkey.putString("key", ct.c_id);
                                            mBan_accountkey.putString("Chat_Main_memberkey", id2);

                                            Intent intent = new Intent(getContext(), waitroomActivity.class);
                                            intent.putExtras(mBan_accountkey);
                                            startActivity(intent);
                                        }
                                    } else {
                                        Toast.makeText(getContext(), "L幣不足，記得每日簽到賺L幣唷~", Toast.LENGTH_SHORT).show();
                                    }

                                    //房間進行中的時候
                                } else {
                                    //房間設定可中途加入
                                    if (c_join.equals("1")) {
                                        if (m_coin[0] > 3) {
                                            if (Utils.isFastClick()) {
                                                final String id2 = cM_databaseRef.push().getKey();
                                                final chat_room_member_firebase cm = new chat_room_member_firebase(ct.c_id, id2, "1", user_uid);
                                                if (cm.chat_room_member_id.equals(id2)) {
                                                    cM_databaseRef.child(cm.chat_room_member_id).setValue(cm);
                                                } else {
                                                    cM_databaseRef.child(id2).setValue(cm);
                                                }

                                                persum = ct.c_personnum + 1;
                                                total_ct_databaseRef.child(ct.c_id).child("c_personnum").setValue(persum);
                                                //帳號跟房間key包起來傳chatroom
                                                Bundle mBan_accountkey = new Bundle();
                                                mBan_accountkey.putString("jkey", ct.c_id); //取得房間keyid值
                                                mBan_accountkey.putString("Chat_Main_memberkey", id2);  //取得建立member的keyid值
                                                mBan_accountkey.putString("member_account", m_account);
                                                Intent intent = new Intent(getContext(), chatActivity.class); //直接進到聊天室
                                                intent.putExtras(mBan_accountkey);
                                                startActivity(intent);
                                            }
                                        } else {
                                            Toast.makeText(getContext(), "L幣不足，記得每日簽到賺L幣唷~", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {

                                        Toast.makeText(getContext(), "房間進行中，未開放中途加入", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                if (ct.c_gender.equals("0")) {
                                    Toast.makeText(getContext(), "此房限女性", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "此房限男性", Toast.LENGTH_SHORT).show();
                                }

                            }

                        }
                    } else {
                        Toast.makeText(getContext(), "房間已滿", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        return view;
    }

    public static class Utils {
        // 两次点击按钮之间的点击间隔不能少于1000毫秒
        private static final int MIN_CLICK_DELAY_TIME = 1000;
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

    private void createListSort() {
        Collections.sort(createlist, new Comparator<create_firebase>() {
            @Override
            public int compare(create_firebase o1, create_firebase o2) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                try {

                    millionSeconds = sdf.parse(o1.c_stime).getTime();
                    millionSeconds2 = sdf.parse(o2.c_stime).getTime();
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
        for (int i = 0; i < createlist.size(); i++) {
            System.out.println("房間陣列---" + createlist.get(i).c_roomname);
        }
        createList adapter = new createList(getContext(), createlist);
        chatmain_listview1.setAdapter(adapter);
    }

    private void Allroom() {
        total_ct_databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                createlist.clear();
//                bt_rStatus = getContext().getSharedPreferences("RoomStatus", Context.MODE_PRIVATE).getString("bt_rStatus", "undefine");
//                System.out.println("bt_rStatus按鈕狀態-----" + bt_rStatus);
                for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                    create_firebase artist = artistSnapshot.getValue(create_firebase.class);
                    if (checkSearch == false) {
                        if (artist.c_status.equals("0") || artist.c_status.equals("1")) {
                            if (Title != null) {
                                if (artist.c_title.equals(Title)) {
                                    createlist.add(artist);
                                }
                            } else {
                                createlist.add(artist);
                            }
                        } else {
                            createlist.remove(artist);
                        }
                    } else {
                        if (artist.c_roomname.contains(query)) {
//                            if(bt_rStatus.equals("All")) {
                            if (artist.c_status.equals("0") || artist.c_status.equals("1")) {
                                if (Title != null) {
                                    if (artist.c_title.equals(Title)) {
                                        createlist.add(artist);
                                    }
                                } else {
                                    createlist.add(artist);
                                }
                            } else {
                                createlist.remove(artist);
                            }
//                            }
                        }
                    }
                }

                if (getContext() != null) {
                    createListSort();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //
    private void Waitroon() {
        total_ct_databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                createlist.clear();
//                bt_rStatus = getContext().getSharedPreferences("RoomStatus", Context.MODE_PRIVATE).getString("bt_rStatus", "undefine");
//                System.out.println("bt_rStatus按鈕狀態-----" + bt_rStatus);
                for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                    create_firebase artist = artistSnapshot.getValue(create_firebase.class);
                    if (checkSearch == false) {
                        if (artist.c_status.equals("0")) {
                            if (Title != null) {
                                if (artist.c_title.equals(Title)) {
                                    createlist.add(artist);
                                }
                            } else {
                                createlist.add(artist);
                            }
                        } else {
                            createlist.remove(artist);
                        }
                    } else {
                        if (artist.c_roomname.contains(query)) {
//                            if(bt_rStatus.equals("wait")) {
                            if (artist.c_status.equals("0")) {
                                if (Title != null) {
                                    if (artist.c_title.equals(Title)) {
                                        createlist.add(artist);
                                    }
                                } else {
                                    createlist.add(artist);
                                }
                            } else {
                                createlist.remove(artist);
                            }
//                            }
                        }
                    }


                }

                if (getContext() != null) {
                    createListSort();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void Processroom() {
        total_ct_databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                createlist.clear();
//                bt_rStatus = getContext().getSharedPreferences("RoomStatus", Context.MODE_PRIVATE).getString("bt_rStatus", "undefine");
//                System.out.println("bt_rStatus按鈕狀態-----" + bt_rStatus);
                for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                    create_firebase artist = artistSnapshot.getValue(create_firebase.class);
                    if (checkSearch == false) {
                        if (artist.c_status.equals("1")) {
                            if (Title != null) {
                                if (artist.c_title.equals(Title)) {
                                    createlist.add(artist);
                                }
                            } else {
                                createlist.add(artist);
                            }
                        } else {
                            createlist.remove(artist);
                        }
                    } else {
                        if (artist.c_status.equals("1")) {
                            if (artist.c_roomname.contains(query)) {
//                            if(bt_rStatus.equals("process")) {
                                System.out.println("房間---" + artist.c_roomname);
                                if (Title != null) {
                                    if (artist.c_title.equals(Title)) {
                                        createlist.add(artist);
                                    }
                                } else {
                                    createlist.add(artist);
                                }
                            }
//                            }
                        } else {
                            createlist.remove(artist);
                        }
                    }

                }
                if (getContext() != null) {
                    createListSort();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
