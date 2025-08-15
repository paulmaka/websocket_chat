package com.example.websocket_chat;

public class RTCSessionDescriptionDTO {

    private String type;
    private String sdp;
    private String username;

    public RTCSessionDescriptionDTO() {}
    public RTCSessionDescriptionDTO(String type, String sdp, String username) {
        this.sdp = sdp;
        this.type = type;
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSdp() {
        return sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
