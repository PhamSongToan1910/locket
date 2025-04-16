package com.example.locket_clone.runner;

import com.corundumstudio.socketio.SocketIOServer;
import com.example.locket_clone.entities.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NettySocketIOServerRunner implements CommandLineRunner {

    private final SocketIOServer server;

    @Override
    public void run(String... args) throws Exception {
        server.addConnectListener(client ->
                System.out.println("Client connected: " + client.getSessionId())
        );

        server.addDisconnectListener(client -> {
            System.out.println("Client disconnected: " + client.getSessionId());
        });

        server.addEventListener("chat_message", String.class, (client, data, ackSender) -> {
            //data : conversationId&userSenderId&PostId&Content
            //if PostId == null => data = conversationId&UserSenderId&--&Content
            System.out.println("Received message: " + data);
            String[] message = parseFromMessage(data);
            if(Objects.nonNull(message)) {
                String conversationId = message[0];
                String userSenderId = message[1];
                String postId = message[2];
                String content = message[3];
                Message newMessage = new Message(content, conversationId, userSenderId, false, postId);

                server.getBroadcastOperations().sendEvent("chat_message", data);
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
