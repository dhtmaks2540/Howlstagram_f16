package com.example.howlstagram_f16.navigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.howlstagram_f16.LoginActivity;
import com.example.howlstagram_f16.MainActivity;
import com.example.howlstagram_f16.R;
import com.example.howlstagram_f16.navigation.model.ContentDTO;
import com.example.howlstagram_f16.navigation.model.FollowDTO;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class UserFragment extends Fragment {
    View fragmentView = null;
    RecyclerView recyclerAccountView = null;
    FirebaseFirestore firestore = null;
    String uid = null;
    FirebaseAuth auth = null;
    String currentUserId = null;

    TextView accountPostCount = null;
    Button accountFollowSignout = null;
    TextView toolbarUserName = null;
    ImageView toolbarBack = null;
    BottomNavigationView bottomNavigationView = null;
    ImageView toolbarTitleLogo = null;
    ImageView accountProfile = null;

    public static int PICK_PROFILE_FROM_ALBUM = 10;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_user, container, false);
        uid = getArguments().getString("destinationUid");
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        accountFollowSignout = fragmentView.findViewById(R.id.btn_account_follow_signout);

        if(uid == currentUserId) {
            // MyPage
            accountFollowSignout.setText(getString(R.string.signout));
            accountFollowSignout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    auth.signOut();
                }
            });
        } else {
            // Other user page
            accountFollowSignout.setText(getString(R.string.follow));
            final MainActivity mainActivity = (MainActivity)getActivity();
            toolbarUserName = mainActivity.findViewById(R.id.tv_toolbar_username);
            toolbarUserName.setText(getArguments().getString("userId"));
            toolbarBack = mainActivity.findViewById(R.id.iv_toolbar_back);
            toolbarBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomNavigationView = mainActivity.findViewById(R.id.bnv_main);
                    bottomNavigationView.setSelectedItemId(R.id.action_home);
                }
            });
            toolbarTitleLogo = mainActivity.findViewById(R.id.iv_toolbar_title);
            toolbarTitleLogo.setVisibility(View.GONE);
            toolbarUserName.setVisibility(View.VISIBLE);
            toolbarBack.setVisibility(View.VISIBLE);

            // Follow 버튼 이벤트
            accountFollowSignout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestFollow();
                }
            });
        }

        accountPostCount = fragmentView.findViewById(R.id.tv_account_post_count);

        recyclerAccountView = fragmentView.findViewById(R.id.rcv_accont);
        recyclerAccountView.setAdapter(new UserFragmentRecyclerViewAdapter());
        recyclerAccountView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        // 프로필사진 클릭 이벤트
        accountProfile = fragmentView.findViewById(R.id.iv_account_profile);
        accountProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                getActivity().startActivityForResult(photoPickerIntent, PICK_PROFILE_FROM_ALBUM);
            }
        });

        // 프로필 이미지 불러오기
        getProfileImage();
        return fragmentView;
    }

    void requestFollow() {
        // Save data to my account
        final DocumentReference tsDocFollowing = firestore.collection("users").document(currentUserId);
        firestore.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                FollowDTO followDTO = transaction.get(tsDocFollowing).toObject(FollowDTO.class);
                if(followDTO == null) {
                    followDTO = new FollowDTO();
                    followDTO.setFollowingCount(1);
                    followDTO.getFollowings().put(uid, true);

                    transaction.set(tsDocFollowing, followDTO);
                }

                if(followDTO.getFollowings().containsKey(uid)) {
                    // It remove following third person when a third person follow me
                    followDTO.setFollowingCount(followDTO.getFollowingCount() - 1);
                    followDTO.getFollowers().remove(uid);
                } else {
                    // It add following third person when a third person do not follow me
                    followDTO.setFollowingCount(followDTO.getFollowingCount() + 1);
                    followDTO.getFollowers().put(uid, true);
                }

                transaction.set(tsDocFollowing, followDTO);
                return null;
            }
        });

        // Save data to third person
        final DocumentReference tsDocFollower = firestore.collection("users").document(uid);
        firestore.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                FollowDTO followDTO = transaction.get(tsDocFollowing).toObject(FollowDTO.class);
                if(followDTO == null) {
                    followDTO = new FollowDTO();
                    followDTO.setFollowerCount(1);
                    followDTO.getFollowers().put(uid, true);

                    transaction.set(tsDocFollower, followDTO);
                }

                if(followDTO.getFollowers().containsKey(currentUserId)) {
                    // It cancel my follower when i follow a third person
                    followDTO.setFollowerCount(followDTO.getFollowerCount() - 1);
                    followDTO.getFollowers().remove(currentUserId);
                } else {
                    // It add my follower when i don't follow a third person
                    followDTO.setFollowerCount(followDTO.getFollowerCount() + 1);
                    followDTO.getFollowers().put(currentUserId, true);
                }

                transaction.set(tsDocFollower, followDTO);
                return null;
            }
        });
    }

    void getProfileImage() {
        firestore.collection("profileImages").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if(documentSnapshot == null) return;
                if(documentSnapshot != null) {
                    String url = (String)documentSnapshot.getData().get("image");
                    Glide.with(getActivity()).load(url).apply(RequestOptions.circleCropTransform()).into(accountProfile);
                }
            }
        });
    }

    class UserFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        ImageView imageView = null;
        ArrayList<ContentDTO> contentDTOs = new ArrayList<>();

        UserFragmentRecyclerViewAdapter() {
            firestore.collection("images").whereEqualTo("uid",uid).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    // Sometimes, This code return null of queryDocumentSnapshots when it signout
                    if(queryDocumentSnapshots == null) return;

                    // Get Data
                    for(DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        contentDTOs.add(snapshot.toObject(new ContentDTO().getClass()));
                    }

                    accountPostCount.setText(Integer.toString(contentDTOs.size()));
                    // RecyclerView가 새로고침 될 수 있도록
                    notifyDataSetChanged();
                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int width = getResources().getDisplayMetrics().widthPixels / 3;
            imageView = new ImageView(parent.getContext());
            imageView.setLayoutParams(new LinearLayout.LayoutParams(width,width));
            return new CustomViewHolder(imageView);
        }

        class CustomViewHolder extends RecyclerView.ViewHolder {
            public CustomViewHolder(@NonNull ImageView imageView) {
                super(imageView);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Glide.with(holder.itemView.getContext()).load(contentDTOs.get(position).getImageUrl()).apply(RequestOptions.centerCropTransform()).into(imageView);
        }

        @Override
        public int getItemCount() {
            return contentDTOs.size();
        }
    }
}
