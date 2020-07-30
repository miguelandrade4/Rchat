package com.example.james.rchat;

        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.net.Uri;
        import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.support.v4.app.Fragment;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;

        import com.firebase.ui.database.FirebaseRecyclerAdapter;
        import com.firebase.ui.database.FirebaseRecyclerOptions;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.database.ChildEventListener;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.Query;
        import com.google.firebase.database.ValueEventListener;
        import com.squareup.picasso.Picasso;
        import android.support.v7.app.AppCompatActivity;

        import de.hdodenhof.circleimageview.CircleImageView;

public class BombArchiveList extends AppCompatActivity {

//    private RecyclerView mTopicsList;
//
//    private DatabaseReference mTopicsIDDatabase;
//    private DatabaseReference mTopicsDataDatabase;
//
//    private FirebaseAuth mAuth;
//
//    private String mCurrent_user_id;
//
//    private View mMainView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_bomb_archive_list);
//    }
//
//    package com.example.james.rchat;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.provider.ContactsContract;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.firebase.ui.database.FirebaseRecyclerAdapter;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
//import com.firebase.ui.database.SnapshotParser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.Query;
//import com.squareup.picasso.Picasso;
//
//import java.util.List;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//    public class Search extends AppCompatActivity {
//
//        private EditText searchUser;
//        private Button userButton;
//        private RecyclerView resultList;
//        private DatabaseReference userDatabase;
//
//        private EditText searchEmail;
//        private Button   emailButton;
//
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.activity_search);
//
//            userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
//
//            resultList = (RecyclerView) findViewById(R.id.result_list);
//            resultList.setLayoutManager(new LinearLayoutManager(this));
//            resultList.setHasFixedSize(true);
//        }
//
//        @Override
//        protected void onStart() {
//            super.onStart();
//            String searchText = "";
//            Query SearchQuery = userDatabase.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");
//            firebaseUserSearch(SearchQuery);
//        }
//
//        public static void hideKeyboard(Activity activity) {
//            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
//            //Find the currently focused view, so we can grab the correct window token from it.
//            View view = activity.getCurrentFocus();
//            //If no view currently has focus, create a new one, just so we can grab a window token from it
//            if (view == null) {
//                view = new View(activity);
//            }
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
//
//        private void firebaseUserSearch(Query SearchQuery) {
//
//            FirebaseRecyclerOptions<Users> options =
//                    new FirebaseRecyclerOptions.Builder<Users>()
//                            .setQuery(SearchQuery, Users.class)
//                            .build();
//
//            FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Users, com.example.james.rchat.Search.UserViewHolder>(options) {
//                @Override
//                public com.example.james.rchat.Search.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                    View view = LayoutInflater.from(parent.getContext())
//                            .inflate(R.layout.search_layout, parent, false);
//
//                    return new com.example.james.rchat.Search.UserViewHolder(view);
//                }
//
//                @Override
//                protected void onBindViewHolder(com.example.james.rchat.Search.UserViewHolder userViewHolder, int position, Users model) {
//                    userViewHolder.setName(model.name);
//                    userViewHolder.setStatus("");
//                    userViewHolder.setUserImage(model.image, getApplicationContext());
//                    final String user_id = getRef(position).getKey();
//                    userViewHolder.mView.setOnClickListener(new View.OnClickListener(){
//                        @Override
//                        public void onClick(View view){
//
//                            Intent profileIntent = new Intent(com.example.james.rchat.Search.this, ProfileActivity.class);
//                            profileIntent.putExtra("user_id", user_id);
//                            startActivity(profileIntent);
//                        }
//                    });
//                }
//
//            };
//            resultList.setAdapter(adapter);
//            adapter.startListening();
//            hideKeyboard(com.example.james.rchat.Search.this);
//        }
//
//
//
//        public static class UserViewHolder extends RecyclerView.ViewHolder {
//            View mView;
//            public UserViewHolder(View itemView) {
//                super(itemView);
//                mView = itemView;
//            }
//
//            public void setName(String name){
//                TextView userNameView = (TextView) mView.findViewById(R.id.search_single_name);
//                userNameView.setText(name);
//            }
//
//            public void setUserImage(String imageurl, Context ctx){
//                CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.search_single_image);
//                Picasso.get().load(imageurl).placeholder(R.drawable.default_pic).into(userImageView);
//
//            }
//
//            public void setStatus(String mStatus)
//            {
//                TextView statusView = (TextView) mView.findViewById(R.id.search_single_status);
//                statusView.setText(mStatus);
//            }
//        }
//    }

}