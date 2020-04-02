package com.example.howlstagram_f16.navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.howlstagram_f16.R;
import com.example.howlstagram_f16.navigation.model.ContentDTO;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GridFragment extends Fragment {
    FirebaseFirestore firestore = null;
    View fragmentView = null;
    RecyclerView recyclerGridFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_grid, container, false);
        firestore = FirebaseFirestore.getInstance();

        // 리싸이클러뷰에 어댑터 설정
        recyclerGridFragment = fragmentView.findViewById(R.id.rcv_grid_fragment);
        recyclerGridFragment.setAdapter(new UserFragmentRecyclerViewAdapter());
        recyclerGridFragment.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        return fragmentView;
    }

    class UserFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        ImageView imageView = null;
        ArrayList<ContentDTO> contentDTOs = new ArrayList<>();

        UserFragmentRecyclerViewAdapter() {
            firestore.collection("images").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    // Sometimes, This code return null of queryDocumentSnapshots when it signout
                    if(queryDocumentSnapshots == null) return;

                    // Get Data
                    for(DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        contentDTOs.add(snapshot.toObject(new ContentDTO().getClass()));
                    }

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
            return new GridFragment.UserFragmentRecyclerViewAdapter.CustomViewHolder(imageView);
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
