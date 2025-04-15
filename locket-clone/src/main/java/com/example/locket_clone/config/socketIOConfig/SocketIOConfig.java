package com.example.locket_clone.config.socketIOConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.corundumstudio.socketio.SocketIOServer;

@Configuration
public class SocketIOConfig {

//    private String hostName = System.getenv("HOST_NAME");
//
//    private int port = Integer.parseInt(System.getenv("PORT"));

//    private String hostName = "0.0.0.0";
//
//    private int port = 9092;

//    @Bean
//    public com.corundumstudio.socketio.Configuration getConfiguration() {
//        com.corundumstudio.socketio.Configuration configuration = new com.corundumstudio.socketio.Configuration();
//        configuration.setHostname(hostName);
//        configuration.setPort(port);
//        configuration.setOrigin("*");
//        return configuration;
//    }

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname("0.0.0.0");
        config.setPort(9092);
        config.setOrigin("*");
        SocketIOServer server = new SocketIOServer(config);
        server.start();
        return server;
    }
}
