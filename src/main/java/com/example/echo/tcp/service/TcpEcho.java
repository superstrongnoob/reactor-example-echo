package com.example.echo.tcp.service;

import io.netty.channel.nio.NioEventLoopGroup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.DisposableServer;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;
import reactor.netty.tcp.TcpServer;

import java.time.Duration;

@Service
public class TcpEcho {

    @Value("${echo.host}")
    private String host;

    @Value("${echo.tcp_port}")
    private int port;

    private DisposableServer server;

    @PostConstruct
    public void start() {
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        this.server = TcpServer.create()
                .runOn(group)
                .host(host)
                .port(port)
                .doOnConnection(this::onConnectionMade)
                .handle(this::onData)
                .bindNow();
    }

    @PreDestroy
    public void stop() {
        System.out.println("TcpEcho destroy");
        this.server.disposeNow();
    }

    private void onConnectionMade(Connection conn) {
        System.out.println(conn.channel());
    }

    private Publisher<Void> onData(NettyInbound inbound, NettyOutbound outbound) {
        return inbound.receive()
                .asString()
                .doOnNext(s -> System.out.println(s + " on tcp thread " + Thread.currentThread().getName()))
                .map(s -> "hello " + s)
                .delayElements(Duration.ofSeconds(1))
                .flatMap(s -> outbound.sendString(Mono.just(s)))
                .then();
    }
}
