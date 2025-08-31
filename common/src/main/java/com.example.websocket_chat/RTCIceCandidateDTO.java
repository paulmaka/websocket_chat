package com.example.websocket_chat;

public class RTCIceCandidateDTO {

    private String candidate;
    private String sdpMid;
    private int sdpMLineIndex;
    private String username;

    public RTCIceCandidateDTO() {}

    public RTCIceCandidateDTO(String candidate, String sdpMid, int sdpMLineIndex, String username) {
        this.candidate = candidate;
        this.sdpMid = sdpMid;
        this.sdpMLineIndex = sdpMLineIndex;
        this.username = username;
    }

    public String getCandidate() {
        return candidate;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

    public String getSdpMid() {
        return sdpMid;
    }

    public void setSdpMid(String sdpMid) {
        this.sdpMid = sdpMid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getSdpMLineIndex() {
        return sdpMLineIndex;
    }

    public void setSdpMLineIndex(int sdpMLineIndex) {
        this.sdpMLineIndex = sdpMLineIndex;
    }


}
