package com.android.pilgrimage.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.pilgrimage.LikeAdapter;
import com.android.pilgrimage.Login;
import com.android.pilgrimage.PostAdapter;
import com.android.pilgrimage.Posts;
import com.android.pilgrimage.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GalleryFragment extends Fragment {

    private View mMainView;
    private DatabaseReference mLinkDatabase;
    private RecyclerView mLikedPostsList;
    private LikeAdapter adapter;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private String mCurrUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_gallery,container,  false);

        mLikedPostsList = mMainView.findViewById(R.id.liked_recycler);
        mLikedPostsList.setLayoutManager(new LinearLayoutManager(mMainView.getContext()));
        mCurrUser = mAuth.getUid();

        mLinkDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrUser).child("likedPost");

        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(mLinkDatabase, Posts.class)
                .build();



        adapter = new LikeAdapter(options);
        mLikedPostsList.setAdapter(adapter);

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}