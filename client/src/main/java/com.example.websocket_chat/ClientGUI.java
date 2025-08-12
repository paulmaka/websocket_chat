package com.example.websocket_chat;


import jdk.jshell.execution.Util;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ClientGUI extends JFrame implements MessageListener{

    // Выводим их в поля класса, так как содержимое этих панелей будет обновляться
    private JPanel connectedUsersPanel;
    private JPanel messagePanel;
    private StompClient stompClient;
    private String username;
    private JScrollPane messageScrollPane;

    public ClientGUI(String username) throws ExecutionException, InterruptedException {
        super("User: " + username);
        this.username = username;
        stompClient = new StompClient(this, username);

        setSize(1218, 685);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(ClientGUI.this, "Do you really want to leave?", "Exit", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    stompClient.disconnectUser(username);
                    ClientGUI.this.dispose();
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateMessageSize();
            }
        });

        getContentPane().setBackground(Utilities.PRIMARY_COLOR);
        addGUIComponents();
    }


    private void addGUIComponents() {
        addConnectedUsersComponents();
        addChatComponents();
    }


    private void addConnectedUsersComponents() {
        connectedUsersPanel = new JPanel();
        connectedUsersPanel.setBorder(Utilities.addPadding(10, 10, 10, 10, Utilities.THIRD_COLOR));
        connectedUsersPanel.setLayout(new BoxLayout(connectedUsersPanel, BoxLayout.Y_AXIS));
        connectedUsersPanel.setBackground(Utilities.SECONDARY_COLOR);
        connectedUsersPanel.setPreferredSize(new Dimension(200, getHeight()));

        JLabel connectedUsersLabel = new JLabel("Connected users: ");
        connectedUsersLabel.setFont(new Font("Inter", Font.BOLD, 18));
        connectedUsersLabel.setForeground(Utilities.TEXT_COLOR);

        connectedUsersPanel.add(connectedUsersLabel);

        add(connectedUsersPanel, BorderLayout.WEST);
    }

    private void addChatComponents() {
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBackground(Utilities.TRANSPARENT_COLOR);

        messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(Utilities.TRANSPARENT_COLOR);

        messageScrollPane = new JScrollPane(messagePanel);
        messageScrollPane.setBackground(Utilities.TRANSPARENT_COLOR);
        messageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        messageScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        messageScrollPane.getViewport().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                revalidate();
                repaint();
            }
        });

        chatPanel.add(messageScrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(Utilities.addPadding(5, 5, 5, 5, Utilities.THIRD_COLOR));
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setBackground(Utilities.SECONDARY_COLOR);

        JTextField inputField = new JTextField();
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    String input = inputField.getText();

                    if (input.isEmpty()) {
                        return;
                    }
                    inputField.setText("");

                    stompClient.sendMessage(new Message(username, input));
                }
            }
        });
        inputField.setBackground(Utilities.PRIMARY_COLOR);
        inputField.setForeground(Utilities.TEXT_COLOR);
        inputField.setBorder(Utilities.addPadding(0, 10, 0, 10));
        inputField.setFont(new Font("Inter", Font.PLAIN, 16));
        inputField.setPreferredSize(new Dimension(inputPanel.getWidth(), 50));

        inputPanel.add(inputField, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);


        add(chatPanel, BorderLayout.CENTER);
    }


    private JPanel createChatMessageComponent(Message message) {
        JPanel chatMessage = new JPanel();

        chatMessage.setBackground(Utilities.TRANSPARENT_COLOR);
        chatMessage.setLayout(new BoxLayout(chatMessage, BoxLayout.Y_AXIS));
        chatMessage.setBorder(Utilities.addPadding(20, 20, 10, 20));

        JLabel usernameLabel = new JLabel(message.getUser());
        usernameLabel.setFont(new Font("Inter", Font.BOLD, 18));
        usernameLabel.setForeground(Utilities.TEXT_COLOR);

//        JLabel messageLabel = new JLabel(message.getMessage());
        JLabel messageLabel = new JLabel();
        messageLabel.putClientProperty("rawText", message.getMessage());
        //TODO разобраться с динамическим переносом строк
        updateMessageLabelWidth(messageLabel);
        messageLabel.setFont(new Font("Inter", Font.PLAIN, 16));
        messageLabel.setForeground(Utilities.TEXT_COLOR);

        if (username.equals(message.getUser())) {
            usernameLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            messageLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

            messageLabel.setText("<html><div style='width:" + (0.60 * getWidth()) +
                    "px; text-align:right'>" +
                    message.getMessage() + "</div></html>");

        }

        chatMessage.add(usernameLabel);
        chatMessage.add(messageLabel);

        return chatMessage;
    }

    @Override
    public void onMessageReceive(Message message) {
        messagePanel.add(createChatMessageComponent(message));
        revalidate();
        repaint();

        messageScrollPane.getVerticalScrollBar().setValue(Integer.MAX_VALUE); //TODO аналог для списка активных пользователей
    }


    /**
     * Метод при каждом обновлении списка активных пользователей стирает предыдущий (индекс 1)
     * и отрисовывает новый.
     *
     * @param users
     */
    @Override
    public void onActiveUsersUpdated(ArrayList<String> users) {
        if (connectedUsersPanel.getComponents().length >= 2) {
            connectedUsersPanel.remove(1);
        }

        JPanel userListPanel = new JPanel();
        userListPanel.setBackground(Utilities.TRANSPARENT_COLOR);
        userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));

        for (String user : users) {
            JLabel usernameLabel = new JLabel();
            usernameLabel.setText(user);
            usernameLabel.setForeground(Utilities.TEXT_COLOR);
            usernameLabel.setFont(new Font("Inter", Font.BOLD, 16));

            userListPanel.add(usernameLabel);
        }

        connectedUsersPanel.add(userListPanel);
        revalidate();
        repaint();
    }


    private void updateMessageSize() {
        for (int i = 0; i < messagePanel.getComponents().length; i++) {
            Component component = messagePanel.getComponent(i);

            if (component instanceof JPanel) {
                JPanel chatMessage = (JPanel) component;
                if (chatMessage.getComponent(1) instanceof JLabel) {
                    JLabel messageLabel = (JLabel) chatMessage.getComponent(1);
                    updateMessageLabelWidth(messageLabel);
                }
            }
        }
        revalidate();
        repaint();
    }

    private void updateMessageLabelWidth(JLabel label) {
        String rawText = (String) label.getClientProperty("rawText");
        label.setText("<html><div style='width:" + (int)(0.60 * getWidth()) +
                "px; white-space: normal; word-wrap: break-word;'>" +
                rawText + "</div></html>");
    }
}
