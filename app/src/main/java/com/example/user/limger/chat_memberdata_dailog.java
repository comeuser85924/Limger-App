package com.example.user.limger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
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

import static android.app.AlertDialog.THEME_HOLO_LIGHT;


/**
 * Created by User on 2018/6/28.
 */

public class chat_memberdata_dailog extends AppCompatDialogFragment {
    TextView DailogNick,member_dailog_gender, DailogAccount, DailogSchool;
    CircleImageView Dailog_img;
    DatabaseReference member;
    FirebaseAuth firebaseAuth;

    String account,roomid,othersuer_uid;
    String gender;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        member = FirebaseDatabase.getInstance().getReference("member");
        firebaseAuth = FirebaseAuth.getInstance();

        roomid = getContext().getSharedPreferences("Room", Context.MODE_PRIVATE).getString("roomid", "");
        othersuer_uid = getContext().getSharedPreferences("Room", Context.MODE_PRIVATE).getString("othersuer_uid", "");

    }


    @Override
    public void onStart() {
        super.onStart();

        member.child(othersuer_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Picasso.get().load(dataSnapshot.child("m_head").getValue().toString()).into(Dailog_img);
                DailogNick.setText(dataSnapshot.child("m_nick").getValue().toString());
                if(dataSnapshot.child("m_gender").getValue().toString().equals("0")){
                    gender = "女";
                }else if(dataSnapshot.child("m_gender").getValue().toString().equals("1")){
                    gender = "男";
                }
                member_dailog_gender.setText("("+gender+")");
                DailogAccount.setText(dataSnapshot.child("m_account").getValue().toString());
                DailogSchool.setText(dataSnapshot.child("m_school").getValue().toString());
                account = dataSnapshot.child("m_account").getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),THEME_HOLO_LIGHT);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.chat_memberdata_dailog, null);

        builder.setView(view)
                .setNeutralButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .setPositiveButton("檢舉", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prosecute_dailog prosecute = new prosecute_dailog();
                        Bundle args = new Bundle();
                        args.putString("p_premid",othersuer_uid);
                        args.putString("p_maccount", account);
                        args.putString("roomid", roomid);
                        prosecute.setArguments(args);//透過setArguments傳值
                        prosecute.show(getFragmentManager(), "prosecute");
                        return ;

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.BLACK);

        Dailog_img = view.findViewById(R.id.memdata_dailog_img);

        DailogNick = view.findViewById(R.id.memdata_dailog_nick);
        member_dailog_gender = view.findViewById(R.id.member_dailog_gender);
        DailogAccount = view.findViewById(R.id.memdata_dailog_account);
        DailogSchool = view.findViewById(R.id.memdata_dailog_school);

        return dialog;
    }


}
