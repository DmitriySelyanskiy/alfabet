package com.vp.alf.component.user.controller;

import com.vp.alf.component.user.service.AlfUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;
import user.AlfUserDto;

@Controller
@RequiredArgsConstructor
public class AlfUserController {

    private final AlfUserService userService;

    @MessageMapping("user.create")
    public Mono<String> createUser(AlfUserDto user) {
        return userService.createUser(user);
    }

    @MessageMapping("user.subscribe.{userId}")
    public Mono<Boolean> subscribeOnEvent(@DestinationVariable String userId, Long eventId) {
        return userService.subscribeOnEvent(userId, eventId);
    }
}
