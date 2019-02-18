package com.example.user.limger;

/**
 * Created by User on 2018/12/3.
 */

public class FriendRequest_firebase {
    String fr_member_id;
    String fr_member_id2;
    String fr_rid;

    public FriendRequest_firebase() {
    }

    public FriendRequest_firebase(String fr_member_id, String fr_member_id2, String fr_rid) {
        this.fr_member_id = fr_member_id;
        this.fr_member_id2 = fr_member_id2;
        this.fr_rid = fr_rid;
    }

    public String getfr_member_id() {
        return fr_member_id;
    }

    public String getfr_member_id2() {
        return fr_member_id2;
    }

    public String getfr_rid() {
        return fr_rid;
    }

}
