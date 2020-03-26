package com.example.howlstagram_f16;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.howlstagram_f16.navigation.AddPhotoActivity;
import com.example.howlstagram_f16.navigation.AlarmFragment;
import com.example.howlstagram_f16.navigation.DetailViewFragment;
import com.example.howlstagram_f16.navigation.GridFragment;
import com.example.howlstagram_f16.navigation.UserFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    TextView toolbarUserName = null;
    ImageView toolbarBack = null;
    ImageView toolbarTitleImage = null;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        setToolbarDefault();
        switch (menuItem.getItemId()) {
            case R.id.action_home :
                DetailViewFragment detailViewFragment = new DetailViewFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_main_content, detailViewFragment).commit();
                return true;

            case R.id.action_search :
                GridFragment gridFragment = new GridFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_main_content, gridFragment).commit();
                return true;

            case R.id.action_add_photo :
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(this, AddPhotoActivity.class));
                }

                return true;

            case R.id.action_favorite_alarm :
                AlarmFragment alarmFragment = new AlarmFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_main_content, alarmFragment).commit();
                return true;

            case R.id.action_account :
                UserFragment userFragment = new UserFragment();
                Bundle bundle = new Bundle();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                bundle.putString("destinationUid", uid);
                userFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_main_content, userFragment).commit();
                return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String word = Manifest.permission.READ_EXTERNAL_STORAGE;
        ActivityCompat.requestPermissions(this, new String[]{word}, 1);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bnv_main);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        toolbarUserName = findViewById(R.id.tv_toolbar_username);
        toolbarBack = findViewById(R.id.iv_toolbar_back);
        toolbarTitleImage = findViewById(R.id.iv_toolbar_title);

        // Set default screen
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    void setToolbarDefault() {
        toolbarUserName.setVisibility(View.GONE);
        toolbarBack.setVisibility(View.GONE);
        toolbarTitleImage.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == UserFragment.PICK_PROFILE_FROM_ALBUM && resultCode == Activity.RESULT_OK) {
            Uri imageUri = data.getData();
            final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("userProfileImages").child(uid);
            storageRef.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return storageRef.getDownloadUrl();
                }
            }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Map<String, String> map = new HashMap<>();
                    map.put("image", uri.toString());
                    FirebaseFirestore.getInstance().collection("profileImages").document(uid).set(map);
                }
            });
        }
    }
}
