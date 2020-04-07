package com.example.howlstagram_f16.navigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.howlstagram_f16.R;
import com.example.howlstagram_f16.navigation.model.AlarmDTO;
import com.example.howlstagram_f16.navigation.model.Comment;
import com.example.howlstagram_f16.navigation.util.FcmPush;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class CommentActivity extends AppCompatActivity {
    String contentUid = null;
    String destinationUid = null;

    Button commentSend = null;
    EditText commentMessage = null;
    RecyclerView recyclerviewComment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // Intent에서 넘어온 sting값을 셋팅
        contentUid = getIntent().getStringExtra("contentUid");
        destinationUid = getIntent().getStringExtra("destinationUid");

        recyclerviewComment = findViewById(R.id.rcv_comment);
        recyclerviewComment.setAdapter(new CommentRecyclerviewAdapter());
        recyclerviewComment.setLayoutManager(new LinearLayoutManager(this));

        commentMessage = findViewById(R.id.et_comment_message);
        commentSend = findViewById(R.id.btn_comment_send);
        commentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comment comment = new Comment();
                comment.setUserId(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                comment.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                comment.setComment(commentMessage.getText().toString());
                comment.setTimestamp(System.currentTimeMillis());

                // DB에 넣기
                FirebaseFirestore.getInstance().collection("images").document(contentUid).collection("comments").document().set(comment);
                commentAlarm(destinationUid, commentMessage.getText().toString());
                commentMessage.setText("");
            }
        });
    }

    // Comment가 달렸을 때 알려주는 알림메서드
    void commentAlarm(String destinationUid, String message) {
        AlarmDTO alarmDTO = new AlarmDTO();
        alarmDTO.setDestinationUid(destinationUid);
        alarmDTO.setUserId(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        alarmDTO.setKind(1);
        alarmDTO.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        alarmDTO.setTimestamp(System.currentTimeMillis());
        alarmDTO.setMessage(message);

        // Firestore에 데이터 저장
        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO);

        String msg = FirebaseAuth.getInstance().getCurrentUser().getEmail() + " " + getString(R.string.alarm_comment) + " of " + message;
        new FcmPush().sendMessage(destinationUid, "Howlstagram", msg);
    }

    class CommentRecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        ArrayList<Comment> comments = new ArrayList<>();

        TextView commentViewItemComment = null;
        TextView commentViewItemProfile = null;
        ImageView ImageCommentViewItemProfile = null;

        {
            FirebaseFirestore.getInstance()
                    .collection("images")
                    .document(contentUid)
                    .collection("comments")
                    .orderBy("timestamp")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            comments.clear();
                            if (queryDocumentSnapshots == null) return;

                            for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                comments.add(snapshot.toObject(new Comment().getClass()));
                            }
                            // 리싸이클러뷰를  새로고침
                            notifyDataSetChanged();
                        }
                    });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
            return new CustomViewHolder(view);
        }


        private class CustomViewHolder extends RecyclerView.ViewHolder {
            CustomViewHolder(View view) {
                super(view);
            }
        }

        @Override
        public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
            super.onViewRecycled(holder);
            clear();
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
            View view = holder.itemView;

            commentViewItemComment = view.findViewById(R.id.tv_commentviewitem_comment);
            commentViewItemComment.setText(comments.get(position).getComment());

            commentViewItemProfile = view.findViewById(R.id.tv_commentviewitem_profile);
            commentViewItemProfile.setText(comments.get(position).getUserId());

            ImageCommentViewItemProfile = view.findViewById(R.id.iv_commentviewitem_profile);

            FirebaseFirestore.getInstance()
                    .collection("profileImages")
                    .document(comments.get(position).getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                Object url = task.getResult().get("image");
                                Glide.with(holder.itemView.getContext()).load(url).apply(RequestOptions.circleCropTransform()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).thumbnail(0.1f).into(ImageCommentViewItemProfile);
                            } else {
                                Glide.with(holder.itemView.getContext()).load(R.drawable.ic_account);
                            }
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        public void clear() {
            comments.clear();
        }
    }
}
