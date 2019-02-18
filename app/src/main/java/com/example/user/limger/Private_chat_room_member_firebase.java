package com.example.user.limger;

/**
 * Created by User on 2018/8/3.
 */

public class Private_chat_room_member_firebase {
    String pcm_id;
//    String pcm_account;
    String pcm_status;
    String pcm_member_id;

    public Private_chat_room_member_firebase(){}

    public Private_chat_room_member_firebase(String pcm_id,String pcm_status,String pcm_member_id){
        this.pcm_id= pcm_id;
//        this.pcm_account = pcm_account;
        this.pcm_status= pcm_status;
        this.pcm_member_id = pcm_member_id;
    }
    public String getpcm_id(){
        return pcm_id;
    }
//    public String getpcm_account(){
//        return pcm_account;
//    }
    public String getpcm_status(){
        return pcm_status;
    }
    public String getpcm_member_id(){
        return pcm_member_id;
    }

}
