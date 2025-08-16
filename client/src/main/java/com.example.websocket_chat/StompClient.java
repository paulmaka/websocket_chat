package com.example.websocket_chat;

import dev.onvoid.webrtc.RTCIceCandidate;
import dev.onvoid.webrtc.RTCSessionDescription;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * Инкапсулирует логику подключения к серверу и создания сессии
 * Предоставляет возможность отправки сообщений
 */
public class StompClient {

    // Позволяет подключиться к Stomp серверу.

    private StompSession session;
    private String username;

    public StompClient(MessageListener messageListener, String username) throws ExecutionException, InterruptedException {
        this.username = username;


        // Создаётся список транспортов для SockJS, в данном случае только WebSocket
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));

        // Создаётся SockJS клиент, оборачивающий транспорты
        SockJsClient sockJsClient = new SockJsClient(transports); // Базово не поддерживает STOMP, поэтому нужно создать следующий новый объект

        // Создаётся STOMP клиент, добавляющий поддержку STOMP
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);

        // Устанавливается конвертер сообщейни при помощи Jackson в JSON, сериализаци и десериализация объектов инкапсулирована
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        // Создаётся обработчик сессии STOMP, он управляет логикой внутри сессии. Задаёт действия после подключения, при получении сообщений, при получнеии ошибок
        StompSessionHandler sessionHandler = new StompSessionHandler(messageListener, username);

        // Адрес WebSocket сервера.
        String url = "http://localhost:80/ws"; //TODO выенсти в CI/CD

        // Создание STOMP сессии с подключением к заданному серверу и передачей созданного обработчика.
        session = stompClient.connectAsync(url, sessionHandler).get();
    }

    /** Отправляет объект message на /app/message
     *
     * @param message
     */
    public void sendMessage(Message message) {
        try {
            session.send("/app/message", message);
            System.out.println("Message sent " + message.getMessage() + " by: " + message.getUser());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Передаёт сообщение на /app/disconnect для отключения пользователя с данным именем
     *
     * @param username
     */
    public void disconnectUser(String username) {
        session.send("/app/disconnect", username);
        System.out.println("Disconnect user: " + username);
    }


    /**
     * Отправляет offer на /app/description
     *
     */
    public void sendOfferDescription(RTCSessionDescriptionDTO dto) {
        session.send("/app/offer", dto);
        System.out.println("Offer has sent: " + dto);
    }

    public void sendAnswerDescription(RTCSessionDescriptionDTO dto) {
        session.send("/app/answer", dto);
        System.out.println("Answer has sent: " + dto);
    }


    /**
     * Отправляет ICE candidate на signaling server /app/candidate
     *
     */

    public void sendCandidate(RTCIceCandidateDTO dto) {
        session.send("/app/candidate", dto);
        System.out.println("ICE candidate has sent: " + dto);
    }

    public void requestRemoteCandidate(String username) {
        session.send("/app/request-candidate", username);
        System.out.println("Request candidate: " + username);
    }

    public void requestRemoteDescription() {
        session.send("/app/request-offer", "");
        System.out.println("Request description");
    }

}
