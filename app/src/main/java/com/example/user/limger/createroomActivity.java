package com.example.user.limger;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.user.limger.Util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class createroomActivity extends AppCompatActivity {
    ActionBar actionBar;
    EditText ct_et_roomname;
    Spinner ct_sp_title, ct_sp_number, ct_sp_sex, ct_sp_time;
    Button ct_bt_detail, ct_bt_cancle, ct_bt_create;
    RadioButton ct_rb_yes, ct_rb_no;
    RadioGroup ct_rg;


    DatabaseReference ct_databaseRef;
    DatabaseReference cM_databaseRef;
    DatabaseReference member_databaseRef;
    FirebaseAuth firebaseAuth;
    String user_uid;
    String m_account;
    String m_head;
    String midwayadd = null;


    String date_time;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    SharedPreferences mpref;
    SharedPreferences.Editor mEditor;

    int sptime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createroom);

        //title字體顏色設定
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFA38473")));
        View viewActionBar = getLayoutInflater().inflate(R.layout.title, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("Limger");
        actionBar.setCustomView(viewActionBar, params);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        ct_databaseRef = FirebaseDatabase.getInstance().getReference("chat_room");
        cM_databaseRef = FirebaseDatabase.getInstance().getReference("chat_room_member");
        member_databaseRef = FirebaseDatabase.getInstance().getReference("member");
        firebaseAuth = FirebaseAuth.getInstance();
        user_uid = firebaseAuth.getCurrentUser().getUid();


        createroom_defin();//創房定義
        ct_radioGrouplistener();//是否中途加入

    }

    public void createroom_defin() {
        ct_et_roomname = findViewById(R.id.ct_et_roomname);
        ct_sp_title = findViewById(R.id.ct_sp_title);
        ct_sp_number = findViewById(R.id.ct_sp_number);
        ct_sp_sex = findViewById(R.id.ct_sp_sex);
        ct_sp_time = findViewById(R.id.ct_sp_time);
        ct_rb_yes = findViewById(R.id.ct_rb_yes);
        ct_rb_no = findViewById(R.id.ct_rb_no);
        ct_rg = findViewById(R.id.ct_rg);
    }

    private void ct_radioGrouplistener() {
        ct_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.ct_rb_yes) {
                    midwayadd = "1";
                } else {
                    midwayadd = "0";
                }
            }
        });
    }


    //顯示menu中的字樣
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.createroom, menu);
        return true;

    }

    //製作返回健和"創立房間"功能
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        boolean flag;
        //noinspection SimplifiableIfStatement
        if (id == R.id.createroom) {
            final String ctrm = ct_et_roomname.getText().toString();
            if (midwayadd == null) {
                Toast.makeText(createroomActivity.this, "請選擇是否中途加入", Toast.LENGTH_SHORT).show();
            } else {
                if (ctrm.isEmpty()) {
                    ct_et_roomname.setError("未輸入");
                } else {
                    if (Utils.isFastClick()) {
                        final String sptit = ct_sp_title.getSelectedItem().toString();
                        final int spnum = Integer.parseInt(ct_sp_number.getSelectedItem().toString());
                        final String spsex;
                        Log.d("ct_sp_sex", "ct_sp_sex" + ct_sp_sex.getSelectedItem().toString());
                        if (ct_sp_sex.getSelectedItem().toString().equals("女")) {
                            spsex = "0";
                        } else if (ct_sp_sex.getSelectedItem().toString().equals("男")) {
                            spsex = "1";
                        } else {
                            spsex = "2";
                        }
                        if(ct_sp_time.getSelectedItem().toString().equals("30s")){
                            sptime = 30;
                        }else if(ct_sp_time.getSelectedItem().toString().equals("1:30s")){
                            sptime = 90;
                        } else{
                            sptime = Integer.parseInt(ct_sp_time.getSelectedItem().toString());
                        }
                        SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String text = simpleFormatter.format(System.currentTimeMillis());
                        String cid = ct_databaseRef.push().getKey();

                        create_firebase cf = new create_firebase(cid, user_uid, ctrm, sptit, 1, spnum, spsex, sptime, midwayadd, "0", text, null);
                        ct_databaseRef.child(cid).setValue(cf);

                        String id2 = cM_databaseRef.push().getKey();
                        chat_room_member_firebase cm = new chat_room_member_firebase(cid, id2, "1", user_uid);
                        cM_databaseRef.child(id2).setValue(cm);


                        Bundle mBan_createkey = new Bundle();
                        mBan_createkey.putString("createkey", cid);
                        mBan_createkey.putString("create_memberkey", id2);

                        Intent intent = new Intent(createroomActivity.this, waitroomActivity.class);
                        intent.putExtras(mBan_createkey);
                        startActivity(intent);
                        finish();
                        Toast.makeText(createroomActivity.this,"創房成功",Toast.LENGTH_LONG).show();
                    }
                }
            }

            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                flag = true;
                break;
            default:
                flag = super.onOptionsItemSelected(item);
                break;
        }
        return flag;
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
    @Override
    protected void onStart() {
        super.onStart();


        member_databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot memberSnapshot : dataSnapshot.getChildren()) {
                    registe_firebase memberData = memberSnapshot.getValue(registe_firebase.class);

                    if (memberData.id.equals(user_uid)) {
                        m_account = memberData.m_account;
                        m_head = memberData.m_head;

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //返回建功能
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
