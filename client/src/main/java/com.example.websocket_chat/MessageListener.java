package com.example.websocket_chat;

import dev.onvoid.webrtc.RTCIceCandidate;
import dev.onvoid.webrtc.RTCSessionDescription;

import java.util.ArrayList;

public interface MessageListener {

    void onMessageReceive(Message message);
    void onActiveUsersUpdated(ArrayList<String> users);
    void onAnswerReceive(RTCSessionDescriptionDTO dto);
    void onICECandidateReceive(RTCIceCandidateDTO dto);
    void onOfferReceive(RTCSessionDescriptionDTO dto);
    void onNullOfferReceive();
}
