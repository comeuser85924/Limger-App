package com.example.user.limger;

import java.util.Date;

/**
 * Created by User on 2018/6/13.
 */

public class registe_firebase {
    //                (姓名)   (暱稱) (性別:女0男1)  (帳號)   (密碼)  (信箱)   (手機)  (學校)      (科系)    (頭貼)     (興趣)  (處罰狀態)
    public String id, m_name, m_nick, m_gender, m_account, m_pw, m_email, m_tel, m_school, m_subject, m_head, m_interest, m_deal;
    public String m_birth;   //生日
    public int m_coin;     //L幣
    public String m_pro_time_start;  //處罰開始時間
    public String m_pro_time_end;  //處罰結束時間


    public registe_firebase() {

    }

    public registe_firebase(String id,String m_name, String m_nick, String m_birth, String m_gender, String m_account, String m_pw
            , String m_email, String m_tel, String m_school, String m_subject, String m_head, String m_interest, int m_coin
                , String m_deal, String m_pro_time_start, String m_pro_time_end) {
        this.id = id;
        this.m_name = m_name;
        this.m_nick = m_nick;
        this.m_birth = m_birth;
        this.m_gender = m_gender;
        this.m_account = m_account;
        this.m_pw = m_pw;
        this.m_email = m_email;
        this.m_tel = m_tel;
        this.m_school = m_school;
        this.m_subject = m_subject;
        this.m_head = m_head;
        this.m_interest = m_interest;
        this.m_coin = m_coin;
        this.m_deal = m_deal;
        this.m_pro_time_start = m_pro_time_start;
        this.m_pro_time_end = m_pro_time_end;

    }




}
