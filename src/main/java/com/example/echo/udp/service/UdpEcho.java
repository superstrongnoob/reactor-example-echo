package com.example.echo.udp.service;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.*;
import reactor.netty.resources.LoopResources;
import reactor.netty.udp.UdpInbound;
import reactor.netty.udp.UdpOutbound;
import reactor.netty.udp.UdpServer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class UdpEcho {

    @Value("${echo.host}")
    private String host;

    @Value("${echo.udp_port}")
    private int port;

    private Connection server;

    @PostConstruct
    public void start() {
        LoopResources loopResources = LoopResources.create("udp-echo", 1, true);
        this.server = UdpServer.create()
                .runOn(loopResources)
                .host(host)
                .port(port)
                .handle(this::onData)
                .bindNow();
    }

    @PreDestroy
    public void stop() {
        System.out.println("UdpEcho destroy");
        this.server.disposeNow();
    }

    private Publisher<Void> onData(UdpInbound inbound, UdpOutbound outbound) {

        return inbound.receiveObject()
                .flatMap(o -> {
                    if (o instanceof DatagramPacket p) {
                        String receivedMessage = p.content().toString(CharsetUtil.UTF_8);
                        System.out.println(receivedMessage + " on udp thread " + Thread.currentThread().getName());

                        String s = "hello " + receivedMessage;
                        Charset charset = StandardCharsets.UTF_8;
                        ByteBuf buffer = Unpooled.copiedBuffer(s, charset);
                        return Mono.just(new DatagramPacket(buffer, p.sender()))
                                .delayElement(Duration.ofSeconds(1))
                                .publishOn(Schedulers.parallel()); // 使用并行调度器
                    } else {
                        return Mono.error(new Exception("Unexpected type of the message: " + o));
                    }
                })
                .as(outbound::sendObject);
    }
}
