package com.example.websocket_chat;

import dev.onvoid.webrtc.*;
import dev.onvoid.webrtc.media.MediaStream;

public class ChatPeerConnectionObserver implements PeerConnectionObserver {

    private StompClient stompClient;
    private String username;

    public ChatPeerConnectionObserver(StompClient stompClient, String username) {
        this.stompClient = stompClient;
        this.username = username;
    }

    @Override
    public void onSignalingChange(RTCSignalingState state) {
    }

    @Override
    public void onConnectionChange(RTCPeerConnectionState state) {
    }

    @Override
    public void onIceConnectionChange(RTCIceConnectionState state) {
    }

    @Override
    public void onStandardizedIceConnectionChange(RTCIceConnectionState state) {
    }

    @Override
    public void onIceConnectionReceivingChange(boolean receiving) {
    }

    @Override
    public void onIceGatheringChange(RTCIceGatheringState state) {
    }


    @Override
    public void onIceCandidate(RTCIceCandidate candidate) {
        // Send the ICE candidate to the remote peer via your signaling channel
        RTCIceCandidateDTO dto = new RTCIceCandidateDTO(candidate.sdp, candidate.sdpMid, candidate.sdpMLineIndex, username);
        stompClient.sendCandidate(dto);
    }

    @Override
    public void onIceCandidateError(RTCPeerConnectionIceErrorEvent event) {
    }

    @Override
    public void onIceCandidatesRemoved(RTCIceCandidate[] candidates) {
    }

    @Override
    public void onAddStream(MediaStream stream) {
    }

    @Override
    public void onRemoveStream(MediaStream stream) {
    }

    @Override
    public void onDataChannel(RTCDataChannel dataChannel) {
    }

    @Override
    public void onRenegotiationNeeded() {
    }

    @Override
    public void onAddTrack(RTCRtpReceiver receiver, MediaStream[] mediaStreams) {
    }

    @Override
    public void onRemoveTrack(RTCRtpReceiver receiver) {
    }

    @Override
    public void onTrack(RTCRtpTransceiver transceiver) {
    }
}
