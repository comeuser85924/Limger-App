package com.example.user.limger;

/**
 * Created by User on 2018/7/24.
 */

public class Notice_receiver_firebase {
    String mes_receiver_id;//收通知資料表編號

    String mes_sender_id;//發送通知編號(透過這個ID 取的該發送通知的資料(內容、時間、標題等等))
    String mes_receiver_member_id;//接收通知的使用者會員ID
    String mes_receiver_account;//接收通知的使用者會員帳號



    public Notice_receiver_firebase(){

    }

    public Notice_receiver_firebase(String mes_receiver_id ,String mes_sender_id,String mes_receiver_account,String mes_receiver_member_id){
        this.mes_receiver_id= mes_receiver_id;
        this.mes_sender_id= mes_sender_id;
        this.mes_receiver_member_id=mes_receiver_member_id;
        this.mes_receiver_account = mes_receiver_account;

    }

    public String getmes_receiver_id(){
        return mes_receiver_id;
    }
    public String getmes_sender_id(){
        return mes_sender_id;
    }
    public String getmes_receiver_member_id(){
        return mes_receiver_member_id;
    }
    public String getmes_receiver_account(){
        return mes_receiver_account;
    }

}
