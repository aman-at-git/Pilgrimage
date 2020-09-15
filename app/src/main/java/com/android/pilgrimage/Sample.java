package com.android.pilgrimage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Sample extends AppCompatActivity {

    private RecyclerView ppp;
    private DatabaseReference mLinkDatabase;
    private PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        ppp = findViewById(R.id.ppp);
        ppp.setLayoutManager(new LinearLayoutManager(this));
        mLinkDatabase = FirebaseDatabase.getInstance().getReference().child("images").child("category").child("all");

        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(mLinkDatabase, Posts.class)
                .build();

        adapter = new PostAdapter(options);
        ppp.setAdapter(adapter);
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