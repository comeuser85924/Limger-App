package com.example.user.limger;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

/**
 * Created by User on 2018/5/1.
 */

public class create_firebase {


    String c_id;    //
    String c_mid; //使用者編號
    String c_roomname;//房名
    String c_title;   //主題
    int c_personnum ;//房內人數
    int c_population;  //總人數
    String c_gender;     //是否限男女
    int c_time;    //限制聊天時間
    String c_join;      //是否中途加入
    String c_status;    //參與狀態 等待中:0、進行中:1、結束:2、被迫結束:3
    String c_stime;     //創房時間

    String c_intoroomtime;  //等待室跳至聊天室時間點


    public create_firebase(){

    }
    public create_firebase(String c_id,String c_mid,String c_roomname,String c_title,int c_personnum,int c_population,
                           String c_gender,int c_time,String c_join,String c_status,String c_stime,String c_intoroomtime){
        this.c_id=c_id;
        this.c_mid=c_mid;
        this.c_roomname =c_roomname;
        this.c_title = c_title;
        this.c_personnum =c_personnum;
        this.c_population =c_population;
        this.c_gender = c_gender;
        this.c_time =c_time;
        this.c_join = c_join;
        this.c_status = c_status;
        this.c_stime= c_stime;
        this.c_intoroomtime = c_intoroomtime;
    }
    public String getc_id(){
        return c_id;
    }
    public String getc_mid(){
        return c_mid;
    }
    public String getc_roomname(){
        return c_roomname;
    }
    public String getc_title(){
        return c_title;
    }
    public int getc_personnum(){return c_personnum;}
    public int getc_population(){
        return c_population;
    }
    public String getc_gender(){
        return c_gender;
    }
    public int getc_time(){
        return c_time;
    }
    public String getc_join(){
        return c_join;
    }
    public String getc_status(){
        return c_status;
    }
    public String getc_stime(){
        return c_stime;
    }

    public  String getc_intoroomtime(){return c_intoroomtime;}


}
