package com.example.websocket_chat;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

@SpringBootApplication
public class WebsocketChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebsocketChatApplication.class, args);
	}

}
