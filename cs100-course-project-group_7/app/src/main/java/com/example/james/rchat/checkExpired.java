package com.example.james.rchat;

import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import static java.lang.Integer.parseInt;

public class checkExpired {
    private String userID;
    private String groupID;
    DatabaseReference mDataRef;

    public checkExpired(String user_id, String group_id){
        user_id = userID;
        group_id = group_id;
        mDataRef = FirebaseDatabase.getInstance().getReference();
    }

    public Boolean isExpired(){
        String timeStamp = mDataRef.child("GroupData").child(userID).child(groupID).child("timeStamp").getKey();
        String serverStamp = ServerValue.TIMESTAMP.toString();
        if(parseInt(serverStamp) > parseInt(timeStamp)){
            return true;
        }
        return false;


    }

    public void setExpired(){
        mDataRef.child("GroupData").child(userID).child(groupID).child("isArchived").setValue("true");
        return;
    }
}
