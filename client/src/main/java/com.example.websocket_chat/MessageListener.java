package com.example.websocket_chat;

import java.util.ArrayList;

public interface MessageListener {

    void onMessageReceive(Message message);
    void onActiveUsersUpdated(ArrayList<String> users);
}
