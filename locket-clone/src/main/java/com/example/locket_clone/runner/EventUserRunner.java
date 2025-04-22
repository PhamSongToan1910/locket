package com.example.locket_clone.runner;

import com.example.locket_clone.entities.Post;
import com.example.locket_clone.entities.User;
import com.example.locket_clone.entities.request.ObjectRequest;
import com.example.locket_clone.service.UserService;
import com.example.locket_clone.utils.Constant.Constant;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.SendResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class EventUserRunner implements CommandLineRunner {

    public static ConcurrentLinkedQueue<ObjectRequest> requests = new ConcurrentLinkedQueue<>();

    UserService userService;

    private final int MAX_THREAD_POOL = 5;
    private final ExecutorService schedule = Executors.newFixedThreadPool(MAX_THREAD_POOL);

    @Override
    public void run(String... args) throws Exception {
        for(int i = 0; i < MAX_THREAD_POOL; i++){
            schedule.execute(this::proccessRequest);
        }
    }

    private void proccessRequest() {
        try{
            ObjectRequest objectRequest;
            while((objectRequest = requests.poll()) != null) {
                switch (objectRequest.getType()) {
                    case Constant.API.UPDATE_DEVICE_TOKEN -> updateDeviceToken(objectRequest);
                    case Constant.API.ADD_NOTIFICATION_NEW_POST -> pushMessageToFCM(objectRequest);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateDeviceToken(ObjectRequest objectRequest) {
        User user = (User) objectRequest.getData();
        userService.updateDeviceToken(user);
    }

    private void pushMessageToFCM(ObjectRequest objectRequest) {
        Post post = (Post) objectRequest.getData();
        List<String> listUserIDs = post.getFriendIds();
        HashMap<String, Set<String>> deviceTokens = userService.getDeviceTokens(listUserIDs);
        deviceTokens.forEach((key, value) -> {
            MulticastMessage multicastMessage = MulticastMessage.builder()
                    .addAllTokens(value)
                    .putData("new_post_available", "true")
                    .build();
            BatchResponse response;
            try {
                response = FirebaseMessaging.getInstance().sendMulticast(multicastMessage);
                if(response.getFailureCount() > 0) {
                    List<String> listToken = value.stream().toList();
                    List<SendResponse> responses = response.getResponses();
                    List<String> failedTokens = new ArrayList<>();
                    for (int i = 0; i < responses.size(); i++) {
                        if (!responses.get(i).isSuccessful()) {
                            // The order of responses corresponds to the order of the registration tokens.
                            failedTokens.add(listToken.get(i));
                        }
                    }

                    System.out.println("List of tokens that caused failures: " + failedTokens);
                }
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
            }
        });
    }
}
