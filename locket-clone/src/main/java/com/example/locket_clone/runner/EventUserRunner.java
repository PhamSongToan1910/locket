package com.example.locket_clone.runner;

import com.example.locket_clone.entities.Post;
import com.example.locket_clone.entities.User;
import com.example.locket_clone.entities.request.LogoutRequest;
import com.example.locket_clone.entities.request.ObjectRequest;
import com.example.locket_clone.entities.request.RemovePostNotificationRequest;
import com.example.locket_clone.service.UserService;
import com.example.locket_clone.utils.Constant.Constant;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class EventUserRunner implements CommandLineRunner {

    public static ConcurrentLinkedQueue<ObjectRequest> eventUserRequests = new ConcurrentLinkedQueue<>();
    private static final ConcurrentHashMap<String, Set<String>> deviceTokensMap = new ConcurrentHashMap<>();

    private final UserService userService;

    private final int MAX_THREAD_POOL = 5;
    private final ExecutorService schedule = Executors.newFixedThreadPool(MAX_THREAD_POOL);

    @Override
    public void run(String... args) throws Exception {
        for (int i = 0; i < MAX_THREAD_POOL; i++) {
            schedule.execute(() -> {
                while (true) {
                    ObjectRequest objectRequest = eventUserRequests.poll();
                    if(objectRequest != null) {
                        proccessRequest(objectRequest);
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }
    }

    private void proccessRequest(ObjectRequest objectRequest) {
        try {
            switch (objectRequest.getType()) {
                case Constant.API.UPDATE_DEVICE_TOKEN -> updateDeviceToken(objectRequest);
                case Constant.API.ADD_NOTIFICATION_NEW_POST -> pushMessageToFCM(objectRequest);
                case Constant.API.LOGOUT -> logout(objectRequest);
                case Constant.API.DELETE_POST_BY_ADMIN -> pushNotificationToFCM(objectRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateDeviceToken(ObjectRequest objectRequest) {
        User user = (User) objectRequest.getData();
        if(deviceTokensMap.containsKey(user.getId().toString())) {
            Set<String> userDeviceTokens = deviceTokensMap.get(user.getId().toString());
            userDeviceTokens.addAll(user.getDeviceToken());
        } else {
            deviceTokensMap.put(user.getId().toString(), user.getDeviceToken());
        }
        userService.updateDeviceToken(user);
    }

    private void pushMessageToFCM(ObjectRequest objectRequest) {
        Post post = (Post) objectRequest.getData();
        List<String> listUserIDs = post.getFriendIds();
        Set<String> deviceTokens = new HashSet<>();
        for(String userId : listUserIDs) {
            if(deviceTokensMap.containsKey(userId)) {
                deviceTokens.addAll(deviceTokensMap.get(userId));
            }
        }
        deviceTokens.forEach((value) -> {
            Message message = Message.builder()
                    .setToken(value)
                    .putData("new_post_available", "true")
                    .build();
            try {
                FirebaseMessaging.getInstance().send(message);
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
            }
        });
    }

    private void logout(ObjectRequest objectRequest) {
        LogoutRequest logoutRequest = (LogoutRequest) objectRequest.getData();
        User user = userService.findUserById(logoutRequest.getUserId());
        if(user != null && Objects.nonNull(user.getDeviceToken())) {
            user.getDeviceToken().remove(logoutRequest.getDeviceToken());
            if(deviceTokensMap.containsKey(user.getId().toString())) {
                Set<String> userDeviceTokens = deviceTokensMap.get(user.getId().toString());
                userDeviceTokens.removeAll(user.getDeviceToken());
                deviceTokensMap.put(user.getId().toString(), userDeviceTokens);
            }
            userService.updateDeviceToken(user);
        }
    }

    private void pushNotificationToFCM(ObjectRequest objectRequest) {
        RemovePostNotificationRequest removePostNotificationRequest = (RemovePostNotificationRequest) objectRequest.getData();
        Notification notification = Notification.builder()
                .setTitle(removePostNotificationRequest.getTitle())
                .setBody(removePostNotificationRequest.getCaption())
                .setImage(removePostNotificationRequest.getImageURL())
                .build();

        Set<String> deviceToken = userService.getDeviceTokenByUserID(removePostNotificationRequest.getUserId());
        deviceToken.forEach((value) -> {
            Message message = Message.builder()
                    .setToken(value)
                    .setNotification(notification)
                    .build();
            try {
                FirebaseMessaging.getInstance().send(message);
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
            }
        });
    }
}
