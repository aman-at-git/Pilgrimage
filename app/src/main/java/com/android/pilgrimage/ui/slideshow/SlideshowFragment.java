package com.android.pilgrimage.ui.slideshow;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.pilgrimage.LikeAdapter;
import com.android.pilgrimage.PostAdapter;
import com.android.pilgrimage.Posts;
import com.android.pilgrimage.R;
import com.android.pilgrimage.UploadAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.datatransport.runtime.time.TimeModule;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.ACTIVITY_SERVICE;

public class SlideshowFragment extends Fragment {


    private  View mMainView;
    private Button upload;
    private FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    private StorageReference mImageStorage;
    private DatabaseReference mAllPostDatabase = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mPostDatabase = FirebaseDatabase.getInstance().getReference();
    public static final int CODE = 1;
    String download_url;
    private DatabaseReference mUserPostDatabase;
    private RecyclerView mUserPostList;
    private UploadAdapter adapter;
    private FirebaseAuth mAuth= FirebaseAuth.getInstance();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_slideshow, container, false);

        mUserPostList = mMainView.findViewById(R.id.my_post_recycler);
        mUserPostList.setLayoutManager(new LinearLayoutManager(mMainView.getContext()));

        String mCurrUser = mAuth.getUid();

        mUserPostDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrUser).child("userPosts");

        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(mUserPostDatabase, Posts.class)
                .build();

        adapter = new UploadAdapter(options);
        mUserPostList.setAdapter(adapter);
        upload = mMainView.findViewById(R.id.upload_btn);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/jpeg");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"),CODE);
                }
                catch (Exception e){
                    Toast.makeText(getActivity(),"Button Error", Toast.LENGTH_SHORT).show();
                }

            }
        });

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==CODE && resultCode== RESULT_OK) {

            Uri resultUri = data.getData();
            final String imgUri = data.getDataString();

            //Toast.makeText(getContext(), "1", Toast.LENGTH_LONG).show();

            mImageStorage=FirebaseStorage.getInstance().getReference();

            final StorageReference filepath = mImageStorage.child("post").child(imgUri);

            filepath.putFile(resultUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    download_url = task.getResult().toString();
                                    final String user_id = mCurrentUser.getUid();

                                    HashMap<String , String > Map = new HashMap<>();
                                    Map.put("user", user_id);
                                    Map.put("link", download_url);
                                    mPostDatabase.child("Users").child(user_id).child("userPosts").push().child("link").setValue(download_url);
                                    mAllPostDatabase.child("images").child("category").child("all").push().setValue(Map);
                                }
                            });
                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(getContext(), "Image Upload Error", Toast.LENGTH_LONG).show();
                        }
                    });

        }
//            CropImage.activity(imageUri)
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .setAspectRatio(1, 1)
//                    .start(getActivity());
//        }
        }

}

