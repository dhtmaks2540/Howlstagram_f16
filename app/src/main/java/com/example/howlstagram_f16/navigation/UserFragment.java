package com.example.howlstagram_f16.navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.howlstagram_f16.R;
import com.example.howlstagram_f16.navigation.model.ContentDTO;
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
import androidx.recyclerview.widget.RecyclerView;


public class UserFragment extends Fragment {
    View fragmentView = null;
    FirebaseFirestore firestore = null;
    String uid = null;
    FirebaseAuth auth = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_user, container, false);
        return fragmentView;
    }

    class UserFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        ArrayList<ContentDTO> contentDTOs = new ArrayList<>();

        UserFragmentRecyclerViewAdapter() {
            firestore.collection("images").whereEqualTo("uid",uid).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    // Sometimes, This code return null of queryDocumentSnapshots when it signout
                    if(queryDocumentSnapshots == null);

                    // Get Data
                    for(DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        contentDTOs.add(snapshot.toObject(new ContentDTO().getClass()));
                    }
                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
}
