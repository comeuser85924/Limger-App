package com.example.user.limger;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.JobTrigger;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by User on 2018/7/14.
 */

public class SigninFragment extends Fragment {

    DatePicker datePicker;
    EditText et_Time;
    Button bt_sign, bt_Time;
    DatabaseReference member_dataRef, Notice_sender_DataRef, Notice_receiver_DataRef;
    FirebaseAuth firebaseAuth;
    int m_coin;
    Boolean sginBool = true;
    long tommtime;
    AlertDialog dialogs;
    SimpleDateFormat simpleDateFormat;
    String Date;
    Calendar calendar;
    String user_uid;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.frmgemt_signin, container, false);
        datePicker = view.findViewById(R.id.datePicker);
        bt_sign = view.findViewById(R.id.bt_sign);

        bt_Time = view.findViewById(R.id.bt_testingTime);

        member_dataRef = FirebaseDatabase.getInstance().getReference("member");
        Notice_sender_DataRef = FirebaseDatabase.getInstance().getReference().child("Notice_message_sender");
        Notice_receiver_DataRef = FirebaseDatabase.getInstance().getReference().child("Notice_message_receiver");
        firebaseAuth = FirebaseAuth.getInstance();
        user_uid = firebaseAuth.getCurrentUser().getUid();

//        bt_Time.setVisibility(View.GONE);

        bt_Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SharedPreferences pref = getContext().getSharedPreferences("SigninData", MODE_PRIVATE);
                pref.edit()
                        .putLong("currenttime", System.currentTimeMillis())
                        .putBoolean("sginBool", true)
                        .commit();


                calendar = Calendar.getInstance();
//                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
//                        17, 59, 59);
                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                Date = simpleDateFormat.format(calendar.getTime());
                final String not_id = Notice_sender_DataRef.push().getKey();
                Notice_sender_firebase not_sender = new Notice_sender_firebase(
                        not_id
                        , "O1n30ZUQSuaLQhAhNItGiOUSavm1"
                        , "趕快完成簽到來獲得L幣吧！"
                        , Date
                        , "今日尚未簽到歐"
                        , "5"
                        , null);
                Notice_sender_DataRef.child(not_id).setValue(not_sender);

                member_dataRef.child(user_uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String myself_account = dataSnapshot.child("m_account").getValue().toString();
                                Notice_receiver_firebase not_receiver = new Notice_receiver_firebase(
                                        not_id,
                                        not_id,
                                        myself_account,
                                        user_uid
                                );
                                Notice_receiver_DataRef.child(not_id).setValue(not_receiver);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                pref = getContext().getSharedPreferences("singin_sendID", MODE_PRIVATE);
                pref.edit()
                        .putString("singin_sendID", not_id)
                        .commit();
                SharedPreferences first_pre = getContext().getSharedPreferences("FIRST", MODE_PRIVATE);
                first_pre.edit()
                        .putString("firstUse", "ThirdupUse")
                        .commit();

                bt_sign.setEnabled(true);


            }
        });


        final Long bted_time = getContext().getSharedPreferences("SigninData", MODE_PRIVATE)
                .getLong("currenttime", 0);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, 12);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);


        Date result = cal.getTime();
        datePicker.setMaxDate(cal.getTimeInMillis());
        datePicker.setMinDate(System.currentTimeMillis()-1000);


        long b = 86400000;
//        final long c = 64800000;
        long sum = bted_time + b;
        final boolean sginBool = getContext().getSharedPreferences("SigninData", MODE_PRIVATE)
                .getBoolean("sginBool", false);
        System.out.println("sginBoolSigin----"+sginBool);
//        if (sginBool) {
//            bt_sign.setEnabled(true);
//        } else if(System.currentTimeMillis() >= sum){
//            bt_sign.setEnabled(true);
//        }
//        else {
//            bt_sign.setEnabled(false);
//
//        }


        member_dataRef.child(firebaseAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        m_coin = Integer.parseInt(dataSnapshot.child("m_coin").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        bt_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                final int Cyear =calendar.get(Calendar.YEAR);
                final int Cmonth =calendar.get(Calendar.MONTH)+1;
                final int Cday =calendar.get(Calendar.DAY_OF_MONTH);

                Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
                t.setToNow(); // 取得系统时间。
                final int Tyear = t.year;
                final int Tmonth = t.month+1;
                final int Tday = t.monthDay;


                if((Cyear == Tyear)&&(Cmonth==Tmonth)&&(Cday==Tday)){
                    Notice_receiver_DataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                                final Notice_receiver_firebase receiverData = artistSnapshot.getValue(Notice_receiver_firebase.class);
                                if(receiverData.mes_receiver_member_id.equals(user_uid)){
                                    Notice_sender_DataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                                                final Notice_sender_firebase senderData = artistSnapshot.getValue(Notice_sender_firebase.class);
                                                if(receiverData.mes_sender_id.equals(senderData.mes_sender_id)){
                                                    if(senderData.mes_sender_type.equals("5")){
                                                        SimpleDateFormat signinTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                        System.out.println("通知時間"+senderData.mes_sender_time);
                                                        try {
                                                            Date dt =signinTime.parse(senderData.mes_sender_time);
                                                            calendar.setTime(dt);
                                                            int Syear = calendar.get(Calendar.YEAR);
                                                            int Smonth = calendar.get(Calendar.MONTH)+1;
                                                            int Sday = calendar.get(Calendar.DAY_OF_MONTH);

                                                            if((Cyear == Syear)&&(Cmonth==Smonth)&&(Cday==Sday)){
                                                                Notice_sender_DataRef.child(senderData.mes_sender_id).child("mes_sender_type").setValue("9");
                                                                Notice_sender_DataRef.child(senderData.mes_sender_id).child("mes_sender_title").setValue("今日簽到完成");
                                                            }
                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }
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
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else{
                    Toast.makeText(getContext(),"時間不正確",Toast.LENGTH_SHORT).show();
                }

                SharedPreferences pref = getContext().getSharedPreferences("SigninData", MODE_PRIVATE);
                pref.edit()
                        .putLong("currenttime", System.currentTimeMillis())
                        .putBoolean("sginBool", false)
                        .commit();

                m_coin += 5;
                member_dataRef.child(firebaseAuth.getCurrentUser().getUid()).child("m_coin").setValue(m_coin)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                SharedPreferences first_pre = getContext().getSharedPreferences("FIRST", MODE_PRIVATE);
                                first_pre.edit()
                                        .putString("firstUse", "SecondupUse")
                                        .commit();

                            }
                        });

//                dialogs = new AlertDialog.Builder(getContext())
//                        .setTitle("簽到成功")
//                        .setMessage("系統已經將簽到獎勵發給您囉~")
//
//                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialogs.dismiss();
//                            }
//                        })
//                        .create();
                Toast.makeText(getContext(),"簽到成功",Toast.LENGTH_LONG).show();
//                dialogs.show();
//                dialogs.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
//                dialogs.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.GRAY);
                bt_sign.setText("已簽到");
                bt_sign.setEnabled(false);
            }
        });


        return view;
    }

}

