package com.example.james.rchat;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoProvider;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter  extends  RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {


    private List<Messages> mMessageList;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private DatabaseReference mUserDatabase;
    private Context context;
    private SimpleExoPlayerView simpleExoPlayerView;

    public MessageAdapter(List<Messages> mMessageList, Context x) {
        this.mMessageList = mMessageList;
        context = x;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout,parent,false);

        return new MessageViewHolder(v);

    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
//        public TextView timeText;
        public CircleImageView profileImage;
        public ImageView messageImage;
        public VideoView messageVideo;

        public MessageViewHolder(View view) {
            super(view);


            messageText = (TextView) view.findViewById(R.id.message_text_layout);
//            timeText = (TextView) view.findViewById(R.id.message_item_time);
            profileImage = (CircleImageView) view.findViewById(R.id.user_single_image);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
//            messageVideo = (VideoView) view.findViewById(R.id.message_video_layout);
            simpleExoPlayerView = (SimpleExoPlayerView) view.findViewById(R.id.exoplayer);

        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        String current_user_id = mAuth.getCurrentUser().getUid();

        Messages c = mMessageList.get(i);

        String from_user = c.getFrom();
        String message_type = c.getType();

        if(from_user.equals(current_user_id)){
            viewHolder.messageText.setBackgroundResource(R.drawable.user_message_text_background);
            //viewHolder.messageText.setBackgroundColor(Color.WHITE);
            viewHolder.messageText.setTextColor(Color.WHITE);

        }else{

            viewHolder.messageText.setBackgroundResource(R.drawable.message_text_background);
            viewHolder.messageText.setTextColor(Color.WHITE);

        }

//        viewHolder.messageText.setText(c.getMessage());
//        viewHolder.timeText.setText(c.getTime());
//        ---------PROFILE IMAGE SETTING HERE (NOT WORKING YET) ------------------------------------
       mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String image = dataSnapshot.child("image").getValue().toString();

                Picasso.get().load(image).placeholder(R.drawable.default_pic).into(viewHolder.profileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(message_type.equals("text")) {

            viewHolder.messageImage.setVisibility(View.GONE);
            simpleExoPlayerView.setVisibility(View.GONE);
            viewHolder.messageText.setVisibility(View.VISIBLE);
            viewHolder.messageText.setText(c.getMessage());

        } else if (message_type.equals("image")) {
            viewHolder.messageText.setVisibility(View.GONE);
            simpleExoPlayerView.setVisibility(View.GONE);
            viewHolder.messageImage.setVisibility(View.VISIBLE);
            Picasso.get().load(c.getMessage())
                    .placeholder(R.drawable.default_pic).into(viewHolder.messageImage);
        } else if (message_type.equals("video")){
            viewHolder.messageImage.setVisibility(View.GONE);
            viewHolder.messageText.setVisibility(View.GONE);
//            viewHolder.messageVideo.setVisibility(View.VISIBLE);
//
//            MediaController mediaController = new MediaController(context);
//            mediaController.setAnchorView(viewHolder.messageVideo);
//            viewHolder.messageVideo.setMediaController(mediaController);
//
            String path = c.getMessage();
            Uri uri = Uri.parse(path);
//            viewHolder.messageVideo.setVideoURI(uri);
//            viewHolder.messageVideo.start();


                // Create a default TrackSelector
                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                TrackSelection.Factory videoTrackSelectionFactory =
                        new AdaptiveTrackSelection.Factory(bandwidthMeter);
                TrackSelector trackSelector =
                        new DefaultTrackSelector(videoTrackSelectionFactory);

                //Initialize the player
                SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

                //Initialize simpleExoPlayerView
                simpleExoPlayerView.setPlayer(player);

                // Produces DataSource instances through which media data is loaded.
                DataSource.Factory dataSourceFactory =
                        new DefaultDataSourceFactory(context, Util.getUserAgent(context, "CloudinaryExoplayer"));

                // Produces Extractor instances for parsing the media data.
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

                // This is the MediaSource representing the media to be played.
                MediaSource videoSource = new ExtractorMediaSource(uri,
                        dataSourceFactory, extractorsFactory, null, null);

                // Prepare the player with the source.
                player.prepare(videoSource);

            simpleExoPlayerView.setVisibility(View.VISIBLE);
            simpleExoPlayerView.requestFocus();

        }
    }


    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


}
