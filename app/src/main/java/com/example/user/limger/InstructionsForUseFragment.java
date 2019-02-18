package com.example.user.limger;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class InstructionsForUseFragment extends Fragment {

    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String,List<String>> listHash;

    public InstructionsForUseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_instructions_for_use, container, false);

        listView = (ExpandableListView)view.findViewById(R.id.lvExpQA);
        initData();
        listAdapter = new ExpandableListAdapter(getContext(),listDataHeader,listHash);
        listView.setAdapter(listAdapter);

        return view;
    }

    private void initData() {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        listDataHeader.add("主題版");
        listDataHeader.add("L幣");
        listDataHeader.add("每日簽到");
        listDataHeader.add("創房");
        listDataHeader.add("限時配對");
        listDataHeader.add("私聊");
        listDataHeader.add("多人聊天");
        listDataHeader.add("封鎖");

        List<String> Title = new ArrayList<>();
        Title.add("主題版分為：電玩、有趣、美食、動漫等等...."+"\n"+"可依照不同類型選擇參與聊天或創房");

        List<String> Lcoin = new ArrayList<>();
        Lcoin.add("所有獎惩機制皆透過 L幣來運作！\n"+
                "有L幣才能進入聊天室進行聊天。\n" +
                "進聊天室或被檢舉等等會扣L幣。\n" +
                "創立房間或參與特殊活動等，系統會獎勵L幣給使用者。");

        List<String> Signin = new ArrayList<>();
        Signin.add("每24小時登入系統簽到一次，則會贈送L幣，提高使用者登入系統的使用率");

        List<String> Createroom = new ArrayList<>();
        Createroom.add("使用者可以透過創房來增加L幣值，並且還可以認識更多人");

        List<String> Addfriend = new ArrayList<>();
        Addfriend.add("命運般的配對！\n"+
                "透過限時的效果可以更快的反應是否要加為好友\n"+
                "且是雙方皆須互加為好友才可成為好友");


        List<String> Privatechat = new ArrayList<>();
        Privatechat.add("與好友進行一對一聊天"+"\n"+"彼此可以更進一步認識對方");

        List<String> peoplechat = new ArrayList<>();
        peoplechat.add("與好友進行一對多聊天\n"+
                "可讓自己Friend圈的好友們互相認識！可以快速的拉近彼此的距離");

        List<String> blockade = new ArrayList<>();
        blockade.add("請謹慎小心！！！！！!\n"+
                "使用者們已命運般的成功配對互成為好友！\n"+
                "若非遭遇騷擾或其他等感到困擾因素\n"+"請在封鎖Friend圈好友前謹慎思考\n"+
                "因為若封鎖好友則是永久封鎖且不得在互加為好友\n"+
                "並且連對方的通知也會一併消失\n");

        listHash.put(listDataHeader.get(0),Title);
        listHash.put(listDataHeader.get(1),Lcoin);
        listHash.put(listDataHeader.get(2),Signin);
        listHash.put(listDataHeader.get(3),Createroom);
        listHash.put(listDataHeader.get(4),Addfriend);
        listHash.put(listDataHeader.get(5),Privatechat);
        listHash.put(listDataHeader.get(6),peoplechat);
        listHash.put(listDataHeader.get(7),blockade);
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
}
