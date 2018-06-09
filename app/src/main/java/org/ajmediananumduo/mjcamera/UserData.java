package org.ajmediananumduo.mjcamera;
import com.google.firebase.database.FirebaseDatabase;

public class UserData {
    private String userID;    //아이디
    private String userPW;    //비밀번호

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public UserData(String userID, String userPW) {
        this.userID = userID;
        this.userPW = userPW;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUserPW(String userPW) {
        this.userPW = userPW;
    }

    public String getUserName() {
        return userID;
    }

    public String getUserPW() {
        return userPW;
    }
}
