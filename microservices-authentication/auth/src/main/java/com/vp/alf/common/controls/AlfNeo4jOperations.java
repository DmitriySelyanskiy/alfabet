package com.vp.alf.common.controls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlfNeo4jOperations {

    private final AlfNeo4jQueryRunner queryRunner;
    private final BCryptPasswordEncoder passwordEncoder;

    public Mono<UserDetails> getUserByEmail(String email) {
        return queryRunner.readQuerySingle(AlfNeo4jTemplates.getUserByEmail(email))
                .map(record -> User.builder()
                                .username(record.get("email").asString())
                                .password(passwordEncoder.encode(record.get("password").asString()))
                                .authorities(record.get("permission").asString())
                                .build()
                       );
    }
}
