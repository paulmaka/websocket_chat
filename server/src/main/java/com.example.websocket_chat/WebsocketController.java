package com.example.websocket_chat;


import dev.onvoid.webrtc.RTCIceCandidate;
import dev.onvoid.webrtc.RTCSessionDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

/**
 * Выполняет роль обработчика входящих сообщений от клиентов и ретранслятора этих сллющений остальным клиентам чрез брокер сообщений
 */
@Controller
public class WebsocketController {

    // Используется Websocket для отправки сообщений клиентам.
    private final WebsocketSessionManager sessionManager;

    @Autowired
    public WebsocketController(WebsocketSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }


    /**
     * Метод обработчик сообщений, отправленных из GUI.
     * Вызывается, когда клиент отправил сообщение на /app/message
     * Выводит лог принятого сообщения в консоль и отправляет сообщение на /topic/messages, где MessageBroker будет рассылать его клиентам.
     *
     * @param message
     */
    @MessageMapping("/message")
    public void handleMessage(Message message) {
        System.out.println("Received message from user: " + message.getUser() + ": " + message.getMessage());
        sessionManager.broadcastNewMessage(message);
    }

    /**
     * Метод добавляет имя пользователя в список активных имён пользователей, при получении сообщения с /app/connect
     * и затем рассылает всем пользователям, подписанным на /topic/users список активных имён.
     * Выводит лог в консоль.
     *
     * @param username
     */
    @MessageMapping("/connect")
    public void connectUser(String username) {
        sessionManager.addUsername(username);
        sessionManager.broadcastActiveUsernames();
        System.out.println(username + " connected!");
    }

    /**
     * Метод удаляет имя пользователя из списка активных имён пользователей, при получении сообщения с /app/disconnect
     * и затем рассылает всем пользователям, подписанным на /topic/users список активных имён.
     * Выводит лог в консоль.
     *
     * @param username
     */
    @MessageMapping("/disconnect")
    public void disconnectUser(String username) {
        sessionManager.removedUsername(username);
        sessionManager.broadcastActiveUsernames();
        System.out.println(username + " disconnected!");
    }


    @MessageMapping("/request-users")
    public void requestUsers() {
        sessionManager.broadcastActiveUsernames();
        System.out.println("Requesting Users");
    }


    /**
     * Метод принимает description от клиента, собирающегося создать прямое подключение и рассылает всем пользователся,
     * подписанным на /topic/peer
     */
    @MessageMapping("/description")
    public void handleDescription(RTCSessionDescriptionDTO dto) {
        sessionManager.addDescription(dto);
        System.out.println("Adding description");
    }

    @MessageMapping("/candidate")
    public void handleICECandidates(RTCIceCandidateDTO dto) {
        sessionManager.addICECandidate(dto);
        System.out.println("Adding ICE candidate");
    }

    @MessageMapping("/request-description")
    public void requestDescription() {
        sessionManager.broadcastDescriptions();
        System.out.println("Broadcasting description");
    }

    @MessageMapping("/request-candidates")
    public void requestICECandidate() {
        sessionManager.broadcastICECandidate();
        System.out.println("Broadcasting ICE candidate");
    }
}
