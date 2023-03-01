package com.example.demoprint.services;

import com.example.demoprint.requests.CardRequest;
import reactor.core.publisher.Mono;

public interface PrintService {

   // void print() throws Exception;
    Mono<String> print(Mono<CardRequest> requestMono) throws Exception;
}
