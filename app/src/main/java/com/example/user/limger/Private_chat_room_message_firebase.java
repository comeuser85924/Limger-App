package com.example.user.limger;

/**
 * Created by User on 2018/8/3.
 */

public class Private_chat_room_message_firebase {
    public static final String TYPE_RECEIVED ="0";
    public static final String TYPE_SEND = "1";

    String pcmes_id;
    String pcmes_member_id;
//    String pcmes_account;
    String pcmes_time;
    String pcmes_message_id;
    String pcmes_message;
    String pcmes_type;

    public Private_chat_room_message_firebase(){
    }
    public Private_chat_room_message_firebase(String pcmes_id,String pcmes_member_id,String pcmes_time,
                                              String pcmes_message_id,String pcmes_message,
                                              String pcmes_type){
        this.pcmes_id = pcmes_id;
        this.pcmes_member_id = pcmes_member_id;
//        this.pcmes_account = pcmes_account;
        this.pcmes_time = pcmes_time;
        this.pcmes_message_id = pcmes_message_id;
        this.pcmes_message = pcmes_message;
        this.pcmes_type = pcmes_type;
    }
    public String getpcmes_id(){return pcmes_id;}
    public String getpcmes_member_id(){return pcmes_member_id;}
//    public String getpcmes_account(){return pcmes_account;}
    public String getpcmes_time(){return pcmes_time;}
    public String getpcmes_message_id(){return pcmes_message_id;}
    public String getpcmes_message(){return pcmes_message;}
    public String getpcmes_type(){return pcmes_type;}


}
