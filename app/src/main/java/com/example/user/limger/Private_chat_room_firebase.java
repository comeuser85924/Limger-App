package com.example.user.limger;

/**
 * Created by User on 2018/8/3.
 */

public class Private_chat_room_firebase {
    String pc_id;           //房間編號
    String pc_member_id;      //邀請人會員帳號
    String pc_status;       //房間狀態

    public Private_chat_room_firebase(){}

    public Private_chat_room_firebase(String pc_id,String pc_member_id,String pc_status){
        this.pc_id= pc_id;
        this.pc_member_id = pc_member_id;

        this.pc_status= pc_status;
    }

    public String getpc_id(){
        return pc_id;
    }
    public String getpc_member_id(){
        return pc_member_id;
    }

    public String getpc_status(){
        return pc_status;
    }

}
