package com.example.websocket_chat;

import dev.onvoid.webrtc.RTCIceCandidate;
import dev.onvoid.webrtc.RTCSessionDescription;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * Обработчик событий для STOMP сессии клиента.
 */
public class StompSessionHandler extends StompSessionHandlerAdapter {
    private String username;
    private MessageListener messageListener;

    public StompSessionHandler(MessageListener messageListener, String username) {
        this.username = username;
        this.messageListener = messageListener;
    }


    // Вызывается после успешного подключения к серверу, основная точка инициализации после подключения
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        System.out.println("Client " + username + " Connected!");

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
                        messageListener.onMessageReceive(message);
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


        session.subscribe("/topic/users", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return new ArrayList<String>().getClass();
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try {
                    if (payload instanceof ArrayList) {
                        ArrayList<String> listOfUsers = (ArrayList<String>) payload;
                        messageListener.onActiveUsersUpdated(listOfUsers);
                        System.out.println("Received list of active users: " + listOfUsers);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        System.out.println("Client subscribed to /topic/users");

        session.subscribe("/topic/descriptions", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return new RTCSessionDescriptionDTO().getClass();
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try {
                    if (payload instanceof RTCSessionDescriptionDTO) {
                        RTCSessionDescriptionDTO dto = (RTCSessionDescriptionDTO) payload;
                        messageListener.onDescriptionReceive(dto);
                        System.out.println("Receive remote description: " + dto);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        System.out.println("Client subscribed to /topic/descriptions");

        session.subscribe("/topic/candidates", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return new RTCIceCandidateDTO().getClass();
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try {
                    if (payload instanceof RTCIceCandidateDTO) {
                        RTCIceCandidateDTO dto = (RTCIceCandidateDTO) payload;
                        messageListener.onICECandidateReceive(dto);
                        System.out.println("Receive remote ICE candidate " + dto);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        System.out.println("Client subscribed to /topic/candidates");

        // Отправка сообщения на /app/connect, чтобы добавить нового пользователя в список
        session.send("/app/connect", username);
        session.send("/app/request-users", "");
    }

    // Вызывается если происходит ошибка соединения
    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        exception.printStackTrace();
    }
}
