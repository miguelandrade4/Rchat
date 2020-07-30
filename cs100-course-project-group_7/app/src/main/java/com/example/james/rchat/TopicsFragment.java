package com.example.james.rchat;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class TopicsFragment extends Fragment {

    private RecyclerView mTopicsList;

    private DatabaseReference mTopicsIDDatabase;
    private DatabaseReference mTopicsDataDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;

    public TopicsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_topics, container, false);
        mTopicsList = (RecyclerView) mMainView.findViewById(R.id.topicsList);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mTopicsIDDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrent_user_id).child("Topics");
        mTopicsDataDatabase = FirebaseDatabase.getInstance().getReference().child("TopicData");
        LinearLayoutManager linLayout = new LinearLayoutManager(getContext());
        mTopicsList.setHasFixedSize(true);
        mTopicsList.setLayoutManager(linLayout);

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        topicsDisplay();
    }

    public void topicsDisplay(){
        Query topicsQuery = mTopicsIDDatabase;    //TODO modify so only group thay user is in can be viewed



        FirebaseRecyclerOptions<Topics> options =
                new FirebaseRecyclerOptions.Builder<Topics>()
                        .setQuery(topicsQuery, Topics.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Topics, TopicsFragment.TopicsViewHolder>(options) {
            @Override
            public TopicsFragment.TopicsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.topics_single_layout, parent, false);

                return new TopicsFragment.TopicsViewHolder(view);
            }

            @Override
            public void onBindViewHolder(final TopicsFragment.TopicsViewHolder topicsViewHolder, int position, Topics model) {
                topicsViewHolder.setTopicName(model.topicName);

                final String topic_id = getRef(position).getKey();

                mTopicsIDDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){


                            topicsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    Intent topicChatIntent = new Intent(getContext(), TopicChatActivity.class);
                                    topicChatIntent.putExtra("topic_id", topic_id);
                                    startActivity(topicChatIntent);

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }


        };


//        mGroupsDataDatabase.child()

        mTopicsList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class TopicsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public TopicsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTopicName(String name){
            TextView TopicNameView = (TextView) mView.findViewById(R.id.topics_single_name);
            TopicNameView.setText(name);
        }
    }
}