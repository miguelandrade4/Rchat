package com.example.james.rchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class GroupProfile extends AppCompatActivity {


    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus;//, mProfileFriendsCount;
    private Button mProfileSendMsgBtn;
    private String groupID;

    private ProgressDialog mProgressDialog;
    String display_name;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mGroupsDatabase;
    private FirebaseUser mCurrentUser;
    private String current_uid;
    private Boolean RecipientExists;
    private String groupname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);

        final String profile_user_id = getIntent().getStringExtra("user_id");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        current_uid = mCurrentUser.getUid();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(profile_user_id);

        //Getting the info relevant to groupActivity
        groupID = getIntent().getExtras().getString("groupID");
        mGroupsDatabase = FirebaseDatabase.getInstance().getReference().child("GroupData").child(groupID);
        mGroupsDatabase.child("Recipients").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(profile_user_id)){
                    RecipientExists = true;
                } else {
                    RecipientExists = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileImage = (ImageView) findViewById(R.id.group_profile_image);
        mProfileName = (TextView) findViewById(R.id.group_profile_displayName);
        //mProfileFriendsCount = (TextView) findViewById(R.id.profile_totalFriends);
        mProfileSendMsgBtn = (Button) findViewById(R.id.group_profile_send_msg_btn);
        mProfileStatus = (TextView) findViewById(R.id.group_profile_status);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setTitle("Please wait while we load user data");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText("");

                Picasso.get().load(image).placeholder(R.drawable.default_pic).into(mProfileImage);

                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileSendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (RecipientExists){
                    Toast.makeText(GroupProfile.this, "The user is already a member of your group!",
                            Toast.LENGTH_LONG).show();
                } else {

                    mGroupsDatabase.child("Recipients").child(profile_user_id).setValue(display_name);  //Adds the recipient

                    mGroupsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {           //Copies the content to the new user
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            groupname = dataSnapshot.child("groupName").getValue().toString();
                            mUsersDatabase.child("Groups").child(groupID).child("groupName").setValue(groupname);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Toast.makeText(GroupProfile.this, "The user has been added to your group!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });





    }
}
