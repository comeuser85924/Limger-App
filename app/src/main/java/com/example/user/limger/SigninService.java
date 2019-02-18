package com.example.user.limger;

import android.app.Service;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by User on 2018/10/27.
 */

public class SigninService extends JobService{

    DatabaseReference member_dataRef, Notice_sender_DataRef, Notice_receiver_DataRef;
    FirebaseAuth firebaseAuth;
    String user_uid;

    SimpleDateFormat simpleDateFormat;
    String Date;
    Calendar calendar;

    BackgroundTask backgroundTask;
    private static final String Job_Tag = "My_job_tag2";
    private FirebaseJobDispatcher jobDispatcher;
    @Override
    public boolean onStartJob(final JobParameters job) {
        member_dataRef = FirebaseDatabase.getInstance().getReference("member");
        Notice_sender_DataRef = FirebaseDatabase.getInstance().getReference().child("Notice_message_sender");
        Notice_receiver_DataRef = FirebaseDatabase.getInstance().getReference().child("Notice_message_receiver");
        firebaseAuth = FirebaseAuth.getInstance();
        user_uid = firebaseAuth.getCurrentUser().getUid();

        backgroundTask = new BackgroundTask(){
            @Override
            protected void onPostExecute(String s) {
                calendar = Calendar.getInstance();
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                        17, 59, 59);
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
                Notice_sender_DataRef.child(not_id).setValue(not_sender).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
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
                                            SharedPreferences pref = getSharedPreferences("SigninData", MODE_PRIVATE);
                                            pref.edit()
                                                    .putBoolean("sginBool", true)
                                                    .commit();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }
                });

                Toast.makeText(getApplicationContext(),"1111訊息來自後台 : "+s,Toast.LENGTH_SHORT).show();
                jobFinished(job,false);
            }
        };
        backgroundTask.execute();



        return true;

    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return true;
    }

    public class BackgroundTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {

            SharedPreferences pref = getSharedPreferences("SigninData", MODE_PRIVATE);
            pref.edit()
                    .putBoolean("sginBool", true)
                    .commit();

            return "後台運作的工作";
        }
    }
}
