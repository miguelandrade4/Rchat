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

public class BombsFragment extends Fragment {

    private RecyclerView mBombsList;

    private DatabaseReference mBombsIDDatabase;
    private DatabaseReference mBombsDataDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;

    public BombsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_bombs, container, false);
        mBombsList = (RecyclerView) mMainView.findViewById(R.id.bombsList);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mBombsIDDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrent_user_id).child("Bombs");
        mBombsDataDatabase = FirebaseDatabase.getInstance().getReference().child("BombData");
        LinearLayoutManager linLayout = new LinearLayoutManager(getContext());
        mBombsList.setHasFixedSize(true);
        mBombsList.setLayoutManager(linLayout);

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        bombsDisplay();
    }

    public void bombsDisplay(){
        Query bombsQuery = mBombsIDDatabase;    //TODO modify so only group thay user is in can be viewed



        FirebaseRecyclerOptions<Bombs> options =
                new FirebaseRecyclerOptions.Builder<Bombs>()
                        .setQuery(bombsQuery, Bombs.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Bombs, BombsFragment.BombsViewHolder>(options) {
            @Override
            public BombsFragment.BombsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.groups_single_layout, parent, false);

                return new BombsFragment.BombsViewHolder(view);
            }

            @Override
            public void onBindViewHolder(final BombsFragment.BombsViewHolder bombsViewHolder, int position, Bombs model) {
                bombsViewHolder.setBombName(model.bombName);

                final String bomb_id = getRef(position).getKey();

                mBombsIDDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){


                            bombsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    Intent bombChatIntent = new Intent(getContext(), BombActivity.class);
                                    bombChatIntent.putExtra("bomb_id", bomb_id);
                                    startActivity(bombChatIntent);

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

        mBombsList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class BombsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public BombsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setBombName(String name){
            TextView GroupNameView = (TextView) mView.findViewById(R.id.groups_single_name);
            GroupNameView.setText(name);
        }
    }
}
