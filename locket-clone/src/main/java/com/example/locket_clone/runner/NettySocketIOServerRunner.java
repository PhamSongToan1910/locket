package com.example.locket_clone.runner;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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
            System.out.println("Received message: " + data);
            server.getBroadcastOperations().sendEvent("chat_message", data);
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Stopping Netty Socket.IO Server...");
            server.stop();
        }));
    }
}
