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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class PostAdapter extends FirebaseRecyclerAdapter<Posts, PostAdapter.PostViewHolder> {

    public PostAdapter(@NonNull FirebaseRecyclerOptions<Posts> options) {
        super(options);
    }

    private DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

    @Override
    protected void onBindViewHolder(@NonNull final PostViewHolder holder, final int position, @NonNull final Posts model) {

        Picasso.get().load(model.getLink()).placeholder(R.drawable.default_image).into(holder.postImage);
        final String postUser = model.getUser();
        if(postUser!=null) {
            mUserDatabase.child(postUser).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild("name")) {
                            final String name = snapshot.child("name").getValue().toString();
                            holder.userName.setText(name);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {  }
            });
        }
        else{
            holder.userName.setVisibility(View.GONE);
        }

        if(postUser!=null) {
            mUserDatabase.child(postUser).child("likedPost").orderByChild("link").equalTo(model.getLink()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        for (DataSnapshot childSnap : snapshot.getChildren()) {
                            String likeKey = childSnap.getKey();
                            if (likeKey != null) {
                                holder.heart.setImageResource(R.drawable.red_heart);
                            }
                        }
                    }
                    else{
                        holder.heart.setImageResource(R.drawable.like_border);

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {                        }
            });
        }

        holder.heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(postUser!=null) {
                    mUserDatabase.child(postUser).child("likedPost").orderByChild("link").equalTo(model.getLink()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                for (DataSnapshot childSnap : snapshot.getChildren()) {
                                    String likeKey = childSnap.getKey();
                                    if (likeKey != null) {
                                        snapshot.child(likeKey).getRef().removeValue();
                                        holder.heart.setImageResource(R.drawable.like_border);
                                    }
                                }
                            }
                            else{
                                holder.heart.setImageResource(R.drawable.red_heart);
                                mUserDatabase.child(postUser).child("likedPost").push().child("link").setValue(model.getLink());
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {                        }
                    });
                }
            }
        });
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_post_layout_1, parent, false);

        return new PostViewHolder(v);
    }

    class PostViewHolder extends RecyclerView.ViewHolder{

        ImageView postImage;
        ImageView heart;
        TextView userName;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.post_image);
            heart = itemView.findViewById(R.id.like_img);
            userName = itemView.findViewById(R.id.creator_name);
        }
    }
}