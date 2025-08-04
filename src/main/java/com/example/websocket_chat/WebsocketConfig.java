package com.example.websocket_chat;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


/**
 * Конфигурирует поддержку WebSocket и STOMP-протокола для обмена сообщениями между клиентом и сервером.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {


    /**
     * Метод, настраивающий брокер сообщений.
     * Он будет получать запросы от клиентов с /app,
     * а отдавать с /topic.
     * MessageBroker рассылает сообщения клиентам.
     *
     * @param config
     */

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Включает простой брокер сообщений,
        // поддерживает рассылку от сервера всем подписанным клиентам, маршрутизирует сообщения по префиксу topic
        config.enableSimpleBroker("/topic");

        // Устанавлиевает префикс сообщений от клиента.
        // Сообщения, отправленные с /app/path будут попадать в метод помеченный @MessageMapping("/path")
        config.setApplicationDestinationPrefixes("/app");
    }


    /**
     * Метод, устанавливающий эндпоинт для протокола обмена сообщениями Stomp.
     * В данном случае /ws.
     * withSockJS() - метод настраивающий работу в браузерах, не поддерживающих Websocket при помощи SockJS
     *
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // Определяет точку подключения клиентов к WebSocket/STOMP-серверу.
        // Клиенты будут подключаться по адресу: ws://<host>:<port>/ws
        registry.addEndpoint("/ws").withSockJS();
    }
}
