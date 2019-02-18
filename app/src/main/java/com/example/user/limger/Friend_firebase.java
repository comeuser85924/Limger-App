package com.example.user.limger;

/**
 * Created by User on 2018/7/1.
 */

public class Friend_firebase {
    String f_member_id;//member1的key
    String f_member_id2;//member2的key
    String f_bstatus;   //封鎖狀態



    public Friend_firebase(){

    }
    public Friend_firebase(String f_member_id,String f_member_id2,String f_bstatus){
        this.f_member_id = f_member_id;
        this.f_member_id2 = f_member_id2;
        this.f_bstatus = f_bstatus;


    }

    public String getf_member_id(){
        return f_member_id;
    }
    public String getf_member_id2(){
        return f_member_id2;
    }
    public String getf_bstatus(){
        return f_bstatus;
    }

}
