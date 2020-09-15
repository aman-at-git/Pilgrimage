package com.android.pilgrimage.ui.slideshow;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

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

import com.android.pilgrimage.R;
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

public class SlideshowFragment extends Fragment {


    private  View mMainView;
    private Button upload;
    private FirebaseUser mCurrentUser;
    private StorageReference mImageStorage;
    private DatabaseReference mUserDatabase;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_slideshow, container, false);

        upload = mMainView.findViewById(R.id.upload_btn);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(getActivity());
            }
        });

        return mMainView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mImageStorage = FirebaseStorage.getInstance().getReference();
        mUserDatabase = FirebaseDatabase.getInstance().getReference();

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                final Uri resultUri = result.getUri();

                final StorageReference filepath = mImageStorage.child("posts");


                Uri file = resultUri;
                StorageReference riversRef = filepath.child("images/rivers.jpg");

                riversRef.putFile(file)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                Toast.makeText(getContext(), "Image Upload Success", Toast.LENGTH_LONG).show();
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



              /*  filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    final String download_url = task.getResult().toString();
//

                                    Map update_hashmap = new HashMap();
                                    update_hashmap.put("image", download_url);

                                    mUserDatabase.updateChildren(update_hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Done Upload", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(getContext(), "Thumbnail Upload Error", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getContext(), "Image Upload Error", Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(getContext(), "Image Upload Error", Toast.LENGTH_LONG).show();
        }*/
        }

    }
}

