package com.example.howlstagram_f16.navigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.howlstagram_f16.R;
import com.example.howlstagram_f16.navigation.model.ContentDTO;
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

    Button commentSend = null;
    EditText commentMessage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // Intent에서 넘어온 sting값을 셋팅
        contentUid = getIntent().getStringExtra("contentUid");

        commentMessage = findViewById(R.id.et_comment_message);
        commentSend = findViewById(R.id.btn_comment_send);
        commentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentDTO.Comment comment = new ContentDTO().new Comment();
                comment.setUserId(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                comment.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                comment.setComment(commentMessage.getText().toString());
                comment.setTimestamp(System.currentTimeMillis());

                // DB에 넣기
                FirebaseFirestore.getInstance().collection("images").document(contentUid).collection("comments").document().set(comment);

                commentMessage.setText("");
            }
        });
    }

    class CommentRecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        ArrayList<ContentDTO.Comment> comments = new ArrayList<>();

        TextView commentViewItemComment = null;
        TextView commentViewItemProfile = null;

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
                            if(queryDocumentSnapshots == null) return;

                            for(DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                comments.add(snapshot.toObject(ContentDTO.Comment.class));
                            }
                            // 리싸이클러뷰를 새로고침
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
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            View view = holder.itemView;

            commentViewItemComment = view.findViewById(R.id.tv_commentviewitem_comment);
            commentViewItemComment.setText(comments.get(position).getComment());

            commentViewItemProfile = view.findViewById(R.id.tv_commentviewitem_profile);
            commentViewItemProfile.setText(comments.get(position).getUserId());

            /*FirebaseFirestore.getInstance()
                    .collection("profileImages")
                    .document(comments.get(position).getUid())
                    .get()
                    .addOnCompleteListener(new )*/
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }
    }
}
