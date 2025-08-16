package com.example.websocket_chat;


import dev.onvoid.webrtc.RTCIceCandidate;
import dev.onvoid.webrtc.RTCSessionDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebsocketSessionManager {
    private final ArrayList<String> activeUsernames = new ArrayList<>();
    private final Map<String, RTCIceCandidateDTO> candidates = new HashMap<>();
    private RTCSessionDescriptionDTO offer = null;
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


    public void setOfferDescription(RTCSessionDescriptionDTO dto) {
        offer = dto;
    }

    public void broadcastAnswer(RTCSessionDescriptionDTO dto) {
        messagingTemplate.convertAndSend("/topic/answers", dto);
        System.out.println("Broadcasting answer to /topic/answers " + dto);
    }

    public void broadcastICECandidate(RTCIceCandidateDTO dto) {
        candidates.put(dto.getUsername(), dto);
        messagingTemplate.convertAndSend("/topic/candidates", dto);
        System.out.println("Broadcasting ICE candidate to /topic/candidates " + dto);
    }

    public void requestCandidate(String username) {
        messagingTemplate.convertAndSend("/topic/candidates", candidates.get(username));
    }

    public void requestOfferDescription() {
        if (offer == null) {
            messagingTemplate.convertAndSend("/topic/null-offer", new Message("server", "Offer is null!!!"));
            System.out.println("Sent from server null offer description.");
        } else {
            messagingTemplate.convertAndSend("/topic/offers", offer);
            System.out.println("Sent from server offer description: " + offer);
        }
    }
}
