package com.example.echo.websocket.config;

import com.example.echo.websocket.service.WebSocketEcho;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.resources.LoopResources;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebSocketConfig {

    @Value("${echo.host}")
    private String host;

    @Value("${echo.websocket_port}")
    private int port;

    @Bean
    public HandlerMapping handlerMapping(WebSocketEcho echoHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/websocket_echo", echoHandler);
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    public DisposableServer disposableServer(HttpHandler httpHandler) {
        ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
        HttpServer httpServer = HttpServer.create()
                .host(host)
                .port(port)
                .runOn(LoopResources.create("webflux-loop", 1, 1, true));
        return httpServer.handle(adapter).bindNow();
    }
}
