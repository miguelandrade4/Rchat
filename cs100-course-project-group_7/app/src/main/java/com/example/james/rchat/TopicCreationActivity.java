

package com.example.james.rchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class TopicCreationActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;

    private TextInputLayout mTopicName;
    private TextInputLayout mDescription;
    private Button button;

    private ProgressDialog mRegProgress;

    private String mCurrentUserId;
    private String userName;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_creation);

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

        button = (Button) findViewById(R.id.button_create_topic);   //get button id
        mTopicName = (TextInputLayout) findViewById(R.id.input_topic_name);     //get text box id
        mDescription = (TextInputLayout) findViewById(R.id.input_description);     //get text box id


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                String name = mTopicName.getEditText().getText().toString();
                String description = mDescription.getEditText().getText().toString();
                register_topic(name, description);
            }
        });
    }

    private void register_topic(String topic_name, String description){
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef = mDatabaseRef.child("Topics" + mCurrentUserId);
        DatabaseReference topicRef = userRef.push();

        String key = mDatabaseRef.child("Topics").child(userID).push().getKey();
        mDatabaseRef.child("Users").child(userID).child("Topics").child(key).child("topicID").setValue(key);
        mDatabaseRef.child("Users").child(userID).child("Topics").child(key).child("topicName").setValue(topic_name);
        mDatabaseRef.child("TopicData").child(key).child("topicName").setValue(topic_name);
        mDatabaseRef.child("TopicData").child(key).child("Description").setValue(description);
        mDatabaseRef.child("TopicData").child(key).child("Recipients").child(userID).setValue(userName);

        //String path = "Groups/" + mCurrentUserId + "/" + uniqueGroupID; //path to group ID

        //Pair recipeients = new Pair(userRef.toString(), userName);
        //mDatabaseRef.child("Groups").child(userRef.toString()).child("Recipients").setValue(recipeients);


        //mDatabaseRef.setValue(dataMap);//.addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()){
//
        // Closes registration dialogue
        Intent mainIntent = new Intent(TopicCreationActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Makes sure back button doesn't lead you back to Start Page
        startActivity(mainIntent);
        finish();
//                }?
//            }
//        });
    }
}
