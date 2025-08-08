package com.example.websocket_chat.server;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class WebSocketSessionManager {
    private final ArrayList<String> activeUsernames = new ArrayList<>();
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketSessionManager(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    public void addUsername(String username) {
        activeUsernames.add(username);
    }


    public void removedUsername(String username) {
        activeUsernames.remove(username);
    }


    public void broadcastActiveUsernames() {
        messagingTemplate.convertAndSend("/topic/users", activeUsernames);
        System.out.println("Broadcasting active usernames to /topic/users " + activeUsernames);
    }
}
