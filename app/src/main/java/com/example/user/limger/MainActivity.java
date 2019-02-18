package com.example.user.limger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.Date;
import java.util.List;




import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.AlertDialog.THEME_HOLO_LIGHT;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private FragmentManager manager;
    private android.support.v4.app.FragmentTransaction transaction;

    String Title;

    ActionBar actionBar;
    TextView textviewTitle;
    View viewActionBar;
    MenuInflater inflater;
    SearchView searchView;

    AlertDialog dialogs;
    LinearLayout linear_xinxi, linear_shouye, linear_gongju, linner_wo;
    List<create_firebase> createlist;
    chatlist_Fragment fragment1;
    myFriend_Fragment myfriend_fragment;
    Notice_Fragment notice_fragment;
    SigninFragment signin_fragment;
    title_Fragment title_fragment;
    InstructionsForUseFragment instructionsForUseFragment;
    QAFragment qaFragment;

    DatabaseReference total_ct_databaseRef;
    DatabaseReference cM_databaseRef;
    DatabaseReference member_databaseRef, Notice_sender_DataRef, Notice_receiver_DataRef;
    FirebaseAuth firebaseAuth;
    String user_uid, m_deal,m_coin;

    CircleImageView circleImageView;
    NavigationView navigationView;

    SimpleDateFormat simpleDateFormat;
    String Date;
    Calendar calendar;
    List<Notice_receiver_firebase> Not_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





//        jobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        manager = getSupportFragmentManager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFA38473")));
        viewActionBar = getLayoutInflater().inflate(R.layout.title, null);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);

        actionBar.setCustomView(viewActionBar, params);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeButtonEnabled(true);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        total_ct_databaseRef = FirebaseDatabase.getInstance().getReference("chat_room");
        cM_databaseRef = FirebaseDatabase.getInstance().getReference("chat_room_member");
        member_databaseRef = FirebaseDatabase.getInstance().getReference("member");
        Notice_sender_DataRef = FirebaseDatabase.getInstance().getReference().child("Notice_message_sender");
        Notice_receiver_DataRef = FirebaseDatabase.getInstance().getReference().child("Notice_message_receiver");
        firebaseAuth = FirebaseAuth.getInstance();
        user_uid = firebaseAuth.getCurrentUser().getUid();
//
//        txt_num.setVisibility(View.INVISIBLE);
        createlist = new ArrayList<>();
        chatmain_defin();//定義
        singinNotice();
        chatlistView();
        BottomField();//下方欄位

    }

    private void singinNotice() {

        final String first = getSharedPreferences("FIRST", MODE_PRIVATE)
                .getString("firstUse", "first");
        System.out.println("使用first------" + first);
        if (first.equals("first")) {
//            Toast.makeText(MainActivity.this, "你是第一次使用", Toast.LENGTH_SHORT).show();
        } else {
            Notice_receiver_DataRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                        final Notice_receiver_firebase artist = artistSnapshot.getValue(Notice_receiver_firebase.class);
                        if (artist.mes_receiver_member_id.equals(user_uid)) {
                            if (first.equals("SecondupUse")) {

                                SharedPreferences first_pre = getSharedPreferences("FIRST", MODE_PRIVATE);
                                first_pre.edit()
                                        .putString("firstUse", "ThirdupUse")
                                        .commit();
                            } else if (first.equals("ThirdupUse")) {
//                                    Toast.makeText(MainActivity.this, "已發送過簽到通知", Toast.LENGTH_SHORT).show();
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

    private void chatlistView() {
        final Bundle mBun_Title = this.getIntent().getExtras();
        if (mBun_Title != null) {
            Title = mBun_Title.getString("Title");
            textviewTitle.setText("聊天室"+"("+Title+")");
        }else{
            textviewTitle.setText("聊天室");
        }
        transaction = manager.beginTransaction();
        chatlist_Fragment fragment1 = new chatlist_Fragment();
        transaction.replace(R.id.LinearLayout_chatlist, fragment1, "fragment1");
        Bundle bundles = new Bundle();
        bundles.putString("Title", Title);
        fragment1.setArguments(bundles);
        transaction.commit();
    }

    public void chatmain_defin() {

//        txt_num = findViewById(R.id.txt_num);
        linear_xinxi = findViewById(R.id.linear_xinxi);
        linner_wo = findViewById(R.id.linner_wo);
        linear_shouye = findViewById(R.id.linear_shouye);
        linear_gongju = findViewById(R.id.linear_gongju);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //搜尋和創房
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        final Bundle mBun_Title = this.getIntent().getExtras();
        if (mBun_Title != null) {
            Title = mBun_Title.getString("Title");
        }


        MenuItem item = menu.findItem(R.id.action_search);
        MenuItem item2 = menu.findItem(R.id.action_create);
        //創房

        item2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                member_databaseRef.child(user_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("m_deal").getValue().toString().equals("1")){
                            Toast.makeText(MainActivity.this, "處罰中...聊天功能已封鎖", Toast.LENGTH_SHORT).show();
                        }else{
                            Intent intent = new Intent(MainActivity.this, createroomActivity.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return false;
            }
        });

        searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == true) {
                    textviewTitle.setVisibility(View.GONE);
                } else {
                    textviewTitle.setVisibility(View.VISIBLE);
                }
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(final String query) {
                transaction = manager.beginTransaction();
                fragment1 = new chatlist_Fragment();
                transaction.replace(R.id.LinearLayout_chatlist, fragment1, "fragment1");
                Bundle bundle = new Bundle();
                bundle.putString("query", query);
                bundle.putBoolean("checkSearch", true);
                bundle.putString("Title", Title);
                fragment1.setArguments(bundle);

                transaction.commit();

                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {


                return false;
            }
        });

        searchView.setSubmitButtonEnabled(true);

        //查詢叉叉按紐
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                SharedPreferences roomid = getSharedPreferences("RoomStatus", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = roomid.edit();
                editor.putString("bt_rStatus", "undefine");
                editor.apply();

                transaction = manager.beginTransaction();
                fragment1 = new chatlist_Fragment();
                transaction.replace(R.id.LinearLayout_chatlist, fragment1, "fragment1");
                Bundle bundle = new Bundle();
                bundle.putBoolean("checkSearch", false);
                fragment1.setArguments(bundle);
                transaction.commit();

                return false;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //選單各個功能
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.nav_title) {
            searchView.setVisibility(View.VISIBLE);
            SharedPreferences roomid = getSharedPreferences("RoomStatus", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = roomid.edit();
            editor.putString("bt_rStatus", "undefine");
            editor.commit();
            textviewTitle.setText("聊天室");
            transaction = manager.beginTransaction();
            fragment1 = new chatlist_Fragment();
            transaction.replace(R.id.LinearLayout_chatlist, fragment1, "chatList");
            transaction.commit();

        } else if (id == R.id.nav_coin) {
            dialogs = new AlertDialog.Builder(MainActivity.this,THEME_HOLO_LIGHT)
                    .setMessage("目前擁有"+m_coin+"幣")

                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialogs.dismiss();
                        }
                    })
                    .create();

            dialogs.show();
            dialogs.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);


        } else if (id == R.id.nav_member) {

            Intent intent = new Intent(MainActivity.this, memberView.class);
            startActivity(intent);

        } else if (id == R.id.nav_qa) {

            transaction = manager.beginTransaction();
            qaFragment = new QAFragment();
            transaction.replace(R.id.LinearLayout_chatlist, qaFragment, "QA");
            transaction.commit();


        } else if (id == R.id.nav_introduction) {
            transaction = manager.beginTransaction();
            instructionsForUseFragment = new InstructionsForUseFragment();
            transaction.replace(R.id.LinearLayout_chatlist, instructionsForUseFragment, "InstrForUse");
            transaction.commit();

        } else if (id == R.id.nav_aboutus) {
            Intent intent = new Intent(MainActivity.this, Aboutus_Activity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            dialogs = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("登出視窗")
                    .setMessage("確定要登出嗎？")
                    .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialogs.dismiss();
                        }
                    })
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            firebaseAuth.signOut();
                            firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                                @Override
                                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    if (user != null) {
                                        System.out.println("User登入中");
                                    } else {
                                        System.out.println("User已登出");
                                        Intent intent = new Intent(MainActivity.this, Login.class);
                                        startActivity(intent);
                                        finish();
                                        Toast.makeText(MainActivity.this,"登出成功",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    })
                    .create();

            dialogs.show();
            dialogs.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
            dialogs.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.GRAY);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //下方欄位
    private void BottomField() {
        //主題版清單
        linear_xinxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences roomid = getSharedPreferences("RoomStatus", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = roomid.edit();
                editor.putString("bt_rStatus", "undefine");
                editor.commit();

                searchView.setVisibility(View.VISIBLE);
                textviewTitle.setText("主題版");
                transaction = manager.beginTransaction();
                title_Fragment title_fragment = new title_Fragment();
                transaction.replace(R.id.LinearLayout_chatlist, title_fragment, "Title");
                transaction.commit();


            }
        });

        linner_wo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setVisibility(View.GONE);
                textviewTitle.setText("每日簽到");
                transaction = manager.beginTransaction();
                signin_fragment = new SigninFragment();
                transaction.replace(R.id.LinearLayout_chatlist, signin_fragment, "Signin");
                transaction.commit();
            }
        });
        Notice_receiver_DataRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                txt_num.setVisibility(View.VISIBLE);
                Not_list= new ArrayList<>();
                final Notice_receiver_firebase notices_re = dataSnapshot.getValue(Notice_receiver_firebase.class);
                if(notices_re.mes_receiver_member_id.equals(user_uid)){
                    Not_list.add(notices_re);
                }
//                txt_num.setText(String.valueOf(Not_list.size()));


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

        //通知列表
        linear_gongju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                txt_num.setVisibility(View.INVISIBLE);
                searchView.setVisibility(View.GONE);
                textviewTitle.setText("通知");
                transaction = manager.beginTransaction();
                notice_fragment = new Notice_Fragment();
                transaction.replace(R.id.LinearLayout_chatlist, notice_fragment, "Notic");
                transaction.commit();
            }
        });


        //我的好友
        linear_shouye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchView.setVisibility(View.GONE);
                textviewTitle.setText("Friend圈");
                transaction = manager.beginTransaction();
                myfriend_fragment = new myFriend_Fragment();
                transaction.replace(R.id.LinearLayout_chatlist, myfriend_fragment, "Freind");
                transaction.commit();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        member_databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot memberSnapshot : dataSnapshot.getChildren()) {
                    registe_firebase memberData = memberSnapshot.getValue(registe_firebase.class);
                    if (memberData.id.equals(user_uid)) {

                        View headview = navigationView.getHeaderView(0);

                        final TextView txt_account = headview.findViewById(R.id.txt_school);
                        final TextView txt_email = headview.findViewById(R.id.txt_email);
                        m_deal = memberData.m_deal;
                        circleImageView = headview.findViewById(R.id.profile_image2);
                        Picasso.get().load(memberData.m_head).into(circleImageView);

                        txt_account.setText(memberData.m_account);
                        txt_email.setText(memberData.m_email);
                        Menu menu = navigationView.getMenu();
                        MenuItem nav_coin = menu.findItem(R.id.nav_coin);
                        nav_coin.setTitle(String.valueOf(memberData.m_coin));
                        m_coin = String.valueOf(memberData.m_coin);
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

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("確認視窗")
                    .setMessage("確定要結束應用程式嗎?")
                    .setIcon(R.drawable.warning1)
                    .setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    SharedPreferences roomid =getSharedPreferences("RoomStatus", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = roomid.edit();
                                    editor.putInt("firstSearch", 1);
                                    editor.commit();

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

}
