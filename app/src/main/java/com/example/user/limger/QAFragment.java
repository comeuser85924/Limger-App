package com.example.user.limger;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class QAFragment extends Fragment {

    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String,List<String>> listHash;
    int i=0;
    DatabaseReference qa_dataRef;

    Button bt_contact;
    public QAFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qa, container, false);
        qa_dataRef = FirebaseDatabase.getInstance().getReference("qa");
        listView = (ExpandableListView)view.findViewById(R.id.lvExpQA);
        bt_contact = view.findViewById(R.id.bt_contact);

        bt_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),QA_iwantAskActivity.class);
                startActivity(intent);
            }
        });


        initData();

        return view;
    }

    private void initData() {


        qa_dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listDataHeader = new ArrayList<>();
                listHash = new HashMap<>();
                i=0;
                for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                    final QA_firebase artist = artistSnapshot.getValue(QA_firebase.class);
                    listDataHeader.add(artist.getq_quest());
                    List<String> ansList = new ArrayList<>();
                    ansList.add(artist.q_ans);
                    listHash.put(listDataHeader.get(i),ansList);
                    i +=1;
                }
                listAdapter = new ExpandableListAdapter(getContext(),listDataHeader,listHash);
                System.out.println("QA抓資料Adapter-----"+listAdapter);
                listView.setAdapter(listAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


    class ExpandableListAdapter extends BaseExpandableListAdapter {
        private Context context;
        private List<String> listDataHeader;
        private HashMap<String,List<String>> listHashMap;

        public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listHashMap) {
            this.context = context;
            this.listDataHeader = listDataHeader;
            this.listHashMap = listHashMap;
        }

        @Override
        public int getGroupCount() {
            return listDataHeader.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return listHashMap.get(listDataHeader.get(i)).size();
        }

        @Override
        public Object getGroup(int i) {
            return listDataHeader.get(i);
        }

        @Override
        public Object getChild(int i, int i1) {
            return listHashMap.get(listDataHeader.get(i)).get(i1); // i = Group Item , i1 = ChildItem
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            String headerTitle = (String)getGroup(i);
            if(view == null)
            {
                LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.list_group,null);
            }
            TextView lblListHeader = (TextView)view.findViewById(R.id.lblListHeader);
            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle);
            return view;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            final String childText = (String)getChild(i,i1);
            if(view == null)
            {
                LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.list_item,null);
            }

            TextView txtListChild = (TextView)view.findViewById(R.id.lblListItem);
            txtListChild.setText(childText);
            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }


    public static class QA_firebase {
        String q_id;//QA編號
        String q_quest;////問題
        String q_ans;//答案

        public QA_firebase(){

        }
        public QA_firebase(String q_id,String q_quest, String q_ans){
            this.q_id = q_id;
            this.q_quest = q_quest;
            this.q_ans = q_ans;
        }

        public String getq_id(){
            return q_id;
        }
        public String getq_quest(){
            return q_quest;
        }
        public String getq_ans(){
            return q_ans;
        }

    }
}
