package com.example.websocket_chat;


import dev.onvoid.webrtc.RTCIceCandidate;
import dev.onvoid.webrtc.RTCSessionDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class WebsocketSessionManager {
    private final ArrayList<String> activeUsernames = new ArrayList<>();
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebsocketSessionManager(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastNewMessage(Message message) {
        messagingTemplate.convertAndSend("/topic/messages", message);
        System.out.println("Broadcasting message to /topic/messages: " + message.getUser() + ": " + message.getMessage());
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


    public void broadcastDescriptions(RTCSessionDescriptionDTO dto) {
        messagingTemplate.convertAndSend("/topic/descriptions", dto);
        System.out.println("Broadcasting description to /topic/descriptions " + dto);
    }


    public void broadcastICECandidate(RTCIceCandidateDTO dto) {
        messagingTemplate.convertAndSend("topic/candidates", dto);
        System.out.println("Broadcasting ICE candidate to /topic/candidates " + dto);
    }
}
