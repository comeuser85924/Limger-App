package com.example.user.limger;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by User on 2018/5/1.
 */

public class createList extends ArrayAdapter<create_firebase> {
    private Context context;
    private List<create_firebase> createList;
    DatabaseReference memberRef;
    FirebaseAuth firebaseAuth;
    String user_uid;
    String c_account;

    public createList(@NonNull Context context, List<create_firebase> createList) {
        super(context, R.layout.layout_roomlist_createitem, createList);
        this.context = context;
        this.createList = createList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        memberRef = FirebaseDatabase.getInstance().getReference("member");
        firebaseAuth = FirebaseAuth.getInstance();
        user_uid = firebaseAuth.getCurrentUser().getUid();
        final create_firebase artist = createList.get(position);


        View view;
        final ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.layout_roomlist_createitem, null);
            viewHolder = new ViewHolder();
            viewHolder.txt_title = (TextView) view.findViewById(R.id.txttitle);
            viewHolder.txt_roomname = (TextView) view.findViewById(R.id.txtroomname);
            viewHolder.txt_account = (TextView) view.findViewById(R.id.txtaccount);
            viewHolder.txt_num = (TextView) view.findViewById(R.id.txtnum);
            viewHolder.txt_stime = (TextView) view.findViewById(R.id.txttime);
            viewHolder.txt_personnum = view.findViewById(R.id.txtpersonnum);

            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
            view.setTag(viewHolder);
        }
        memberRef.child(artist.getc_mid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                viewHolder.txt_account.setText(dataSnapshot.child("m_account").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        viewHolder.txt_title.setText(artist.getc_title());
        viewHolder.txt_roomname.setText(artist.getc_roomname());
        viewHolder.txt_num.setText(String.valueOf(artist.getc_population()));
        viewHolder.txt_stime.setText(artist.getc_stime());
        viewHolder.txt_personnum.setText(String.valueOf(artist.getc_personnum()));

        return view;
    }



    class ViewHolder {

        TextView txt_title;
        TextView txt_roomname;
        TextView txt_account;
        TextView txt_num;
        TextView txt_stime;
        TextView txt_personnum;
    }


}
