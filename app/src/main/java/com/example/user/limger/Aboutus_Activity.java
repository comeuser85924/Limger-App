package com.example.user.limger;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Aboutus_Activity extends AppCompatActivity {
    ActionBar actionBar;
    CircleImageView ab_imgKyle,ab_imgRyan,ab_imgKevin,ab_imgKiarrianna,ab_imgTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
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
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        ab_imgKyle =findViewById(R.id.ab_imgKyle);
        ab_imgRyan = findViewById(R.id.ab_imgRyan);
        ab_imgKevin =findViewById(R.id.ab_imgKevin);
        ab_imgTeacher = findViewById(R.id.ab_imgTeacher);
        ab_imgKiarrianna = findViewById(R.id.ab_imgKiarrianna);
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/limger-df9ac.appspot.com/o/Limger%20Team%20Member%2Fkyle.webp?alt=media&token=e609c410-d184-43fa-ac2d-5c0c52e6a6da")
                .into(ab_imgKyle);
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/limger-df9ac.appspot.com/o/Limger%20Team%20Member%2Fzicheng.webp?alt=media&token=53c55df2-7c19-4cdd-a3cc-cadb5ae4847a")
                .into(ab_imgRyan);
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/limger-df9ac.appspot.com/o/Limger%20Team%20Member%2Fzheyan.JPG?alt=media&token=4fb9ce22-40da-4c38-9a65-ebd1b2c20559")
                .into(ab_imgKevin);
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/limger-df9ac.appspot.com/o/Limger%20Team%20Member%2Fbigsister.jpg?alt=media&token=c37b492d-12a4-4271-b51c-5a21b7e39a48")
                .into(ab_imgKiarrianna);
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/limger-df9ac.appspot.com/o/Limger%20Team%20Member%2FLimger-Teacher.jpg?alt=media&token=7fd68049-ef02-4a06-b1bd-c183acb688bb")
                .into(ab_imgTeacher);

    }


    //製作返回健
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        boolean flag = false;

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                flag = true;
                break;
            default:
                flag = super.onOptionsItemSelected(item);
                break;
        }
        return flag;
    }
}
