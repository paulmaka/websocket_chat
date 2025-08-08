package com.example.websocket_chat.client;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public class App {
    public static void main(String[] args) {
        // Используется такой способ как более thread safe для обновления UI с использованием Spring Framework
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String username = JOptionPane.showInputDialog(null,
                        "Enter username (Max 16 character): ",
                        "Our Chat",
                        JOptionPane.QUESTION_MESSAGE);

                if (username == null || username.trim().isEmpty() || username.trim().length() > 16) {
                    JOptionPane.showMessageDialog(null,
                            "Invalid username",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }


                ClientGUI clientGUI = null;
                try {
                    clientGUI = new ClientGUI(username.trim());
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                clientGUI.setVisible(true);
            }
        });
    }
}
