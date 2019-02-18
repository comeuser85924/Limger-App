package com.example.user.limger;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences.Editor;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class Login extends AppCompatActivity {


    ProgressDialog pd;
    ActionBar actionBar;
    TextView txtforgetpw, txt_log_registe;
    EditText et_log_account, et_log_password;
    Button but_log_login;
    CheckBox rem_pw, auto_login;
    LinearLayout forgetpwLinearLayout;
    private SharedPreferences sp;
    Editor editor;

    DatabaseReference log_memberRef;
    FirebaseAuth firebaseAuth;
    String user_uid;

    private static final String Job_Tag = "My_job_tag";
    private FirebaseJobDispatcher jobDispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        jobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

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
//        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        log_memberRef = FirebaseDatabase.getInstance().getReference("member");
        firebaseAuth = FirebaseAuth.getInstance();


        log_defin();//定義
        log_rem_pw_checkbox(); //記住密碼
//        log_auto_login_checkbox(); //自動登入
        log_txtforgetpw();//忘記密碼
        log_registe();//註冊
        log_login();//登入

        System.out.println("resetpwBoolean----" + sp.getBoolean("resetpw", false));
        if (sp.getBoolean("resetpw", false) == true) {
            et_log_password.setText("");
        } else {
            System.out.println("resetpwBoolean----" + "");
        }


        if (sp.getBoolean("ISCHECK", false)) {
            rem_pw.setChecked(true);
            et_log_account.setText(sp.getString("account", ""));
            et_log_password.setText(sp.getString("password", ""));
//            if(sp.getBoolean("AUTO_ISCHECK", false))
//            {
//                //设置默认是自动登录状态
//                auto_login.setChecked(true);
//                Bundle Ban_account = new Bundle();
//                Ban_account.putString("member_account", sp.getString("account",""));
//                Bundle Ban_pw = new Bundle();
//                Ban_pw.putString("member_pw", sp.getString("password",""));
//                Intent intent = new Intent(Login.this, Chat_MainActivity.class);
//                intent.putExtras(Ban_account);
//                intent.putExtras(Ban_pw);
//                startActivity(intent);
//            }
//
        } else {
            rem_pw.setChecked(false);
            et_log_account.setText("");
            et_log_password.setText("");
        }

    }

    public void log_defin() {


        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        forgetpwLinearLayout = findViewById(R.id.forgetpwLinearLayout);
        txtforgetpw = findViewById(R.id.txtforgetpw);
        txt_log_registe = findViewById(R.id.txt_log_registe);
        et_log_account = findViewById(R.id.fg_email);
        et_log_password = findViewById(R.id.et_log_password);
        but_log_login = findViewById(R.id.but__log_login);

        rem_pw = findViewById(R.id.checkBox);
        auto_login = findViewById(R.id.checkBoxauto);
    }

    private void log_rem_pw_checkbox() {
        rem_pw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (rem_pw.isChecked()) {
                    System.out.println("記住密碼已選中");
                    sp.edit().putBoolean("ISCHECK", true).commit();

                } else {

                    System.out.println("記住密碼沒有選中");
                    sp.edit().putBoolean("ISCHECK", false).commit();

                }
            }
        });
    }

//    private void log_auto_login_checkbox(){
//        auto_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
//                if (auto_login.isChecked()) {
//                    System.out.println("自动登录已选中");
//                    sp.edit().putBoolean("AUTO_ISCHECK", true).commit();
//
//                } else {
//                    System.out.println("自动登录没有选中");
//                    sp.edit().putBoolean("AUTO_ISCHECK", false).commit();
//                }
//            }
//        });
//    }

    private void log_login() {

        but_log_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                editor = sp.edit();
                if (rem_pw.isChecked()) {
                    //記住帳號、密碼

                    editor.putString("account", et_log_account.getText().toString());
                    editor.putString("password", et_log_password.getText().toString());

                }
                ConnectivityManager mConnectivityManager =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

                if (mNetworkInfo != null) {
                    final boolean sginBool = getSharedPreferences("SigninData", MODE_PRIVATE)
                            .getBoolean("sginBool", false);
                    if (sginBool) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                                17, 59, 59);

                        if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
//                            Toast.makeText(Login.this, "時間已過沒事....", Toast.LENGTH_SHORT).show();
                        } else {

                            long a = calendar.getTimeInMillis() - System.currentTimeMillis();
                            int b = Integer.parseInt(String.valueOf(a / 1000));
                            Job job = jobDispatcher.newJobBuilder().
                                    setService(SigninService.class).
                                    setLifetime(Lifetime.UNTIL_NEXT_BOOT).
                                    setRecurring(false).
                                    setTag(Job_Tag).
                                    setTrigger(Trigger.executionWindow(b, b + 10)).
                                    setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL).
                                    setReplaceCurrent(true).
                                    setConstraints(Constraint.ON_ANY_NETWORK).
                                    build();
//                            Toast.makeText(Login.this, "1後台工作啟動....", Toast.LENGTH_SHORT).show();
                            jobDispatcher.mustSchedule(job);
                        }
                    }


                    pd = ProgressDialog.show(Login.this, "登入中...", "系統驗證中請稍等");
                    firebaseAuth.signInWithEmailAndPassword(et_log_account.getText().toString(), et_log_password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        editor.putBoolean("restpw", false);
                                        editor.commit();
                                        SharedPreferences roomid = getSharedPreferences("RoomStatus", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = roomid.edit();
                                        editor.putInt("firstSearch", 1);
                                        editor.commit();
                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                        Toast.makeText(Login.this, "登入成功", Toast.LENGTH_SHORT).show();
                                        pd.dismiss();
                                    } else if (task.getException().equals(" A network error (such as timeout, interrupted connection or unreachable host) has occurred.")) {
                                        Toast.makeText(Login.this, "網路逾時", Toast.LENGTH_SHORT).show();
                                        pd.dismiss();
                                    } else {
                                        Toast.makeText(Login.this, "帳號密碼有誤或尚未註冊", Toast.LENGTH_SHORT).show();
                                        pd.dismiss();

                                    }
                                }
                            });
                } else {
                    new AlertDialog.Builder(Login.this).setMessage("目前沒有網路")
                            .setPositiveButton("請前往設定網路或開啟wifi", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();

                }


            }

        });
    }

    private void log_txtforgetpw() {
        forgetpwLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, forgetpw.class);
                startActivity(intent);
            }
        });
    }

    private void log_registe() {
        txt_log_registe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, registe.class);
                startActivity(intent);
            }
        });
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        ClosingService adapter = new ClosingService();
//
//    }
//
//    class ClosingService extends Service {
//        @Nullable
//        @Override
//        public IBinder onBind(Intent intent) {
//            return null;
//        }
//
//        @Override
//        public void onTaskRemoved(Intent rootIntent) {
//            super.onTaskRemoved(rootIntent);
//
////        // Handle application closing
////        fireClosingNotification();
//
//            Toast.makeText(ClosingService.this, "456456", Toast.LENGTH_SHORT).show();
//            // Destroy the service
//            stopSelf();
//        }
//    }
}
