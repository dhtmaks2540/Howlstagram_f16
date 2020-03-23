package com.example.howlstagram_f16.navigation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.howlstagram_f16.R;
import com.example.howlstagram_f16.navigation.model.ContentDTO;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddPhotoActivity extends AppCompatActivity {
    final int PICK_IMAGE_FROM_ALBUM = 0;

    FirebaseStorage storage = null;

    FirebaseAuth auth = null;
    FirebaseFirestore firestore  = null;

    Uri photoUri = null;
    ImageView addPhotoImage = null;
    EditText addPhotoEditExplain = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        addPhotoImage = findViewById(R.id.iv_addphoto);
        addPhotoEditExplain = findViewById(R.id.et_addphoto_edit_explain);

        // Initiate
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Open the album
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM);

        // Add Image upload event
        Button addPhotoUpload = findViewById(R.id.btn_addphoto_upload);
        addPhotoUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentUpload();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_FROM_ALBUM) {
            if(resultCode == Activity.RESULT_OK) {
                // This is path to the selected image (사진을 선택했을 때, 이미지의 경로가 넘어온다)
                photoUri = data.getData();
                addPhotoImage.setImageURI(photoUri);

            } else {
                // Exit the addPhotoActivity if you leave the album without selecting it
                finish();
            }
        }
    }

    void contentUpload() {
        // Make filename
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timestamp + "_.png";

        final StorageReference storageRef = storage.getReference().child("images").child(imageFileName);

       /* storageRef.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), getString(R.string.upload_success), Toast.LENGTH_LONG).show();
            }
        });*/

        /*// Promise method
        Task<Uri> uriTask = storageRef.putFile(photoUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return storageRef.getDownloadUrl();
            }
        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ContentDTO contentDTO = new ContentDTO();

                // Insert downloadUrl of image
                contentDTO.setImageUrl(uri.toString());

                // Insert uid of user
                contentDTO.setUid(auth.getCurrentUser().getUid());

                // Insert userId
                contentDTO.setUserId(auth.getCurrentUser().getEmail());

                // Insert explain of content
                contentDTO.setExplain(addPhotoEditExplain.getText().toString());

                // Insert timestamp
                contentDTO.setTimestamp(System.currentTimeMillis());

                firestore.collection("images").document().set(contentDTO);

                setResult(Activity.RESULT_OK);

                finish();
            }
        });*/

        // Callback method
        storageRef.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ContentDTO contentDTO = new ContentDTO();

                        // Insert downloadUrl of image
                        contentDTO.setImageUrl(uri.toString());

                        // Insert uid of user
                        contentDTO.setUid(auth.getCurrentUser().getUid());

                        // Insert userId
                        contentDTO.setUserId(auth.getCurrentUser().getEmail());

                        // Insert explain of content
                        contentDTO.setExplain(addPhotoEditExplain.getText().toString());

                        // Insert timestamp
                        contentDTO.setTimestamp(System.currentTimeMillis());

                        firestore.collection("images").document().set(contentDTO);

                        setResult(Activity.RESULT_OK);

                        finish();
                    }
                });
            }
        });
    }
}
