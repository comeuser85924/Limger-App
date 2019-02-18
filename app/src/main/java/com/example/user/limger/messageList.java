package com.example.user.limger;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 2018/5/13.
 */
public class messageList extends ArrayAdapter<message_firebase> {

    private Activity context;
    private List<message_firebase> messageList;

    public messageList(@NonNull Context context, List<message_firebase> messageList) {
        super(context, R.layout.layout_message,messageList);
        this.context = (Activity) context;
        this.messageList = messageList;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        final message_firebase msg = getItem(position);

        View view;
        final ViewHolder viewHolder;
        DatabaseReference member_dataRef;
        member_dataRef = FirebaseDatabase.getInstance().getReference("member");
        if(convertView == null) {

            view = LayoutInflater.from(getContext()).inflate(R.layout.layout_message, null);
            viewHolder = new ViewHolder();
            viewHolder.leftLayout = (LinearLayout)view.findViewById(R.id.left_layout);
            viewHolder.rightLayout = (LinearLayout)view.findViewById(R.id.right_layout);
            viewHolder.leftMsg = (TextView)view.findViewById(R.id.left_msg);
            viewHolder.rightMsg = (TextView)view.findViewById(R.id.right_msg);
            viewHolder.lefttime = view.findViewById(R.id.left_txtmsgtime);
            viewHolder.righttime = view.findViewById(R.id.right_txtmsgtime);
            viewHolder.left_img = view.findViewById(R.id.left_img);;
            viewHolder.leftaccount=view.findViewById(R.id.left_account);

            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        if(msg.getcmes_msgtype().equals( message_firebase.TYPE_RECEIVED)) {
            if(msg.getcmes_member_id().equals("O1n30ZUQSuaLQhAhNItGiOUSavm1")){
                viewHolder.rightLayout.setVisibility(View.GONE);
                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/limger-df9ac.appspot.com/o/Limger%20Team%20Member%2Frobot-head.png?alt=media&token=4c3ac3eb-68a7-4445-9bc6-8f844a0df4c1")
                        .into(viewHolder.left_img);
                viewHolder.leftLayout.setVisibility(View.VISIBLE);
                viewHolder.leftMsg.setText(msg.getcmes_message());
                viewHolder.lefttime.setText(msg.getcmes_time());
                viewHolder.leftaccount.setText("管理者");

            }else {
                member_dataRef.child(msg.cmes_member_id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        viewHolder.leftaccount.setText(dataSnapshot.child("m_account").getValue().toString());
                        Picasso.get().load(dataSnapshot.child("m_head").getValue().toString()).into(viewHolder.left_img);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                viewHolder.rightLayout.setVisibility(View.GONE);
                viewHolder.leftLayout.setVisibility(View.VISIBLE);
                viewHolder.leftMsg.setText(msg.getcmes_message());
                viewHolder.lefttime.setText(msg.getcmes_time());

                //點選大頭貼會觸發彈跳視窗(個人資料)
                viewHolder.left_img.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        //存取個人資料
                        SharedPreferences roomid = context.getSharedPreferences("Room", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = roomid.edit();
                        editor.putString("othersuer_uid",msg.cmes_member_id);
                        editor.putString("roomid",msg.cmes_id);
                        editor.commit();


                        chat_memberdata_dailog member_dailog = new chat_memberdata_dailog();
                        member_dailog.show(((AppCompatActivity)context).getSupportFragmentManager(),"YESSSSSSSS!!");
                        return false;
                    }
                });
            }

        } else if(msg.getcmes_msgtype().equals(message_firebase.TYPE_SEND)) {
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.rightMsg.setText(msg.getcmes_message());
            viewHolder.righttime.setText(msg.getcmes_time());

        }
        return view;
    }
    class ViewHolder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        TextView lefttime;
        TextView righttime;
        CircleImageView left_img;
        TextView leftaccount;
    }


}
