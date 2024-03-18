package com.vp.alf.common.security;

import com.vp.alf.common.controls.AlfNeo4jOperations;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class AlfUserDetailsService implements UserDetailsService {

    private final AlfNeo4jOperations neo4jOperations;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return neo4jOperations.getUserByEmail(email)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UsernameNotFoundException("User not found"))))
                .block();
    }
}
