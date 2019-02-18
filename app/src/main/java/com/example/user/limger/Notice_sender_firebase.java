package com.example.user.limger;

/**
 * Created by User on 2018/7/24.
 */

public class Notice_sender_firebase {

    String mes_sender_id;//發送通知編號

    String mes_sender_member_id;//發送通知的使用者會員編號

    String mes_sender_content;//通知內容
    String mes_sender_time;//通知時間
    String mes_sender_title;//通知標題
    String mes_sender_type;//通知類型(0:一般系統訊息(無功能)、1:配對通知(無功能) 2:私聊 3:多聊、4:檢舉通知(無功能) 5:簽到通知 6:活動通知(無功能))

    String mes_pc_id;//房間編號(只有私多聊有)
    public Notice_sender_firebase(){

    }
    public Notice_sender_firebase(String mes_sender_id,String mes_sender_member_id,String mes_sender_content,
                                  String mes_sender_time ,String mes_sender_title,String mes_sender_type,String mes_pc_id){
        this.mes_sender_id= mes_sender_id;

        this.mes_sender_member_id= mes_sender_member_id;
        this.mes_sender_content =mes_sender_content;
        this.mes_sender_time=mes_sender_time;
        this.mes_sender_title = mes_sender_title;
        this.mes_sender_type = mes_sender_type;
        this.mes_pc_id =mes_pc_id;
    }
    public String getmes_sender_id(){
        return mes_sender_id;
    }

    public String getmes_sender_member_id(){return mes_sender_member_id;}

    public String getmes_sender_content(){
        return mes_sender_content;
    }
    public String getmes_sender_time(){
        return mes_sender_time;
    }
    public  String getmes_sender_title(){
        return mes_sender_title;
    }
    public  String getmes_sender_type(){
        return mes_sender_type;
    }
    public  String getmes_pc_id(){
        return mes_pc_id;
    }

}
