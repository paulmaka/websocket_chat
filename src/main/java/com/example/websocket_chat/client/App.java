package com.example.websocket_chat.client;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        // Используется такой способ как более thread safe для обновления UI с использованием Spring Framework
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ClientGUI clientGUI = new ClientGUI("NewClient");
                clientGUI.setVisible(true);
            }
        });
    }
}
