package com.example.locket_clone.runner;

import com.example.locket_clone.entities.Device;
import com.example.locket_clone.entities.Post;
import com.example.locket_clone.entities.User;
import com.example.locket_clone.entities.request.ObjectRequest;
import com.example.locket_clone.entities.request.RemoveDeviceRequest;
import com.example.locket_clone.entities.request.RemovePostNotificationRequest;
import com.example.locket_clone.entities.request.UpdateDeviceTokenRequest;
import com.example.locket_clone.service.DeviceService;
import com.example.locket_clone.service.UserService;
import com.example.locket_clone.service.impl.DeviceServiceImpl;
import com.example.locket_clone.utils.Constant.Constant;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class EventUserRunner implements CommandLineRunner {

    public static ConcurrentLinkedQueue<ObjectRequest> eventUserRequests = new ConcurrentLinkedQueue<>();
//    private static final ConcurrentHashMap<String, Set<String>> deviceIdsMap = new ConcurrentHashMap<>();
//    private static final ConcurrentHashMap<String, String> deviceTokensMap = new ConcurrentHashMap<>();

    private final UserService userService;
    private final DeviceService deviceService;

    private final int MAX_THREAD_POOL = 5;
    private final ExecutorService schedule = Executors.newFixedThreadPool(MAX_THREAD_POOL);

    @Override
    public void run(String... args) throws Exception {
        for (int i = 0; i < MAX_THREAD_POOL; i++) {
            schedule.execute(() -> {
                while (true) {
                    ObjectRequest objectRequest = eventUserRequests.poll();
                    if (objectRequest != null) {
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
        UpdateDeviceTokenRequest request = (UpdateDeviceTokenRequest) objectRequest.getData();
        deviceService.updateDeviceToken(request.getUserId(), request.getDeviceId(), request.getDeviceToken());
    }

    private void pushMessageToFCM(ObjectRequest objectRequest) {
        Post post = (Post) objectRequest.getData();
        List<String> listUserIDs = post.getFriendIds();
        Set<String> deviceTokens = deviceService.getDeviceTokenByUserIds(listUserIDs);

        deviceTokens.forEach((value) -> {
            Message message = Message.builder()
                    .setToken(value)
                    .putData("new_post_available", "true")
                    .build();
            System.out.println("token: " + value);
            try {
                FirebaseMessaging.getInstance().send(message);
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
            }
        });
    }

    private void logout(ObjectRequest objectRequest) {
        RemoveDeviceRequest logoutRequest = (RemoveDeviceRequest) objectRequest.getData();
        deviceService.deleteDeviceToken(logoutRequest.getUserId(), logoutRequest.getDeviceId());
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
