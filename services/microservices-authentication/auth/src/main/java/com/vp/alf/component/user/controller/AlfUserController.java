package com.vp.alf.component.user.controller;

import com.vp.alf.component.user.service.AlfUserService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import user.AlfUserDto;

import javax.websocket.server.PathParam;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class AlfUserController {

    private final AlfUserService userService;

    @PostMapping("/create")
    @RateLimiter(name = "alf")
    public Mono<String> upsertExpert(@RequestBody AlfUserDto user) {
        return userService.createUser(user);
    }

    @GetMapping("/subscribe")
    public Mono<Boolean> subscribe(@PathParam("userId") String userId, @PathParam("eventId") Long eventId) {
        return userService.subscribeOnEvent(userId, eventId);
    }

}
