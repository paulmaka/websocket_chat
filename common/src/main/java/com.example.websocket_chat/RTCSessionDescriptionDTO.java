package com.example.websocket_chat;

public class RTCSessionDescriptionDTO {

    private String type;
    private String sdp;

    public RTCSessionDescriptionDTO() {}
    public RTCSessionDescriptionDTO(String type, String sdp) {
        this.sdp = sdp;
        this.type = type;
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
}
