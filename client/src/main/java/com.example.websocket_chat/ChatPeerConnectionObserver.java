package com.example.websocket_chat;

import dev.onvoid.webrtc.*;
import dev.onvoid.webrtc.media.MediaDevices;
import dev.onvoid.webrtc.media.MediaStream;
import dev.onvoid.webrtc.media.MediaStreamTrack;
import dev.onvoid.webrtc.media.audio.AudioDevice;
import dev.onvoid.webrtc.media.audio.AudioTrack;

import java.util.List;

public class ChatPeerConnectionObserver implements PeerConnectionObserver {

    private StompClient stompClient;
    private String username;

    public ChatPeerConnectionObserver(StompClient stompClient, String username) {
        this.stompClient = stompClient;
        this.username = username;

        List<AudioDevice> outputDevices = MediaDevices.getAudioRenderDevices();
        if (!outputDevices.isEmpty()) {

            System.out.println("Audio output set to: " + outputDevices.get(0).getName());
        } else {
            System.err.println("No audio output devices found!");
        }
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
        System.out.println("Remote stream added: " + stream.id());
        for (AudioTrack track : stream.getAudioTracks()) {
            System.out.println("Remote audio track: " + track.getId());
            track.setEnabled(true); // Включаем аудиотрек
        }
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
        for (MediaStream stream : mediaStreams) {
            for (AudioTrack track : stream.getAudioTracks()) {
                System.out.println("Remote audio track received via onAddTrack: " + track.getId());
                track.setEnabled(true); // Включаем воспроизведение
            }
        }
    }

    @Override
    public void onRemoveTrack(RTCRtpReceiver receiver) {
    }

    @Override
    public void onTrack(RTCRtpTransceiver transceiver) {
        MediaStreamTrack track = transceiver.getReceiver().getTrack();
        System.out.println("Remote audio track received via onTrack: " + track.getId());
        track.setEnabled(true);
    }
}
