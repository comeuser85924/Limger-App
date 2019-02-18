package com.example.user.limger;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;

public class memberView extends AppCompatActivity {
    ActionBar actionBar;
    TextView txt_memV_name, txt_memV_nickname, txt_memV_account, txt_memV_password, txt_memV_gender,
            txt_memV_email, txt_memV_tel, txt_memV_school, txt_memV_subject, txt_memV_birth, txt_memV_interest;

    MenuInflater inflater;

    CircleImageView circleImageView;
    DatabaseReference memberviewRef;
    FirebaseAuth firebaseAuth;
    String user_uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_view);

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

        memberviewRef = FirebaseDatabase.getInstance().getReference("member");
        firebaseAuth = FirebaseAuth.getInstance();
        memView_idefin();


    }

    //顯示menu中的字樣
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater = getMenuInflater();
        inflater.inflate(R.menu.member, menu);
        return true;
    }

    //製作返回健和修改功能
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        boolean flag = false;
        //noinspection SimplifiableIfStatement
        if (id == R.id.member_modify) {


            Intent intent = new Intent(memberView.this, membermodify.class);
            startActivity(intent);

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

    public void memView_idefin() {
        txt_memV_name = findViewById(R.id.txt_memV_name);
        txt_memV_nickname = findViewById(R.id.txt_memV_nickname);
        txt_memV_account = findViewById(R.id.txt_memV_account);
        txt_memV_password = findViewById(R.id.txt_memV_password);
        txt_memV_gender = findViewById(R.id.txt_memV_gender);
        txt_memV_email = findViewById(R.id.txt_memV_email);
        txt_memV_tel = findViewById(R.id.txt_memV_tel);
        txt_memV_school = findViewById(R.id.txt_memV_school);
        txt_memV_subject = findViewById(R.id.txt_memV_subject);
        txt_memV_birth = findViewById(R.id.txt_memV_birth);
        txt_memV_interest = findViewById(R.id.txt_memV_interest);


        circleImageView =findViewById(R.id.profile_image);

    }

    @Override
    protected void onStart() {
        super.onStart();

        user_uid = firebaseAuth.getCurrentUser().getUid();
        memberviewRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot memberSnapshot : dataSnapshot.getChildren()) {
                    registe_firebase memberData = memberSnapshot.getValue(registe_firebase.class);
                    if (memberData.id.equals(user_uid)) {
                        System.out.println("性別test-------"+memberData.m_gender);
                        txt_memV_account.setText(memberData.m_account);
                        txt_memV_password.setText(memberData.m_pw);
                        txt_memV_name.setText(memberData.m_name);
                        txt_memV_nickname.setText(memberData.m_nick);
                        txt_memV_birth.setText(memberData.m_birth);
                        txt_memV_email.setText(memberData.m_email);
                        txt_memV_tel.setText(memberData.m_tel);
                        txt_memV_school.setText(memberData.m_school);
                        txt_memV_subject.setText(memberData.m_subject);
                        txt_memV_interest.setText(memberData.m_interest);
                        if (memberData.m_gender.equals("0")) {
                            txt_memV_gender.setText("女");
                        } else if(memberData.m_gender.equals("1")){
                            txt_memV_gender.setText("男");
                        }
                        Picasso.get().load(memberData.m_head).into(circleImageView);
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
        finish();
        return true;
    }
}
