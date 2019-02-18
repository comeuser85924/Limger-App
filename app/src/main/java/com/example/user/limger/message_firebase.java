package com.example.user.limger;

/**
 * Created by User on 2018/5/13.
 */

public class message_firebase {
    public static final String TYPE_RECEIVED ="0";
    public static final String TYPE_SEND = "1";

    String chat_room_message_id;  //訊息ID
    String cmes_id;     //房間ID
    String cmes_member; //成員帳號
    String cmes_time;   //發出時間
    String cmes_message;//訊息內容;
    String cmes_member_id;
    private String cmes_msgtype;//訊息種類 (接收端:0，發送端:1)

    public message_firebase(){

    }
    public  message_firebase( String chat_room_message_id,String cmes_id,String cmes_member, String cmes_time,
                              String cmes_message, String cmes_msgtype,String cmes_member_id){
        this.chat_room_message_id = chat_room_message_id;
        this.cmes_id =cmes_id;
        this.cmes_member = cmes_member;
        this.cmes_time =cmes_time;
        this.cmes_message=cmes_message;
        this.cmes_msgtype = cmes_msgtype;
        this.cmes_member_id = cmes_member_id;


    }

    public String getchat_room_message_id(){return chat_room_message_id;}
    public String getcmes_id(){return cmes_id;}
    public String getcmes_member(){return cmes_member;}

    public String getcmes_time(){return cmes_time;}
    public String getcmes_message(){return cmes_message;}
    public String getcmes_msgtype(){return cmes_msgtype;}

    public String getcmes_member_id(){return cmes_member_id;}

}
