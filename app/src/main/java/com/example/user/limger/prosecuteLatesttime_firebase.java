package com.example.user.limger;

/**
 * Created by User on 2018/12/3.
 */

public class prosecuteLatesttime_firebase {
    String pl_id;//檢舉的ID
    String pl_premid;//被檢舉者編號


    public prosecuteLatesttime_firebase(String pl_id,String pl_premid){
        this.pl_id = pl_id;
        this.pl_premid =pl_premid;

    }
    public String getpl_id(){
        return pl_id;
    }
    public String getpl_premid(){
        return pl_premid;
    }
}
