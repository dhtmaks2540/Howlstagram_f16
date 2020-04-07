package com.example.howlstagram_f16.navigation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.howlstagram_f16.R;
import com.example.howlstagram_f16.navigation.model.AlarmDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AlarmFragment extends Fragment {
    RecyclerView recyclerAlarmFragment = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_alarm, container, false);

        recyclerAlarmFragment = view.findViewById(R.id.rcv_alarm_fragment);
        recyclerAlarmFragment.setAdapter(new AlarmRecyclerviewAdapter());
        recyclerAlarmFragment.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("AlarmFragment stop");
        recyclerAlarmFragment.setAdapter(null);
    }

    class AlarmRecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        ArrayList<AlarmDTO> alarmDTOList = new ArrayList<>();
        TextView commentViewItemProfile;
        TextView commentViewItemComment;
        ImageView ImageCommentViewItemProfile;

        {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseFirestore.getInstance().collection("alarms").whereEqualTo("destinationUid", uid).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    alarmDTOList.clear();
                    if (queryDocumentSnapshots == null) return;

                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        alarmDTOList.add(snapshot.toObject(AlarmDTO.class));
                    }
                    notifyDataSetChanged();
                }
            });

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);

            commentViewItemProfile = view.findViewById(R.id.tv_commentviewitem_profile);
            commentViewItemComment = view.findViewById(R.id.tv_commentviewitem_comment);
            ImageCommentViewItemProfile = view.findViewById(R.id.iv_commentviewitem_profile);

            return new CustomViewHolder(view);
        }

        class CustomViewHolder extends RecyclerView.ViewHolder {
            public CustomViewHolder(View view) {
                super(view);
            }
        }

        @Override
        public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
            super.onViewRecycled(holder);
            clear();
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            final View view = holder.itemView;

            FirebaseFirestore.getInstance()
                    .collection("profileImages")
                    .document(alarmDTOList.get(position).getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()) {
                                String url = (String)task.getResult().get("image");
                                Glide.with(view.getContext()).load(url).apply(RequestOptions.circleCropTransform()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).thumbnail(0.1f).into(ImageCommentViewItemProfile);
                            } else {
                                Glide.with(view.getContext()).load(R.drawable.ic_account).into(ImageCommentViewItemProfile);
                            }
                        }
                    });

            switch (alarmDTOList.get(position).getKind()) {
                case 0: {
                    String str_0 = alarmDTOList.get(position).getUserId() + getString(R.string.alarm_favorite);
                    commentViewItemProfile.setText(str_0);
                    break;
                }
                case 1: {
                    String str_0 = alarmDTOList.get(position).getUserId() + " " + getString(R.string.alarm_comment) + " of " + alarmDTOList.get(position).getMessage();
                    commentViewItemProfile.setText(str_0);
                    break;
                }
                case 2: {
                    String str_0 = alarmDTOList.get(position).getUserId() + " " + getString(R.string.alarm_follow);
                    commentViewItemProfile.setText(str_0);
                    break;
                }
            }
            commentViewItemComment.setVisibility(View.INVISIBLE);

        }

        @Override
        public int getItemCount() {
            return alarmDTOList.size();
        }

        public void clear() {
            alarmDTOList.clear();
        }

    }
}
