package com.example.user.limger;

/**
 * Created by User on 2018/7/3.
 */

public class prosecute_firebase {

    String p_id;//檢舉ID
    String p_premid;//被檢舉帳號
    String p_mid; //檢舉帳號
    String p_title;//檢舉主旨
    String p_content;//檢舉內容
    String p_image;//檢舉時的截圖
    String p_time;//檢舉時當系時間
    String p_rid;//檢舉時當下的房間
    String p_type;//處罰狀態

    public prosecute_firebase(){

    }
    public prosecute_firebase(String p_id,String p_premid,String p_mid,String p_title,String p_content,String p_image,String p_time,String p_rid,String p_type){
        this.p_id = p_id;
        this.p_premid =p_premid;
        this.p_mid =p_mid;
        this.p_title =p_title;
        this.p_content =p_content;
        this.p_image =p_image;
        this.p_time =p_time;
        this.p_rid =p_rid;
        this.p_type = p_type;

    }
    public String getp_id(){
        return p_id;
    }
    public String getp_premid(){
        return p_premid;
    }
    public String getp_mid(){
        return p_mid;
    }
    public String getp_title(){
        return p_title;
    }
    public String getp_content(){
        return p_content;
    }
    public String getp_image(){
        return p_image;
    }
    public String getp_time(){
        return p_time;
    }
    public String getp_rid(){
        return p_rid;
    }
    public String getp_type(){
        return p_type;
    }




}
