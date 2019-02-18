package com.example.user.limger;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class title_Fragment extends Fragment {
    GridView title_gridview;
    String[] str = {"電玩版", "有趣版", "美食版", "動漫版", "電影版", "星座版", "生活版", "感情版"};
    public title_Fragment() {

        // Required empty public constructor
    }
    private FragmentManager manager;
    private android.support.v4.app.FragmentTransaction transaction;
    chatlist_Fragment fragment1;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        manager =getActivity().getSupportFragmentManager();



        View view = inflater.inflate(R.layout.fragment_title, container, false);

        title_gridview = view.findViewById(R.id.title_gridview);
        TitleList TitleList = new TitleList();
        title_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i=0;i<8;i++){
                    if(i==position){
                        Bundle bundles = new Bundle();
                        Intent intent = new Intent(getActivity(),MainActivity.class);
                        bundles.putString("Title",str[i]);

                        intent.putExtras(bundles);
                        startActivity(intent);
                        getActivity().finish();
//                        transaction = manager.beginTransaction();
//                        fragment1 = new chatlist_Fragment();
//                        transaction.replace(R.id.LinearLayout_chatlist, fragment1, "chatList");
//                        Bundle bundles = new Bundle();
//                        bundles.putString("Title",str[i]);
//                        fragment1.setArguments(bundles);
//                        transaction.commit();

                    }

                }
            }
        });


        title_gridview.setNumColumns(3);
        title_gridview.setVerticalSpacing(50);
        title_gridview.setAdapter(TitleList);


        return view;
    }
    class TitleList extends BaseAdapter {

        @Override
        public int getCount() {
            return str.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.layout_title_item,null);
            ImageView bg = view.findViewById(R.id.layout_bg_img);
            TextView textView = view.findViewById(R.id.layout_txt_title);
            textView.setText(str[position]);

            return view;
        }
    }
}
