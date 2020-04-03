package com.example.howlstagram_f16.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.howlstagram_f16.R;
import com.example.howlstagram_f16.navigation.model.AlarmDTO;
import com.example.howlstagram_f16.navigation.model.ContentDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DetailViewFragment extends Fragment {
    FirebaseFirestore firestore = null;
    RecyclerView recyclerDetailViewFragment = null;
    String uid = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_detail, container, false);

        firestore = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        recyclerDetailViewFragment = view.findViewById(R.id.rcv_detailviewfragment);
        recyclerDetailViewFragment.setAdapter(new DetailViewRecyclerViewAdapter());
        recyclerDetailViewFragment.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    class DetailViewRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        ArrayList<ContentDTO> contentDTOs = new ArrayList<>();
        ArrayList<String> contentUidList = new ArrayList<>();
        protected TextView detailViewItemProfile;
        protected TextView detailViewItemExplain;
        protected TextView detailViewitemFavoriteCounter;
        protected ImageView detailViewItemContent;
        protected ImageView detailViewItemProfileImage;
        protected ImageView detailViewItemFavoriteImageview;
        protected ImageView detailViewItemComment;

        DetailViewRecyclerViewAdapter() {
            firestore.collection("images").orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    contentDTOs.clear();
                    contentUidList.clear();
                    if(queryDocumentSnapshots == null) return;

                    for(DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        ContentDTO item = snapshot.toObject(new ContentDTO().getClass());
                        contentDTOs.add(item);
                        contentUidList.add(snapshot.getId());
                    }
                    // 값이 새로고침 되도록 만들어준다.
                    notifyDataSetChanged();
                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail, parent, false);
            return new CustomViewHolder(view);
        }

        // 메모리를 적게 사용하기 위해서 CustomViewHolder를 만들어달라는 일종의 약속
        class CustomViewHolder extends RecyclerView.ViewHolder {
            public CustomViewHolder(View view) {
                super(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            View viewHolder = holder.itemView;

            // UserId
            detailViewItemProfile = viewHolder.findViewById(R.id.tv_detailviewitem_profile);
            detailViewItemProfile.setText(contentDTOs.get(position).getUserId());

            // Image
            detailViewItemContent = viewHolder.findViewById(R.id.iv_detailviewitem_content);
            Glide.with(holder.itemView).load(contentDTOs.get(position).getImageUrl()).into(detailViewItemContent);

            // Explain of content
            detailViewItemExplain = viewHolder.findViewById(R.id.tv_detailviewitem_explain);
            detailViewItemExplain.setText(contentDTOs.get(position).getExplain());

            // Likes
            detailViewitemFavoriteCounter = viewHolder.findViewById(R.id.tv_detailviewitem_favoritecounter);
            detailViewitemFavoriteCounter.setText("Likes " + contentDTOs.get(position).getFavoriteCount());

            // Profile Image
            detailViewItemProfileImage = viewHolder.findViewById(R.id.iv_detailviewitem_profile);
            Glide.with(holder.itemView).load(contentDTOs.get(position).getImageUrl()).into(detailViewItemProfileImage);

            // This code is when the button is clicked
            detailViewItemFavoriteImageview = viewHolder.findViewById(R.id.iv_detailviewitem_favorite);
            detailViewItemFavoriteImageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favoriteEvent(position);
                }
            });

            // This code is when tha page is loaded
            if(contentDTOs.get(position).getFavorites().containsKey(uid)) {
                // This is like status
                detailViewItemFavoriteImageview.setImageResource(R.drawable.ic_favorite);
            } else {
                // This is unlike status
                detailViewItemFavoriteImageview.setImageResource(R.drawable.ic_favorite_border);
            }

            // This code is when the profile image is clicked
            detailViewItemProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserFragment fragment = new UserFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("destinationUid", contentDTOs.get(position).getUid());
                    bundle.putString("userId", contentDTOs.get(position).getUserId());
                    fragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fl_main_content, fragment).commit();
                }
            });

            // 말풍선 클릭 이벤트
            detailViewItemComment = viewHolder.findViewById(R.id.iv_detailviewitem_comment);
            detailViewItemComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), CommentActivity.class);
                    intent.putExtra("contentUid", contentUidList.get(position));
                    intent.putExtra("destinationUid", contentDTOs.get(position).getUid());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return contentDTOs.size();
        }

        public void favoriteEvent(final int position) {
            final DocumentReference tsDoc = firestore.collection("images").document(contentUidList.get(position));
            firestore.runTransaction(new Transaction.Function<Void>() {

                @Nullable
                @Override
                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                    ContentDTO contentDTO = transaction.get(tsDoc).toObject(ContentDTO.class);

                    if(contentDTO.getFavorites().containsKey(uid)) {
                        // When the button is clicked
                        contentDTO.setFavoriteCount(contentDTO.getFavoriteCount() - 1);
                        contentDTO.getFavorites().remove(uid);
                    } else {
                        // When the button is not clicked
                        contentDTO.setFavoriteCount(contentDTO.getFavoriteCount() + 1);
                        contentDTO.getFavorites().put(uid, true);
                        favoriteAlarm(contentDTOs.get(position).getUid());
                    }

                    transaction.set(tsDoc, contentDTO);

                    return null;
                }
            });
        }

        // 알람기능의 메소드
        void favoriteAlarm(String destinationUid) {
            AlarmDTO alarmDTO = new AlarmDTO();
            alarmDTO.setDestinationUid(destinationUid);
            alarmDTO.setUserId(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            alarmDTO.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
            alarmDTO.setKind(0);
            alarmDTO.setTimestamp(System.currentTimeMillis());

            // Firestore에 데이터 넣어주기
            FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO);
        }
    }
}
