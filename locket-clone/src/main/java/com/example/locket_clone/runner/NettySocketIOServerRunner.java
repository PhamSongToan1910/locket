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
import org.bson.types.ObjectId;
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

    public static final Map<String, Set<UUID>> onlineUsers = new ConcurrentHashMap<>();
    public static final Map<UUID, String> socketMap = new ConcurrentHashMap<>();

    private final SocketIOServer server;
    private final TokenProvider tokenProvider;
    private final MessageService messageService;
    private final LastMessageService lastMessageService;

    @Override
    public void run(String... args) throws Exception {
        server.addConnectListener(client -> {
            String token = client.getHandshakeData().getSingleUrlParam("token");
            System.out.println("token: " + token);
            if (Objects.nonNull(token)) {
                String userId = tokenProvider.getUserIdByToken(token);
                client.set("userId", userId);
                if(!onlineUsers.containsKey(userId)) {
                    onlineUsers.put(userId, new HashSet<>());
                }
                onlineUsers.get(userId).add(client.getSessionId());
                socketMap.put(client.getSessionId(), userId);
                System.out.println("UserId: " + userId);
                System.out.println("list sessionID: " + onlineUsers.get(userId));
            } else {
                System.out.println("unauthorized " + client.getSessionId());
            }
            System.out.println("Connect success: " + client.getSessionId());
        });

        server.addDisconnectListener(client -> {
            String token = client.getHandshakeData().getSingleUrlParam("token");
            if(Objects.nonNull(token)) {
                System.out.println("removed token: " + token);
                String userId = tokenProvider.getUserIdByToken(token);
                onlineUsers.get(userId).remove(client.getSessionId());
                socketMap.remove(client.getSessionId());
                System.out.println("UserId: " + userId);
                System.out.println("list sessionID: " + onlineUsers.get(userId));
            } else {
                System.out.println("unauthorized " + client.getSessionId());
            }
            System.out.println("disconnect success: " + client.getSessionId());
        });

        server.addEventListener("send_message", String.class, (client, data, ackSender) -> {
            //data : conversationId&userReceiverId&PostURL&Content
            //if PostId == null => data = conversationId&UserSenderId&--&Content
            System.out.println("data: " + data);
            System.out.println("mapSocketID: " + onlineUsers);
            System.out.println("socketID: " + client.getSessionId());
            System.out.println("token: + " + client.getHandshakeData().getSingleUrlParam("token"));
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> jsonMap = mapper.readValue(data, Map.class);
            String conversationId = (String) jsonMap.get("conversation_id");
            String userReceiver = (String) jsonMap.get("user_receiver");
            String postURL = (String) jsonMap.get("post_url");
            String content = (String) jsonMap.get("content");
            String userSender = socketMap.get(client.getSessionId());
            System.out.println("userId: + " + userSender);
            System.out.println("send message: " + conversationId + " " + userSender + " " + userReceiver + " " + postURL + " " + content);
            Set<UUID> usersOnline = onlineUsers.get(userReceiver);
            Message newMessage = new Message(content, conversationId, userSender, userReceiver, false, postURL);

            System.out.println("data received: " + data);
            if(Objects.nonNull(usersOnline)) {
                usersOnline.forEach(uuid -> {
                    SocketIOClient socketReceiver = server.getClient(uuid);
                    if(socketReceiver != null) {
                        socketReceiver.sendEvent("receiver_message", data);
                    }
                });
            }
            String savedMessageId = messageService.saveMessage(newMessage);
            newMessage.setId(new ObjectId(savedMessageId));
            lastMessageService.updateLastMessage(newMessage);
        });

        server.addEventListener("read", String.class, (client, data, ackSender) -> {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> jsonMap = mapper.readValue(data, Map.class);
            String conversationId = (String) jsonMap.get("conversation_id");
            ObjectRequest request = new ObjectRequest(Constant.API.UPDATE_UNREAD_MESSAGE, conversationId);
            EventMessageRunner.eventMessageRequests.add(request);
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
