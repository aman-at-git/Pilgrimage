package com.android.pilgrimage;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.pilgrimage.ui.slideshow.SlideshowFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class PostAdapter extends FirebaseRecyclerAdapter<Posts, PostAdapter.PostViewHolder> {

    public PostAdapter(@NonNull FirebaseRecyclerOptions<Posts> options) {
        super(options);
    }

    private FirebaseAuth mAuth;
    private String  current_user;
    private DatabaseReference likeDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
    private boolean likeStatus;

    @Override
    protected void onBindViewHolder(@NonNull final PostViewHolder holder, final int position, @NonNull Posts model) {

        Picasso.get().load(model.getLink()).placeholder(R.drawable.default_pic).into(holder.postImage);


        holder.heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    holder.heart.setImageResource(R.drawable.red_heart);
            }
        });
    }

    private void removeLike() {


    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_post_layout, parent, false);

        return new PostViewHolder(v);
    }

    class PostViewHolder extends RecyclerView.ViewHolder{

        TextView linker;
        ImageView postImage;
        ImageView heart;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            linker = itemView.findViewById(R.id.creator_name);
            postImage = itemView.findViewById(R.id.post_image);
            heart = itemView.findViewById(R.id.like_img);
        }
    }
}