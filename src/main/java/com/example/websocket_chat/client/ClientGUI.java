package com.example.websocket_chat.client;

import com.example.websocket_chat.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutionException;

public class ClientGUI extends JFrame {
    private JPanel connectedUsersPanel;

    public ClientGUI(String username) {
        super("User: " + username);

        setSize(1218, 685);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(ClientGUI.this, "Do you really want to leave?", "Exit", JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.YES_OPTION) {
                    ClientGUI.this.dispose();
                }

            }
        });

        getContentPane().setBackground(Utilities.PRIMARY_COLOR);
        addGUIComponents();
    }

    private void addGUIComponents() {
        addConnectedUsersComponents();
    }


    private void addConnectedUsersComponents() {
        connectedUsersPanel = new JPanel();
        connectedUsersPanel.setLayout(new BoxLayout(connectedUsersPanel, BoxLayout.Y_AXIS));
        connectedUsersPanel.setBackground(Utilities.SECONDARY_COLOR);
        connectedUsersPanel.setPreferredSize(new Dimension(200, getHeight()));

        JLabel connectedUsersLabel = new JLabel("Connected users: ");
        connectedUsersLabel.setFont(new Font("Inter", Font.BOLD, 18));
        connectedUsersLabel.setForeground(Utilities.TEXT_COLOR);

        connectedUsersPanel.add(connectedUsersLabel);

        add(connectedUsersPanel, BorderLayout.WEST);
    }

}
