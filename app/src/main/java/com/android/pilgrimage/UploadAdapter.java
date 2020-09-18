package com.android.pilgrimage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UploadAdapter extends FirebaseRecyclerAdapter<Posts, UploadAdapter.PostViewHolder> {

    public UploadAdapter(@NonNull FirebaseRecyclerOptions<Posts> options) {
        super(options);
    }

    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onBindViewHolder(@NonNull final PostViewHolder holder, final int position, @NonNull final Posts model) {

        Picasso.get().load(model.getLink()).placeholder(R.drawable.default_image).into(holder.postImage);
        String mCurrUser = mAuth.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrUser).child("userPosts");
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_post_layout_own_posts, parent, false);

        return new PostViewHolder(v);
    }

    class PostViewHolder extends RecyclerView.ViewHolder{

        ImageView postImage;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.post_image);
        }
    }
}