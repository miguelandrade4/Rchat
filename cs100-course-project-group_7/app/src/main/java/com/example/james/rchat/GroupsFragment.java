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

public class GroupsFragment extends Fragment {

    private RecyclerView mGroupsList;

    private DatabaseReference mGroupsIDDatabase;
    private DatabaseReference mGroupsDataDatabase;
    private DatabaseReference mListUnArchived;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_groups, container, false);
        mGroupsList = (RecyclerView) mMainView.findViewById(R.id.groupsList);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mGroupsIDDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrent_user_id).child("Groups");
        mGroupsDataDatabase = FirebaseDatabase.getInstance().getReference().child("GroupData");
        LinearLayoutManager linLayout = new LinearLayoutManager(getContext());
        mGroupsList.setHasFixedSize(true);
        mGroupsList.setLayoutManager(linLayout);

        mGroupsIDDatabase.keepSynced(true);

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        groupsDisplay();
    }

    public void groupsDisplay(){
        Query groupsQuery = mGroupsIDDatabase;

        FirebaseRecyclerOptions<Groups> options =
                new FirebaseRecyclerOptions.Builder<Groups>()
                .setQuery(groupsQuery, Groups.class)
                .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Groups, GroupsFragment.GroupsViewHolder>(options) {
            @Override
            public GroupsFragment.GroupsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.groups_single_layout, parent, false);

                return new GroupsFragment.GroupsViewHolder(view);
            }

            @Override
            public void onBindViewHolder(final GroupsFragment.GroupsViewHolder groupsViewHolder, int position, Groups model) {
                groupsViewHolder.setGroupName(model.groupName);

                final String group_id = getRef(position).getKey();




                mGroupsIDDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){


                            groupsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    Intent groupChatIntent = new Intent(getContext(), GroupChatActivity.class);
                                    groupChatIntent.putExtra("group_id", group_id);
                                    startActivity(groupChatIntent);

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

        mGroupsList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class GroupsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public GroupsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setGroupName(String name){
            TextView GroupNameView = (TextView) mView.findViewById(R.id.groups_single_name);
            GroupNameView.setText(name);
        }
    }
}
