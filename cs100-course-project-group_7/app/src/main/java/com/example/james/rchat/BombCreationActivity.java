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
        import android.widget.CalendarView;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

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

public class BombCreationActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;

    private TextInputLayout mBombName;
    private Button button;

    private ProgressDialog mRegProgress;

    private String mCurrentUserId;
    private String userName;
    private String userID;

    private CalendarView mStartView;
    private CalendarView mEndView;
    private long startDate;
    private long endDate;
    private Button setStartDate;
    private Button setEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bomb_creation);

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

        mStartView = (CalendarView) findViewById(R.id.startCalendar);
        mEndView = (CalendarView) findViewById(R.id.endCalendar);
        setStartDate = (Button) findViewById(R.id.startdate);
        setEndDate = (Button) findViewById(R.id.enddate);

        setStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                openStart();
            }
        });

        setEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                openEnd();
            }
        });

        userName = "";
        userID = mAuth.getCurrentUser().getUid();

        button = (Button) findViewById(R.id.button_create_bomb);   //get button id
        mBombName = (TextInputLayout) findViewById(R.id.input_bomb_name);     //get text box id

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                String name = mBombName.getEditText().getText().toString();
                register_bomb(name);
            }
        });
    }

    private void openStart(){
        mStartView.setVisibility(View.VISIBLE);
        mEndView.setVisibility(View.GONE);
    }
    private void openEnd(){
        mStartView.setVisibility(View.GONE);
        mEndView.setVisibility(View.VISIBLE);
    }
    private void register_bomb(String bomb_name){
        startDate = mStartView.getDate();
        endDate = mEndView.getDate();
        if (startDate < endDate) {
            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference userRef = mDatabaseRef.child("Bombs" + mCurrentUserId);
            DatabaseReference bombRef = userRef.push();

            String key = mDatabaseRef.child("Bombs").child(userID).push().getKey();
            mDatabaseRef.child("Users").child(userID).child("Bombs").child(key).child("bombID").setValue(key);
            mDatabaseRef.child("Users").child(userID).child("Bombs").child(key).child("bombName").setValue(bomb_name);
            mDatabaseRef.child("BombData").child(key).child("bombName").setValue(bomb_name);
            mDatabaseRef.child("BombData").child(key).child("Recipients").child(userID).setValue(userName);
            mDatabaseRef.child("BombData").child(key).child("start").setValue(startDate);
            mDatabaseRef.child("BombData").child(key).child("end").setValue(endDate);


            //String path = "Groups/" + mCurrentUserId + "/" + uniqueGroupID; //path to group ID

            //Pair recipeients = new Pair(userRef.toString(), userName);
            //mDatabaseRef.child("Groups").child(userRef.toString()).child("Recipients").setValue(recipeients);


            //mDatabaseRef.setValue(dataMap);//.addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()){
//
            // Closes registration dialogue
            Intent mainIntent = new Intent(BombCreationActivity.this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Makes sure back button doesn't lead you back to Start Page
            startActivity(mainIntent);
            finish();
//                }?
//            }
//        });
        } else {
            Toast.makeText(BombCreationActivity.this, "Start Date has to be earlier than the End Date!",
                    Toast.LENGTH_LONG).show();
        }
    }
}
