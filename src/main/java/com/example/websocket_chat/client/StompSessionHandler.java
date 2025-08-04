package com.example.websocket_chat.client;

import com.example.websocket_chat.Message;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;


/**
 * Обработчик событий для STOMP сессии клиента.
 */
public class StompSessionHandler extends StompSessionHandlerAdapter {
    private String username;

    public StompSessionHandler(String username) {
        this.username = username;
    }


    // Вызывается после успешного подключения к серверу, основная точка инициализации после подключения
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        System.out.println("Client " + username + " Connected!");

        // Отправка сообщения на /app/connect, чтобы добавить нового пользователя в список
        session.send("/app/connect", username);

        // Все сообщения, отправленные сервером на /topic/messages будут попадать в handleFrame
        session.subscribe("/topic/messages", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Message.class;
            }
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try {
                    if(payload instanceof Message) {
                        Message message = (Message) payload;
                        System.out.println("Received message: " + message.getUser() + ": " + message.getMessage());
                    } else {
                        System.out.println("Received unexpected payload type: " + payload.getClass());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        System.out.println("Client subscribed to /topic/messages");

    }

    // Вызывается если происходит ошибка соединения
    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        exception.printStackTrace();
    }
}
