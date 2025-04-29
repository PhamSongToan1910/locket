package com.example.locket_clone.runner;

import com.example.locket_clone.entities.Notification;
import com.example.locket_clone.entities.request.ObjectRequest;
import com.example.locket_clone.service.NotificationService;
import com.example.locket_clone.utils.Constant.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class EventNotificationRunner implements CommandLineRunner {

    public static ConcurrentLinkedQueue<ObjectRequest> requests = new ConcurrentLinkedQueue<>();

    private final NotificationService notificationService;

    private final int MAX_THREAD_POOL = 5;
    private final ExecutorService schedule = Executors.newFixedThreadPool(MAX_THREAD_POOL);

    @Override
    public void run(String... args) {
        for (int i = 0; i < MAX_THREAD_POOL; i++) {
            schedule.execute(() -> {
                while (true) {
                    ObjectRequest objectRequest = requests.poll();
                    if (objectRequest != null) {
                        proccessEventNotification(objectRequest);
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

    private void proccessEventNotification(ObjectRequest objectRequest) {
        switch (objectRequest.getType()) {
            case Constant.API.SAVE_NOTIFICATION -> saveNotification(objectRequest);
        }
    }

    private void saveNotification(ObjectRequest objectRequest) {
        Notification notification = (Notification) objectRequest.getData();
        notificationService.saveNotification(notification);
    }
}
