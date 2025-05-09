package com.example.locket_clone.runner;

import com.example.locket_clone.entities.LastMessage;
import com.example.locket_clone.entities.Message;
import com.example.locket_clone.entities.request.ObjectRequest;
import com.example.locket_clone.service.LastMessageService;
import com.example.locket_clone.service.MessageService;
import com.example.locket_clone.utils.Constant.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class EventMessageRunner implements CommandLineRunner {

    public static final ConcurrentLinkedQueue<ObjectRequest> eventMessageRequests = new ConcurrentLinkedQueue<>();

    private final MessageService messageService;
    private final LastMessageService lastMessageService;

    private final int MAX_THREAD_POOL = 5;
    private final ExecutorService schedule = Executors.newFixedThreadPool(MAX_THREAD_POOL);

    @Override
    public void run(String... args) throws Exception {
        for(int i = 0; i < MAX_THREAD_POOL; i++) {
            schedule.execute(() -> {
                while(true) {
                    ObjectRequest request = eventMessageRequests.poll();
                    if(request != null) {
                        processEventMessage(request);
                    } else {
                        try{
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private void processEventMessage(ObjectRequest request) {
        try{
            switch (request.getType()) {
                case Constant.API.UPLOAD_MESSAGE -> uploadMessage(request);
                case Constant.API.UPLOAD_LAST_MESSAGE -> uploadLastMessage(request);
                case Constant.API.UPDATE_UNREAD_MESSAGE -> updateUnreadMessage(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadMessage(ObjectRequest request) {
        Message message = (Message) request.getData();
        messageService.saveMessage(message);
    }

    private void uploadLastMessage(ObjectRequest request) {
        Message message = (Message) request.getData();
        lastMessageService.updateLastMessage(message);
    }

    private void updateUnreadMessage(ObjectRequest request) {
        String conversationId = (String) request.getData();
        LastMessage lastMessage = lastMessageService.getLastMessageByConversationId(conversationId);
        if(lastMessage != null) {
            messageService.updateReadStatus(lastMessage.getMessageId());
        }
    }
}
