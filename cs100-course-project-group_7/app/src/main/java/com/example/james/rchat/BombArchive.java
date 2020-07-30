package com.example.james.rchat;

        import android.content.Context;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.net.Uri;
        import android.os.Build;
        import android.provider.MediaStore;
        import android.support.annotation.NonNull;
        import android.support.v4.app.ActivityCompat;
        import android.support.v7.app.ActionBar;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.support.v7.widget.Toolbar;
        import android.text.Editable;
        import android.text.TextUtils;
        import android.text.TextWatcher;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageButton;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.firebase.ui.auth.data.model.User;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.database.ChildEventListener;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ServerValue;
        import com.google.firebase.database.ValueEventListener;
        import com.google.firebase.storage.FirebaseStorage;
        import com.google.firebase.storage.StorageMetadata;
        import com.google.firebase.storage.StorageReference;
        import com.google.firebase.storage.UploadTask;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

        import de.hdodenhof.circleimageview.CircleImageView;

        import static com.example.james.rchat.ChatActivity.REQUEST_VIDEO_CAPTURE;

public class BombArchive extends AppCompatActivity {


    private String mChatUser;
    private String BombName;
    private String ContentType;

    private Toolbar mChatToolbar;
    private String mBombID;
    private DatabaseReference mRootRef;
    private DatabaseReference mBombRef;

    private TextView mTitleView;
    private TextView mLastSeenView;
    private TextView mCurrentlyTyping;
    private CircleImageView mProfileImage;
    private FirebaseAuth mAuth;

    private String mCurrentUserId;

    private EditText mChatMessageView;
    private ImageButton mChatAddBtn;
    private ImageButton mChatSendBtn;
    private ImageButton mBombToMainBtn;
    private Button mAddUserBtn;
    private Button mVideoBtn;


    private final List<Messages> bombarchivemessagesList = new ArrayList<>();
    private MessageAdapter mAdapter;
    private RecyclerView mbombarchivemessagesList;


    private static final int GALLERY_PICK = 1;

    // Firebase Storage
    private StorageReference mImageStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bomb_archive);

        mChatToolbar = (Toolbar) findViewById(R.id.chat_app__bar);
        setSupportActionBar(mChatToolbar);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mBombID = getIntent().getStringExtra("bomb_id");
        mBombRef = mRootRef.child("BombData").child(mBombID);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);

        actionBar.setCustomView(action_bar_view);

        // Custom Action Bar Items

        mTitleView = (TextView) findViewById(R.id.custom_bar_title);
        mLastSeenView = (TextView) findViewById(R.id.custom_bar_seen);
        mVideoBtn = (Button) findViewById(R.id.add_video_to_bomb_menu);

        mBombRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                BombName = dataSnapshot.child("bombName").getValue().toString();
                mTitleView.setText(BombName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        mChatAddBtn = (ImageButton) findViewById(R.id.bomb_add_btn);
//        mChatSendBtn = (ImageButton) findViewById(R.id.bomb_send_btn);
//        mBombToMainBtn = (ImageButton) findViewById(R.id.bomb_to_main_btn);
//        mCurrentlyTyping = (TextView) findViewById(R.id.currently_typing_text);
//        mChatMessageView = (EditText) findViewById(R.id.bomb_message_view);


        //------IMAGE STORAGE-------------
        mImageStorage = FirebaseStorage.getInstance().getReference();

        mAdapter = new MessageAdapter(bombarchivemessagesList, this);
        mbombarchivemessagesList = (RecyclerView) findViewById(R.id.bombarchivemessages_list);


        final LinearLayoutManager mLinearLayout = new LinearLayoutManager(this);

        mbombarchivemessagesList.setHasFixedSize(true);
        mbombarchivemessagesList.setItemViewCacheSize(20);
        mbombarchivemessagesList.setDrawingCacheEnabled(true);
        mbombarchivemessagesList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mbombarchivemessagesList.setLayoutManager(mLinearLayout);
        mbombarchivemessagesList.addOnLayoutChangeListener(new View.OnLayoutChangeListener(){
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom){
                if (bottom < oldBottom && ((mbombarchivemessagesList.getAdapter().getItemCount() - 1) > 3)){
                    mbombarchivemessagesList.postDelayed(new Runnable(){
                        @Override
                        public void run() {
                            mbombarchivemessagesList.smoothScrollToPosition(
                                    mbombarchivemessagesList.getAdapter().getItemCount() - 1);
                        }
                    }, 100);
                }
            }
        });

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver(){
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount){
                super.onItemRangeInserted(positionStart, itemCount);
                int count = mAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayout.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (count - 1) &&
                                lastVisiblePosition == (positionStart - 1))){
                    mbombarchivemessagesList.smoothScrollToPosition(positionStart);
                }
            }
        });

        mbombarchivemessagesList.setAdapter(mAdapter);

        loadMessages();

//        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                sendMessage();
//                mbombarchivemessagesList.smoothScrollToPosition(mbombarchivemessagesList.getAdapter().getItemCount()); //scrolls to the bottom with new message.
//            }
//        });
//
//        mBombToMainBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v){
//                Intent toMainIntent = new Intent(BombArchive.this, MainActivity.class);
//                finish();
//                startActivity(toMainIntent);
//            }
//        });
//
//        mChatAddBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view){
//
//                Intent galleryIntent = new Intent();
//                galleryIntent.setType("*/*");
//                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//
//                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
//
//            }
//        });
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK && isStoragePermissionGranted()){

            Uri imageUri = data.getData();

            final String current_user_ref = "messages/";

            DatabaseReference user_message_push = mBombRef.child("messages").push();

            final String push_id = user_message_push.getKey();

            final StorageReference filepath = mImageStorage.child("message_images").child(push_id);

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {
                        filepath.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                            @Override
                            public void onSuccess(StorageMetadata storageMetadata) {
                                ContentType = storageMetadata.getContentType();

                                String download_uri = task.getResult().getDownloadUrl().toString();

                                Map messageMap = new HashMap();
                                messageMap.put("message", download_uri);
                                if (ContentType.startsWith("image")) {
                                    messageMap.put("type", "image");
                                } else if (ContentType.startsWith("video")) {
                                    messageMap.put("type", "video");
                                }

                                messageMap.put("from", mCurrentUserId);
                                messageMap.put("timestamp", ServerValue.TIMESTAMP);

                                Map messageUserMap = new HashMap();
                                messageUserMap.put(current_user_ref + "/" + push_id, messageMap);

                                mChatMessageView.setText("");

                                mBombRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                        if (databaseError != null) {

                                            Log.d("CHAT_LOG", databaseError.getMessage().toString());

                                        }

                                    }
                                });
                            }
                        });
                    }
                }
            });
        } else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK && isStoragePermissionGranted()){

            Uri imageUri = data.getData();

            final String current_user_ref = "messages/";

            DatabaseReference user_message_push = mBombRef.child("messages").push();

            final String push_id = user_message_push.getKey();

            final StorageReference filepath = mImageStorage.child("message_images").child(push_id);

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {

                        String download_uri = task.getResult().getDownloadUrl().toString();

                        Map messageMap = new HashMap();
                        messageMap.put("message", download_uri);
                        messageMap.put("type", "video");
                        messageMap.put("timestamp", ServerValue.TIMESTAMP);
                        messageMap.put("from", mCurrentUserId);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);

                        mChatMessageView.setText("");

                        mBombRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if(databaseError != null){

                                    Log.d("CHAT_LOG",databaseError.getMessage().toString());

                                }

                            }
                        });

                    }
                }
            });
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return true;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    private void loadMessages() {

        mBombRef.child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);

                bombarchivemessagesList.add(message);

                mAdapter.notifyDataSetChanged();

                mbombarchivemessagesList.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        mbombarchivemessagesList.smoothScrollToPosition(
                                mbombarchivemessagesList.getAdapter().getItemCount() - 1);
                    }
                }, 100);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

//    private void sendMessage() {
//
//        String message = mChatMessageView.getText().toString();
//
//        if(!TextUtils.isEmpty(message)){
//
//            DatabaseReference user_message_push = mBombRef.child("messages").push();
//
//            String push_id = user_message_push.getKey();
//
//            Map messageMap = new HashMap();
//            messageMap.put("message", message);
//            messageMap.put("type", "text");
//            messageMap.put("timestamp", ServerValue.TIMESTAMP);
//            messageMap.put("from", mCurrentUserId);
//
//            Map messageUserMap = new HashMap();
//            messageUserMap.put("messages" + "/" + push_id, messageMap);
//
//            mChatMessageView.setText("");
//
//            mBombRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
//                @Override
//                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//
//                    if(databaseError != null){
//
//                        Log.d("CHAT_LOG",databaseError.getMessage().toString());
//
//                    }
//
//                }
//            });
//        }
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//
//        getMenuInflater().inflate(R.menu.bomb_menu, menu);
//
//        return true;
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        super.onOptionsItemSelected(item);
//
//        if(item.getItemId() == R.id.add_user_to_bomb_menu){
//
//            Intent startBombChat = new Intent(BombArchive.this, GroupUserSearch.class );
//            startBombChat.putExtra("bombID", mBombID);
//            startActivity(startBombChat);
//        }
//
//        if(item.getItemId() == R.id.add_video_to_bomb_menu){
//
//            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//            takeVideoIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//
//            if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
//                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
//            }
//        }
//
//        return true;
//    }
}
