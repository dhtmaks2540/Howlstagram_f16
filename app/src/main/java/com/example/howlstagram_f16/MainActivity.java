package com.example.howlstagram_f16;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
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


}
