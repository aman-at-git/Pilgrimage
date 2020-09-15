package com.android.pilgrimage.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.pilgrimage.PostAdapter;
import com.android.pilgrimage.Posts;
import com.android.pilgrimage.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeFragment extends Fragment {

    private View mMainView;
    private DatabaseReference mLinkDatabase;
    private RecyclerView mPostsList;
    private PostAdapter adapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_home,container,  false);

        mPostsList = mMainView.findViewById(R.id.home_recycler);
        mPostsList.setLayoutManager(new LinearLayoutManager(mMainView.getContext()));

        mLinkDatabase = FirebaseDatabase.getInstance().getReference().child("images").child("category").child("all");

        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(mLinkDatabase, Posts.class)
                .build();

        adapter = new PostAdapter(options);
        mPostsList.setAdapter(adapter);

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

