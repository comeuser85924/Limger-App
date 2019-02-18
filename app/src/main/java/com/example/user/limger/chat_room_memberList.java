package com.example.user.limger;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 2018/5/8.
 */

public class chat_room_memberList extends ArrayAdapter<chat_room_member_firebase> {
    private Activity context;
    private List<chat_room_member_firebase> chat_room_memberList;
    DatabaseReference ct_databaseRef;
    FirebaseAuth firebaseAuth;
    String user_uid;


    public chat_room_memberList(@NonNull Context context, List<chat_room_member_firebase> chat_room_memberList) {
        super(context, R.layout.layout_waitroom_member,chat_room_memberList);
        this.context = (Activity) context;
        this.chat_room_memberList = chat_room_memberList;

    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ct_databaseRef = FirebaseDatabase.getInstance().getReference("chat_room");
        final chat_room_member_firebase crmf = getItem(position);
        DatabaseReference member_dataRef = FirebaseDatabase.getInstance().getReference("member");
        View view;
        final ViewHolder viewHolder;
        if(convertView == null) {


            view = LayoutInflater.from(getContext()).inflate(R.layout.layout_waitroom_member, null);
            viewHolder = new ViewHolder();
            viewHolder.wr_img = view.findViewById(R.id.wr_img);
            viewHolder.wr_txtaccount = (TextView)view.findViewById(R.id.wr_txtaccount);
            viewHolder.createhome = view.findViewById(R.id.createhome);
            viewHolder.createrLayout = view.findViewById(R.id.LinearLayout);

            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        final chat_room_member_firebase artist =chat_room_memberList.get(position);

        member_dataRef.child(artist.cm_member_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Picasso.get().load(dataSnapshot.child("m_head").getValue().toString()).into(viewHolder.wr_img);
                        viewHolder.wr_txtaccount.setText(dataSnapshot.child("m_account").getValue().toString());
                        ct_databaseRef.child(artist.cm_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.child("c_status").getValue().toString().equals("0")){
                                    if (artist.cm_member_id.equals(dataSnapshot.child("c_mid").getValue().toString())){
                                        System.out.println("房主編號----"+artist.cm_member_id);
                                        viewHolder.createhome.setVisibility(View.VISIBLE);
                                    }else{
                                        System.out.println("一般使用者編號----"+artist.cm_member_id);
                                        viewHolder.createrLayout.setBackgroundColor(Color.parseColor("#00ffffff"));
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });



        return view;
    }
    class ViewHolder {
        ConstraintLayout createrLayout;
        ConstraintLayout createhome;
        TextView wr_txtaccount;
        CircleImageView wr_img;
    }


}
