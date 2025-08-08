package com.example.websocket_chat.client;

import com.example.websocket_chat.common.Message;

import java.util.ArrayList;

public interface MessageListener {

    void onMessageReceive(Message message);
    void onActiveUsersUpdated(ArrayList<String> users);
}
