package com.example.user.limger;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class forgetpw extends AppCompatActivity {

    ActionBar actionBar;
    View viewActionBar;
    MenuInflater inflater;
    TextView textviewTitle;

    EditText fg_phone, fg_email;
    Button but_fg_ok;

    DatabaseReference member_dataRef;
    FirebaseAuth firebaseAuth;

    AlertDialog dialogs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpw);

        //title字體顏色設定
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFA38473")));
        View viewActionBar = getLayoutInflater().inflate(R.layout.title, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("Limger");
        CircleImageView upImg = viewActionBar.findViewById(R.id.Img_self);
        upImg.setVisibility(View.GONE);
        actionBar.setCustomView(viewActionBar, params);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        member_dataRef = FirebaseDatabase.getInstance().getReference("member");

        fg_defin(); //定義
        but_fg_oklisterer();  //確認按鈕


    }

    public void fg_defin() {
        fg_email = findViewById(R.id.fg_email);
        fg_phone = findViewById(R.id.fg_phone);
        but_fg_ok = findViewById(R.id.but_fg_ok);
    }

    private void but_fg_oklisterer() {
        but_fg_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Stringfg_email = fg_email.getText().toString();
                final String Stringfg_phone = fg_phone.getText().toString();
                for (int i = 0; i < 1; i++) {
                    if (Stringfg_email.isEmpty()) {
                        fg_email.setError("未輸入");
                    }
                    if (Stringfg_phone.isEmpty()) {
                        fg_phone.setError("未輸入");
                    }
                    break;

                }

                member_dataRef
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {

                                    registe_firebase regfir = artistSnapshot.getValue(registe_firebase.class);
                                    if(regfir.m_email.equals(Stringfg_email)){
                                            if (regfir.m_tel.equals(Stringfg_phone)) {
                                                dialogs = new AlertDialog.Builder(forgetpw.this)
                                                        .setTitle("密碼")
                                                        .setMessage("你的密碼是：" + regfir.m_pw + "\n下次不要再忘囉～")

                                                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialogs.dismiss();
                                                                finish();
                                                            }
                                                        })
                                                        .create();

                                                dialogs.show();
                                                dialogs.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
                                            } else {
                                                Toast.makeText(forgetpw.this, "聯絡電話有誤，請確認", Toast.LENGTH_SHORT).show();
                                            }
                                    }
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        boolean flag;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                flag = super.onOptionsItemSelected(item);
                break;
        }
        return flag;
    }

}


