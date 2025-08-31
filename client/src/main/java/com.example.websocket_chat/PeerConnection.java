package com.example.websocket_chat;

import dev.onvoid.webrtc.*;
import dev.onvoid.webrtc.media.MediaDevices;
import dev.onvoid.webrtc.media.audio.AudioDevice;
import dev.onvoid.webrtc.media.audio.AudioOptions;
import dev.onvoid.webrtc.media.audio.AudioTrack;
import dev.onvoid.webrtc.media.audio.AudioTrackSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PeerConnection {

    private PeerConnectionFactory factory;
    private RTCConfiguration config;
    private RTCIceServer iceServerStun;
    private RTCIceServer iceServerTurn;
    private RTCPeerConnection peerConnection;
    private StompClient stompClient;
    private List<AudioDevice> audioDevices;
    private List<String> streamIds;
    private AudioTrack audioTrack;
    private String username;
    private final Queue<RTCIceCandidate> localBuffer = new ConcurrentLinkedQueue<>();


    public PeerConnection(StompClient stompClient, String username) {
        factory = new PeerConnectionFactory();
        config = new RTCConfiguration();
        this.username = username;
        this.stompClient = stompClient;

        iceServerStun = new RTCIceServer();
        iceServerStun.urls.add("stun:stun.l.google.com:19302");

        iceServerTurn = new RTCIceServer();
        iceServerTurn.urls.add("turn:turnserverip:3478");

        iceServerTurn.username = "client";
        iceServerTurn.password = "secret01012005";

        config.iceServers.add(iceServerStun);
        config.iceServers.add(iceServerTurn);

        config.iceTransportPolicy = RTCIceTransportPolicy.ALL;
        config.bundlePolicy = RTCBundlePolicy.BALANCED;
        config.rtcpMuxPolicy = RTCRtcpMuxPolicy.REQUIRE;


        // Create a peer connection with an observer to handle events
        PeerConnectionObserver peerConnectionObserver = new ChatPeerConnectionObserver(stompClient, username);
        peerConnection = factory.createPeerConnection(config, peerConnectionObserver);
        getAudioDevices();
        createAudioSourceAndTracks();
        addTracksToPeerConnection();
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
                        RTCSessionDescriptionDTO dto = new RTCSessionDescriptionDTO(description.sdpType.name().toLowerCase(), description.sdp, username);
                        stompClient.sendOfferDescription(dto);
                    }

                    @Override
                    public void onFailure(String error) {
                        System.err.println("Failed to set local description: " + error);
                    }
                });
                System.out.println("Local description set successfully.");
            }

            @Override
            public void onFailure(String error) {
                System.err.println("Failed to create offer: " + error);
            }
        });
    }

    public void receiveRemoteDescription(RTCSessionDescriptionDTO dto) {
        if (!dto.getUsername().equals(username)) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stompClient.requestRemoteCandidate(dto.getUsername());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RTCSessionDescription remoteDescription = new RTCSessionDescription(RTCSdpType.valueOf(dto.getType().toUpperCase()), dto.getSdp());

            if (dto.getType().equalsIgnoreCase("offer")) {
                peerConnection.setRemoteDescription(remoteDescription, new SetSessionDescriptionObserver() {
                    @Override
                    public void onSuccess() {
                        peerConnection.createAnswer(new RTCAnswerOptions(), new CreateSessionDescriptionObserver() {
                            @Override
                            public void onSuccess(RTCSessionDescription answer) {
                                peerConnection.setLocalDescription(answer, new SetSessionDescriptionObserver() {
                                    @Override
                                    public void onSuccess() {
                                        RTCSessionDescriptionDTO dtoAnswer = new RTCSessionDescriptionDTO(answer.sdpType.name().toLowerCase(), answer.sdp, username);
                                        stompClient.sendAnswerDescription(dtoAnswer);
                                        System.out.println("Local description has set.");
                                    }
                                    @Override
                                    public void onFailure(String error) {
                                        System.err.println("Failed to set local description: " + error);
                                    }
                                });
                            }
                            @Override
                            public void onFailure(String error) {
                                System.err.println("Failed to create answer: " + error);
                            }
                        });
                        System.out.println("Remote description has set.");
                        setCandidates();
                    }
                    @Override
                    public void onFailure(String error) {
                        System.err.println("Failed to set remote description: " + error);
                    }
                });
            } else {
                peerConnection.setRemoteDescription(remoteDescription, new SetSessionDescriptionObserver() {
                    @Override
                    public void onSuccess() {
                        System.out.println("Remote description set successfully");
                        setCandidates();
                    }

                    @Override
                    public void onFailure(String error) {
                        System.err.println("Failed to set remote description: " + error);
                    }
                });
            }
        }
    }

    public void receiveRemoteCandidate(RTCIceCandidateDTO dto) {
        if (dto.getUsername().equals(username)) return;

        RTCIceCandidate c = new RTCIceCandidate(dto.getSdpMid(), dto.getSdpMLineIndex(), dto.getCandidate());

        if (peerConnection.getRemoteDescription() == null) {
            localBuffer.add(c);
            System.out.println("Buffered remote ICE candidate.");
        } else {
            peerConnection.addIceCandidate(c);
            System.out.println("Applied remote ICE candidate immediately to " + username);
        }
    }
    private void setCandidates() {
        RTCIceCandidate candidate;
        while ((candidate = localBuffer.poll()) != null) {
            peerConnection.addIceCandidate(candidate);
            System.out.println(candidate + " set successfully to " + username);
        }
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
//        audioTrack.dispose();
        peerConnection.close();
        factory.dispose();
    }
}
