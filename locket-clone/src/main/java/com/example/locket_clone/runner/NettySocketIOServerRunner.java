package com.example.locket_clone.runner;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.example.locket_clone.config.security.TokenProvider;
import com.example.locket_clone.entities.Message;
import com.example.locket_clone.entities.request.ObjectRequest;
import com.example.locket_clone.service.LastMessageService;
import com.example.locket_clone.service.MessageService;
import com.example.locket_clone.utils.Constant.Constant;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NettySocketIOServerRunner implements CommandLineRunner {

    private final Map<String, Set<UUID>> onlineUsers = new ConcurrentHashMap<>();

    private final SocketIOServer server;
    private final TokenProvider tokenProvider;
    private final MessageService messageService;
    private final LastMessageService lastMessageService;

    @Override
    public void run(String... args) throws Exception {
        server.addConnectListener(client -> {
            System.out.println("User connection: " + client.getSessionId());
        });

        server.addEventListener("authentication", String.class, (client, data, ackSender) -> {
            String token = client.getHandshakeData().getSingleUrlParam("token");
            if (Objects.nonNull(token)) {
                String userId = tokenProvider.getUserIdByToken(token);
                client.set("userId", userId);
                if(!onlineUsers.containsKey(userId)) {
                    onlineUsers.put(userId, new HashSet<>());
                }
                onlineUsers.get(userId).add(client.getSessionId());
            }
        });

        server.addDisconnectListener(client -> {
            String token = client.getHandshakeData().getSingleUrlParam("token");
            if(Objects.nonNull(token)) {
                String userId = tokenProvider.getUserIdByToken(token);
                onlineUsers.get(userId).remove(client.getSessionId());
            }
        });

        server.addEventListener("send_message", String.class, (client, data, ackSender) -> {
            //data : conversationId&userReceiverId&PostURL&Content
            //if PostId == null => data = conversationId&UserSenderId&--&Content
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> jsonMap = mapper.readValue(data, Map.class);
            String conversationId = (String) jsonMap.get("conversation_id");
            String userReceiver = (String) jsonMap.get("user_receiver");
            String postURL = (String) jsonMap.get("post_url");
            String content = (String) jsonMap.get("content");
            String userSender = client.getHandshakeData().getSingleUrlParam("userId");
            Set<UUID> usersOnline = onlineUsers.get(userReceiver);
            if(Objects.nonNull(usersOnline)) {
                usersOnline.forEach(uuid -> {
                    SocketIOClient socketReceiver = server.getClient(uuid);
                    if(socketReceiver != null) {
                        Message newMessage = new Message(content, conversationId, userSender, userReceiver, false, postURL);
                        ObjectRequest updateLastMessageRequest = new ObjectRequest(Constant.API.UPLOAD_LAST_MESSAGE, newMessage);
                        ObjectRequest saveMessageRequest = new ObjectRequest(Constant.API.UPLOAD_MESSAGE, newMessage);
                        EventMessageRunner.eventMessageRequests.add(updateLastMessageRequest);
                        EventMessageRunner.eventMessageRequests.add(saveMessageRequest);
                        socketReceiver.sendEvent("receiver_message", data);
                    }
                });
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Stopping Netty Socket.IO Server...");
            server.stop();
        }));
    }

    private String[] parseFromMessage(String data) {
        String[] message = data.split("&");
        if(message == null || message.length < 4) {
            return null;
        }
        return message;
    }
}
