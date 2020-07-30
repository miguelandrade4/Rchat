

package com.example.james.rchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class ExpireCreationActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;

    private TextInputLayout mGroupName;
    private Button button;

    private ProgressDialog mRegProgress;

    private String mCurrentUserId;
    private String userName;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation);

//        mRegProgress = new ProgressDialog(this);


        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();     //Firebase authorization

        mCurrentUserId = mAuth.getCurrentUser().getUid();   //get current user id for database entry
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = (String) dataSnapshot.child("Users").child(userID).child("name").getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        userName = "";
        userID = mAuth.getCurrentUser().getUid();

        button = (Button) findViewById(R.id.button_create_group);   //get button id
        mGroupName = (TextInputLayout) findViewById(R.id.input_group_name);     //get text box id

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                String name = mGroupName.getEditText().getText().toString();
                register_group(name);
            }
        });
    }

    private void register_group(String group_name){
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef = mDatabaseRef.child("Groups" + mCurrentUserId);
        DatabaseReference groupRef = userRef.push();

        String key = mDatabaseRef.child("Groups").child(userID).push().getKey();
        mDatabaseRef.child("Users").child(userID).child("Groups").child(key).child("groupID").setValue(key);
        mDatabaseRef.child("Users").child(userID).child("Groups").child(key).child("groupName").setValue(group_name);
        mDatabaseRef.child("Users").child(userID).child("Groups").child(key).child("timeStamp").setValue(ServerValue.TIMESTAMP);
        mDatabaseRef.child("GroupData").child(key).child("groupName").setValue(group_name);
        mDatabaseRef.child("GroupData").child(key).child("Recipients").child(userID).setValue(userName);
        mDatabaseRef.child("GroupData").child(key).child("timeStamp").setValue(ServerValue.TIMESTAMP);

        //String path = "Groups/" + mCurrentUserId + "/" + uniqueGroupID; //path to group ID

        //Pair recipeients = new Pair(userRef.toString(), userName);
        //mDatabaseRef.child("Groups").child(userRef.toString()).child("Recipients").setValue(recipeients);


        //mDatabaseRef.setValue(dataMap);//.addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()){
//
        // Closes registration dialogue
        Intent mainIntent = new Intent(ExpireCreationActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Makes sure back button doesn't lead you back to Start Page
        startActivity(mainIntent);
        finish();
//                }?
//            }
//        });
    }
}
