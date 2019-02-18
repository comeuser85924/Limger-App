package com.example.user.limger;

/**
 * Created by User on 2018/5/8.
 */

public class chat_room_member_firebase {




    String cm_id;//房間編號(跟創房時key一樣)
    String chat_room_member_id; //成員狀態編號

    String cm_status;//參與狀態 (離開:0、參與:1)
    String cm_member_id;//會員編號

    public chat_room_member_firebase(){

    }
    public chat_room_member_firebase(String cm_id,String chat_room_member_id, String cm_status ,String cm_member_id){
        this.cm_id= cm_id;
        this.chat_room_member_id= chat_room_member_id;

        this.cm_status=cm_status;
        this.cm_member_id = cm_member_id;

    }
    public String getcm_id(){
        return cm_id;
    }
    public String getchat_room_member_id(){return chat_room_member_id;}
    public String getcm_status(){
        return cm_status;
    }
    public  String getcm_member_id(){
        return cm_member_id;
    }
}
