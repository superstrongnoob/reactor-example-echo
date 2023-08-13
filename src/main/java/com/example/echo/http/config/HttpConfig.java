package com.example.echo.http.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.resources.LoopResources;


@Configuration
public class HttpConfig {

    @Value("${echo.host}")
    private String host;

    @Value("${echo.http_port}")
    private int port;

    @Bean
    public LoopResources loopResources() {
        return LoopResources.create("myEventLoop", 1, true);
    }

    @Bean
    public NettyReactiveWebServerFactory nettyReactiveWebServerFactory(LoopResources loopResources) {
        NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory();
        factory.setPort(port);
        factory.addServerCustomizers(httpServer -> httpServer.runOn(loopResources),
                httpServer -> httpServer.host(host));
        return factory;
    }
}
