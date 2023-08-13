package com.example.echo.websocket.service;


import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class WebSocketEcho implements WebSocketHandler {
    @Override
    public @NonNull Mono<Void> handle(@NonNull WebSocketSession session) {
        return session.receive()
                .map(msg -> {
                    String receivedMessage = msg.getPayloadAsText();
                    System.out.println(receivedMessage + " on websocket thread " + Thread.currentThread().getName());
                    return "hello " + receivedMessage;
                })
                .map(session::textMessage)
                .delayElements(Duration.ofSeconds(1))
                .as(session::send);
    }
}
