package com.example.websocket_chat;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WebsocketSessionManager {
    private final ArrayList<String> activeUsernames = new ArrayList<>();
    private final Map<String, Queue<RTCIceCandidateDTO>> candidates = new HashMap<>();
    private Map<String, Boolean> userReady = new HashMap<>();
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

    public void addICECandidate(RTCIceCandidateDTO dto) {
        if (candidates.get(dto.getUsername()) == null) {
            candidates.put(dto.getUsername(), new LinkedList<>());
        }
        candidates.get(dto.getUsername()).add(dto);
//        messagingTemplate.convertAndSend("/topic/candidates", dto);
        System.out.println("Adding ICE candidate to /topic/candidates " + dto.getUsername());
    }

    public void requestCandidate(String username) {
        userReady.put(username, true);

        if (allUsersReady()) {
            for (String user : candidates.keySet()) {
                for (var element : candidates.get(user)) {
                    System.out.println("ICE candidate: " + element + " " + user);
                }
                while (!candidates.get(user).isEmpty()) {
                    messagingTemplate.convertAndSend("/topic/candidates", candidates.get(user).poll());
                }
            }
        }
    }

    private boolean allUsersReady() {
        for (String user : userReady.keySet()) {
            if (!userReady.get(user)) {
                return false;
            }
        }
        return true;
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
