package com.example.echo.http.controller;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class HttpEcho {

    @PostMapping("/http_post_echo")
    public Mono<String> echo(@RequestBody Mono<String> body) {
        return body
                .doOnNext(s -> System.out.println(s + " on http thread (post) " + Thread.currentThread().getName()))
                .map(s -> "hello " + s)
                .delayElement(Duration.ofSeconds(1));
    }

    @RequestMapping("/http_get_echo")
    public Mono<String> echo2() {
        return Mono.just("hello world")
                .doOnNext(s -> System.out.println(s + " on http thread (get) " + Thread.currentThread().getName()))
                .delayElement(Duration.ofSeconds(1));
    }
}