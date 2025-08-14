package com.example.websocket_chat;

import dev.onvoid.webrtc.*;
import dev.onvoid.webrtc.media.MediaDevices;
import dev.onvoid.webrtc.media.audio.AudioDevice;
import dev.onvoid.webrtc.media.audio.AudioOptions;
import dev.onvoid.webrtc.media.audio.AudioTrack;
import dev.onvoid.webrtc.media.audio.AudioTrackSource;

import java.util.ArrayList;
import java.util.List;

public class PeerConnection {

    private PeerConnectionFactory factory;
    private RTCConfiguration config;
    private RTCIceServer iceServer;
    private RTCPeerConnection peerConnection;
    private StompClient stompClient;
    private List<AudioDevice> audioDevices;
    private List<String> streamIds;
    private AudioTrack audioTrack;

    public PeerConnection(StompClient stompClient) {
        factory = new PeerConnectionFactory();
        config = new RTCConfiguration();
        iceServer = new RTCIceServer();
        this.stompClient = stompClient;


        iceServer.urls.add("stun:stun.l.google.com:19302");
        config.iceServers.add(iceServer);

        // Create a peer connection with an observer to handle events
        PeerConnectionObserver peerConnectionObserver = new ChatPeerConnectionObserver(stompClient);
        peerConnection = factory.createPeerConnection(config, peerConnectionObserver);
        getAudioDevices();
    }

    public void createAndSendDescription() {
        // Create an offer
        RTCOfferOptions options = new RTCOfferOptions();

        peerConnection.createOffer(options, new CreateSessionDescriptionObserver() {
            @Override
            public void onSuccess(RTCSessionDescription description) {
                // Set local description
                peerConnection.setLocalDescription(description, new SetSessionDescriptionObserver() {
                    @Override
                    public void onSuccess() {
                        // Send the offer to the remote peer via your signaling channel
                        stompClient.sendDescription(description);
                    }

                    @Override
                    public void onFailure(String error) {
                        System.err.println("Failed to set local description: " + error);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                System.err.println("Failed to create offer: " + error);
            }
        });
    }

    public void receiveRemoteDescription(RTCSessionDescription remoteDescription) {
        peerConnection.setRemoteDescription(remoteDescription, new SetSessionDescriptionObserver() {
            @Override
            public void onSuccess() {
                System.out.println("Remote description set successfully");
            }

            @Override
            public void onFailure(String error) {
                System.err.println("Failed to set remote description: " + error);
            }
        });
    }

    public void receiveRemoteCandidate(RTCIceCandidate candidate) {
        peerConnection.addIceCandidate(candidate);
        createAudioSourceAndTracks();
        addTracksToPeerConnection();
    }

    private void getAudioDevices() {
        audioDevices = MediaDevices.getAudioCaptureDevices();
        for (AudioDevice device : audioDevices) {
            System.out.println("Audio device: " + device.getName());
        }
    }


    private void createAudioSourceAndTracks() {
        // Create an audio source and track
        AudioOptions audioOptions = new AudioOptions();
        audioOptions.echoCancellation = true;
        audioOptions.autoGainControl = true;
        audioOptions.noiseSuppression = true;

        // Create an audio source using the default audio device
        AudioTrackSource audioSource = factory.createAudioSource(audioOptions);
        audioTrack = factory.createAudioTrack("audio0", audioSource);
    }

    private void addTracksToPeerConnection() {
        streamIds = new ArrayList<>();
        streamIds.add("stream1");
        peerConnection.addTrack(audioTrack, streamIds);
    }

    public void cleanup() {
        audioTrack.dispose();
        peerConnection.close();
        factory.dispose();
    }
}
