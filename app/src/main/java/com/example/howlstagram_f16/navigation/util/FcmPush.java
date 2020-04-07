package com.example.howlstagram_f16.navigation.util;

import com.example.howlstagram_f16.navigation.model.PushDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.io.IOException;

import androidx.annotation.NonNull;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FcmPush {
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    String url = "https://fcm.googleapis.com/fcm/send";
    String serverKey = "AAAAQ1tqk4I:APA91bEGw7YPlupAc-iFg9-OkHsjS00RY0LjnWc9RUerFJHrK_fKUm2tsxUvXOtiD1zMkq1DUmlhxlLr-Hk5CN4fqetgK0PTOneRKMskrGE6gFRVfJVvL18vKEe9JBAZ9E1dPJ_AWoq0";
    Gson gson = null;
    OkHttpClient okHttpClient = null;

    {
        gson = new Gson();
        okHttpClient = new OkHttpClient();
    }

    public void sendMessage(String destinationUid, final String title, final String message) {
        FirebaseFirestore.getInstance().collection("pushtokens").document(destinationUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    String token = task.getResult().get("pushToken").toString();

                    PushDTO pushDTO = new PushDTO();
                    pushDTO.setTo(token);
                    pushDTO.setNotification(title, message);

                    RequestBody body = RequestBody.create(JSON, gson.toJson(pushDTO));
                    Request request = new Request.Builder()
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", "key="+serverKey)
                            .url(url)
                            .post(body)
                            .build();

                    // Google의 Apppush에 값 넘기기
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            System.out.println(response.body().toString());
                        }
                    });
                }
            }
        });
    }
}
