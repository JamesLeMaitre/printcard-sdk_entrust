package com.example.demoprint.controllers;

import com.example.demoprint.requests.CardRequest;
import com.example.demoprint.services.PrintService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Slf4j
@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping(value = "api/v1", produces = {APPLICATION_JSON_VALUE})
public class HomeController {

    private final PrintService service;

    @PostMapping(value = "print/card", consumes = {MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> index(@ModelAttribute @Valid CardRequest request, BindingResult bindResult) throws Exception {
        if (bindResult.hasErrors()) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(bindResult.getAllErrors());
        }
        return ResponseEntity.status(OK)
                .body(service.print(Mono.just(request)));
    }
}
